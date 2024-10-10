package com.poly.datn.service;

import com.poly.datn.entities.Voucher;
import com.poly.datn.repository.VoucherRepository;
import com.poly.datn.utils.VoucherCodeGenerator;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;

   

    @Transactional
    public Voucher addVoucher(Voucher voucher) {
        // Sinh mã voucher tự động
        voucher.setVoucherCode(VoucherCodeGenerator.generateVoucherCode());

        validateVoucher(voucher);

        if (voucherRepository.existsByVoucherCode(voucher.getVoucherCode())) {
            throw new RuntimeException("Voucher đã tồn tại với mã: " + voucher.getVoucherCode());
        }
        return voucherRepository.save(voucher);
    }

    @Transactional
    public Voucher updateVoucher(Integer id, Voucher voucher) {
        validateVoucher(voucher);

        Optional<Voucher> existingVoucherOpt = voucherRepository.findById(id);
        if (existingVoucherOpt.isPresent()) {
            Voucher existingVoucher = existingVoucherOpt.get();

            // Không cho phép thay đổi mã voucher
            if (!existingVoucher.getVoucherCode().equals(voucher.getVoucherCode())) {
                throw new RuntimeException("Không thể thay đổi mã voucher.");
            }

            existingVoucher.setQuantity(voucher.getQuantity());
            existingVoucher.setPercent(voucher.getPercent());
            existingVoucher.setMaxMoney(voucher.getMaxMoney());
            existingVoucher.setMinMoney(voucher.getMinMoney());
            existingVoucher.setStartDate(voucher.getStartDate());
            existingVoucher.setEndDate(voucher.getEndDate());

            return voucherRepository.save(existingVoucher);
        } else {
            throw new RuntimeException("Voucher không tồn tại với ID: " + id);
        }
    }

    public void deleteVoucher(Integer id) {
        if (voucherRepository.existsById(id)) {
            voucherRepository.deleteById(id);
        } else {
            throw new RuntimeException("Voucher không tồn tại với ID: " + id);
        }
    }

    public List<Voucher> getAllVouchers() {
        return voucherRepository.findAll();
    }

    public Voucher getVoucherById(Integer id) {
        return voucherRepository.findById(id).orElse(null);
    }

    private void validateVoucher(Voucher voucher) {
        if (voucher.getQuantity() < 1) {
            throw new RuntimeException("Số lượng voucher phải lớn hơn hoặc bằng 1");
        }
        if (voucher.getMaxMoney() < 10.000) {
            throw new RuntimeException("maxMoney không được dưới 10.000");
        }
        if (voucher.getMinMoney() < 10.000) {
            throw new RuntimeException("minMoney không được dưới 10.000");
        }
        if (voucher.getPercent() < 50) {
            throw new RuntimeException("Phần trăm giảm giá phải từ 50%");
        }
        if (voucher.getPercent() > 100) {
            throw new RuntimeException("Phần trăm giảm giá không được lớn hơn 100%");
        }
    }
}
