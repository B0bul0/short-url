package me.bruno.shorturl.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.util.UUID;

/**
 * DTO for ShortURLEntity
 */
@Data
@Builder
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShortURLDTO {

    /**
     * Random UUID
     */
    private UUID id;

    /**
     * Token to be used as short URL
     */
    @Size(max = 144)
    private String code;

    /**
     * Destination URL
     */
    @URL
    @NotEmpty
    @NotNull
    private String redirectUrl;


    /**
     * Note about the URL
     */
    private String note;

}
