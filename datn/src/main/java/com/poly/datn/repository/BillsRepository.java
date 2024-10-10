package com.poly.datn.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poly.datn.entities.Bills;

public interface BillsRepository extends JpaRepository<Bills,Integer>{
    List<Bills> findByUserId(Integer user);
}
