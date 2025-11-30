package com.encurtaai.short_link_key_service.service;

import com.encurtaai.short_link_key_service.model.Key;
import com.encurtaai.short_link_key_service.repository.KeyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeyService {
    private final KeyRepository repository;
    private final KeyBloomFilterService bloomFilterService;

    @Transactional
    public Key saveKey() {
        Key key = new Key();

        while (bloomFilterService.mightContain(key.getToken())) {
            if (repository.exists(key.getToken())) {
                log.warn("Collision detected for token: {}. Generating a new token.", key.getToken());
                key = new Key();
            } else {
                log.info("False positive detected for token: {}. Proceeding to save.", key.getToken());
                break;
            }
        }

        repository.save(key);
        bloomFilterService.add(key.getToken());
        return key;
    }
}
