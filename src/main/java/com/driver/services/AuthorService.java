package com.driver.services;

import com.driver.repositories.AuthorRepository;
import com.driver.models.Author;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorService {

    @Autowired
    AuthorRepository authorRepository1;

    public void create(Author author){
        authorRepository1.save(author);
    }
}
