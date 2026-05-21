package com.ilya.books.mapper;

import com.ilya.books.domain.entity.Author;
import com.ilya.books.dto.request.AuthorPatchRequestDto;
import com.ilya.books.dto.request.AuthorRequestDto;
import com.ilya.books.dto.response.AuthorResponseDto;
import com.ilya.books.dto.response.BookResponseDto;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class AuthorMapper {

    private final BookMapper bookMapper;

    public AuthorMapper(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
    }

    public Author toEntity(AuthorRequestDto dto) {
        if (dto == null) {
            return null;
        }
        Author author = new Author();
        author.setFirstName(dto.firstName());
        author.setLastName(dto.lastName());
        author.setMiddleName(dto.middleName());
        author.setBirthDate(dto.birthDate());
        return author;
    }

    public AuthorResponseDto toResponseDto(Author author) {
        if (author == null) {
            return null;
        }

        List<BookResponseDto> bookDtos = Collections.emptyList();
        if (author.getBooks() != null) {
            bookDtos = author.getBooks().stream()
                    .map(bookMapper::toResponseDto)
                    .toList(); // В Java 21 используем лаконичный .toList() вместо Collectors.toList()
        }

        return new AuthorResponseDto(
                author.getId(),
                author.getFirstName(),
                author.getLastName(),
                author.getMiddleName(),
                author.getBirthDate(),
                bookDtos
        );
    }

    // Метод для PATCH: обновляем только то, что пришло
    public void updateEntityFromPatchDto(AuthorPatchRequestDto dto, Author author) {
        if (dto == null || author == null) {
            return;
        }
        if (dto.firstName() != null) {
            author.setFirstName(dto.firstName());
        }
        if (dto.lastName() != null) {
            author.setLastName(dto.lastName());
        }
        if (dto.middleName() != null) {
            author.setMiddleName(dto.middleName());
        }
        if (dto.birthDate() != null) {
            author.setBirthDate(dto.birthDate());
        }
    }
}
