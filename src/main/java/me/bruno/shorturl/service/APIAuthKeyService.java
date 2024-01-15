package me.bruno.shorturl.service;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import lombok.extern.log4j.Log4j2;
import me.bruno.shorturl.dto.APIAuthKeyDTO;
import me.bruno.shorturl.entity.APIAuthKeyEntity;
import me.bruno.shorturl.mapper.APIAuthTokenMapper;
import me.bruno.shorturl.repository.ApiKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Log4j2
@Service
public class APIAuthKeyService {

    @Autowired
    private ApiKeyRepository apiKeyRepository;

    @Autowired
    private APIAuthTokenMapper authApiMapper;

    // Configuration
    @Value("${short-url.api.default.key}")
    private String apiDefaultKey;

    // Cache
    private final LoadingCache<String, APIAuthKeyEntity> keyCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(20))
            .refreshAfterWrite(Duration.ofMinutes(10))
            .build(key -> this.apiKeyRepository.findById(key).orElse(null));

    /**
     * Gets the APIAuthKeyEntity with the specified id
     * Uses cache
     *
     * @param id the id to get
     * @return the APIAuthKeyEntity with the specified id or null if it does not exist
     */
    public APIAuthKeyEntity getByKey(@NotNull String id) {
        return this.keyCache.get(id);
    }

    /**
     * Gets the APIAuthKeyEntity with the specified id
     *
     * @param id the id to get
     * @return the APIAuthKeyEntity with the specified id or null if it does not exist
     */
    public APIAuthKeyEntity getById(@NotNull String id) {
        return this.apiKeyRepository.findById(id).orElse(null);
    }

    /**
     * Gets all APIAuthKeyEntity
     *
     * @return all APIAuthKeyEntity
     */
    @NotNull
    public List<APIAuthKeyEntity> getAll() {
        return this.apiKeyRepository.findAll();
    }

    /**
     * Removes and deletes the APIAuthKeyEntity with the specified id
     *
     * @param id the id to remove
     */
    public void removeById(@NotNull String id) {
        this.apiKeyRepository.deleteById(id);
        this.keyCache.invalidate(id);
    }

    /**
     * Checks if the key is valid (can be used)
     * Uses cache
     *
     * @param key the key to validate
     * @return true if the key is valid, false otherwise
     */
    public boolean validateKey(@NotNull String key) {
        return this.keyCache.get(key) != null;
    }

    /**
     * Saves the APIAuthKeyDTO
     * If the key is null, generates a new one
     *
     * @param APIAuthKeyDTO the APIAuthKeyDTO to save
     * @return the saved APIAuthKeyEntity
     */
    @NotNull
    public APIAuthKeyEntity save(@NotNull APIAuthKeyDTO APIAuthKeyDTO) {
        APIAuthKeyEntity apiAuthKeyEntityKey = authApiMapper.toModel(APIAuthKeyDTO);

        // Generate a key if it's null
        if (apiAuthKeyEntityKey.getKey() == null) {
            apiAuthKeyEntityKey.setKey(UUID.randomUUID().toString());
        }

        return apiKeyRepository.save(apiAuthKeyEntityKey);
    }


    /**
     * Generate a default key to be the first admin
     * Called on startup
     */
    @PostConstruct
    public void createDefaultKey() {
        try {
            createDefaultKey(apiDefaultKey);
        } catch (IllegalArgumentException | IllegalStateException ignored) {
            log.info("Default key already exists, skipping");
        }
    }

    /**
     * Creates a default key, used for the first admin]
     * If there is already a key, throws an IllegalStateException
     *
     * @throws IllegalStateException if there is already a key or the key already exists
     * @throws IllegalArgumentException if the key is null, empty or blank
     * @param key the key to create
     */
    public void createDefaultKey(String key) {
        // Check if key is null, empty or blank
        if (!StringUtils.hasText(key)) {
            throw new IllegalArgumentException("Invalid key");
        }

        // Check if key already exists
        if (apiKeyRepository.existsById(key)) {
            throw new IllegalStateException("Key already exists");
        }

        // Check if there is already a key
        if (!apiKeyRepository.isEmpty()) {
            throw new IllegalStateException("There is already a key");
        }

        APIAuthKeyEntity APIAuthKeyEntity = new APIAuthKeyEntity();
        APIAuthKeyEntity.setKey(key);
        APIAuthKeyEntity.setName("Default Key");
        apiKeyRepository.save(APIAuthKeyEntity);
        log.info("Created default api key: " + key);
    }

}
