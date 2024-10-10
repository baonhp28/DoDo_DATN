package com.poly.datn.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.poly.datn.entities.Skus;
import com.poly.datn.repository.SkusRepository;


@Service
public class SKUService {
    @Autowired
    SkusRepository skusRepository;
    public List<Skus> getSKUByPrice(double minPrice, double maxPrice) {
        return skusRepository.findByPriceBetween(Double.valueOf(minPrice), Double.valueOf(maxPrice));
    }
}
