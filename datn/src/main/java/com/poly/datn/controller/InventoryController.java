package com.poly.datn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.poly.datn.dto.InboundDTO;
import com.poly.datn.dto.OutboundDTO;
import com.poly.datn.exceptions.IllegalUpdateException;
import com.poly.datn.service.*;
import com.poly.datn.service.OutboundService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);

    @Autowired
    private InboundService inboundService;

    @Autowired
    private OutboundService outboundService;

    @PreAuthorize("hasRole('ADMIN_KHO') or hasRole('ADMIN')")
    @PostMapping("/inbound")
    public ResponseEntity<InboundDTO> createInbound(@RequestBody InboundDTO inboundDTO) {
        try {
            Map<String, Object> response = inboundService.createInbound(
                    inboundDTO.getSkuId(),
                    inboundDTO.getQuantity(),
                    inboundDTO.getTotal());

            inboundDTO.setMessage((String) response.get("message"));
            inboundDTO.setError((Boolean) response.get("isError"));
            inboundDTO.setDate((LocalDateTime) response.get("date")); // Cập nhật ngày giờ trong DTO

            if ((Boolean) response.get("isError")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(inboundDTO);
            }

            return ResponseEntity.ok(inboundDTO);
        } catch (AccessDeniedException e) {
            logger.error("Lỗi quyền truy cập: " + e.getMessage());
            inboundDTO.setMessage("Bạn không có quyền thực hiện chức năng này.");
            inboundDTO.setError(true);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(inboundDTO);
        } catch (IllegalUpdateException e) {
            logger.error("Lỗi khi tạo bản ghi nhập hàng: " + e.getMessage());
            inboundDTO.setMessage(e.getMessage());
            inboundDTO.setError(true);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(inboundDTO);
        } catch (Exception e) {
            logger.error("Lỗi khi tạo bản ghi nhập hàng: " + e.getMessage());
            inboundDTO.setMessage("Lỗi không xác định xảy ra.");
            inboundDTO.setError(true);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(inboundDTO);
        }
    }

    @PreAuthorize("hasRole('ADMIN_KHO') or hasRole('ADMIN')")
    @PostMapping("/outbound")
    public ResponseEntity<OutboundDTO> createOutbound(@RequestBody OutboundDTO outboundDTO) {
        try {
            Map<String, Object> response = outboundService.createOutbound(
                    outboundDTO.getSkuId(),
                    outboundDTO.getQuantity());

            outboundDTO.setMessage((String) response.get("message"));
            outboundDTO.setError((Boolean) response.get("isError"));
            outboundDTO.setDate((LocalDateTime) response.get("date")); // Cập nhật ngày giờ trong DTO
            outboundDTO.setTotal((Double) response.get("total")); // Cập nhật tổng giá trị trong DTO

            if ((Boolean) response.get("isError")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(outboundDTO);
            }

            return ResponseEntity.ok(outboundDTO);
        } catch (AccessDeniedException e) {
            logger.error("Lỗi quyền truy cập: " + e.getMessage());
            outboundDTO.setMessage("Bạn không có quyền thực hiện chức năng này.");
            outboundDTO.setError(true);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(outboundDTO);
        } catch (IllegalUpdateException e) {
            logger.error("Lỗi khi tạo bản ghi xuất hàng: " + e.getMessage());
            outboundDTO.setMessage(e.getMessage());
            outboundDTO.setError(true);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(outboundDTO);
        } catch (Exception e) {
            logger.error("Lỗi khi tạo bản ghi xuất hàng: " + e.getMessage());
            outboundDTO.setMessage("Lỗi không xác định xảy ra.");
            outboundDTO.setError(true);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(outboundDTO);
        }
    }
}
