package com.poly.datn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.poly.datn.dto.UserDTO;

import com.poly.datn.dto.UserUpdateDTO;
import com.poly.datn.entities.Users;
import com.poly.datn.service.UsersService;

@RestController
public class UserController {

    @Autowired
    private UsersService userService;

    @GetMapping("/api/users")
    public ResponseEntity<UserDTO> getUserById() {
        return userService.getUserByIdFromToken()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/api/users/update")
    public ResponseEntity<Users> updateUser(@RequestBody UserUpdateDTO userDetails) {
        try {
            Users updatedUser = userService.updateUser(userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            // Xử lý lỗi khi không tìm thấy user hoặc lỗi khác
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
