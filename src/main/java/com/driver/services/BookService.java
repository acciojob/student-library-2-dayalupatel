package com.driver.services;

import com.driver.models.Author;
import com.driver.models.Genre;
import com.driver.repositories.AuthorRepository;
import com.driver.repositories.BookRepository;
import com.driver.models.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    BookRepository bookRepository2;

    @Autowired
    AuthorRepository authorRepository1;

    public void createBook(Book book){


        //Save the author in book

        int authorId = book.getAuthor().getId();

        Author author =  authorRepository1.findById(authorId).get();

        //Update the bookList written by Author
        author.getBooksWritten().add(book);

        //Updated the book
        book.setAuthor(author);
        //bookRepository2.save(book);
        bookRepository2.save(book);

        authorRepository1.save(author);



    }

    //This has to be rectified....and given a thought

    public List<Book> getBooks(Genre genre, boolean available, String author){


        if(genre != null && author != null){
            return bookRepository2.findBooksByGenreAuthor(genre, author, available);
        }else if(genre != null){
            return bookRepository2.findBooksByGenre(genre, available);
        }else if(author != null){
            return bookRepository2.findBooksByAuthor(author, available);
        }else{
            return bookRepository2.findByAvailability(available);
        }
    }
}
