package com.poly.datn.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO1 {
    private String fullname;
    private Date birthday;
    private Boolean gender;
    private String phone;
    private String email;
    private Integer roleID;
    private String username;
    private String password;
    private String confirmPassword; // Thêm trường này
    private Boolean status;
}