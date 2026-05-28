package com.ilya.books.controller;

import com.ilya.books.dto.request.BookPatchRequestDto;
import com.ilya.books.dto.response.BookResponseDto;
import com.ilya.books.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@Validated
public class BookController {

    private final BookService bookService;

    /**
     * GET /api/v1/books : Получение страницы с книгами.
     *
     * @param pageable параметры пагинации и сортировки
     * @return статус 200 (OK) со страницей DTO книг
     */
    @GetMapping
    public ResponseEntity<Page<BookResponseDto>> getAllPaged(Pageable pageable) {
        Page<BookResponseDto> booksPage = bookService.getAllPaged(pageable);
        return ResponseEntity.ok(booksPage);
    }

    /**
     * GET /api/v1/books/{id} : Получить книгу по ID.
     *
     * @param id ID книги
     * @return статус 200 (OK) с DTO книги
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDto> getById(@PathVariable Long id) {
        BookResponseDto book = bookService.getById(id);
        return ResponseEntity.ok(book);
    }

    /**
     * PATCH /api/v1/books/{id} : Частичное обновление данных книги.
     *
     * @param id  ID книги
     * @param dto DTO с измененными полями
     * @return статус 200 (OK) с обновленным DTO книги
     */
    @PatchMapping("/{id}")
    public ResponseEntity<BookResponseDto> partialUpdate(@PathVariable Long id,
                                                         @Valid @RequestBody BookPatchRequestDto dto) {
        BookResponseDto updatedBook = bookService.partialUpdate(id, dto);
        return ResponseEntity.ok(updatedBook);
    }

    /**
     * DELETE /api/v1/books/{id} : Мягкое удаление книги по ID.
     *
     * @param id ID автора
     * @return статус 204 (No Content) в случае успешного удаления
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        bookService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
