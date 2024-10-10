package com.poly.datn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.poly.datn.entities.Voucher;

public interface VoucherRepository extends JpaRepository<Voucher,Integer>{
    // void deleteByEndDateBefore(LocalDateTime dateTime);
    boolean existsByVoucherCode(String voucherCode);
}
