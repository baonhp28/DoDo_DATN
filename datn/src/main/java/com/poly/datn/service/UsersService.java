package com.poly.datn.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.poly.datn.dto.AddressDTO;
import com.poly.datn.dto.UserDTO;
import com.poly.datn.dto.UserDTO1;
import com.poly.datn.dto.UserResponseDTO;
import com.poly.datn.dto.UserUpdateDTO;
import com.poly.datn.entities.PasswordResetToken;
import com.poly.datn.entities.Role;
import com.poly.datn.entities.Users;
import com.poly.datn.exceptions.AccessDeniedException;
import com.poly.datn.exceptions.IllegalUpdateException;
import com.poly.datn.repository.PasswordResetTokenRepository;
import com.poly.datn.repository.RoleRepository;
import com.poly.datn.repository.UserRepository;

import jakarta.transaction.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UsersService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    // Phương thức lấy tất cả nhân viên và lọc dữ liệu
    public List<UserResponseDTO> getAllUsers() {
        // Lấy thông tin người dùng hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String role = userDetails.getAuthorities().toString();
    
        // Kiểm tra quyền truy cập
        if (role.contains("ROLE_STAFF") || role.contains("ROLE_ADMIN")) {
            // Chỉ lọc người dùng có vai trò "Customer"
            return userRepository.findAll().stream()
                    .filter(user -> user.getRole().getRoleName().equalsIgnoreCase("Customer")) // Lọc vai trò "Customer"
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } else {
            // Ném ngoại lệ nếu người dùng không có quyền
            throw new AccessDeniedException("Bạn không có quyền thực hiện chức năng này.");
        }
    }
    

    public Optional<UserDTO> getUserByIdFromToken() {
        // Lấy thông tin người dùng đã đăng nhập từ SecurityContextHolder
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
    
        return userRepository.findByUsername(username)
            .map(user -> {
                // Kiểm tra roleName của khách hàng
                if (!user.getRole().getRoleName().equals("Customer")) {
                    throw new IllegalArgumentException("Chỉ hiện thông tin của Khách hàng!");
                }
    
                // Tạo và thiết lập thông tin cho UserDTO
                UserDTO userDTO = new UserDTO();
                userDTO.setId(user.getId());
                userDTO.setFullname(user.getFullName());
                userDTO.setBirthday(user.getBirthday());
                userDTO.setGender(user.getGender());
                userDTO.setPhone(user.getPhone());
                userDTO.setEmail(user.getEmail());
                userDTO.setUsername(user.getUsername());
                userDTO.setRoleID(user.getRole().getRoleName());
    
                // Lấy toàn bộ danh sách địa chỉ của người dùng
                List<AddressDTO> addressDTOs = user.getAddresses().stream()
                    .map(address -> {
                        AddressDTO addressDTO = new AddressDTO();
                        addressDTO.setId(address.getId());
                       
                        return addressDTO;
                    })
                    .collect(Collectors.toList());
    
                userDTO.setAddresses(addressDTOs);  // Thiết lập danh sách địa chỉ cho DTO
    
                return userDTO;
            });
    }
    
    


    public Optional<Users> getEmployeeByIdOp(int id) {
        return userRepository.findById(id);
    }

    // Phương thức chuyển đổi từ Users sang UserResponseDTO
    private UserResponseDTO convertToDTO(Users user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setBirthday(user.getBirthday());
        dto.setGender(user.getGender());
        dto.setPhone(user.getPhone());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setAddresses(user.getAddresses());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());
        return dto;
    }

    // Phương thức lưu nhân viên mới hoặc cập nhật thông tin nhân viên
    public Users saveEmployee(Users user) {
        return userRepository.save(user);
    }

    public Users createUser(Users user) {
        return userRepository.save(user);
    }

    public Users updateUser(UserUpdateDTO userDetails) {
        // Lấy id từ Token
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
    
        // Kiểm tra xem role của user có phải là "Customer" hay không
        if (user.getRole() == null || !user.getRole().getRoleName().equals("Customer")) {
            throw new IllegalUpdateException("Chỉ cho phép cập nhật thông tin của Khách hàng");
        }
    
        // Cập nhật các trường có thể thay đổi
        user.setFullName(userDetails.getFullname());
        user.setBirthday(userDetails.getBirthday());
        user.setGender(userDetails.getGender());
        user.setPhone(userDetails.getPhone());
        user.setEmail(userDetails.getEmail());
    
        // Lưu lại các thay đổi
        userDetails.setMessage("Cập nhật thông tin thành công!");
        return userRepository.save(user);
    }
    

    // Phần STAFF & ADMIN
    public ResponseEntity<String> updateUserStatus(Integer id, Boolean status, String username) {
        // Tìm người dùng theo ID
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
    
        // Kiểm tra xem role của người dùng có phải là "Customer" không
        if (user.getRole() == null || !user.getRole().getRoleName().equals("Customer")) {
            throw new IllegalUpdateException("Chỉ cho phép cập nhật trạng thái của người dùng với role là Khách hàng");
        }
    
        if (!status.equals(user.getStatus())) {
            user.setStatus(status);
            userRepository.save(user);
            return new ResponseEntity<>("Cập nhật trạng thái người dùng thành công", HttpStatus.OK);
        } else {
            throw new IllegalUpdateException("Cập nhật trạng thái thất bại. Không được quyền chỉnh sửa trường khác!");
        }
    }

    



    //////////////////////////////////PHẦN CỦA G.BẢO/////////////////////////////////////////


    public Map<String, String> registerEmployee(UserDTO1 employeeDTO) {
        Map<String, String> response = new HashMap<>();
        try {
            validateEmployeeDTO(employeeDTO); // Ném lỗi nếu có
            Users employee = createUserEntity(employeeDTO);
            userRepository.save(employee);
            response.put("message", "Người dùng đã được đăng ký thành công");
        } catch (IllegalUpdateException e) {
            // Để GlobalExceptionHandler xử lý
            throw e;
        } catch (Exception e) {
            response.put("message", "Đã xảy ra lỗi trong quá trình đăng ký: " + e.getMessage());
        }
        return response;
    }
    
    
    
    private void validateEmployeeDTO(UserDTO1 employeeDTO) {
        Map<String, String> errors = new HashMap<>();
    
        if (isNullOrEmpty(employeeDTO.getUsername())) {
            errors.put("username", "Tên người dùng không được bỏ trống!");
        } else if (!Pattern.matches("[A-Za-z0-9]*", employeeDTO.getUsername())) {
            errors.put("username", "Tên người dùng không được chứa ký tự đặc biệt hoặc khoảng trắng!");
        } else if (findByUsername(employeeDTO.getUsername()) != null) {
            errors.put("username", "Tên người dùng đã được sử dụng!");
        }
    
        if (isNullOrEmpty(employeeDTO.getPassword())) {
            errors.put("password", "Mật khẩu không được bỏ trống!");
        } else if (employeeDTO.getPassword().length() < 6) {
            errors.put("password", "Mật khẩu phải có ít nhất 6 ký tự!");
        } else if (!Pattern.matches("[A-Za-z0-9]*", employeeDTO.getPassword())) {
            errors.put("password", "Mật khẩu không được chứa ký tự đặc biệt!");
        } else if (!employeeDTO.getPassword().equals(employeeDTO.getConfirmPassword())) {
            errors.put("confirmPassword", "Mật khẩu và nhập lại mật khẩu không khớp!");
        }
    
        if (isNullOrEmpty(employeeDTO.getFullname())) {
            errors.put("fullname", "Tên đầy đủ không được bỏ trống!");
        } else if (!Pattern.matches("[\\p{L} ]+", employeeDTO.getFullname())) {
            errors.put("fullname", "Tên đầy đủ chỉ được chứa ký tự chữ cái và khoảng trắng!");
        }
    
        if (isNullOrEmpty(employeeDTO.getEmail())) {
            errors.put("email", "Email không được bỏ trống!");
        } else if (!isValidEmail(employeeDTO.getEmail())) {
            errors.put("email", "Email không hợp lệ!");
        } else if (emailContainsWhitespaceOrMultipleAtSymbols(employeeDTO.getEmail())) {
            errors.put("email", "Email không được chứa khoảng trắng hoặc nhiều hơn một ký tự '@'!");
        }
    
        if (employeeDTO.getBirthday() != null && employeeDTO.getBirthday().after(new java.util.Date())) {
            errors.put("birthday", "Ngày sinh không hợp lệ!");
        }
    
        if (employeeDTO.getPhone() == null || !employeeDTO.getPhone().matches("\\d{10}")) {
            errors.put("phone", "Số điện thoại phải có đúng 10 số và không được chứa ký tự chữ cái!");
        }
    
        Integer roleId = employeeDTO.getRoleID() != null ? employeeDTO.getRoleID() : 2;
        Role role = roleRepository.findById(roleId).orElse(null);
        if (role == null) {
            errors.put("role", "Vai trò không hợp lệ!");
        }
    
        if (!errors.isEmpty()) {
            throw new IllegalUpdateException("Lỗi khi xác thực dữ liệu: " + errors);
        }
    }
    
    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return Pattern.matches(emailRegex, email);
    }

    private boolean emailContainsWhitespaceOrMultipleAtSymbols(String email) {
        return email.contains(" ") || email.chars().filter(ch -> ch == '@').count() != 1;
    }

    private Users createUserEntity(UserDTO1 employeeDTO) {
        Users employee = new Users();
        employee.setFullName(employeeDTO.getFullname());
        employee.setBirthday(employeeDTO.getBirthday());
        employee.setGender(employeeDTO.getGender());
        employee.setPhone(employeeDTO.getPhone());
        employee.setEmail(employeeDTO.getEmail());
        employee.setRole(roleRepository.findById(employeeDTO.getRoleID() != null ? employeeDTO.getRoleID() : 2).orElseThrow(() -> new IllegalUpdateException("Vai trò không hợp lệ!")));
        employee.setUsername(employeeDTO.getUsername());
        employee.setPassword(passwordEncoder.encode(employeeDTO.getPassword()));
        employee.setStatus(true);
        return employee;
    }

    public Users findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public Map<String, String> authenticate(String username, String rawPassword) {
        Users employee = userRepository.findByUsername(username).orElse(null);
    
        if (employee == null || !passwordEncoder.matches(rawPassword, employee.getPassword())) {
            throw new IllegalUpdateException("Tên người dùng hoặc mật khẩu không đúng.");
        }
    
        if (!employee.getStatus()) { // Kiểm tra trạng thái tài khoản
            throw new IllegalUpdateException("Tài khoản của bạn đã bị khóa.");
        }
    
        // Đăng nhập thành công, trả về thông báo thành công
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Đăng nhập thành công!");
        return response;
    }

    public void changePassword(String oldPassword, String newPassword) {
        // Lấy tên người dùng từ SecurityContextHolder
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // Tìm người dùng theo username
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));
        
        // Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu cũ không đúng");
        }
        
        // Kiểm tra mật khẩu mới có giống với mật khẩu cũ không
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu mới không được giống với mật khẩu cũ");
        }
        
        // Kiểm tra độ dài mật khẩu mới
        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("Mật khẩu mới phải có ít nhất 6 ký tự");
        }
        // Cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public Users findByResetToken(String token) {
        Optional<PasswordResetToken> resetTokenOptional = passwordResetTokenRepository.findByToken(token);
        
        if (resetTokenOptional.isPresent()) {
            PasswordResetToken resetToken = resetTokenOptional.get();
            return resetToken.getUser();
        }
        return null;
    }
    
    @Transactional
    public void save(Users user) {
        userRepository.save(user);
    }

    
    
}