package me.bruno.shorturl.controller;

import lombok.extern.log4j.Log4j2;
import me.bruno.shorturl.dto.ShortURLDTO;
import me.bruno.shorturl.entity.ShortURLEntity;
import me.bruno.shorturl.service.ShortURLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * CRUD API for ShortURLEntity
 */
@Log4j2
@RestController
@RequestMapping("/api/v1/shorturl")
public class ShortURLController {

    @Autowired
    private ShortURLService shortURLService;

    @GetMapping
    public ResponseEntity<List<ShortURLEntity>> list() {
        List<ShortURLEntity> all = shortURLService.getAll();

        return ResponseEntity.ok(all);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShortURLEntity> getById(@PathVariable("id") UUID id) {

        ShortURLEntity shortURLEntity = shortURLService.getById(id);

        if (shortURLEntity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "id not found");
        }

        log.info("Found short url: " + shortURLEntity);
        return ResponseEntity.ok(shortURLEntity);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ShortURLEntity> getByCode(@PathVariable("code") String code) {

        ShortURLEntity shortURLEntity = shortURLService.getByCode(code);

        if (shortURLEntity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "code not found");
        }

        log.info("Found short url: " + shortURLEntity);
        return ResponseEntity.ok(shortURLEntity);
    }

    @PostMapping
    public ResponseEntity<ShortURLEntity> create(@RequestBody @Validated ShortURLDTO shortURLDto) {
        if (shortURLDto.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id must be null");
        }

        ShortURLEntity shortURLEntity = shortURLService.save(shortURLDto);

        log.info("Created short url: " + shortURLEntity);
        return ResponseEntity.created(URI.create("/api/v1/shorturl/" + shortURLEntity.getId()))
                .body(shortURLEntity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShortURLEntity> updateById(
            @PathVariable("id") UUID id,
            @RequestBody @Validated ShortURLDTO shortURLDto
    ) {
        ShortURLEntity shortURLEntity = shortURLService.getById(id);

        if (shortURLEntity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "id not found");
        }

        shortURLDto.setId(id);

        shortURLEntity = shortURLService.save(shortURLDto);

        log.info("Updated short url: " + shortURLEntity);
        return ResponseEntity.ok(shortURLEntity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ShortURLEntity> deleteById(@PathVariable("id") UUID id) {

        ShortURLEntity shortURLEntity = shortURLService.getById(id);

        if (shortURLEntity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "id not found");
        }

        shortURLService.removeById(id);

        log.info("Deleted short url: " + shortURLEntity);
        return ResponseEntity.ok(shortURLEntity);
    }

}
