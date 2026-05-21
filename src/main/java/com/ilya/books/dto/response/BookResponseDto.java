package com.ilya.books.dto.response;

public record BookResponseDto(
        Long id,
        String isbn,
        GenreResponseDto genre
) {}