package com.poly.datn.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "voucher")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "voucher_code", length = 10, nullable = false, unique = true)
    @Size(max = 20, message = "Mã voucher không được quá 10 ký tự")
    private String voucherCode;

    @Column(name = "quantity", nullable = false)
    @Min(value = 1, message = "Số lượng voucher phải lớn hơn hoặc bằng 1")
    private Integer quantity;

    @Column(name = "percent", nullable = false)
    @Min(value = 50, message = "Phần trăm giảm giá phải từ 50%")
    @Max(value = 100, message = "Phần trăm giảm giá không được lớn hơn 100%")
    private Integer percent;

    @Column(name = "max_money", nullable = false)
    @DecimalMin(value = "10.000", message = "maxMoney không được dưới 10.000")
    private Double maxMoney;

    @Column(name = "min_money", nullable = false)
    @DecimalMin(value = "10.000", message = "minMoney không được dưới 10.000")
    private Double minMoney;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @PrePersist
    @PreUpdate
    private void validateDates() {
        LocalDateTime now = LocalDateTime.now();
        if (startDate.isBefore(now.plusDays(1))) {
            throw new IllegalArgumentException("Ngày bắt đầu phải lớn hơn ngày hiện tại 1 ngày.");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Ngày kết thúc không được bé hơn ngày bắt đầu.");
        }
    }
    
}
