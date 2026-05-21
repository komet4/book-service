package com.ilya.books.dto.request;

import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record AuthorPatchRequestDto(
        @Size(min = 1, max = 255, message = "First name must be between 1 and 255 characters.")
        String firstName,

        @Size(min = 1, max = 255, message = "Last name must be between 1 and 255 characters.")
        String lastName,

        @Size(max = 255, message = "Middle name must not exceed 255 characters.")
        String middleName,

        LocalDate birthDate
) {}
