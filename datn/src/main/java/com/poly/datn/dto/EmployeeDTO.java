package com.poly.datn.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {
    private Integer id;
    private String fullname;
    private Date birthday;
    private Boolean gender;
    private String phone;
    private String email;
    private Integer roleId;  // Đây là ID của vai trò từ lớp Role
    private String username;
    private String password;
    private Boolean status;
}
