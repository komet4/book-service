package com.ilya.books.service;

import com.ilya.books.domain.entity.Author;
import com.ilya.books.domain.entity.Book;
import com.ilya.books.domain.entity.Genre;
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
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final GenreService genreService;

    /**
     * Возвращает страницу со списком авторов (GET).
     * Если в параметрах запроса не указана сортировка, автоматически применяется сортировка по ID (по возрастанию).
     *
     * @param pageable параметры пагинации и сортировки
     * @return страница с авторами
     */
    @Transactional(readOnly = true)
    public Page<Author> getAllPaged(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.ASC, "id")
            );
        }

        return authorRepository.findAll(pageable);
    }

    /**
     * Возвращает список авторов, соответствующих переданным фильтрам (GET).
     * Поиск по текстовым полям осуществляется по частичному совпадению без учета регистра.
     * Параметры, переданные как null или пустые строки, игнорируются при фильтрации.
     *
     * @param firstName  имя автора (или его часть)
     * @param lastName   фамилия автора (или её часть)
     * @param middleName отчество автора (или его часть)
     * @param birthDate  точная дата рождения автора
     * @return список найденных авторов (или пустой список)
     */
    @Transactional(readOnly = true)
    public List<Author> getByFilters(String firstName, String lastName,
                                     String middleName, LocalDate birthDate) {
        return authorRepository.findAll((Specification<Author>)
                (root, query, criteriaBuilder) -> {
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
                });
    }

    /**
     * Находит автора по его идентификатору (GET).
     *
     * @param authorId уникальный идентификатор автора
     * @return найденный автор
     * @throws EntityNotFoundException если автор с указанным id не найден в базе данных
     */
    @Transactional(readOnly = true)
    public Author getById(Long authorId) {
        return authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Author not found with id: %d!", authorId)));
    }

    /**
     * Добавляет новые книги к уже существующему автору (PUT).
     *
     * @param authorId уникальный идентификатор автора
     * @param newBooks список новых книг для добавления
     * @return обновленный объект автора со списком книг
     * @throws EntityNotFoundException если автор не найден
     */
    @Transactional
    public Author addBooksToAuthor(Long authorId, List<Book> newBooks) {
        Author author = getById(authorId);

        for (Book book : newBooks) {
            Genre managedGenre = genreService.getOrCreateByName(book.getGenre().getName());
            book.setGenre(managedGenre);
            author.addBook(book);
        }

        return authorRepository.save(author);
    }

    /**
     * Выполняет частичное обновление данных автора (PATCH).
     * Обновляются только те поля, которые были переданы.
     *
     * @param authorId   уникальный идентификатор автора
     * @param firstName  новое имя (если требуется обновить)
     * @param lastName   новая фамилия (если требуется обновить)
     * @param middleName новое отчество (если требуется обновить)
     * @param birthDate  новая дата рождения (если требуется обновить)
     * @return обновленный объект автора
     * @throws EntityNotFoundException если автор не найден
     */
    @Transactional
    public Author partialUpdate(Long authorId, String firstName, String lastName,
                                String middleName, LocalDate birthDate) {
        Author author = getById(authorId);

        if (StringUtils.hasText(firstName)) {
            author.setFirstName(firstName);
        }
        if (StringUtils.hasText(lastName)) {
            author.setLastName(lastName);
        }
        if (StringUtils.hasText(middleName)) {
            author.setMiddleName(middleName);
        }
        if (birthDate != null) {
            author.setBirthDate(birthDate);
        }

        return authorRepository.save(author);
    }

    /**
     * Выполняет мягкое удаление автора по его идентификатору (DELETE).
     * Удаление запрещено, если у автора есть привязанные книги.
     *
     * @param authorId уникальный идентификатор автора
     * @throws EntityNotFoundException if author not found
     * @throws IllegalStateException   если у автора есть книги
     */
    @Transactional
    public void deleteById(Long authorId) {
        Author author = getById(authorId);

        if (!author.getBooks().isEmpty()) {
            throw new IllegalStateException("Can't delete an author who has books!");
        }

        authorRepository.delete(author);
    }
}
