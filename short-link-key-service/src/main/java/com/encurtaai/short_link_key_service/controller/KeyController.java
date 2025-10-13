package com.encurtaai.short_link_key_service.controller;

import com.encurtaai.short_link_key_service.dto.KeyResponseDto;
import com.encurtaai.short_link_key_service.model.Key;
import com.encurtaai.short_link_key_service.service.KeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class KeyController {
    private final KeyService keyService;

    @GetMapping
    public ResponseEntity<KeyResponseDto> getUniqueTokenId() {
        Key key = keyService.saveKey();
        KeyResponseDto keyResponseDto = new KeyResponseDto(key.getToken());
        return ResponseEntity.ok(keyResponseDto);
    }
}
