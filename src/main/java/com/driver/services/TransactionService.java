package com.driver.services;

import com.driver.models.*;
import com.driver.repositories.BookRepository;
import com.driver.repositories.CardRepository;
import com.driver.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class TransactionService {
    @Autowired
    BookRepository bookRepository5;

    @Autowired
    CardRepository cardRepository5;

    @Autowired
    TransactionRepository transactionRepository5;

    @Value("${books.max_allowed}")
    public int max_allowed_books;

    @Value("${books.max_allowed_days}")
    public int getMax_allowed_days;

    @Value("${books.fine.per_day}")
    public int fine_per_day;

    public String issueBook(int cardId, int bookId) throws Exception {
        //check whether bookId and cardId already exist
        Book book = bookRepository5.findById(bookId).get();
        Card card = cardRepository5.findById(cardId).get();
        //conditions required for successful transaction of issue book:

        Transaction issueTransaction = new Transaction();

        issueTransaction.setBook(book);
        issueTransaction.setCard(card);
        issueTransaction.setIssueOperation(true);

        //1. book is present and available
        if(book == null || !book.isAvailable()){
            issueTransaction.setTransactionStatus(TransactionStatus.FAILED);
            transactionRepository5.save(issueTransaction);
            throw new Exception("Book is either unavailable or not present");
        }

        // If it fails: throw new Exception("Book is either unavailable or not present");
        //2. card is present and activated
        if(card == null || card.getCardStatus().equals(CardStatus.DEACTIVATED)){
            issueTransaction.setTransactionStatus(TransactionStatus.FAILED);
            transactionRepository5.save(issueTransaction);
            throw new Exception("Card is invalid");
            // If it fails: throw new Exception("Card is invalid");
        }

        //3. number of books issued against the card is strictly less than max_allowed_books
        List<Book> booksInCard = card.getBooks();
        if(booksInCard==null) {
            booksInCard = new ArrayList<>();
        }
        if(booksInCard.size()>=max_allowed_books) {
            transactionRepository5.save(issueTransaction);
            throw new Exception("Book limit has reached for this card");
            // If it fails: throw new Exception("Book limit has reached for this card");
        }

        //If the transaction is successful, save the transaction to the list of transactions and return the id
        issueTransaction.setTransactionStatus(TransactionStatus.SUCCESSFUL);

        // Adding Book in the card
        booksInCard.add(book);
        card.setBooks(booksInCard);
        cardRepository5.save(card);

        // Adding Card To book && make book Unavailable to others
        book.setCard(card);
        book.setAvailable(false);
        List<Transaction> transactions = book.getTransactions();
        if ( transactions == null) {
            transactions = new ArrayList<>();
        }
        transactions.add(issueTransaction);
        book.setTransactions(transactions);

        bookRepository5.save(book);

        transactionRepository5.save(issueTransaction);

       return issueTransaction.getTransactionId(); //return transactionId instead
    }

    public Transaction returnBook(int cardId, int bookId) throws Exception{

        List<Transaction> transactions = transactionRepository5.find(cardId, bookId, TransactionStatus.SUCCESSFUL, true);
        Transaction transaction = transactions.get(transactions.size() - 1);

        //for the given transaction calculate the fine amount
        // considering the book has been returned exactly when this function is called
//        String issueDate = transaction.getTransactionDate().toString();
//        Date date= new Date();
//        String todayDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
//
//        int countDays = daysBetweenDates(issueDate, todayDate);
//
//        int extraDay = 0;
        Date issueDate = transaction.getTransactionDate();

        long timeIssuetime = Math.abs(System.currentTimeMillis() - issueDate.getTime());

        long totalDay = TimeUnit.DAYS.convert(timeIssuetime, TimeUnit.MILLISECONDS);

        int fine = 0;
        if(totalDay > getMax_allowed_days){
            fine = (int)((totalDay - getMax_allowed_days) * fine_per_day);
        }


        //make the book available for other users
        Book book = transaction.getBook();
        book.setAvailable(true);
        book.setCard(null);
        bookRepository5.updateBook(book);

        //make a new transaction for return book which contains the fine amount as well

//        Transaction returnBookTransaction = Transaction.builder()
//                .fineAmount(fine)
//                .transactionId(UUID.randomUUID().toString())
//                .book(book)
//                .card(transaction.getCard())
//                .transactionStatus(TransactionStatus.SUCCESSFUL)
//                .isIssueOperation(false)
//                .build();
        Transaction transaction1 = new Transaction();
        transaction1.setBook(transaction.getBook());
        transaction1.setCard(transaction.getCard());
        transaction1.setIssueOperation(false);
        transaction1.setFineAmount(fine);
        transaction1.setTransactionStatus(TransactionStatus.SUCCESSFUL);

        transactionRepository5.save(transaction1);

        return transaction1;

        // Removing the Book from card BookList
//        Card card = cardRepository5.findById(cardId).get();
//        List<Book> booksInCard = card.getBooks();
//        Iterator<Book> iterator = booksInCard.iterator();
//        while(iterator.hasNext()) {
//            Book currBook = iterator.next();
//            if(currBook.getId()==bookId) {
//                iterator.remove();
//            }
//        }
//        card.setBooks(booksInCard);
//
//        cardRepository5.save(card);

        // saving transaction
//        transactionRepository5.save(returnBookTransaction);
//
//        return returnBookTransaction; //return the transaction after updating all details
    }
    private int daysBetweenDates(String date1, String date2) {
        int year1 = Integer.parseInt(date1.substring(0,4));
        int month1 = Integer.parseInt(date1.substring(5,7));
        int day1 = Integer.parseInt(date1.substring(8,10));

        int year2 = Integer.parseInt(date2.substring(0,4));
        int month2 = Integer.parseInt(date2.substring(5,7));
        int day2 = Integer.parseInt(date2.substring(8,10));

        // we are going to count all days from 01-01-2010
        int countDays1 = countAllDays(day1, month1, year1);
        int countDays2 = countAllDays(day2, month2, year2);

        return Math.abs(countDays1-countDays2);
    }
    private int[] monthsDays = {0, 31,28,31,30,31,30,31,31,30,31,30,31};
    public int countAllDays(int day, int month, int year) {

        int countDays = day-1;
        int yearDiff = year-1 - 2010;

        countDays += (365*yearDiff) + (yearDiff/4);

        for(int i=1;i<month;i++){
            countDays += monthsDays[i];
        }
        if(month>2 && isLeapYear(year)) {
            ++countDays;
        }
        return countDays;
    }
    public boolean isLeapYear(int year) {
        if(year%400==0) return true;
        if(year%100==0) return false;

        return year%4==0;
    }
}