package com.demobook.demobookapp;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Long createNewBook(BookRequest bookRequest) {
        Book book = new Book();
        book.setAuthor(bookRequest.getAuthor());
        book.setIsbn(bookRequest.getIsbn());
        book.setTitle(bookRequest.getTitle());

        book = bookRepository.save(book);

        return book.getId();
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Long id) {
        Optional<Book> requestedBook = bookRepository.findById(id);

        if (!requestedBook.isPresent()) {
            throw new BookNotFoundException(String.format("Book with id: '%s' not found", id));
        }
        return requestedBook.get();
    }

    @Transactional
    public Book updateBook(Long id, BookRequest bookToUpdateRequest) {
        Optional<Book> bookFromDatabase = bookRepository.findById(id);

        if (!bookFromDatabase.isPresent()) {
            throw new BookNotFoundException(String.format("Book with id: '%s' not found", id));
        }
        Book bookToUpdate = bookFromDatabase.get();
        bookToUpdate.setAuthor(bookToUpdateRequest.getAuthor());
        bookToUpdate.setTitle(bookToUpdateRequest.getTitle());
        bookToUpdate.setIsbn(bookToUpdateRequest.getIsbn());

        return bookToUpdate;
    }

    public void deleteBookById(Long id) {
        bookRepository.deleteById(id);
    }
}