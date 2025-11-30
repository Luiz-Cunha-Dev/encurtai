package com.encurtaai.short_link_key_service.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeyBloomFilterService {
    private final RedissonClient redissonClient;
    private RBloomFilter<String> bloomFilter;

    @PostConstruct
    private void init() {
        String BLOOM_FILTER_KEY = "short-link-keys-bloom-filter";
        bloomFilter = redissonClient.getBloomFilter(BLOOM_FILTER_KEY);
        bloomFilter.tryInit(10_000_000, 0.01);
        log.info("Bloom filter '{}' initialized with capacity {} and false positive rate {}",
                BLOOM_FILTER_KEY, 10_000_000, 0.01);
    }

    public boolean mightContain(String key) {
        boolean exists = bloomFilter.contains(key);
        log.info("Checked existence of key '{}' in Bloom filter: {}", key, exists);
        return exists;
    }

    public void add(String key) {
        bloomFilter.add(key);
        log.info("Added key '{}' to Bloom filter", key);
    }
}
