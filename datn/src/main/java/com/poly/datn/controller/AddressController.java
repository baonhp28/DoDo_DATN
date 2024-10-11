package com.poly.datn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.poly.datn.dto.AddressRequest;
import com.poly.datn.entities.Address;
import com.poly.datn.entities.Users;
import com.poly.datn.repository.AddressRepository;
import com.poly.datn.security.CustomUserDetails;
import com.poly.datn.service.GhnService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private GhnService ghnService;

    // Tạo phương thức phản hồi lỗi chung
    private ResponseEntity<AddressRequest> createErrorResponse(String message) {
        AddressRequest response = new AddressRequest();
        response.setMessage(message);
        response.setIsError(true);
        return ResponseEntity.badRequest().body(response);
    }

    // Lấy danh sách Tỉnh
    @GetMapping("/provinces")
    public ResponseEntity<List<Map<String, Object>>> getProvinces() {
        List<Map<String, Object>> provinces = ghnService.getProvinces();
        return ResponseEntity.ok(provinces);
    }

    // Lấy danh sách Huyện theo Tỉnh
    @GetMapping("/districts/{provinceId}")
    public ResponseEntity<List<Map<String, Object>>> getDistricts(@PathVariable int provinceId) {
        List<Map<String, Object>> districts = ghnService.getDistricts(provinceId);
        return ResponseEntity.ok(districts);
    }

    // Lấy danh sách Xã theo Huyện
    @GetMapping("/wards/{districtId}")
    public ResponseEntity<List<Map<String, Object>>> getWards(@PathVariable int districtId) {
        List<Map<String, Object>> wards = ghnService.getWards(districtId);
        return ResponseEntity.ok(wards);
    }

    // Tạo địa chỉ mới
    // Controller - Thêm logic kiểm tra huyện thuộc tỉnh và xã thuộc huyện
    @PostMapping
