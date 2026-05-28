package com.ilya.books.repository;

import com.ilya.books.domain.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long>, JpaSpecificationExecutor<Author> {

    @NonNull
        /*@EntityGraph(attributePaths = {"books", "books.genre"})*/
    Page<Author> findAll(@NonNull Pageable pageable);


    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE authors SET updated_at = :now, deleted = true WHERE id = :id", nativeQuery = true)
    void softDeleteWithTimestamp(@Param("id") Long id, @Param("now") LocalDateTime now);
}
