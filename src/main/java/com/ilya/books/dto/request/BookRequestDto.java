package com.ilya.books.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record BookRequestDto(
        @NotBlank(message = "ISBN is required.")
        @Pattern(
                regexp = "^(?:ISBN(?:-1[03])?:?\\s*)?(?=[0-9X]{10}$|(?=(?:[0-9]{1}?\\s*){13}$)[0-9]{1,5}[-\\s]?[0-9]{1,7}[-\\s]?[0-9]{1,7}[-\\s]?[0-9]{1}X?)(?:[0-9]{1,5}[-\\s][0-9]{1,7}[-\\s][0-9]{1,7}[-\\s][0-9]{1}X?|[0-9]{10,13}X?)$",
                message = "Invalid ISBN format."
        )
        String isbn,

        @NotNull(message = "Genre is required.")
        @Valid
        GenreRequestDto genre
) {}