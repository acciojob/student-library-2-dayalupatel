package com.driver.services;

import com.driver.models.Author;
import com.driver.models.Book;
import com.driver.models.Card;
import com.driver.repositories.AuthorRepository;
import com.driver.repositories.BookRepository;
import com.driver.repositories.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {


    @Autowired
    BookRepository bookRepository2;
    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    CardRepository cardRepository;

    public void createBook(Book book){
        Author author = book.getAuthor();

        if(author!=null) {
            author = authorRepository.findById(author.getId()).get();
            List<Book> bookWritten = author.getBooksWritten();
            if(bookWritten==null) {
                bookWritten = new ArrayList<>();
            }
            bookWritten.add(book);
            author.setBooksWritten(bookWritten);
            authorRepository.save(author);
            book.setAuthor(author);
        }

        bookRepository2.save(book);
    }

    public List<Book> getBooks(String genre, boolean available, String author){
        //find the elements of the list by yourself

        // if both are not null
        if(genre!=null && author!=null) {
            return bookRepository2.findBooksByGenreAuthor(genre, author, available);
        }

        // one of them is not null
        if(genre!=null) {
            return bookRepository2.findBooksByGenre(genre, available);
        }
        if(author!=null) {
            return bookRepository2.findBooksByAuthor(author, available);
        }

        // both are null
        return bookRepository2.findByAvailability(available);
    }
}
