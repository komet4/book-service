package com.ilya.books.mapper;

import com.ilya.books.domain.entity.Genre;
import com.ilya.books.dto.request.GenreRequestDto;
import com.ilya.books.dto.response.GenreResponseDto;
import org.springframework.stereotype.Component;

@Component
public class GenreMapper {

    public Genre toEntity(GenreRequestDto dto) {
        if (dto == null) {
            return null;
        }
        Genre genre = new Genre();
        genre.setName(dto.name());
        return genre;
    }

    public GenreResponseDto toResponseDto(Genre genre) {
        if (genre == null) {
            return null;
        }
        return new GenreResponseDto(genre.getId(), genre.getName());
    }
}
