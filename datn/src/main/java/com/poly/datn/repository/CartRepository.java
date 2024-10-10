package com.poly.datn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poly.datn.entities.Cart;

public interface CartRepository extends JpaRepository<Cart,Integer> {
    Cart findByUserId(Integer userId);
}
