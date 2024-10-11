package com.poly.datn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.poly.datn.dto.EmployeeDTO;
import com.poly.datn.dto.UserResponseDTO;
import com.poly.datn.dto.UserStatusUpdateDTO;
import com.poly.datn.entities.Users;
import com.poly.datn.exceptions.AccessDeniedException;
import com.poly.datn.exceptions.IllegalUpdateException;
import com.poly.datn.service.EmployeeService;
import com.poly.datn.service.UsersService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    //hehhe
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UsersService userService;

    // API để thêm nhân viên mới
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/addEmployee")
    public ResponseEntity<?> addEmployee(@RequestBody EmployeeDTO employeeDTO) {
        try {
            Users newEmployee = employeeService.addEmployee(employeeDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(newEmployee);
        } catch (IllegalUpdateException e) {
            // Trả về thông báo lỗi dạng JSON nếu có lỗi về dữ liệu nhập vào
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("isError", "true");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            // Trả về thông báo lỗi dạng JSON cho các lỗi khác không xác định
            Map<String, String> response = new HashMap<>();
            response.put("message", "Đã xảy ra lỗi khi thêm nhân viên.");
            response.put("isError", "true");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // API để thay đổi trạng thái tài khoản nhân viên
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/changeAccountStatus/{id}")
    public ResponseEntity<?> changeAccountStatus(@PathVariable Integer id, @RequestParam Boolean status) {
        try {
            Users updatedEmployee = employeeService.changeAccountStatus(id, status);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Trạng thái tài khoản đã được thay đổi thành công");
            response.put("employee", updatedEmployee);
            response.put("isError", "false");
            return ResponseEntity.ok(response);
        } catch (IllegalUpdateException e) {
            // Trả về thông báo lỗi dạng JSON nếu có lỗi về dữ liệu nhập vào
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("isError", "true");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            // Trả về thông báo lỗi dạng JSON cho các lỗi khác không xác định
            Map<String, String> response = new HashMap<>();
            response.put("message", "Đã xảy ra lỗi khi thay đổi trạng thái tài khoản.");
            response.put("isError", "true");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Hiển thị danh sách khách hàng
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        try {
            List<UserResponseDTO> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (AccessDeniedException e) {
            // Xử lý lỗi truy cập bị từ chối
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            // Xử lý các lỗi khác nếu cần thiết
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Cập nhật trạng thái khách hàng
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    @PutMapping("/users/{id}")
    public ResponseEntity<String> updateUserStatus(
            @PathVariable Integer id,
            @RequestBody UserStatusUpdateDTO statusUpdateDTO,
            Authentication authentication) {
        try {
            // Lấy tên người dùng từ token
            String username = authentication.getName();

            // Gọi phương thức cập nhật trạng thái từ UserService
            return userService.updateUserStatus(id, statusUpdateDTO.isStatus(), username);
        } catch (IllegalUpdateException e) {
            // Trả về trạng thái 403 nếu có vấn đề về quyền cập nhật
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (RuntimeException e) {
            // Trả về trạng thái 404 nếu người dùng không tồn tại
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
