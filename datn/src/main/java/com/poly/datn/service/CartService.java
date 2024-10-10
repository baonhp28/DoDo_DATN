package com.poly.datn.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.poly.datn.dto.CartDTO;
import com.poly.datn.dto.CartItemDTO;
import com.poly.datn.entities.Cart;
import com.poly.datn.entities.CartItem;
import com.poly.datn.entities.Skus;
import com.poly.datn.entities.Users;
import com.poly.datn.repository.CartItemRepository;
import com.poly.datn.repository.CartRepository;
import com.poly.datn.repository.SkusRepository;
import com.poly.datn.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private SkusRepository skusRepository;

    public CartDTO getCartByUserId(Integer userId) {
        Cart cart = cartRepository.findByUserId(userId);
        CartDTO responseDTO = new CartDTO();

        if (cart != null) {
            responseDTO = convertToDTO(cart);
            String fullName = cart.getUser().getFullName();
            responseDTO.setMessage("Giỏ hàng của người dùng " + fullName + " (ID: " + userId + ") đã được tìm thấy.");
            responseDTO.setIsError(false);
        } else {
            responseDTO.setMessage("Không tìm thấy giỏ hàng cho người dùng với ID " + userId);
            responseDTO.setIsError(true);
        }

        return responseDTO;
    }

    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        return carts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CartDTO addToCart(Integer userId, CartItemDTO cartItemDTO) {
        CartDTO responseDTO = new CartDTO();
        try {
            // Kiểm tra số lượng
            if (cartItemDTO.getQuantity() <= 0) {
                responseDTO.setMessage("Số lượng sản phẩm phải lớn hơn 0.");
                responseDTO.setIsError(true);
                return responseDTO;
            }

            Cart cart = cartRepository.findByUserId(userId);
            if (cart == null) {
                cart = new Cart();
                Users user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("Người dùng không tìm thấy"));
                cart.setUser(user);
                cart = cartRepository.save(cart);
            }

            Skus sku = skusRepository.findById(cartItemDTO.getSkuId())
                    .orElseThrow(() -> new RuntimeException("SKU không tìm thấy"));

            CartItem existingCartItem = cartItemRepository.findByCartAndSku(cart, sku);
            if (existingCartItem != null) {
                existingCartItem.setQuantity(existingCartItem.getQuantity() + cartItemDTO.getQuantity());
                cartItemRepository.save(existingCartItem);
                responseDTO.setMessage("Cập nhật số lượng sản phẩm thành công.");
                responseDTO.setIsError(false);
            } else {
                CartItem newCartItem = new CartItem();
                newCartItem.setCart(cart);
                newCartItem.setSku(sku);
                newCartItem.setQuantity(cartItemDTO.getQuantity());
                cartItemRepository.save(newCartItem);
                cart.getCartItems().add(newCartItem);
                responseDTO.setMessage("Thêm sản phẩm vào giỏ hàng thành công.");
                responseDTO.setIsError(false);
            }

            cart = cartRepository.save(cart);
            responseDTO = convertToDTO(cart);
            responseDTO.setIsError(false);
        } catch (Exception e) {
            responseDTO.setMessage("Lỗi khi thêm sản phẩm vào giỏ hàng: " + e.getMessage());
            responseDTO.setIsError(true);
        }
        return responseDTO;
    }

    public CartDTO updateCartItem(Integer userId, CartItemDTO cartItemDTO) {
        CartDTO responseDTO = new CartDTO();
        try {
            // Kiểm tra số lượng
            if (cartItemDTO.getQuantity() <= 0) {
                responseDTO.setMessage("Số lượng sản phẩm phải lớn hơn 0.");
                responseDTO.setIsError(true);
                return responseDTO;
            }

            Cart cart = cartRepository.findByUserId(userId);
            if (cart == null) {
                responseDTO.setMessage("Không tìm thấy giỏ hàng cho người dùng với ID " + userId);
                responseDTO.setIsError(true);
                return responseDTO;
            }

            CartItem cartItem = cartItemRepository.findById(cartItemDTO.getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Sản phẩm trong giỏ hàng không tìm thấy cho ID " + cartItemDTO.getId()));

            if (!cart.getCartItems().contains(cartItem)) {
                responseDTO.setMessage("Sản phẩm không thuộc về giỏ hàng của người dùng với ID " + userId);
                responseDTO.setIsError(true);
                return responseDTO;
            }

            cartItem.setQuantity(cartItemDTO.getQuantity());
            cartItemRepository.save(cartItem);

            responseDTO = convertToDTO(cart);
            responseDTO.setMessage("Cập nhật số lượng sản phẩm trong giỏ hàng thành công.");
            responseDTO.setIsError(false);
        } catch (Exception e) {
            responseDTO.setMessage("Lỗi khi cập nhật sản phẩm trong giỏ hàng: " + e.getMessage());
            responseDTO.setIsError(true);
        }
        return responseDTO;
    }

    public CartDTO removeCartItem(Integer userId, Integer cartItemId) {
        CartDTO responseDTO = new CartDTO();
        try {
            Cart cart = cartRepository.findByUserId(userId);
            if (cart == null) {
                responseDTO.setMessage("Không tìm thấy giỏ hàng của người dùng với ID " + userId);
                responseDTO.setIsError(true);
                return responseDTO;
            }

            CartItem cartItem = cartItemRepository.findById(cartItemId)
                    .orElseThrow(() -> new RuntimeException("CartItem không tìm thấy với ID " + cartItemId));

            cart.getCartItems().remove(cartItem);
            cartItemRepository.delete(cartItem);
            cartRepository.save(cart);

            responseDTO.setMessage("Xóa sản phẩm khỏi giỏ hàng thành công.");
            responseDTO.setIsError(false);
        } catch (Exception e) {
            responseDTO.setMessage("Lỗi khi xóa sản phẩm khỏi giỏ hàng: " + e.getMessage());
            responseDTO.setIsError(true);
        }
        return responseDTO;
    }

    private CartDTO convertToDTO(Cart cart) {
        CartDTO cartDTO = new CartDTO();
        if (cart != null) {
            cartDTO.setId(cart.getId());
            cartDTO.setUserId(cart.getUser().getId());
            cartDTO.setCartItems(cart.getCartItems().stream()
                    .map(this::convertToCartItemDTO)
                    .collect(Collectors.toList()));
        }
        return cartDTO;
    }

    private CartItemDTO convertToCartItemDTO(CartItem cartItem) {
        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setId(cartItem.getId());
        cartItemDTO.setSkuId(cartItem.getSku().getId());
        cartItemDTO.setQuantity(cartItem.getQuantity());
        return cartItemDTO;
    }

    public void clearCart(Integer userId) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart != null) {
            List<CartItem> cartItems = cart.getCartItems();
            for (CartItem item : cartItems) {
                cartItemRepository.delete(item);
            }
            cart.getCartItems().clear();
            cartRepository.save(cart);
        }
    }
}
