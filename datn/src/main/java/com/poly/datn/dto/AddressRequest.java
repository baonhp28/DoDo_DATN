package com.poly.datn.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.poly.datn.entities.Address;

import lombok.Data;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class AddressRequest {
    private List<Address> addresses;
    private String message;
    private boolean isError;

    public AddressRequest() {}

    public AddressRequest(List<Address> addresses, String message, boolean isError) {
        this.addresses = addresses;
        this.message = message;
        this.isError = isError;
    }
    public boolean isError() {
        return isError;
    }

    public void setIsError(boolean isError) {
        this.isError = isError;
    }
}
