package com.ilya.books.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "authors")
public class Author extends AbstractEntity {

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String middleName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @OneToMany(mappedBy = "author", cascade = {CascadeType.PERSIST, CascadeType.MERGE}/*, orphanRemoval = true*/)
    @BatchSize(size = 20)
    private List<Book> books = new ArrayList<>();

    public void addBook(Book book) {
        if (book != null) {
            this.books.add(book);
            book.setAuthor(this);
        }
    }

    public void removeBook(Book book) {
        if (book != null) {
            this.books.remove(book);
            book.setAuthor(null);
        }
    }

}
