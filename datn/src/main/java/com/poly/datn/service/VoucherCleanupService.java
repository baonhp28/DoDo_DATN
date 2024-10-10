package com.poly.datn.service;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Service;
// import com.poly.datn.repository.VoucherRepository;
// import java.time.LocalDateTime;
// import java.time.LocalTime;

// @Service
public class VoucherCleanupService {

    // @Autowired
    // private VoucherRepository voucherRepository;

    // // Phương thức kiểm tra
    // public void testDeleteExpiredVouchers() {
    //     LocalDateTime now = LocalDateTime.now();
    //     voucherRepository.deleteByEndDateBefore(now);
    // }

    // // Phương thức được lên lịch để xóa voucher hết hạn vào thời điểm cụ thể
    // @Scheduled(cron = "0 45 17 * * ?") // Chạy vào 17:45 hàng ngày
    // public void deleteExpiredVouchers() {
    //     LocalDateTime now = LocalDateTime.now();
    //     voucherRepository.deleteByEndDateBefore(now);
    // }
}
