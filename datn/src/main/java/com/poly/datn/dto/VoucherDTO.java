package com.poly.datn.dto;

import com.poly.datn.entities.Voucher;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;  // Đảm bảo import đúng

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherDTO {
    private List<Voucher> vouchers;
    private String message;
    private boolean isError;

    //   public static String formatAsVND(Double amount) {
    //     if (amount == null) return "0 VND";
    //     DecimalFormat formatter = new DecimalFormat("#,###");
    //     return formatter.format(amount) + " VND";
    // }
}
