package com.poly.datn.dto;

import com.poly.datn.entities.Address;
import com.poly.datn.entities.Role;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserResponseDTO {
    private Integer id;
    private String fullName;
    private Date birthday;
    private Boolean gender;
    private String phone;
    private String email;
    private String username;
    private List<Address> addresses;
    private Role role;
    private Boolean status;
}

