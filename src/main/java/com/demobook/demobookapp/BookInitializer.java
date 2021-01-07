package com.demobook.demobookapp;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class BookInitializer implements CommandLineRunner {

    private BookRepository bookRepository;

    public BookInitializer(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }


    @Override
    public void run(String... args) throws Exception {
        log.info("Starting to initializer data ...");
        Faker faker = new Faker();

        for (int i = 0; i < 10; i++) {
            Book book = new Book();
            book.setTitle(faker.book().title());
            book.setIsbn(UUID.randomUUID().toString());
            book.setAuthor(faker.book().author());
            bookRepository.save(book);
        }
        log.info("... finished with data initialization");
    }
}
