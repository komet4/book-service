package com.ilya.books.repository;

import com.ilya.books.domain.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

    Optional<Genre> findByName(String name);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE genres SET updated_at = :now, deleted = true WHERE id = :id", nativeQuery = true)
    void softDeleteWithTimestamp(@Param("id") Long id, @Param("now") LocalDateTime now);
}
