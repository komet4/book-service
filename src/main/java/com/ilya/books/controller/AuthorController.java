package com.ilya.books.controller;

import com.ilya.books.dto.request.AuthorPatchRequestDto;
import com.ilya.books.dto.request.AuthorRequestDto;
import com.ilya.books.dto.request.BookRequestDto;
import com.ilya.books.dto.response.AuthorResponseDto;
import com.ilya.books.service.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/authors")
@RequiredArgsConstructor
@Validated
public class AuthorController {

    private final AuthorService authorService;

    /**
     * GET /api/v1/authors : Получение страницы с авторами.
     *
     * @param pageable параметры пагинации и сортировки
     * @return статус 200 (OK) со страницей DTO авторов
     */
    @GetMapping
    public ResponseEntity<Page<AuthorResponseDto>> getAllPaged(Pageable pageable) {
        Page<AuthorResponseDto> authorsPage = authorService.getAllPaged(pageable);
        return ResponseEntity.ok(authorsPage);
    }

    /**
     * GET /api/v1/authors/search : Поиск авторов по фильтрам.
     *
     * @param firstName  имя автора
     * @param lastName   фамилия автора
     * @param middleName отчество автора
     * @param birthDate  дата рождения
     * @return статус 200 (OK) со списком найденных DTO авторов
     */
    @GetMapping("/search")
    public ResponseEntity<List<AuthorResponseDto>> getByFilters(@RequestParam(required = false) String firstName,
                                                                @RequestParam(required = false) String lastName,
                                                                @RequestParam(required = false) String middleName,
                                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate) {

        List<AuthorResponseDto> filteredAuthors = authorService
                .getByFilters(firstName, lastName, middleName, birthDate);

        return ResponseEntity.ok(filteredAuthors);
    }

    /**
     * GET /api/v1/authors/{id} : Получить автора по ID.
     *
     * @param id ID автора
     * @return статус 200 (OK) с DTO автора
     */
    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponseDto> getById(@PathVariable Long id) {
        AuthorResponseDto author = authorService.getById(id);
        return ResponseEntity.ok(author);
    }

    /**
     * POST /api/v1/authors : Создание нового автора.
     *
     * @param authorRequestDto DTO с данными нового автора
     * @return статус 201 (Created) с DTO созданного автора
     */
    @PostMapping
    public ResponseEntity<AuthorResponseDto> create(@Valid @RequestBody AuthorRequestDto authorRequestDto) {
        AuthorResponseDto createdAuthor = authorService.create(authorRequestDto);
        return new ResponseEntity<>(createdAuthor, HttpStatus.CREATED);
    }

    /**
     * PUT /api/v1/authors/{id}/books : Добавление новых книг к существующему автору.
     *
     * @param id              ID автора
     * @param bookRequestDtos список DTO книг для добавления
     * @return статус 200 (OK) с обновленным DTO автора
     */
    @PutMapping("/{id}/books")
    public ResponseEntity<AuthorResponseDto> addBooksToAuthor(@PathVariable Long id,
                                                              @Valid @RequestBody List<BookRequestDto> bookRequestDtos) {
        AuthorResponseDto updatedAuthor = authorService.addBooksToAuthor(id, bookRequestDtos);
        return ResponseEntity.ok(updatedAuthor);
    }

    /**
     * PATCH /api/v1/authors/{id} : Частичное обновление данных автора.
     *
     * @param id                    ID автора
     * @param authorPatchRequestDto DTO с измененными полями
     * @return статус 200 (OK) с обновленным DTO автора
     */
    @PatchMapping("/{id}")
    public ResponseEntity<AuthorResponseDto> partialUpdate(@PathVariable Long id,
                                                           @Valid @RequestBody AuthorPatchRequestDto authorPatchRequestDto) {
        AuthorResponseDto updatedAuthor = authorService.partialUpdateAuthor(id, authorPatchRequestDto);
        return ResponseEntity.ok(updatedAuthor);
    }

    /**
     * DELETE /api/v1/authors/{id} : Мягкое удаление автора по ID.
     *
     * @param id ID автора
     * @return статус 204 (No Content) в случае успешного удаления
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        authorService.deleteAuthorById(id);
        return ResponseEntity.noContent().build();
    }

}
