package me.bruno.shorturl.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Entity to store API keys
 */
@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "api_key")
public class APIAuthKeyEntity {

    /**
     * Key to be used as authentication
     */
    @Id
    @NotNull
    @NotEmpty
    @Column(name = "access_key", nullable = false, unique = true)
    private String key;

    /**
     * Name of the key for identification
     */
    @NotNull
    @NotEmpty
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Timestamps
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        APIAuthKeyEntity authKey = (APIAuthKeyEntity) o;
        return Objects.equals(key, authKey.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

}