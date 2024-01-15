package me.bruno.shorturl.service;

import me.bruno.shorturl.entity.ShortURLEntity;
import me.bruno.shorturl.repository.ShortURLRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

//@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ShortURLServiceTest {

    //    @Autowired
    @InjectMocks
    ShortURLService shortURLService;

    @Mock
    ShortURLRepository shortURLRepository;

    ShortURLEntity shortURLEntity;

    @BeforeEach
    void setUp() {
        shortURLEntity = ShortURLEntity.builder()
                .id(UUID.randomUUID())
                .code("123")
                .redirectUrl("http://localhost")
                .note("")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .deletedAt(null)
                .build();
    }

    @Test
    void testFindShortURL_Successfull_ReturnShortURL() {
        UUID id = shortURLEntity.getId();

        when(shortURLRepository.findById(id))
                .thenReturn(Optional.of(shortURLEntity));

        assertEquals(id, shortURLService.getById(id).getId());
    }

    @Test
    void testFindShortURLByCode_Successfull_ReturnShortURL() {
        String code = shortURLEntity.getCode();
        UUID uuid = shortURLEntity.getId();

        when(shortURLRepository.findByCodeAndDeletedAtIsNull(code))
                .thenReturn(Optional.of(shortURLEntity));

        assertEquals(uuid, shortURLService.getByCode(code).getId());
    }

    @Test
    void testExistsByCode_Successfull_ReturnTrue() {
        String code = shortURLEntity.getCode();

        when(shortURLRepository.findByCodeAndDeletedAtIsNull(code))
                .thenReturn(Optional.of(shortURLEntity));

        assertTrue(shortURLService.existsByCode(code));
    }

    @Test
    void testValidateCode_DoesNotThrowException() {
        assertDoesNotThrow(() -> shortURLService.validateCode("123456"));
    }

    @Test
    void testValidateCode_ThrowException_WhenEmptyString() {
        assertThrows(IllegalArgumentException.class, () -> shortURLService.validateCode(""));
    }

    @Test
    void testValidateCode_ThrowException_WhenNullString() {
        assertThrows(NullPointerException.class, () -> shortURLService.validateCode(null));
    }

    @Test
    void testValidateCode_ThrowException_LengthString() {
        String repeat = StringUtils.repeat("a", 145);
        assertThrows(IllegalArgumentException.class, () -> shortURLService.validateCode(repeat));
    }

    @Test
    void testGenerateCode_ReturnString() {
        String code = shortURLService.generateCode();

        assertNotNull(code);
        assertTrue(code.length() < 144);
    }

    @Test
    void testGenerateRandomCode_ReturnString() {
        String code = shortURLService.generateRandomCode(8);

        assertNotNull(code);
        assertEquals(8, code.length());
        assertTrue(StringUtils.isAlphanumeric(code));
    }
}