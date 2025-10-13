package com.encurtaai.short_link_key_service.repository;

import com.encurtaai.short_link_key_service.model.Key;

public interface KeyRepository {
    void save(Key key);
    boolean exists(String token);
}
