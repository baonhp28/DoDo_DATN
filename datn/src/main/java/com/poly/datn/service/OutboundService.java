package com.poly.datn.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.poly.datn.entities.Outbound;
import com.poly.datn.entities.Skus;
import com.poly.datn.exceptions.IllegalUpdateException;
import com.poly.datn.repository.OutboundRepository;
import com.poly.datn.repository.SkusRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class OutboundService {

    @Autowired
    private OutboundRepository outboundRepository;

    @Autowired
    private SkusRepository skuRepository;

    public Map<String, Object> createOutbound(Integer skuId, int quantity) { // Xóa tham số total
        Map<String, Object> response = new HashMap<>();
    
        // Kiểm tra thông tin đầu vào
        if (skuId == null || quantity <= 0) {
            response.put("message", "Thông tin đầu vào không hợp lệ.");
            response.put("isError", true);
            return response;
        }
    
        // Lấy thông tin người dùng hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String role = userDetails.getAuthorities().toString();
    
        // Kiểm tra vai trò người dùng
        if (!role.contains("ADMIN") && !role.contains("ADMIN_KHO")) {
            response.put("message", "Bạn không có quyền thực hiện chức năng này.");
            response.put("isError", true);
            return response;
        }
    
        // Tìm kiếm SKU
        Skus sku = skuRepository.findById(skuId)
                .orElseThrow(() -> new IllegalUpdateException("Không tìm thấy SKU với ID: " + skuId));
    
        // Tính tổng giá trị dựa trên giá của SKU và số lượng
        double total = sku.getPrice() * quantity;

        // Tạo bản ghi xuất hàng với thời gian hiện tại
        Outbound outbound = new Outbound();
        outbound.setSku(sku);
        outbound.setQuantity(quantity);
        outbound.setDate(LocalDateTime.now());  // Sử dụng LocalDateTime.now() để lấy thời gian hiện tại
        outbound.setTotal(total); // Gán giá trị tổng tính toán

        // Lưu bản ghi vào cơ sở dữ liệu
        Outbound savedOutbound = outboundRepository.save(outbound);

        // Cập nhật phản hồi
        response.put("message", "Tạo bản ghi xuất hàng thành công.");
        response.put("isError", false);
        response.put("date", savedOutbound.getDate());  // Thêm ngày giờ vào phản hồi
        response.put("total", savedOutbound.getTotal()); // Thêm tổng giá trị vào phản hồi

        return response;
    }
}
