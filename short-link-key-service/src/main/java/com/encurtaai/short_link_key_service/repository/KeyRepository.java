package com.encurtaai.short_link_key_service.repository;

import com.encurtaai.short_link_key_service.model.Key;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository
public class KeyRepository {

    private final Set<Key> keysStoredOnMemory = new HashSet<>();

    public void saveKey(Key key) {
        keysStoredOnMemory.add(key);
    }

    public boolean existsByUniqueTokenId(String uniqueTokenId) {
        return keysStoredOnMemory.stream()
                .anyMatch(key -> key.getUniqueTokenId().equals(uniqueTokenId));
    }
}
