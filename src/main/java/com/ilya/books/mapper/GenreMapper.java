package com.ilya.books.mapper;

import com.ilya.books.domain.entity.Genre;
import com.ilya.books.dto.request.GenreRequestDto;
import com.ilya.books.dto.response.GenreResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
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

    public void updateEntityFromDto(GenreRequestDto dto, Genre genre) {
        if (dto == null || genre == null) {
            return;
        }

        if (dto.name() != null)
            genre.setName(dto.name());
    }
}
