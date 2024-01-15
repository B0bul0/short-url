package me.bruno.shorturl.repository;

import me.bruno.shorturl.entity.ShortURLEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShortURLRepository extends JpaRepository<ShortURLEntity, UUID> {

    // Find only valid ShortURLs
    Optional<ShortURLEntity> findByCodeAndDeletedAtIsNull(String code);

}
