package com.il.librarybackend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    BookRepository repository;

    ObjectMapper mapper = new ObjectMapper();

    Book book0 = new Book("Harry Potter and the Full Blood Prince that doesn't like him for some reason he doesn't know.",
            "J.K.Rowling", true);
    Book book1 = new Book("Twilight 14 - All Sparkles, No Teeth",
            "Whoever it is that writes these books", false);
    Book book2 = new Book("Star Wars - A New False Hope - Episode 27 - Palpatine still isn't dead",
            "Disney's Accounting Department", false);
    Book book3 = new Book("Fear and Loathing in Las Galvanize - The true story of my struggle staying focused on finishing writing these tests.",
            "Isaiah List", true);

    @BeforeAll
    void setup() {
        repository.saveAll(Arrays.asList(book0, book1, book2));
    }

    @Test
    @Transactional
    @Rollback
    void getAllBooks() throws Exception {
        mvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(book0.getId().intValue())))
                .andExpect(jsonPath("$[1].id", is(book1.getId().intValue())))
                .andExpect(jsonPath("$[2].id", is(book2.getId().intValue())));
    }

    @Test
    @Transactional
    @Rollback
    void postNewBook() throws Exception {
        mvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(book3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title", is("Fear and Loathing in Las Galvanize - The true story of my struggle staying focused on finishing writing these tests.")))
                .andExpect(jsonPath("$.author", is("Isaiah List")))
                .andExpect(jsonPath("$.favorite", is(true)));
    }

    @Test
    @Transactional
    @Rollback
    void getBookById() throws Exception {
        mvc.perform(get("/books/{id}", book1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(book1.getId().intValue())))
                .andExpect(jsonPath("$.title", is("Twilight 14 - All Sparkles, No Teeth")))
                .andExpect(jsonPath("$.author", is("Whoever it is that writes these books")))
                .andExpect(jsonPath("$.favorite", is(false)));
    }

    @Test
    @Transactional
    @Rollback
    void deleteBookById() throws Exception {
        mvc.perform(delete("/books/{id}", book0.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(String.format("Book %d deleted from database", book0.getId())));

        mvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(book1.getId().intValue())))
                .andExpect(jsonPath("$[1].id", is(book2.getId().intValue())));
    }

    @Test
    @Transactional
    @Rollback
    void patchUpdateBookById() throws Exception {
        mvc.perform(patch("/books/{id}", book1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "title": "Twilight 16: Live to Twilight another Twilight",
                            "author": "Still no idea",
                            "favorite": true
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(book1.getId().intValue())))
                .andExpect(jsonPath("$.title", is("Twilight 16: Live to Twilight another Twilight")))
                .andExpect(jsonPath("$.author", is("Still no idea")))
                .andExpect(jsonPath("$.favorite", is(true)));
    }
}