package com.poly.datn.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import java.util.Optional;

import com.poly.datn.entities.AttributeOption;
import com.poly.datn.entities.Attributes;
import com.poly.datn.repository.AttributeOptionRepository;
import com.poly.datn.repository.AttributesRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AttributeService {
     @Autowired
    private AttributesRepository attributeRepository;
    @Autowired
    private AttributeOptionRepository attributeOptionRepository;

    public void addAttribute(String name) {
        if (attributeRepository.existsByName(name)) {
            throw new IllegalArgumentException("Tên thuộc tính đã tồn tại!");
        }
        Attributes attribute = new Attributes();
        attribute.setName(name);
        attributeRepository.save(attribute);
    }
      public void removeAttribute(int id) {
        if (attributeRepository.existsById(id)) {
            attributeRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Thuộc tính với ID" + id + " không tìm thấy.");
        }
    }
    public void updateAttribute(int id, String name) {
        if (attributeRepository.existsByName(name)) {
            throw new IllegalArgumentException("Tên thuộc tính đã tồn tại!");
        }
        Optional<Attributes> existingAttribute = attributeRepository.findById(id);
        if (existingAttribute.isPresent()) {
            Attributes attribute = existingAttribute.get();
            attribute.setName(name);
          
            attributeRepository.save(attribute);
        } else {
            throw new EntityNotFoundException("Không tìm được thuộc tính " + id);
        }
    }

    public void addAttributeOption(String value, int attributeId) {
        if (attributeOptionRepository.existsByValue(value)) {
            throw new IllegalArgumentException("Giá trị thuộc tính đã tồn tại!");
        }
        Optional<Attributes> attribute = attributeRepository.findById(attributeId);
        if (!attribute.isPresent()) {
            throw new EntityNotFoundException("Thuộc tính với id: " + attributeId + " không tồn tại");
        }
       
        AttributeOption attributeOption = new AttributeOption();
        attributeOption.setValue(value);
        attributeOption.setAttribute(attribute.get());
        attributeOptionRepository.save(attributeOption);
    }
    public void updateAttributeOption(int id, String value, int attributeId) {
        if (attributeOptionRepository.existsByValue(value)) {
            throw new IllegalArgumentException("Giá trị thuộc tính đã tồn tại!");
        }
        // Tìm kiếm AttributeOption theo id
        Optional<AttributeOption> existingAttributeOption = attributeOptionRepository.findById(id);
        if (existingAttributeOption.isPresent()) {
            AttributeOption attributeOption = existingAttributeOption.get();
            // Cập nhật giá trị của AttributeOption
            attributeOption.setValue(value);

            // Tìm kiếm Attribute theo attributeId
            Optional<Attributes> attribute = attributeRepository.findById(attributeId);
            if (attribute.isPresent()) {
                // Thiết lập Attribute cho AttributeOption
                attributeOption.setAttribute(attribute.get());

                // Lưu thay đổi vào cơ sở dữ liệu
                attributeOptionRepository.save(attributeOption);
            } else {
                throw new EntityNotFoundException("Không tìm thấy thuộc tính có id: " + attributeId);
            }
        } else {
            throw new EntityNotFoundException("Không tìm thấy tuỳ chỉnh thuộc tính có id: " + id);
        }
    }
    public void removeAttributeOption(int id) {
        if (attributeOptionRepository.existsById(id)) {
            attributeOptionRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Tuỳ chỉnh thuộc tính với ID: " + id + " không tìm thấy.");
        }
    }
}
