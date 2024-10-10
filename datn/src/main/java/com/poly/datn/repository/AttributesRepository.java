package com.poly.datn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poly.datn.entities.Attributes;

public interface AttributesRepository extends JpaRepository<Attributes,Integer> {
    boolean existsByName(String name);
}
