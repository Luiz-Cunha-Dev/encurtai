package com.encurtaai.short_link_key_service.repository;

import com.encurtaai.short_link_key_service.model.Key;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository
public class KeyRepositoryInMemory implements KeyRepository {
    private final Set<Key> storage = new HashSet<>();

    public void save(Key key) {
        storage.add(key);
    }

    public boolean exists(String token) {
        return storage.stream()
                .anyMatch(storedKey -> storedKey.getToken().equals(token));
    }
}
