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

public class BillDetailDTO {
    private Integer id;
    private Integer skuId;
    private String productName;
    private Integer quantity;
    private Double price;
    private Integer voucherId;
}

