package com.ilya.books.dto.response;

import java.time.LocalDate;
import java.util.List;

public record AuthorResponseDto(
        Long id,
        String firstName,
        String lastName,
        String middleName,
        LocalDate birthDate,
        List<BookResponseDto> books
) {}