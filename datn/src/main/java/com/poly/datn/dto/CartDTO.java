package com.poly.datn.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CartDTO {
    private Integer id;
    private Integer userId;
    private List<CartItemDTO> cartItems;
    private boolean isError;
    private String message;
    
    public boolean isError() {
        return isError;
    }

    public void setIsError(boolean isError) {
        this.isError = isError;
    }
 
}