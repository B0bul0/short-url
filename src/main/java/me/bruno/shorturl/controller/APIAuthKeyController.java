package me.bruno.shorturl.controller;

import lombok.extern.log4j.Log4j2;
import me.bruno.shorturl.dto.APIAuthKeyDTO;
import me.bruno.shorturl.entity.APIAuthKeyEntity;
import me.bruno.shorturl.service.APIAuthKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

/**
 * CRUD API for APIAuthKeyEntity
 */
@Log4j2
@RestController
@RequestMapping("/api/v1/auth")
public class APIAuthKeyController {

    @Autowired
    private APIAuthKeyService APIAuthKeyService;

    @GetMapping
    public ResponseEntity<List<APIAuthKeyEntity>> list() {
        List<APIAuthKeyEntity> all = APIAuthKeyService.getAll();

        return ResponseEntity.ok(all);
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIAuthKeyEntity> getById(@PathVariable("id") String id) {
        APIAuthKeyEntity shortURLEntity = APIAuthKeyService.getById(id);

        if (shortURLEntity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "id not found");
        }

        return ResponseEntity.ok(shortURLEntity);
    }

    @PostMapping
    public ResponseEntity<APIAuthKeyEntity> create(@RequestBody @Validated APIAuthKeyDTO shortURLDto) {
        if (shortURLDto.getKey() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "key must be null");
        }

        APIAuthKeyEntity shortURLEntity = APIAuthKeyService.save(shortURLDto);

        return ResponseEntity.created(URI.create("/api/v1/auth/" + shortURLEntity.getKey()))
                .body(shortURLEntity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIAuthKeyEntity> updateById(
            @PathVariable("id") String id,
            @RequestBody @Validated APIAuthKeyDTO shortURLDto
    ) {
        APIAuthKeyEntity shortURLEntity = APIAuthKeyService.getById(id);

        if (shortURLEntity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "id not found");
        }

        shortURLDto.setKey(id);

        shortURLEntity = APIAuthKeyService.save(shortURLDto);

        return ResponseEntity.ok(shortURLEntity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIAuthKeyEntity> deleteById(@PathVariable("id") String id) {
        APIAuthKeyEntity shortURLEntity = APIAuthKeyService.getById(id);

        if (shortURLEntity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "id not found");
        }

        APIAuthKeyService.removeById(id);

        return ResponseEntity.ok(shortURLEntity);
    }

}
