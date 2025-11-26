package com.xcache;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class CacheController {

    private final RedisCacheService cacheService;

    @GetMapping(value = "/{key}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> getCacheEntry(@PathVariable String key) {
        Optional<byte[]> data = cacheService.retrieve(key);

        return data.map(bytes -> ResponseEntity.ok()
                                            .contentType(MediaType.APPLICATION_OCTET_STREAM)
                                            .body(bytes)
        ).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .contentType(MediaType.TEXT_PLAIN)
                            .body("Cache entry not found".getBytes(StandardCharsets.UTF_8))
        );
    }

    @PutMapping(value = "/{key}", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Void> storeCacheEntry(@PathVariable String key, @RequestBody byte[] data) {
        if (data == null || data.length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body cannot be empty");
        }
        cacheService.store(key, data);
        return ResponseEntity.ok().build();
    }
}