package com.encurtaai.short_link_key_service.model;

import lombok.Getter;

@Getter
public class Key {
    private final String token;

    public Key() {
        this.token = generateToken();
    }

    private String generateToken() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder token = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            int idx = (int) (Math.random() * chars.length());
            token.append(chars.charAt(idx));
        }

        return token.toString();
    }
}
