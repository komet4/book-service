package com.ilya.books.service;

import com.ilya.books.domain.entity.Author;
import com.ilya.books.domain.entity.Book;
import com.ilya.books.domain.entity.Genre;
import com.ilya.books.dto.request.AuthorPatchRequestDto;
import com.ilya.books.dto.request.AuthorRequestDto;
import com.ilya.books.dto.request.BookRequestDto;
import com.ilya.books.dto.response.AuthorResponseDto;
import com.ilya.books.mapper.AuthorMapper;
import com.ilya.books.mapper.BookMapper;
import com.ilya.books.repository.AuthorRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;
    private final BookMapper bookMapper;
    private final GenreService genreService;

    /**
     * Возвращает страницу со списком авторов (GET).
     * Если в параметрах запроса не указана сортировка, автоматически применяется сортировка по ID (по возрастанию).
     *
     * @param pageable параметры пагинации и сортировки
     * @return страница с DTO авторов
     */
    public Page<AuthorResponseDto> getAllPaged(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.ASC, "id")
            );
        }

        return authorRepository.findAll(pageable)
                .map(authorMapper::toResponseDto);
    }

    /**
     * Возвращает список авторов, соответствующих переданным фильтрам (GET).
     * Поиск по текстовым полям осуществляется по частичному совпадению без учета регистра.
     *
     * @param firstName  имя автора
     * @param lastName   фамилия автора
     * @param middleName отчество автора
     * @param birthDate  дата рождения автора
     * @return список найденных DTO авторов
     */
    public List<AuthorResponseDto> getByFilters(String firstName, String lastName,
                                                String middleName, LocalDate birthDate) {
        Specification<Author> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(firstName)) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%"
                ));
            }
            if (StringUtils.hasText(lastName)) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%"
                ));
            }
            if (StringUtils.hasText(middleName)) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("middleName")), "%" + middleName.toLowerCase() + "%"
                ));
            }
            if (birthDate != null) {
                predicates.add(criteriaBuilder.equal(root.get("birthDate"), birthDate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return authorRepository.findAll(specification).stream()
                .map(authorMapper::toResponseDto)
                .toList();
    }

    /**
     * Находит автора по ID (GET).
     *
     * @param id ID автора
     * @return найденный DTO автора
     * @throws EntityNotFoundException если автор не найден
     */
    public AuthorResponseDto getById(Long id) {
        return authorMapper.toResponseDto(getAuthorEntityOrThrow(id));
    }

    /**
     * Создает нового автора (POST).
     *
     * @param authorRequestDto данные для создания автора
     * @return DTO созданного автора с сгенерированным ID
     */
    @Transactional
    public AuthorResponseDto create(AuthorRequestDto authorRequestDto) {
        Author author = authorMapper.toEntity(authorRequestDto);
        Author savedAuthor = authorRepository.save(author);
        return authorMapper.toResponseDto(savedAuthor);
    }

    /**
     * Добавляет новые книги к уже существующему автору (PUT).
     *
     * @param id              ID автора
     * @param bookRequestDtos список DTO новых книг для добавления
     * @return обновленный DTO автора со списком книг
     * @throws EntityNotFoundException если автор не найден
     */
    @Transactional
    public AuthorResponseDto addBooksToAuthor(Long id, List<BookRequestDto> bookRequestDtos) {
        Author author = getAuthorEntityOrThrow(id);

        for (BookRequestDto bookRequestDto : bookRequestDtos) {
            Book book = bookMapper.toEntity(bookRequestDto);
            Genre genre = genreService.getOrCreateByName(bookRequestDto.genre().name());
            book.setGenre(genre);
            author.addBook(book);
        }

        Author savedAuthor = authorRepository.save(author);
        return authorMapper.toResponseDto(savedAuthor);
    }

    /**
     * Частично обновляет данные автора (PATCH).
     * Обновляются только те поля, которые были переданы (не null).
     *
     * @param id                    ID автора
     * @param authorPatchRequestDto DTO с изменяемыми полями
     * @return обновленный DTO автора
     * @throws EntityNotFoundException если автор не найден
     */
    @Transactional
    public AuthorResponseDto partialUpdateAuthor(Long id, AuthorPatchRequestDto authorPatchRequestDto) {
        Author author = getAuthorEntityOrThrow(id);
        authorMapper.updateEntityFromPatchDto(authorPatchRequestDto, author);
        Author savedAuthor = authorRepository.save(author);
        return authorMapper.toResponseDto(savedAuthor);
    }

    /**
     * Выполняет мягкое удаление автора по ID (DELETE).
     * Удаление запрещено, если у автора есть привязанные книги.
     *
     * @param id ID автора
     * @throws EntityNotFoundException если автор не найден
     * @throws IllegalStateException   если у автора есть книги
     */
    @Transactional
    public void deleteAuthorById(Long id) {
        Author author = getAuthorEntityOrThrow(id);

        if (!author.getBooks().isEmpty()) {
            throw new IllegalStateException("Can't delete an author who has books!");
        }

        //authorRepository.delete(author);
        authorRepository.softDeleteWithTimestamp(id, LocalDateTime.now());
    }

    /**
     * Получение автора по ID.
     *
     * @param id ID автора
     * @return найденный автор
     * @throws EntityNotFoundException если автор не найден
     */
    private Author getAuthorEntityOrThrow(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Author not found with id: %d!", id)));
    }
}
