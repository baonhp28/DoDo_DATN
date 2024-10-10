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
public class CartItemDTO {
    private Integer id;       // ID của CartItem
    private Integer skuId;
    private Integer quantity;
}


