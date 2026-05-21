package com.ilya.books.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AuthorRequestDto(
        @NotBlank(message = "First name is required.")
        @Size(max = 255, message = "First name must not exceed 255 characters.")
        String firstName,

        @NotBlank(message = "Last name is required.")
        @Size(max = 255, message = "Last name must not exceed 255 characters.")
        String lastName,

        @Size(max = 255, message = "Middle name is required.")
        String middleName,

        @PastOrPresent(message = "Birth date cannot be in the future.")
        LocalDate birthDate
) {}