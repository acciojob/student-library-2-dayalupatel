package com.driver.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
//@Table(name = "author")
@Getter
@Setter
@Builder
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;

    @Column(unique = true)
    private String email;

    private int age;
    private String country;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("author")
    private List<Book> booksWritten;

    public Author() {

    }
    public Author(String name, String email, int age, String country) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.country = country;
    }
    public Author(int id, String name, String email, int age, String country, List<Book> booksWritten) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.country = country;
        this.booksWritten = booksWritten;
    }

}

