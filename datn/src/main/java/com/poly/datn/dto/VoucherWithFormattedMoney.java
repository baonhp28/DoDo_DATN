package com.poly.datn.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherWithFormattedMoney {
    private Integer id;
    private String voucherCode;
    private Integer quantity;
    private Integer percent;
    private String maxMoney; 
    private String minMoney; 
    private String startDate;
    private String endDate;
}