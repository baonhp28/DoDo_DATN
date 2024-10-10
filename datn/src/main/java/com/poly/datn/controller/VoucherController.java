package com.poly.datn.controller;

import com.poly.datn.dto.VoucherDTO;
import com.poly.datn.entities.Voucher;
import com.poly.datn.service.VoucherService;
import com.poly.datn.utils.VoucherCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/vouchers")
public class VoucherController {

    @Autowired
    private VoucherService voucherService;

    // Thêm voucher mới với mã voucher tự sinh
    @PostMapping
    public ResponseEntity<VoucherDTO> addVoucher(@RequestBody Voucher voucher) {
        try {
            // Sinh mã voucher tự động
            String generatedCode = VoucherCodeGenerator.generateVoucherCode();
            voucher.setVoucherCode(generatedCode);

            Voucher addedVoucher = voucherService.addVoucher(voucher);

            // // Format giá tiền
            // String formattedMaxMoney = formatAsVND(addedVoucher.getMaxMoney());
            // String formattedMinMoney = formatAsVND(addedVoucher.getMinMoney());

            // // Cập nhật voucher với giá tiền đã định dạng
            // addedVoucher.setMaxMoney(parseVND(formattedMaxMoney));
            // addedVoucher.setMinMoney(parseVND(formattedMinMoney));

            return ResponseEntity.ok(new VoucherDTO(List.of(addedVoucher), "Thêm voucher thành công", false));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new VoucherDTO(null, "Lỗi khi thêm voucher: " + e.getMessage(), true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new VoucherDTO(null, "Lỗi không xác định khi thêm voucher: " + e.getMessage(), true));
        }
    }

    // Cập nhật thông tin voucher
    @PutMapping("/{id}")
    //   @PreAuthorize("hasRole('Admin') or hasRole('Staff')")
    public ResponseEntity<VoucherDTO> updateVoucher(@PathVariable Integer id, @RequestBody Voucher voucher) {
        try {
            Voucher updatedVoucher = voucherService.updateVoucher(id, voucher);

            // // Format giá tiền
            // String formattedMaxMoney = formatAsVND(updatedVoucher.getMaxMoney());
            // String formattedMinMoney = formatAsVND(updatedVoucher.getMinMoney());

            // // Cập nhật voucher với giá tiền đã định dạng
            // updatedVoucher.setMaxMoney(parseVND(formattedMaxMoney));
            // updatedVoucher.setMinMoney(parseVND(formattedMinMoney));

            return ResponseEntity.ok(new VoucherDTO(List.of(updatedVoucher), "Cập nhật voucher thành công", false));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new VoucherDTO(null, "Lỗi khi cập nhật voucher: " + e.getMessage(), true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new VoucherDTO(null, "Lỗi không xác định khi cập nhật voucher: " + e.getMessage(), true));
        }
    }

    // Xóa voucher
    @DeleteMapping("/{id}")
    public ResponseEntity<VoucherDTO> deleteVoucher(@PathVariable Integer id) {
        try {
            voucherService.deleteVoucher(id);
            return ResponseEntity.ok(new VoucherDTO(null, "Xóa voucher thành công", false));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new VoucherDTO(null, "Lỗi khi xóa voucher: " + e.getMessage(), true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new VoucherDTO(null, "Lỗi không xác định khi xóa voucher: " + e.getMessage(), true));
        }
    }

    // Lấy danh sách tất cả các voucher
    @GetMapping
    public ResponseEntity<VoucherDTO> getAllVouchers() {
        try {
            List<Voucher> vouchers = voucherService.getAllVouchers();
            return ResponseEntity.ok(new VoucherDTO(vouchers, "Lấy danh sách voucher thành công", false));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new VoucherDTO(null, "Lỗi khi lấy danh sách voucher: " + e.getMessage(), true));
        }
    }

    // Lấy thông tin voucher theo ID
    @GetMapping("/{id}")
    public ResponseEntity<VoucherDTO> getVoucherById(@PathVariable Integer id) {
        try {
            Voucher voucher = voucherService.getVoucherById(id);
            if (voucher != null) {
                return ResponseEntity.ok(new VoucherDTO(List.of(voucher), "Lấy voucher thành công", false));
            } else {
                return ResponseEntity.status(404).body(new VoucherDTO(null, "Voucher không tồn tại với ID: " + id, true));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new VoucherDTO(null, "Lỗi khi lấy voucher: " + e.getMessage(), true));
        }
    }

    // Phương thức tiện ích để format giá tiền thành VND
    // private String formatAsVND(Double amount) {
    //     DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
    //     symbols.setGroupingSeparator(',');
    //     DecimalFormat decimalFormat = new DecimalFormat("#,### VND", symbols);
    //     return decimalFormat.format(amount);
    // }

    // // Phương thức tiện ích để parse giá tiền từ định dạng VND
    // private Double parseVND(String amount) {
    //     try {
    //         String sanitizedAmount = amount.replace(" VND", "").replace(",", "");
    //         return Double.parseDouble(sanitizedAmount);
    //     } catch (NumberFormatException e) {
    //         throw new RuntimeException("Không thể parse số tiền từ định dạng VND: " + amount);
    //     }
    // }
}
