package me.bruno.shorturl.service;

import me.bruno.shorturl.dto.APIAuthKeyDTO;
import me.bruno.shorturl.entity.APIAuthKeyEntity;
import me.bruno.shorturl.mapper.APIAuthTokenMapper;
import me.bruno.shorturl.repository.ApiKeyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class APIAuthKeyServiceTest {

    @InjectMocks
    APIAuthKeyService apiAuthKeyService;

    @Mock
    ApiKeyRepository apiKeyRepository;

    @Mock
    APIAuthTokenMapper authApiMapper;

    APIAuthKeyEntity apiAuthKeyEntity;

    APIAuthKeyDTO apiAuthKeyDTO;

    @BeforeEach
    void setUp() {
        apiAuthKeyEntity = APIAuthKeyEntity.builder()
                .key("abc")
                .name("Test Key")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        apiAuthKeyDTO = APIAuthKeyDTO.builder()
                .key("abc")
                .name("Test Key")
                .build();
    }

    @Test
    void testFindKey_Successfull_ReturnKey() {
        String key = apiAuthKeyEntity.getKey();

        when(apiKeyRepository.findById(key))
                .thenReturn(Optional.of(apiAuthKeyEntity));

        assertEquals(key, apiAuthKeyService.getByKey(key).getKey());
    }

    @Test
    void testValidateKey_Successfull_ReturnTrue() {
        String key = apiAuthKeyEntity.getKey();

        when(apiKeyRepository.findById(key))
                .thenReturn(Optional.of(apiAuthKeyEntity));

        assertTrue(apiAuthKeyService.validateKey(key));
    }

    @Test
    void testSave_ReturnString_GenerateCodeOnSave() {
        APIAuthKeyEntity testKeyEntity = APIAuthKeyEntity.builder()
                .key(null)
                .name("Test Key")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        APIAuthKeyDTO testKeyDto = APIAuthKeyDTO.builder()
                .key(null)
                .name("Test Key")
                .build();

        when(authApiMapper.toModel(testKeyDto))
                .thenReturn(testKeyEntity);

        when(apiKeyRepository.save(testKeyEntity))
                .thenReturn(testKeyEntity);

        APIAuthKeyEntity saved = apiAuthKeyService.save(testKeyDto);

        assertNotNull(saved);
        assertNotNull(saved.getKey());
        assertFalse(saved.getKey().isEmpty());
    }

    @Test
    void testCreateDefaultKey_Successful_DoesNotThrow() {
        String apiDefaultKey = "abcdef";

        when(apiKeyRepository.existsById(apiDefaultKey))
                .thenReturn(false);

        when(apiKeyRepository.isEmpty())
                .thenReturn(true);

        assertDoesNotThrow(() -> apiAuthKeyService.createDefaultKey(apiDefaultKey));
        verify(apiKeyRepository, times(1)).save(any(APIAuthKeyEntity.class));
    }

    @Test
    void testCreateDefaultKey_EmptyKey_Throws() {
        String apiDefaultKey = "   "; // blank

        assertThrows(IllegalArgumentException.class, () -> apiAuthKeyService.createDefaultKey(apiDefaultKey), "Invalid key");
        verify(apiKeyRepository, times(0)).save(any(APIAuthKeyEntity.class));
    }

    @Test
    void testCreateDefaultKey_AlreadyExists_Throws() {
        String apiDefaultKey = "abcdef";

        when(apiKeyRepository.existsById(apiDefaultKey))
                .thenReturn(true);

        assertThrows(IllegalStateException.class, () -> apiAuthKeyService.createDefaultKey(apiDefaultKey), "Key already exists");
        verify(apiKeyRepository, times(0)).save(any(APIAuthKeyEntity.class));
    }

    @Test
    void testCreateDefaultKey_NotEmpty_Throws() {
        String apiDefaultKey = "abcdef";

        when(apiKeyRepository.existsById(apiDefaultKey))
                .thenReturn(false);

        when(apiKeyRepository.isEmpty())
                .thenReturn(false);

        assertThrows(IllegalStateException.class, () -> apiAuthKeyService.createDefaultKey(apiDefaultKey), "There is already a key");
        verify(apiKeyRepository, times(0)).save(any(APIAuthKeyEntity.class));
    }

}