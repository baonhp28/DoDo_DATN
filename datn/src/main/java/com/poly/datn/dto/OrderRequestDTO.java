package com.poly.datn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderRequestDTO {
    private Integer userId;
    private String customerName;
    private String numberPhone;
    private String address;
}
