package me.bruno.shorturl.repository;

import me.bruno.shorturl.entity.ShortURLEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class ShortURLRepositoryTest {

    @Autowired
    ShortURLRepository shortURLRepository;

    @Test
    void testFindByCodeAndDeletedAtIsNull() {
        ShortURLEntity shortURL = shortURLRepository.save(ShortURLEntity.builder()
                .id(UUID.randomUUID())
                .code("123")
                .redirectUrl("http://localhost")
                .note("note")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .deletedAt(null)
                .build());

        Optional<ShortURLEntity> shortURLOptional = shortURLRepository.findByCodeAndDeletedAtIsNull("123");

        assertTrue(shortURLOptional.isPresent());

        ShortURLEntity foundShortURL = shortURLOptional.get();
        assertEquals(shortURL.getId(), foundShortURL.getId());
        assertEquals(shortURL.getCode(), foundShortURL.getCode());
        assertEquals(shortURL.getRedirectUrl(), foundShortURL.getRedirectUrl());
        assertEquals(shortURL.getNote(), foundShortURL.getNote());
        assertEquals(shortURL.getCreatedAt(), foundShortURL.getCreatedAt());
        assertEquals(shortURL.getUpdatedAt(), foundShortURL.getUpdatedAt());
        assertNull(shortURL.getDeletedAt());
    }

    @Test
    void testFindByCodeAndDeletedAtIsNull_NotFound_Deleted() {
        shortURLRepository.save(ShortURLEntity.builder()
                .id(UUID.randomUUID())
                .code("123")
                .redirectUrl("http://localhost")
                .note("note")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .deletedAt(OffsetDateTime.now())
                .build());

        Optional<ShortURLEntity> shortURLOptional = shortURLRepository.findByCodeAndDeletedAtIsNull("123");

        assertTrue(shortURLOptional.isEmpty());
    }

    @Test
    void testFindByCodeAndDeletedAtIsNull_NotFound_Code() {
        shortURLRepository.save(ShortURLEntity.builder()
                .id(UUID.randomUUID())
                .code("123456")
                .redirectUrl("http://localhost")
                .note("note")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .deletedAt(null)
                .build());

        Optional<ShortURLEntity> shortURLOptional = shortURLRepository.findByCodeAndDeletedAtIsNull("123");

        assertTrue(shortURLOptional.isEmpty());
    }

}
