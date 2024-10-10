package com.poly.datn.utils;

import java.security.SecureRandom;

public class VoucherCodeGenerator {
    private static final String ALPHANUMERIC_UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int LENGTH = 10;
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateVoucherCode() {
        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            sb.append(ALPHANUMERIC_UPPERCASE.charAt(RANDOM.nextInt(ALPHANUMERIC_UPPERCASE.length())));
        }
        return sb.toString();
    }
}

