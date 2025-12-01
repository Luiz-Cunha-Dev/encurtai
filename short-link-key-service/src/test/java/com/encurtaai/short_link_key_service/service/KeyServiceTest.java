package com.encurtaai.short_link_key_service.service;

import com.encurtaai.short_link_key_service.model.Key;
import com.encurtaai.short_link_key_service.repository.KeyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KeyServiceTest {
    @Mock private KeyRepository repository;
    @Mock private KeyBloomFilterService bloomFilterService;
    @InjectMocks private KeyService keyService;

    @Test
    void shouldCreateKeyWithoutCollisionWhenBloomFilterIndicatesNoExistence() {
        when(bloomFilterService.mightContain(anyString())).thenReturn(false);
        doNothing().when(repository).save(any(Key.class));
        doNothing().when(bloomFilterService).add(anyString());

        var key = keyService.saveKey();

        assertNotNull(key);
        assertEquals(6, key.getToken().length());

        verify(bloomFilterService).mightContain(anyString());
        verify(repository, never()).exists(anyString());
        verify(repository, times(1)).save(any(Key.class));
        verify(bloomFilterService, times(1)).add(anyString());
    }

    @Test
    void shouldCreateKeyWithCollisionWhenBloomFilterFalsePositiveOccurs() {
        when(bloomFilterService.mightContain(anyString())).thenReturn(true);
        when(repository.exists(anyString())).thenReturn(false);
        doNothing().when(repository).save(any(Key.class));
        doNothing().when(bloomFilterService).add(anyString());

        var key = keyService.saveKey();

        assertNotNull(key);
        assertEquals(6, key.getToken().length());

        verify(bloomFilterService).mightContain(anyString());
        verify(repository).exists(anyString());
        verify(repository, times(1)).save(any(Key.class));
        verify(bloomFilterService, times(1)).add(anyString());
    }

    @Test
    void shouldHandleMultipleCollisionsUntilUniqueKeyIsFound() {
        when(bloomFilterService.mightContain(anyString())).thenReturn(true, true, false);
        when(repository.exists(anyString())).thenReturn(true, true, false);
        doNothing().when(repository).save(any(Key.class));
        doNothing().when(bloomFilterService).add(anyString());

        var key = keyService.saveKey();

        assertNotNull(key);
        assertEquals(6, key.getToken().length());

        verify(bloomFilterService, times(3)).mightContain(anyString());
        verify(repository, times(2)).exists(anyString());
        verify(repository, times(1)).save(any(Key.class));
        verify(bloomFilterService, times(1)).add(anyString());
    }
}
