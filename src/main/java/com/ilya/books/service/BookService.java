package com.ilya.books.service;

import com.ilya.books.domain.entity.Book;
import com.ilya.books.dto.request.BookPatchRequestDto;
import com.ilya.books.dto.response.BookResponseDto;
import com.ilya.books.mapper.BookMapper;
import com.ilya.books.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    /**
     * Возвращает страницу со списком книг (GET).
     * Если в параметрах запроса не указана сортировка, автоматически применяется сортировка по ID (по возрастанию).
     *
     * @param pageable параметры пагинации и сортировки
     * @return страница с DTO книг
     */
    public Page<BookResponseDto> getAllPaged(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.ASC, "id")
            );
        }

        return bookRepository.findAll(pageable)
                .map(bookMapper::toResponseDto);
    }

    /**
     * Находит книгу по ID (GET).
     *
     * @param id ID книги
     * @return найденный DTO книги
     * @throws EntityNotFoundException если книга не найден
     */
    public BookResponseDto getById(Long id) {
        return bookMapper.toResponseDto(getBookEntityOrThrow(id));
    }

    /**
     * Частично обновляет данные книги (PATCH).
     * Обновляются только те поля, которые были переданы (не null).
     *
     * @param id                  ID книги
     * @param bookPatchRequestDto DTO с изменяемыми полями
     * @return обновленный DTO книги
     * @throws EntityNotFoundException если книга не найдена
     */
    @Transactional
    public BookResponseDto partialUpdate(Long id, BookPatchRequestDto bookPatchRequestDto) {
        Book book = getBookEntityOrThrow(id);
        bookMapper.updateEntityFromPatchDto(bookPatchRequestDto, book);
        Book savedBook = bookRepository.save(book);
        return bookMapper.toResponseDto(savedBook);
    }

    /**
     * Выполняет мягкое удаление книги по ID (DELETE).
     *
     * @param id ID книги
     * @throws EntityNotFoundException если книга не найдена
     */
    @Transactional
    public void deleteById(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    String.format("Book not found with id: %d!", id));
        }
        bookRepository.softDeleteWithTimestamp(id, LocalDateTime.now());
    }

    /**
     * Получение книги по ID.
     *
     * @param id ID книги
     * @return найденная книга
     * @throws EntityNotFoundException если книга не найдена
     */
    private Book getBookEntityOrThrow(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Book not found with id: %d!", id)));
    }
}
