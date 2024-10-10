package com.poly.datn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.poly.datn.dto.CartDTO;
import com.poly.datn.dto.CartItemDTO;
import com.poly.datn.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<CartDTO> getCartByUser(Authentication authentication) {
        try {
            Integer userId = getUserIdFromAuthentication(authentication);
            CartDTO cart = cartService.getCartByUserId(userId);

            logger.info("Cart Data: {}", cart);

            if (cart.isError()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(cart);
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(cart);
            }
        } catch (IllegalStateException e) {
            CartDTO errorCart = new CartDTO();
            errorCart.setMessage("Lỗi xác thực: " + e.getMessage());
            errorCart.setIsError(true);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorCart);
        }
    }

    @PostMapping
    public ResponseEntity<CartDTO> addToCart(Authentication authentication, @RequestBody CartItemDTO cartItemDTO) {
        try {
            Integer userId = getUserIdFromAuthentication(authentication);
            CartDTO updatedCart = cartService.addToCart(userId, cartItemDTO);
            if (updatedCart.isError()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(updatedCart);
            } else {
                return ResponseEntity.status(HttpStatus.CREATED).body(updatedCart);
            }
        } catch (IllegalStateException e) {
            CartDTO errorCart = new CartDTO();
            errorCart.setMessage("Lỗi xác thực: " + e.getMessage());
            errorCart.setIsError(true);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorCart);
        }
    }

    @PutMapping
    public ResponseEntity<CartDTO> updateCartItem(Authentication authentication, @RequestBody CartItemDTO cartItemDTO) {
        try {
            Integer userId = getUserIdFromAuthentication(authentication);
            CartDTO updatedCart = cartService.updateCartItem(userId, cartItemDTO);
            if (updatedCart.isError()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(updatedCart);
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(updatedCart);
            }
        } catch (IllegalStateException e) {
            CartDTO errorCart = new CartDTO();
            errorCart.setMessage("Lỗi xác thực: " + e.getMessage());
            errorCart.setIsError(true);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorCart);
        }
    }

    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<CartDTO> removeCartItem(Authentication authentication, @PathVariable Integer cartItemId) {
        try {
            Integer userId = getUserIdFromAuthentication(authentication);
            CartDTO deletedCart = cartService.removeCartItem(userId, cartItemId);
            if (deletedCart.isError()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(deletedCart);
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(deletedCart);
            }
        } catch (IllegalStateException e) {
            CartDTO errorCart = new CartDTO();
            errorCart.setMessage("Lỗi xác thực: " + e.getMessage());
            errorCart.setIsError(true);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorCart);
        }
    }

    private Integer getUserIdFromAuthentication(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        logger.error("Principal Class: {}", principal.getClass().getName());
        logger.error("Principal Package: {}", principal.getClass().getPackage().getName());
        logger.error("Principal: {}", principal);

        if (principal instanceof com.poly.datn.security.CustomUserDetails) {
            com.poly.datn.security.CustomUserDetails userDetails = (com.poly.datn.security.CustomUserDetails) principal;
            return userDetails.getId();
        } else {
            logger.error("Principal không phải là CustomUserDetails: {}", principal.getClass().getName());
            throw new IllegalStateException("Principal không phải là CustomUserDetails: " + principal);
        }
    }
}
