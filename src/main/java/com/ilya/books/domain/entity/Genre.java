package com.ilya.books.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "genres")
public class Genre extends AbstractEntity {

    @Column(nullable = false, unique = true)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
