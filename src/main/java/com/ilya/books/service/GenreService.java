package com.ilya.books.service;

import com.ilya.books.domain.entity.Genre;
import com.ilya.books.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;

    /**
     * Возвращает существующий жанр из базы данных по его названию.
     * Если жанр с таким наименованием отсутствует, автоматически создает,
     * сохраняет в базу данных и возвращает новый экземпляр жанра.
     *
     * @param name наименование жанра для поиска или создания
     * @return жанр
     */
    @Transactional
    public Genre getOrCreateByName(String name) {
        return genreRepository.findByName(name)
                .orElseGet(() -> {
                    Genre newGenre = new Genre();
                    newGenre.setName(name);
                    return genreRepository.save(newGenre);
                });
    }
}
