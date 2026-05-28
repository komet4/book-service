package com.ilya.books.service;

import com.ilya.books.domain.entity.Genre;
import com.ilya.books.dto.request.GenreRequestDto;
import com.ilya.books.dto.response.GenreResponseDto;
import com.ilya.books.mapper.GenreMapper;
import com.ilya.books.repository.GenreRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GenreService {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    /**
     * Возвращает страницу со списком жанров (GET).
     * Если в параметрах запроса не указана сортировка, автоматически применяется сортировка по ID (по возрастанию).
     *
     * @param pageable параметры пагинации и сортировки
     * @return страница с DTO жанров
     */
    public Page<GenreResponseDto> getAllPaged(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.ASC, "id")
            );
        }

        return genreRepository.findAll(pageable)
                .map(genreMapper::toResponseDto);
    }

    /**
     * Находит жанр по ID (GET).
     *
     * @param id ID жанра
     * @return найденный DTO жанра
     * @throws EntityNotFoundException если жанр не найден
     */
    public GenreResponseDto getById(Long id) {
        return genreMapper.toResponseDto(getGenreEntityOrThrow(id));
    }

    /**
     * Возвращает существующий жанр по названию.
     * Если жанр с таким наименованием отсутствует, автоматически создает,
     * сохраняет и возвращает новый экземпляр жанра.
     *
     * @param name наименование жанра для поиска или создания
     * @return жанр
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Genre getOrCreateByName(String name) {
        return genreRepository.findByName(name)
                .orElseGet(() -> {
                    GenreRequestDto dto = new GenreRequestDto(name);
                    Genre newGenre = genreMapper.toEntity(dto);
                    return genreRepository.save(newGenre);
                });
    }

    /**
     * Создает новый жанр в системе (POST).
     *
     * @param dto данные для создания жанра
     * @return DTO созданного жанра с сгенерированным ID
     */
    @Transactional
    public GenreResponseDto create(GenreRequestDto dto) {
        Genre genre = genreMapper.toEntity(dto);
        Genre savedGenre = genreRepository.save(genre);
        return genreMapper.toResponseDto(savedGenre);
    }

    /**
     * Обновляет данные жанра (PATCH).
     *
     * @param id  ID жанра
     * @param dto DTO с изменяемыми полями
     * @return обновленный DTO жанра
     * @throws EntityNotFoundException если жанр не найден
     */
    @Transactional
    public GenreResponseDto update(Long id, GenreRequestDto dto) {
        Genre genre = getGenreEntityOrThrow(id);
        genreMapper.updateEntityFromDto(dto, genre);
        Genre savedGenre = genreRepository.save(genre);
        return genreMapper.toResponseDto(savedGenre);
    }

    /**
     * Выполняет мягкое удаление жанра по ID (DELETE).
     *
     * @param id ID жанра
     * @throws EntityNotFoundException если жанр не найден
     */
    @Transactional
    public void deleteById(Long id) {
        if (!genreRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    String.format("Genre not found with id: %d!", id));
        }
        genreRepository.softDeleteWithTimestamp(id, LocalDateTime.now());
    }

    /**
     * Получение жанра по ID.
     *
     * @param id ID жанра
     * @return найденный жанр
     * @throws EntityNotFoundException если жанр не найден
     */
    private Genre getGenreEntityOrThrow(Long id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Genre not found with id: %d!", id)));
    }

}
