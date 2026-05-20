package com.ilya.books.service;

import com.ilya.books.domain.entity.Author;
import com.ilya.books.repository.AuthorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

public class AuthorService {

    private final AuthorRepository authorRepository;

    @Transactional
    public void deleteById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author not found"));

        // Ручная проверка требования ТЗ (защита от удаления автора с книгами)
        if (!author.getBooks().isEmpty()) {
            throw new IllegalStateException("Нельзя удалить автора, у которого есть книги!");
        }

        // Этот метод благодаря @SoftDelete выполнит UPDATE ... SET deleted = true
        authorRepository.delete(author);
    }
}
