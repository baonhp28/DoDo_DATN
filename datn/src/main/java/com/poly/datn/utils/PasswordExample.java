package com.poly.datn.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordExample {
    public static void main(String[] args) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // Mã hóa mật khẩu khi đăng ký
        String rawPassword = "password123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        System.out.println("Mật khẩu mã hóa: " + encodedPassword);

        // Kiểm tra mật khẩu khi đăng nhập
        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
        System.out.println("Mật khẩu khớp: " + matches); // Sẽ in ra true
    }
}
