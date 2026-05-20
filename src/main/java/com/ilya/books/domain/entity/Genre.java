package com.ilya.books.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "genres")
public class Genre extends AbstractEntity {

    @Column(nullable = false, unique = true)
    private String name;

}
