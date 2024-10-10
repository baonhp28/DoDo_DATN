package com.poly.datn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poly.datn.entities.Cart;
import com.poly.datn.entities.CartItem;
import com.poly.datn.entities.Skus;

public interface CartItemRepository extends JpaRepository<CartItem,Integer>{
 CartItem findByCartAndSku(Cart cart, Skus sku);
}
