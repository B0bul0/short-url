package me.bruno.shorturl.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * DTO for APIAuthKeyEntity
 */
@Data
@Builder
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class APIAuthKeyDTO {

    /**
     * Key to be used as authentication
     */
    private String key;

    /**
     * Name of the key for identification
     */
    @NotNull
    @NotEmpty
    @Length(min = 1, max = 255)
    private String name;

}