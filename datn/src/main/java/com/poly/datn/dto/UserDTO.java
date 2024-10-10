package com.poly.datn.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.poly.datn.entities.Address;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private Integer id;
    private String fullname;
    private Date birthday;
    private Boolean gender;
    private String phone;
    private String email;
    private String roleID;
    private String username;
    private List<AddressDTO> addresses;  // Sử dụng AddressDTO
}
