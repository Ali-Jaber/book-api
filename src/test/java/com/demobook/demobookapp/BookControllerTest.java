package com.demobook.demobookapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest()
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookService bookService;

    @Captor
    private ArgumentCaptor<BookRequest> argumentCaptor;

    @Test
    public void postingANewBookShouldCreatedANewBookInTheDatabase() throws Exception {
        BookRequest bookRequest = new BookRequest();
        bookRequest.setAuthor("Ali");
        bookRequest.setTitle("Java 8");
        bookRequest.setIsbn("1773");

        when(bookService.createNewBook(argumentCaptor.capture())).thenReturn(1L);

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(bookRequest))
        ).andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", "http://localhost/api/books/1"));

        assertThat(argumentCaptor.getValue().getAuthor(), is("Ali"));
        assertThat(argumentCaptor.getValue().getIsbn(), is("1773"));
        assertThat(argumentCaptor.getValue().getTitle(), is("Java 8"));
    }

    @Test
    public void allBookEndpointShouldReturnTwoBook() throws Exception {
        when(bookService.getAllBooks()).thenReturn(Arrays.asList(
                createBook(1L, "Java 8", "Ali Jaber", "1337"),
                createBook(2L, "Java EE 8", "Ali Jaber", "42")
        ));

        this.mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Java 8")))
                .andExpect(jsonPath("$[0].author", is("Ali Jaber")))
                .andExpect(jsonPath("$[0].isbn", is("1337")))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    public void getBookWithIdOneShouldReturnABook() throws Exception {
        when(bookService.getBookById(1L))
                .thenReturn(createBook(1L, "Java 8", "Ali Jaber", "1337"));

        this.mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.title", is("Java 8")))
                .andExpect(jsonPath("$.author", is("Ali Jaber")))
                .andExpect(jsonPath("$.isbn", is("1337")))
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    public void getBookWithUnknownIdShouldReturn404() throws Exception {
        when(bookService.getBookById(1L))
                .thenThrow(new BookNotFoundException("Book with id '42' not found"));
        this.mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateBookWithKnownIdShouldUpdateTheBook() throws Exception {
        BookRequest bookRequest = new BookRequest();
        bookRequest.setAuthor("Ali");
        bookRequest.setTitle("Java 11");
        bookRequest.setIsbn("1337");

        when(bookService.updateBook(eq(1L), argumentCaptor.capture())).thenReturn(
                createBook(1L, "Java 11", "Ali Jaber", "1337"));

        this.mockMvc.perform(put("/api/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(bookRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.title", is("Java 11")))
                .andExpect(jsonPath("$.author", is("Ali Jaber")))
                .andExpect(jsonPath("$.isbn", is("1337")))
                .andExpect(jsonPath("$.id", is(1)));

        assertThat(argumentCaptor.getValue().getAuthor(), is("Ali"));
        assertThat(argumentCaptor.getValue().getIsbn(), is("1337"));
        assertThat(argumentCaptor.getValue().getTitle(), is("Java 11"));

    }

    @Test
    public void updateBookWithUnknownIdShouldReturn404() throws Exception{
        BookRequest bookRequest = new BookRequest();
        bookRequest.setAuthor("Ali");
        bookRequest.setTitle("Java 8");
        bookRequest.setIsbn("1773");

        when(bookService.updateBook(eq(1L), argumentCaptor.capture())).thenThrow(
                new BookNotFoundException("The Book with id 42 was not found"));

        this.mockMvc.perform(put("/api/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(bookRequest)))
                .andExpect(status().isNotFound());
    }

    private Book createBook(Long id, String title, String author, String isbn) {
        Book book = new Book();
        book.setId(id);
        book.setAuthor(author);
        book.setTitle(title);
        book.setIsbn(isbn);
        return book;
    }
}
