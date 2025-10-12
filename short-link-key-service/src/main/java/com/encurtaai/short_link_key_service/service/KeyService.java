package com.encurtaai.short_link_key_service.service;

import com.encurtaai.short_link_key_service.dto.KeyResponseDTO;
import com.encurtaai.short_link_key_service.model.Key;
import com.encurtaai.short_link_key_service.repository.KeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

@Service
public class KeyService {

    @Autowired
    private KeyRepository keyRepository;

    private String generateUniqueToken() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder token = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            int idx = (int) (Math.random() * chars.length());
            token.append(chars.charAt(idx));
        }

        return token.toString();
    }

    public Key saveKey() {
        String keyString = this.generateUniqueToken();

        while (keyRepository.existsByUniqueTokenId(keyString)) {
            keyString = this.generateUniqueToken();
        }

        Key key = new Key(keyString);
        keyRepository.saveKey(key);
        return key;
    }
}
