package com.poly.datn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poly.datn.entities.AttributeOption;

public interface AttributeOptionRepository extends JpaRepository<AttributeOption,Integer> {
    boolean existsByValue(String value);
}
