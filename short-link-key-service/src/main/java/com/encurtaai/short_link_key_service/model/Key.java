package com.encurtaai.short_link_key_service.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Key {
    private String uniqueTokenId;

    public Key(String tokenID) {
        this.uniqueTokenId = tokenID;
    }
}