public ResponseEntity<AddressRequest> createAddress(@RequestBody Address address) {
    try {
        System.out.println("API /api/addresses has been triggered.");

        AddressRequest response = new AddressRequest();

        // Kiểm tra các trường ID không được bỏ trống
        if (address.getProvinceId() == null || address.getDistrictId() == null || address.getWardId() == null) {
            return createErrorResponse("Tỉnh, Huyện, hoặc Xã không được bỏ trống.");
        }

        // Kiểm tra tính hợp lệ của số điện thoại
        if (address.getPhone() == null || address.getPhone().isEmpty()) {
            return createErrorResponse("Số điện thoại không được bỏ trống.");
        }

        // Lấy userId hiện tại và lưu địa chỉ
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer currentUserId = customUserDetails.getId();
        address.setUser(new Users(currentUserId));

        // Kiểm tra tính hợp lệ của các ID từ GHN API
        boolean isValidProvince = ghnService.isValidProvince(address.getProvinceId());
        boolean isValidDistrict = ghnService.isValidDistrict(address.getDistrictId(), address.getProvinceId());
        boolean isValidWard = ghnService.isValidWard(address.getWardId(), address.getDistrictId());

        // Nếu bất kỳ giá trị nào không hợp lệ, trả về lỗi
        if (!isValidProvince) {
            return createErrorResponse("Tỉnh không hợp lệ.");
        }
        // if (!isValidDistrict) {
        //     return createErrorResponse("Huyện không hợp lệ.");
        // }
        if (!isValidWard) {
            return createErrorResponse("Xã không hợp lệ.");
        }

        // Nếu tất cả đều hợp lệ, tiếp tục lưu thông tin
        Address savedAddress = addressRepository.save(address);
        response.setAddresses(List.of(savedAddress));
        response.setMessage("Địa chỉ đã được tạo thành công.");
        response.setIsError(false);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    } catch (Exception e) {
        // Kiểm tra nội dung lỗi từ GHN API và hiển thị thông báo tương ứng
        if (e.getMessage().contains("Error while calling GHN API")) {
            // Kiểm tra lỗi cho tỉnh
            if (e.getMessage().contains("province_id")) {
                return createErrorResponse("Tỉnh không hợp lệ!.");
            }
            // Kiểm tra lỗi cho huyện
            // if (e.getMessage().contains("district_id")) {
            //     return createErrorResponse("Huyện không hợp lệ!.");
            // }
        }
        return createErrorResponse("Lỗi khi cập nhật địa chỉ: " + e.getMessage());
    }
}

    

    // 11111
    // Phương thức kiểm tra xem giá trị có phải là số nguyên không
    // private boolean isInteger(Object value) {
    // if (value == null) {
    // return false;
    // }
    // try {
    // Integer.parseInt(value.toString());
    // return true;
    // } catch (NumberFormatException e) {
    // return false;
    // }
    // }

    // Lấy danh sách địa chỉ của người dùng hiện tại
    @GetMapping
    public ResponseEntity<AddressRequest> getAddressesByCurrentUser() {
        AddressRequest response = new AddressRequest();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            Integer currentUserId = customUserDetails.getId();

            List<Address> addresses = addressRepository.findByUserId(currentUserId);

            if (addresses.isEmpty()) {
                return createErrorResponse("Danh sách địa chỉ của bạn hiện tại trống.");
            } else {
                response.setAddresses(addresses);
                response.setMessage("Danh sách địa chỉ của bạn đã được tải thành công.");
                response.setIsError(false);
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            return createErrorResponse("Lỗi khi lấy danh sách địa chỉ: " + e.getMessage());
        }
    }

    // Cập nhật địa chỉ
    @PutMapping("/{id}")
    public ResponseEntity<AddressRequest> updateAddress(@PathVariable Integer id, @RequestBody Address addressDetails) {
        AddressRequest response = new AddressRequest();
        try {
            if (addressDetails.getProvinceId() == null || addressDetails.getDistrictId() == null
                    || addressDetails.getWardId() == null) {
                return createErrorResponse("Tỉnh, Huyện, hoặc Xã không được bỏ trống.");
            }

            // Kiểm tra tính hợp lệ của các ID từ GHN API
            boolean isValidProvince = ghnService.isValidProvince(addressDetails.getProvinceId());
            boolean isValidDistrict = ghnService.isValidDistrict(addressDetails.getDistrictId(),
                    addressDetails.getProvinceId());
            boolean isValidWard = ghnService.isValidWard(addressDetails.getWardId(), addressDetails.getDistrictId());

            if (!isValidProvince) {
                return createErrorResponse("ID Tỉnh không hợp lệ.");
            }
            if (!isValidDistrict) {
                return createErrorResponse("ID Huyện không hợp lệ hoặc không thuộc tỉnh này.");
            }
            if (!isValidWard) {
                return createErrorResponse("ID Xã không hợp lệ hoặc không thuộc huyện này.");
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            Integer currentUserId = customUserDetails.getId();

            Optional<Address> addressOptional = addressRepository.findById(id);
            if (addressOptional.isPresent()) {
                Address address = addressOptional.get();

                if (!address.getUser().getId().equals(currentUserId)) {
                    return createErrorResponse("Bạn không có quyền cập nhật địa chỉ này.");
                }

                // Cập nhật thông tin địa chỉ
                address.setName(addressDetails.getName());
                address.setProvinceId(addressDetails.getProvinceId());
                address.setDistrictId(addressDetails.getDistrictId());
                address.setWardId(addressDetails.getWardId());
                address.setDetailedAddress(addressDetails.getDetailedAddress());

                Address updatedAddress = addressRepository.save(address);
                response.setAddresses(List.of(updatedAddress));
                response.setMessage("Địa chỉ đã được cập nhật thành công.");
                response.setIsError(false);

                return ResponseEntity.ok(response);
            } else {
                return createErrorResponse("Địa chỉ không tồn tại.");
            }
        } catch (Exception e) {
            return createErrorResponse("Lỗi khi cập nhật địa chỉ: " + e.getMessage());
        }
    }

    // Xóa địa chỉ
    @DeleteMapping("/{id}")
    public ResponseEntity<AddressRequest> deleteAddress(@PathVariable Integer id) {
        AddressRequest response = new AddressRequest();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            Integer currentUserId = customUserDetails.getId();

            Optional<Address> addressOptional = addressRepository.findById(id);

            if (addressOptional.isPresent()) {
                Address address = addressOptional.get();

                if (!address.getUser().getId().equals(currentUserId)) {
                    return createErrorResponse("Bạn không có quyền xóa địa chỉ này.");
                }

                addressRepository.delete(address);
                response.setMessage("Địa chỉ đã được xóa thành công.");
                response.setIsError(false);

                return ResponseEntity.ok(response);
            } else {
                return createErrorResponse("Địa chỉ không tồn tại.");
            }
        } catch (Exception e) {
            return createErrorResponse("Lỗi khi xóa địa chỉ: " + e.getMessage());
        }
    }
}
