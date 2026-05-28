package com.ilya.books.mapper;

import com.ilya.books.domain.entity.Book;
import com.ilya.books.domain.entity.Genre;
import com.ilya.books.dto.request.BookPatchRequestDto;
import com.ilya.books.dto.request.BookRequestDto;
import com.ilya.books.dto.response.BookResponseDto;
import com.ilya.books.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookMapper {

    private final GenreMapper genreMapper;
    private final GenreService genreService;

    public Book toEntity(BookRequestDto dto) {
        if (dto == null) {
            return null;
        }
        Book book = new Book();
        book.setIsbn(dto.isbn());
        if (dto.genre() != null) {
            book.setGenre(genreMapper.toEntity(dto.genre()));
        }
        return book;
    }

    public BookResponseDto toResponseDto(Book book) {
        if (book == null) {
            return null;
        }
        return new BookResponseDto(
                book.getId(),
                book.getIsbn(),
                genreMapper.toResponseDto(book.getGenre())
        );
    }

    public void updateEntityFromPatchDto(BookPatchRequestDto dto, Book book) {
        if (dto == null || book == null) {
            return;
        }

        if (dto.isbn() != null)
            book.setIsbn(dto.isbn());

        if (dto.genre() != null) {
            Genre genre = genreService.getOrCreateByName(dto.genre().name());
            book.setGenre(genre);
        }
    }
}
