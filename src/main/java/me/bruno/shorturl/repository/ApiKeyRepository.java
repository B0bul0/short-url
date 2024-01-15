package me.bruno.shorturl.repository;

import me.bruno.shorturl.entity.APIAuthKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiKeyRepository extends JpaRepository<APIAuthKeyEntity, String> {

    // Check if the table is empty
    @Query("SELECT CASE WHEN COUNT(a) = 0 THEN true ELSE false END FROM APIAuthKeyEntity a")
    boolean isEmpty();

}
