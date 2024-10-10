package com.poly.datn.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {
    private String fullname;
    private Date birthday;
    private Boolean gender;
    private String phone;
    private String email;
    private String message;
}
