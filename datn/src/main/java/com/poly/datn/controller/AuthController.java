package com.poly.datn.controller;

import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.poly.datn.dto.AuthRequest;
import com.poly.datn.dto.ChangePasswordRequest;
import com.poly.datn.dto.ResetPasswordRequest;
import com.poly.datn.dto.UserDTO1;
import com.poly.datn.entities.Users;
import com.poly.datn.exceptions.IllegalUpdateException;
import com.poly.datn.service.PasswordResetService;
import com.poly.datn.service.UserDetailsServiceImpl;
import com.poly.datn.service.UsersService;
import com.poly.datn.utils.JwtTokenUtil;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsersService UserService;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    private UserDetailsServiceImpl employeeServiceImpl;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/register")
    public ResponseEntity<?> registerEmployee(@RequestBody UserDTO1 employeeDTO) {
        Map<String, String> response = UserService.registerEmployee(employeeDTO);
        // Lấy thông báo từ response
        String message = response.get("message");
        if (message.contains("Tên người dùng đã được sử dụng!") ||
                message.contains("Vai trò không hợp lệ!") ||
                message.contains("Đã xảy ra lỗi")) {
            throw new IllegalUpdateException(message);
        }

        // Thông báo thành công
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> createAuthenticationToken(@RequestBody AuthRequest authRequest) {
        Map<String, Object> response = new HashMap<>();
    
        // Kiểm tra nếu username hoặc password bị bỏ trống
        if (authRequest.getUsername() == null || authRequest.getUsername().isEmpty()) {
            response.put("status", "error");
            response.put("message", "Tên người dùng không được bỏ trống.");
            return ResponseEntity.badRequest().body(response);
        }
    
        if (authRequest.getPassword() == null || authRequest.getPassword().isEmpty()) {
            response.put("status",  "error");
            response.put("message", "Mật khẩu không được bỏ trống.");
            return ResponseEntity.badRequest().body(response);
        }
    
        try {
            // Xác thực người dùng
            Map<String, String> authResult = UserService.authenticate(authRequest.getUsername(),
                    authRequest.getPassword());
    
            if ("error".equals(authResult.get("status"))) {
                response.put("status", "error");
                response.put("message", authResult.get("message"));
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
    
            // Tải thông tin người dùng
            final UserDetails userDetails = employeeServiceImpl.loadUserByUsername(authRequest.getUsername());
            Users employee = UserService.findByUsername(authRequest.getUsername());
    
            // Kiểm tra trạng thái tài khoản
            if (!employee.getStatus()) {
                response.put("status", "error");
                response.put("message", "Tài khoản của bạn đã bị khóa.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
    
            // Xóa mật khẩu trước khi thêm vào response
            employee.setPassword(null);
    
            // Tạo JWT token
            final String jwt = jwtTokenUtil.generateToken(userDetails);
    
            // Tạo URL chuyển hướng
            String redirectUrl = employee.getRole() != null && employee.getRole().getId() == 1 ? "/admin" : "/user"; // Giả sử roleId = 1 là admin

    
            // Tạo một response bao gồm thông tin người dùng và token
            response.put("token", jwt);
            response.put("user", employee);
            response.put("message", authResult.get("message"));
            response.put("redirectUrl", redirectUrl); // Thêm thuộc tính redirectUrl
    
            return ResponseEntity.ok(response);
        } catch (IllegalUpdateException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Đã xảy ra lỗi khi đăng nhập: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    

    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestBody Map<String, String> payload) {
        String refreshToken = payload.get("refreshToken");

        Map<String, Object> response = new HashMap<>();

        if (refreshToken == null || refreshToken.isEmpty()) {
            response.put("status", "error");
            response.put("message", "Refresh token không được bỏ trống.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            if (!jwtTokenUtil.isTokenValid(refreshToken)) {
                response.put("status", "error");
                response.put("message", "Refresh token không hợp lệ hoặc đã hết hạn.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Extract username from refresh token
            String username = jwtTokenUtil.extractUsername(refreshToken);

            // Load user details
            UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);

            // Generate new access token
            String newAccessToken = jwtTokenUtil.generateToken(userDetails);

            response.put("accessToken", newAccessToken);
            response.put("message", "Token đã được làm mới thành công.");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Đã xảy ra lỗi khi làm mới token: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody ChangePasswordRequest request) {
        Map<String, String> response = new HashMap<>();

        try {
            UserService.changePassword(request.getOldPassword(), request.getNewPassword());
            response.put("message", "Mật khẩu đã được thay đổi thành công");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        passwordResetService.generatePasswordResetToken(email);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Email reset password đã được gửi");

        return ResponseEntity.ok(response);
    }

    @Transactional
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody ResetPasswordRequest request) {
        String token = request.getToken(); // Mã xác thực 6 số từ người dùng
        String newPassword = request.getNewPassword(); // Mật khẩu mới từ người dùng

        // Tìm người dùng bằng mã xác thực (6 số)
        Users user = passwordResetService.findByToken(token);
        if (user == null) {
            // Mã xác thực không hợp lệ hoặc đã hết hạn
            Map<String, String> response = new HashMap<>();
            response.put("message", "Mã xác thực không hợp lệ hoặc đã hết hạn");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Kiểm tra nếu mật khẩu mới giống mật khẩu cũ
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Mật khẩu mới không được giống mật khẩu cũ");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(newPassword));

        try {
            // Lưu người dùng với mật khẩu mới
            UserService.save(user); // Gọi phương thức save để cập nhật user
            // Xóa mã xác thực sau khi sử dụng
            passwordResetService.deleteByToken(token);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Đặt lại mật khẩu thành công");
            return ResponseEntity.ok(response); // Trả về trạng thái thành công
        } catch (Exception e) {
            e.printStackTrace(); // Log lỗi để dễ kiểm tra
            Map<String, String> response = new HashMap<>();
            response.put("message", "Đã xảy ra lỗi khi cập nhật mật khẩu");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}