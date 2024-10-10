package com.poly.datn.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.poly.datn.entities.Inbound;
import com.poly.datn.entities.Skus;
import com.poly.datn.exceptions.IllegalUpdateException;
import com.poly.datn.repository.InboundRepository;
import com.poly.datn.repository.SkusRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class InboundService {

    @Autowired
    private InboundRepository inboundRepository;

    @Autowired
    private SkusRepository skuRepository;

    public Map<String, Object> createInbound(Integer skuId, int quantity, double total) {
        Map<String, Object> response = new HashMap<>();

        // Kiểm tra thông tin đầu vào
        if (skuId == null || quantity <= 0 || total < 0) {
            throw new IllegalUpdateException("Thông tin đầu vào không hợp lệ.");
        }

        // Lấy thông tin người dùng hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String role = userDetails.getAuthorities().toString();

        // Kiểm tra vai trò người dùng
        if (!role.contains("ADMIN") && !role.contains("ADMIN_KHO")) {
            throw new AccessDeniedException("Bạn không có quyền thực hiện chức năng này.");
        }

        // Tìm kiếm SKU
        Skus sku = skuRepository.findById(skuId)
                .orElseThrow(() -> new IllegalUpdateException("Không tìm thấy SKU với ID: " + skuId));

        // Tạo bản ghi nhập hàng với thời gian hiện tại
        Inbound inbound = new Inbound();
        inbound.setSku(sku);
        inbound.setQuantity(quantity);
        inbound.setDate(LocalDateTime.now());  // Sử dụng LocalDateTime.now() để lấy thời gian hiện tại
        inbound.setTotal(total);

        // Lưu bản ghi vào cơ sở dữ liệu
        Inbound savedInbound = inboundRepository.save(inbound);

        // Cập nhật phản hồi
        response.put("message", "Tạo bản ghi nhập hàng thành công.");
        response.put("isError", false);
        response.put("date", savedInbound.getDate());  // Thêm ngày giờ vào phản hồi

        return response;
    }
}
