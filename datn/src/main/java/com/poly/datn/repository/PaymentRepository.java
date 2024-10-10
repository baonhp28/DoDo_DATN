package com.poly.datn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poly.datn.entities.Payment;

public interface PaymentRepository extends JpaRepository<Payment,Integer>{

}
