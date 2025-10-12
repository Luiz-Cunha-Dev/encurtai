package com.encurtaai.short_link_key_service.controller;

import com.encurtaai.short_link_key_service.dto.KeyResponseDTO;
import com.encurtaai.short_link_key_service.model.Key;
import com.encurtaai.short_link_key_service.service.KeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class KeyController {

    @Autowired
    private KeyService keyService;

    @GetMapping
    public ResponseEntity<KeyResponseDTO> getUniqueTokenId() {

        Key key = keyService.saveKey();
        KeyResponseDTO keyResponseDTO = new KeyResponseDTO(key.getUniqueTokenId());
        return ResponseEntity.ok(keyResponseDTO);
    }
}
