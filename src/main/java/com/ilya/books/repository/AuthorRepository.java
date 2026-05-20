package com.ilya.books.repository;

import com.ilya.books.domain.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    // Все методы (findAll, findById) автоматически будут возвращать только deleted = false
}
