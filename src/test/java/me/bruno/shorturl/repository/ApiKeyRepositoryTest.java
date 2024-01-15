package me.bruno.shorturl.repository;

import me.bruno.shorturl.entity.APIAuthKeyEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
public class ApiKeyRepositoryTest {

    @Autowired
    ApiKeyRepository apiKeyRepository;

    @Test
    void testIsEmpty_ReturnFalse() {
        apiKeyRepository.save(APIAuthKeyEntity.builder()
                .key("123")
                .name("Key")
                .build());

        boolean empty = apiKeyRepository.isEmpty();

        assertFalse(empty);
    }

    @Test
    void testIsEmpty_ReturnTrue() {
        boolean empty = apiKeyRepository.isEmpty();

        assertTrue(empty);
    }

}
