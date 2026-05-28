package com.ilya.books.controller;

import com.ilya.books.dto.request.GenreRequestDto;
import com.ilya.books.dto.response.GenreResponseDto;
import com.ilya.books.service.GenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/genres")
@RequiredArgsConstructor
@Validated
public class GenreController {

    private final GenreService genreService;

    /**
     * GET /api/v1/genres : Получение страницы с жанрами.
     *
     * @param pageable параметры пагинации и сортировки
     * @return статус 200 (OK) со страницей DTO жанров
     */
    @GetMapping
    public ResponseEntity<Page<GenreResponseDto>> getAllPaged(Pageable pageable) {
        Page<GenreResponseDto> genresPage = genreService.getAllPaged(pageable);
        return ResponseEntity.ok(genresPage);
    }

    /**
     * GET /api/v1/genres/{id} : Получить жанр по ID.
     *
     * @param id ID жанра
     * @return статус 200 (OK) с DTO жанра
     */
    @GetMapping("/{id}")
    public ResponseEntity<GenreResponseDto> getById(@PathVariable Long id) {
        GenreResponseDto genre = genreService.getById(id);
        return ResponseEntity.ok(genre);
    }

    /**
     * POST /api/v1/genres : Создание нового жанра.
     *
     * @param dto DTO с данными нового жанра
     * @return статус 201 (Created) с DTO созданного жанра
     */
    @PostMapping
    public ResponseEntity<GenreResponseDto> create(@Valid @RequestBody GenreRequestDto dto) {
        GenreResponseDto createdGenre = genreService.create(dto);
        return new ResponseEntity<>(createdGenre, HttpStatus.CREATED);
    }

    /**
     * PATCH /api/v1/genres/{id} : Обновление данных жанра.
     *
     * @param id  ID жанра
     * @param dto DTO с измененными полями
     * @return статус 200 (OK) с обновленным DTO жанра
     */
    @PatchMapping("/{id}")
    public ResponseEntity<GenreResponseDto> update(@PathVariable Long id, @Valid @RequestBody GenreRequestDto dto) {
        GenreResponseDto updatedGenre = genreService.update(id, dto);
        return ResponseEntity.ok(updatedGenre);
    }

    /**
     * DELETE /api/v1/genres/{id} : Мягкое удаление жанра по ID.
     *
     * @param id ID жанра
     * @return статус 204 (No Content) в случае успешного удаления
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        genreService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
