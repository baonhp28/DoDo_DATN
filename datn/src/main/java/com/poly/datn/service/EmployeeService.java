package com.poly.datn.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.poly.datn.dto.EmployeeDTO;
import com.poly.datn.entities.Role;
import com.poly.datn.entities.Users;
import com.poly.datn.exceptions.IllegalUpdateException;
import com.poly.datn.repository.EmployeeRepository;
import com.poly.datn.repository.RoleRepository;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Users addEmployee(EmployeeDTO employeeDTO) {
        // Kiểm tra dữ liệu đầu vào
        validateEmployeeData(employeeDTO);

        // Tạo đối tượng EmployeeEntity từ DTO
        Users employee = new Users();
        employee.setFullName(employeeDTO.getFullname());
        employee.setBirthday(employeeDTO.getBirthday());
        employee.setGender(employeeDTO.getGender());
        employee.setPhone(employeeDTO.getPhone());
        employee.setEmail(employeeDTO.getEmail());

        // Lấy Role từ cơ sở dữ liệu
        Role role = roleRepository.findById(employeeDTO.getRoleId())
                                   .orElseThrow(() -> new IllegalUpdateException("Role không tồn tại"));
        employee.setRole(role);

        // Mã hóa mật khẩu và lưu đối tượng nhân viên
        employee.setUsername(employeeDTO.getUsername());
        employee.setPassword(passwordEncoder.encode(employeeDTO.getPassword()));
        employee.setStatus(employeeDTO.getStatus());

        // Lưu nhân viên vào cơ sở dữ liệu
        return employeeRepository.save(employee);
    }

    private void validateEmployeeData(EmployeeDTO employeeDTO) {
        validateFullname(employeeDTO.getFullname());
        validateUsername(employeeDTO.getUsername());
        validatePassword(employeeDTO.getPassword());
        validateEmail(employeeDTO.getEmail());
        validatePhone(employeeDTO.getPhone());
        validateBirthday(employeeDTO.getBirthday());
        validateGender(employeeDTO.getGender());
        validateRoleId(employeeDTO.getRoleId());
        validateStatus(employeeDTO.getStatus());
    }

    private void validateFullname(String fullname) {
        if (fullname == null || fullname.isEmpty()) {
            throw new IllegalUpdateException("Tên người dùng không được để trống");
        }
        if (fullname.length() < 1 || fullname.length() > 20) {
            throw new IllegalUpdateException("Phải nhập đầy đủ họ và tên người dùng");
        }
        if (!fullname.matches("^[\\p{L} ]+$")) {
            throw new IllegalUpdateException("Tên chỉ được chứa ký tự chữ cái và khoảng trắng");
        }
    }

    private void validateUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalUpdateException("Tên tài khoản không được để trống");
        }
        if (username.length() < 4 || username.length() > 20) {
            throw new IllegalUpdateException("Tên tài khoản có độ dài từ 4 đến 20 ký tự");
        }
        if (employeeRepository.existsByUsername(username)) {
            throw new IllegalUpdateException("Tên tài khoản đã tồn tại");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalUpdateException("Mật khẩu không được để trống");
        }
        if (password.length() < 6) {
            throw new IllegalUpdateException("Mật khẩu phải có ít nhất 6 ký tự");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalUpdateException("Email không được để trống");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalUpdateException("Email không hợp lệ");
        }
        if (employeeRepository.existsByEmail(email)) {
            throw new IllegalUpdateException("Email đã tồn tại");
        }
    }

    private void validatePhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            throw new IllegalUpdateException("Số điện thoại không được để trống");
        }
        if (!phone.matches("^[0-9]{10,11}$")) {
            throw new IllegalUpdateException("Số điện thoại không hợp lệ");
        }
        if (employeeRepository.existsByPhone(phone)) {
            throw new IllegalUpdateException("Số điện thoại đã tồn tại");
        }
    }

    private void validateBirthday(Date birthday) {
        if (birthday == null) {
            throw new IllegalUpdateException("Ngày sinh không được để trống");
        }
        LocalDate today = LocalDate.now();
        LocalDate birthDate = birthday.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (birthDate.isAfter(today)) {
            throw new IllegalUpdateException("Ngày sinh không được là ngày trong tương lai");
        }
    }

    private void validateGender(Boolean gender) {
        if (gender == null) {
            throw new IllegalUpdateException("Giới tính không được để trống");
        }
    }

    private void validateRoleId(Integer roleId) {
        if (roleId == null) {
            throw new IllegalUpdateException("Vai trò không được để trống");
        }
    }

    private void validateStatus(Boolean status) {
        if (status == null) {
            throw new IllegalUpdateException("Trạng thái không được để trống");
        }
    }

    public Users changeAccountStatus(Integer id, Boolean status) {
        Users employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalUpdateException("Tài khoản không tồn tại"));

        if (employee.getRole().getRoleName().equalsIgnoreCase("ADMIN")) {
            throw new IllegalUpdateException("Không thể thay đổi trạng thái của tài khoản quản trị viên.");
        }
        if (employee.getStatus().equals(status)) {
            throw new IllegalUpdateException("Trạng thái tài khoản đã giống như trạng thái hiện tại");
        }

        employee.setStatus(status);
        return employeeRepository.save(employee);
    }
}
