package com.encurtaai.short_link_key_service.service;

import com.encurtaai.short_link_key_service.model.Key;
import com.encurtaai.short_link_key_service.repository.KeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KeyService {
    private final KeyRepository repository;

    public Key saveKey() {
        String keyString = this.generateUniqueToken();

        while (repository.exists(keyString)) {
            keyString = this.generateUniqueToken();
        }

        Key key = new Key(keyString);
        repository.save(key);
        return key;
    }

    private String generateUniqueToken() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder token = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            int idx = (int) (Math.random() * chars.length());
            token.append(chars.charAt(idx));
        }

        return token.toString();
    }
}
