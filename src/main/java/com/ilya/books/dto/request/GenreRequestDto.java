package com.ilya.books.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GenreRequestDto(
        @NotBlank(message = "Genre name is required.")
        @Size(max = 255, message = "Genre name must not exceed 255 characters.")
        String name
) {}
