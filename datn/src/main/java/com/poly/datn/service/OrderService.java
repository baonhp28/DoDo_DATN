package com.poly.datn.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.poly.datn.dto.BillDTO;
import com.poly.datn.dto.BillDetailDTO;
import com.poly.datn.entities.BillDetail;
import com.poly.datn.entities.Bills;
import com.poly.datn.entities.Cart;
import com.poly.datn.entities.Skus;
import com.poly.datn.entities.Status;
import com.poly.datn.entities.Users;
import com.poly.datn.exceptions.AccessDeniedException;
import com.poly.datn.exceptions.IllegalUpdateException;
import com.poly.datn.exceptions.ResourceNotFoundException;
import com.poly.datn.repository.BillDetailRepository;
import com.poly.datn.repository.BillsRepository;
import com.poly.datn.repository.CartRepository;
import com.poly.datn.repository.SkusRepository;
import com.poly.datn.repository.StatusRepository;
import com.poly.datn.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class OrderService {

    @Autowired
    private BillsRepository billRepository;

    @Autowired
    private BillDetailRepository billDetailRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private SkusRepository skusRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    public List<BillDTO> getAllOrdersByUserId(Integer userId) {
        return billRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public BillDTO createOrder(String customerName, String numberPhone, String address) {
        // Lấy userId từ SecurityContextHolder
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // Tìm giỏ hàng theo userId
        Cart cart = cartRepository.findByUserId(user.getId());
        if (cart == null || cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống");
        }

        // Tạo mới hóa đơn
        AtomicReference<Bills> billRef = new AtomicReference<>(new Bills());
        Bills bill = billRef.get();
        bill.setUser(cart.getUser());
        bill.setDate(LocalDateTime.now());
        bill.setTotal(0.0); // Sẽ cập nhật sau

        // Lấy status ID = 1 và gán cho bill
        Status pendingStatus = statusRepository.findDefaultStatus()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy statusId"));
        bill.setStatus(pendingStatus);

        bill.setCustomerName(customerName);
        bill.setNumberPhone(numberPhone);
        bill.setAddress(address);

        // Tính toán tổng tiền và tạo chi tiết hóa đơn
        double[] totalAmount = { 0.0 }; // Sử dụng mảng để giữ giá trị tổng
        List<BillDetail> billDetails = cart.getCartItems().stream()
                .map(cartItem -> {
                    BillDetail billDetail = new BillDetail();
                    billDetail.setBill(billRef.get());
                    billDetail.setSkus(cartItem.getSku());
                    billDetail.setProductName(cartItem.getSku().getCode());
                    billDetail.setQuantity(cartItem.getQuantity());
                    double price = getPriceBySkuId(cartItem.getSku().getId());
                    billDetail.setPrice(price);
                    totalAmount[0] += price * cartItem.getQuantity(); // Cập nhật tổng tiền
                    return billDetail;
                })
                .collect(Collectors.toList());

        // Cập nhật tổng tiền vào hóa đơn và lưu hóa đơn
        bill.setTotal(totalAmount[0]);
        bill = billRepository.save(bill);

        // Lưu chi tiết hóa đơn
        for (BillDetail detail : billDetails) {
            detail.setBill(bill);
            billDetailRepository.save(detail);
        }

        // Xóa giỏ hàng sau khi tạo hóa đơn thành công
        cartService.clearCart(user.getId());

        // Chuyển đổi hóa đơn thành DTO và trả về
        return convertToDTO(bill);
    }

    //Hóa đơn chi tiết
    public BillDTO getOrderById(Integer id, Integer userId) {
        // Lấy hóa đơn từ repository
        Bills bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hóa đơn không tồn tại với ID: " + id));
    
        // Kiểm tra quyền truy cập
        if (!bill.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Bạn không có quyền xem hóa đơn này");
        }
    
        // Chuyển đổi hóa đơn thành DTO
        return convertToDTO(bill);
    }
    

    private double getPriceBySkuId(Integer skuId) {
        // Implement this method to get price from SKU
        return skusRepository.findById(skuId)
                .map(Skus::getPrice) // Ensure SKU entity has a getPrice() method
                .orElse(0.0);
    }

    private BillDTO convertToDTO(Bills bill) {
        BillDTO dto = new BillDTO();
        dto.setId(bill.getId());
        dto.setDate(bill.getDate());
        dto.setUserId(bill.getUser().getId());
        dto.setTotal(bill.getTotal());
        dto.setStatusName(bill.getStatus().getStatusName());
        dto.setCustomerName(bill.getCustomerName());
        dto.setNumberPhone(bill.getNumberPhone());
        dto.setAddress(bill.getAddress());

        // Kiểm tra danh sách billDetails trước khi chuyển đổi
        List<BillDetailDTO> billDetailDTOs = (bill.getBillDetails() != null)
                ? bill.getBillDetails().stream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList())
                : new ArrayList<>(); // Nếu billDetails là null, khởi tạo danh sách rỗng

        dto.setBillDetails(billDetailDTOs);
        return dto;
    }

    private BillDetailDTO convertToDTO(BillDetail billDetail) {
        BillDetailDTO dto = new BillDetailDTO();
        dto.setId(billDetail.getId());
        dto.setSkuId(billDetail.getSkus().getId());
        dto.setProductName(billDetail.getProductName());
        dto.setQuantity(billDetail.getQuantity());
        dto.setPrice(billDetail.getPrice());
        dto.setVoucherId(billDetail.getVoucher() != null ? billDetail.getVoucher().getId() : null);
        return dto;
    }

    // Phần Staff & ADMIN
    // Danh sách hóa đơn
    public List<Bills> getAllBills() {
        return billRepository.findAll();
    }

    // Cập nhật trạng thái hóa đơn
    public ResponseEntity<String> updateOrderStatus(Integer id, Integer statusId) {
        // Lấy username từ token đăng nhập
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
    
        // Tìm hóa đơn theo ID
        Bills existingBill = billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại"));
    
        // Tìm trạng thái mới theo ID
        Status newStatus = statusRepository.findById(statusId)
                .orElseThrow(() -> new RuntimeException("Trạng thái không tồn tại"));
    
        // Cập nhật trạng thái của hóa đơn nếu trạng thái khác
        if (!existingBill.getStatus().equals(newStatus)) {
            existingBill.setStatus(newStatus);
            billRepository.save(existingBill);
            return new ResponseEntity<>("Cập nhật trạng thái hóa đơn thành công", HttpStatus.OK);
        } else {
            throw new IllegalUpdateException("Cập nhật không thành công, trạng thái hóa đơn không thay đổi");
        }
    }

}
