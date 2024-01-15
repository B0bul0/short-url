package me.bruno.shorturl.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.URL;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "short_url", indexes = @Index(name = "index_code", columnList = "code"))
public class ShortURLEntity {

    /**
     * Random UUID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Token to be used as short URL
     */
    @Size(min = 1, max = 144)
    @NotEmpty
    @NotNull
    @Column(name = "code", nullable = false, length = 144)
    private String code;


    /**
     * Destination URL
     */
    @URL
    @NotEmpty
    @NotNull
    @Column(name = "redirect_url", nullable = false)
    private String redirectUrl;

    /**
     * Note about the URL
     */
    @Column(name = "note")
    private String note;

    /**
     * Timestamps
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    /**
     * Date when the URL was deleted
     * If null, the URL is active
     */
    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ShortURLEntity shortURL = (ShortURLEntity) o;
        return Objects.equals(id, shortURL.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
