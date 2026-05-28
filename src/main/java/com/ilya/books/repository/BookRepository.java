package com.ilya.books.repository;

import com.ilya.books.domain.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE books SET updated_at = :now, deleted = true WHERE id = :id", nativeQuery = true)
    void softDeleteWithTimestamp(@Param("id") Long id, @Param("now") LocalDateTime now);

}
