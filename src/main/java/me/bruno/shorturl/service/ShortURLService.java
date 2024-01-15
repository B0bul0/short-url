package me.bruno.shorturl.service;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import jakarta.validation.constraints.NotNull;
import me.bruno.shorturl.dto.ShortURLDTO;
import me.bruno.shorturl.entity.ShortURLEntity;
import me.bruno.shorturl.mapper.ShortURLMapper;
import me.bruno.shorturl.repository.ShortURLRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class ShortURLService {

    @Autowired
    private ShortURLRepository shortURLRepository;

    @Autowired
    private ShortURLMapper shortURLMapper;

    // Cache, valid codes only
    private final LoadingCache<String, ShortURLEntity> validCodeCache = Caffeine.newBuilder()
            .expireAfterWrite(20, TimeUnit.MINUTES)
            .refreshAfterWrite(10, TimeUnit.MINUTES)
            .build(key -> shortURLRepository.findByCodeAndDeletedAtIsNull(key).orElse(null));

    /**
     * Gets the ShortURLEntity with the specified id
     * Valid and deleted ShortURLs are included
     *
     * @param id the id to get
     * @return the ShortURLEntity with the specified id or null if it does not exist
     */
    public ShortURLEntity getById(@NotNull UUID id) {
        return this.shortURLRepository
                .findById(id)
                .orElse(null);
    }

    /**
     * Gets all ShortURLs.
     * Valid and deleted ShortURLs are included
     *
     * @return all ShortURLs
     */
    @NotNull
    public List<ShortURLEntity> getAll() {
        return this.shortURLRepository.findAll();
    }

    /**
     * Removes and deletes the ShortURLEntity with the specified id
     * Sets the deletedAt field to the current time
     *
     * @param id the id to remove
     */
    public void removeById(@NotNull UUID id) {
        this.shortURLRepository.findById(id).ifPresent(shortURLEntity -> {
            shortURLEntity.setDeletedAt(OffsetDateTime.now());
            this.shortURLRepository.save(shortURLEntity);
            this.validCodeCache.invalidate(shortURLEntity.getCode());
        });
    }

    /**
     * Saves the ShortURL
     * If the code is null or empty, generates a random code
     *
     * @param dto the ShortURLDTO to save
     * @return the saved ShortURLEntity
     */
    @NotNull
    public ShortURLEntity save(@NotNull ShortURLDTO dto) {

        // Generate a code if it's null or empty
        if (!StringUtils.hasText(dto.getCode())) {
            dto.setCode(generateCode());
        }

        validateCode(dto.getCode());
        return this.shortURLRepository.save(this.shortURLMapper.toModel(dto));
    }

    /**
     * Gets the ShortURLEntity with the specified code
     *
     * @param code the code to get
     * @return the ShortURLEntity with the specified code or null if it does not exist
     */
    public ShortURLEntity getByCode(String code) {
        return this.validCodeCache.get(code);
    }

    /**
     * Checks if the valid code exists
     *
     * @param code the code to check
     * @return true if the code exists and is not deleted
     */
    public boolean existsByCode(String code) {
        ShortURLEntity byToken = getByCode(code);
        return byToken != null && byToken.getDeletedAt() == null;
    }

    /**
     * Validates the code
     *
     * @param code the code to validate
     * @throws IllegalArgumentException if the code is empty, already exists or is too long
     */
    public void validateCode(String code) {
        Validate.notEmpty(code, "code is empty");
        Validate.isTrue(!existsByCode(code), "code already exists");
        Validate.isTrue(code.length() < 144, "code is too long");
    }

    /**
     * Generates a random code that does not exist in the database
     *
     * @return random code
     */
    public String generateCode() {
        int length = 6; // Initial length
        int maxTries = 10; // Max tries before increasing the length
        int tries = 0; // Tries since last length increase

        while (true) {
            String code = generateRandomCode(length);

            if (!existsByCode(code)) {
                validateCode(code);
                return code;
            }

            tries++;
            if (tries >= maxTries) {
                length++;
                tries = 0;
            }
        }
    }

    /**
     * Generates a random code with the specified length
     *
     * @param length the length of the code
     * @return random code
     */
    public String generateRandomCode(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }

}
