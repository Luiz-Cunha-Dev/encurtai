package com.encurtaai.short_link_key_service.controller;

import com.encurtaai.short_link_key_service.dto.KeyResponseDto;
import com.encurtaai.short_link_key_service.model.Key;
import com.encurtaai.short_link_key_service.repository.KeyRepository;
import com.encurtaai.short_link_key_service.service.KeyBloomFilterService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class KeyControllerTest {
    @Autowired private TestRestTemplate restTemplate;
    @MockitoBean private KeyBloomFilterService bloomFilterService;
    @MockitoBean private KeyRepository keyRepository;

    @Test
    @SneakyThrows
    void shouldGetKey() {
        when(bloomFilterService.mightContain(anyString())).thenReturn(false);
        when(keyRepository.exists(anyString())).thenReturn(false);
        doNothing().when(keyRepository).save(any(Key.class));

        var keyResponse = restTemplate.getForEntity("/", KeyResponseDto.class)
                .getBody();

        assertNotNull(keyResponse.uniqueTokenId());
        assertEquals(6, keyResponse.uniqueTokenId().length());
    }
}
