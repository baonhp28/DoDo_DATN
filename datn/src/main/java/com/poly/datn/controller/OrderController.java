package com.poly.datn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.poly.datn.dto.BillDTO;
import com.poly.datn.dto.BillDetailDTO;
import com.poly.datn.dto.CreateOrderRequest;
import com.poly.datn.dto.OrderStatusUpdateDTO;
import com.poly.datn.entities.Bills;
import com.poly.datn.security.CustomUserDetails;
import com.poly.datn.service.OrderService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import com.poly.datn.exceptions.AccessDeniedException;
import com.poly.datn.exceptions.IllegalUpdateException;
import com.poly.datn.exceptions.ResourceNotFoundException;
import com.poly.datn.repository.BillsRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private BillsRepository billRepository;

    // List order by userId
    @GetMapping("/order")
    public ResponseEntity<List<BillDTO>> getOrdersByUserId(Authentication authentication) {
        try {
            // Lấy userId từ token
            Integer userId = getUserIdFromAuthentication(authentication);

            // Lấy danh sách hóa đơn theo userId
            List<BillDTO> orders = orderService.getAllOrdersByUserId(userId);

            // Trả về danh sách hóa đơn
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            // Xử lý lỗi và trả về danh sách trống
            List<BillDTO> emptyList = Collections.emptyList();
            return ResponseEntity.ok(emptyList);
        }
    }

    // Hóa đơn chi tiết
    @GetMapping("/order/{id}")
    public ResponseEntity<Object> getOrderById(@PathVariable Integer id, Authentication authentication) {
        try {
            // Lấy userId từ token
            Integer userId = getUserIdFromAuthentication(authentication);

            // Lấy hóa đơn theo ID
            BillDTO bill = orderService.getOrderById(id, userId);

            // Trả về hóa đơn chi tiết
            return ResponseEntity.ok(bill);
        } catch (ResourceNotFoundException e) {
            // Xử lý lỗi không tìm thấy hóa đơn
            Map<String, String> response = new HashMap<>();
            response.put("message", "Hóa đơn không tồn tại");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (AccessDeniedException e) {
            // Xử lý lỗi truy cập bị từ chối
            Map<String, String> response = new HashMap<>();
            response.put("message", "Bạn không có quyền xem hóa đơn này");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (Exception e) {
            // Xử lý lỗi không xác định
            Map<String, String> response = new HashMap<>();
            response.put("message", "Có lỗi xảy ra khi lấy hóa đơn");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Add Order
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createOrder(
            @RequestBody(required = false) CreateOrderRequest createOrderRequest,
            Authentication authentication) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Kiểm tra xem createOrderRequest có phải là null không
            if (createOrderRequest == null) {
                throw new IllegalUpdateException("Dữ liệu yêu cầu không được để trống");
            }

            // Kiểm tra các trường không được để trống
            if (createOrderRequest.getCustomerName() == null || createOrderRequest.getCustomerName().isEmpty()) {
                throw new IllegalUpdateException("Tên khách hàng không được để trống");
            }
            if (createOrderRequest.getNumberPhone() == null || createOrderRequest.getNumberPhone().isEmpty()) {
                throw new IllegalUpdateException("Số điện thoại không được để trống");
            }
            if (createOrderRequest.getAddress() == null || createOrderRequest.getAddress().isEmpty()) {
                throw new IllegalUpdateException("Địa chỉ không được để trống");
            }

            // Kiểm tra định dạng số điện thoại Việt Nam và số điện thoại không chứa ký tự
            // âm
            String phoneNumberPattern = "^(03|05|07|08|09|01[2|6|8|9])\\d{8}$";
            if (!createOrderRequest.getNumberPhone().matches(phoneNumberPattern)) {
                throw new IllegalUpdateException(
                        "Số điện thoại không hợp lệ. Số điện thoại phải là số Việt Nam và đúng định dạng.");
            }

            // Nếu tất cả các kiểm tra đều qua, tạo hóa đơn
            BillDTO billDTO = orderService.createOrder(
                    createOrderRequest.getCustomerName(),
                    createOrderRequest.getNumberPhone(),
                    createOrderRequest.getAddress());

            response.put("message", "Thêm hóa đơn thành công");
            response.put("data", billDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalUpdateException e) {
            // Xử lý lỗi với IllegalUpdateException
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            // Xử lý lỗi chung nếu cần
            response.put("message", "Có lỗi xảy ra");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public Integer getUserIdFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getId();
        }
        return null;
    }

    // Phần Staff & ADMIN
    // List order

    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    @GetMapping("/admin/order")
    public ResponseEntity<List<BillDTO>> getAllBills() {
        List<Bills> bills = billRepository.findAll();
        List<BillDTO> billResponseDTOs = bills.stream()
                .map(bill -> {
                    BillDTO dto = new BillDTO();
                    dto.setId(bill.getId());
                    dto.setDate(bill.getDate());
                    dto.setUserId(bill.getUser() != null ? bill.getUser().getId() : null); // Lấy userId từ đối tượng
                                                                                           // user
                    dto.setTotal(bill.getTotal());
                    dto.setStatusName(bill.getStatus().getStatusName());
                    dto.setCustomerName(bill.getCustomerName());
                    dto.setNumberPhone(bill.getNumberPhone());
                    dto.setAddress(bill.getAddress());
                    dto.setBillDetails(bill.getBillDetails().stream()
                            .map(detail -> {
                                BillDetailDTO detailDTO = new BillDetailDTO();
                                detailDTO.setSkuId(detail.getSkus().getId());
                                detailDTO.setId(detail.getId());
                                detailDTO.setProductName(detail.getProductName());
                                detailDTO.setQuantity(detail.getQuantity());
                                detailDTO.setPrice(detail.getPrice());
                                detailDTO
                                        .setVoucherId(detail.getVoucher() != null ? detail.getVoucher().getId() : null);
                                return detailDTO;
                            })
                            .collect(Collectors.toList()));
                    return dto;
                })
                .collect(Collectors.toList());

        return new ResponseEntity<>(billResponseDTOs, HttpStatus.OK);
    }

    // Phương thức để cập nhật trạng thái hóa đơn
    // @PreAuthorize("hasRole('Admin')")
    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateOrderStatus(
            @PathVariable Integer id,
            @RequestBody OrderStatusUpdateDTO statusUpdateDTO) {
        try {
            if (statusUpdateDTO == null) {
                throw new IllegalUpdateException("Dữ liệu yêu cầu không được để trống");
            }
            // Truyền ID của status vào phương thức
            return orderService.updateOrderStatus(id, statusUpdateDTO.getId());
        } catch (IllegalUpdateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

}
