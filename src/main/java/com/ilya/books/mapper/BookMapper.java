package com.ilya.books.mapper;

import com.ilya.books.domain.entity.Book;
import com.ilya.books.dto.request.BookRequestDto;
import com.ilya.books.dto.response.BookResponseDto;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    private final GenreMapper genreMapper;

    public BookMapper(GenreMapper genreMapper) {
        this.genreMapper = genreMapper;
    }

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
}
