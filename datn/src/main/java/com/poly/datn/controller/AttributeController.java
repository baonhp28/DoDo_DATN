package com.poly.datn.controller;

import org.springframework.web.bind.annotation.RestController;

import com.poly.datn.entities.AttributeOption;
import com.poly.datn.entities.Attributes;
import com.poly.datn.repository.AttributeOptionRepository;
import com.poly.datn.repository.AttributesRepository;
import com.poly.datn.service.AttributeService;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AttributeController {
    @Autowired
    AttributesRepository attributesRepository;
    @Autowired
    AttributeService attributesService;
    @Autowired
    AttributeOptionRepository attributeOptionRepository;

    @GetMapping("/attribute")
    public List<Attributes> getAttribute() {
        return attributesRepository.findAll();
    }

    @PostMapping("/attribute/add")
    public ResponseEntity<String> addAttribute(@RequestParam String name) {
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Thuộc tính trống!");
        }
    
        try {
            attributesService.addAttribute(name);
            return ResponseEntity.ok("Thêm thuộc tính thành công!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
    

    @PostMapping("/attribute/remove")
    public ResponseEntity<String> removeAttribute(@RequestParam int id) {
        try {
            attributesService.removeAttribute(id);
            return ResponseEntity.ok("Xoá thuộc tính thành công");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Có lỗi xảy ra khi xoá thuộc tính!");
        }
    }

    @PutMapping("/attribute/update")
    public ResponseEntity<String> updateAttribute(
            @RequestParam int id,
            @RequestParam String name) {
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Tên thuộc tính trống");
        } else {
            try {
                attributesService.updateAttribute(id, name);
                return ResponseEntity.ok("Cập nhật thuộc tính thành công");
            } catch (EntityNotFoundException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(e.getMessage());
            }catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Có lỗi xảy ra khi cập nhật thuộc tính");
            }
        }
    }

    @GetMapping("/attribute/options")
    public List<AttributeOption> getAttributeOptions() {
        return attributeOptionRepository.findAll();
    }

    @PostMapping("/attribute/options/add")
    public ResponseEntity<String> addAttributeOption(
            @RequestParam String value,
            @RequestParam int attributeId) {
        if (value == null || value.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Giá trị thuộc tính trống");
        }
        try {
            attributesService.addAttributeOption(value, attributeId);
            return ResponseEntity.ok("Tuỳ chọn thuộc tính thêm thành công");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi thêm tuỳ chọn thuộc tính");
        }
    }

    @PutMapping("/attribute/options/update")
    public ResponseEntity<String> updateAttributeOption(
            @RequestParam int id,
            @RequestParam String value,
            @RequestParam int attributeId) {
    
        if (value == null || value.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Giá trị thuộc tính trống");
        }
        try {
            attributesService.updateAttributeOption(id, value, attributeId);
            return ResponseEntity.ok("Cập nhật tuỳ chọn thuộc tính thành công");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi cập nhật tuỳ chọn thuộc tính");
        }
    }
    
    @PostMapping("/attribute/options/remove")
    public ResponseEntity<String> removeAttributeOption(@RequestParam int id) {
        try {
            attributesService.removeAttributeOption(id);
            return ResponseEntity.ok("Xoá tuỳ chọn thuộc tính thành công");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Có lỗi xảy ra khi xoá tuỳ chọn thuộc tính!");
        }
    }

}