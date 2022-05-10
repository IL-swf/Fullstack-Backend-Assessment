package com.il.librarybackend.exceptions;

import com.il.librarybackend.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionHandlerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    BookRepository repository;


    @Test
    void handleNoSuchElementException() throws Exception {
        mvc.perform(get("/books/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.request", is("uri=/books/0")))
                .andExpect(jsonPath("$.error", is("That book isn't in the database.")))
                .andExpect(jsonPath("$.message", is("No Element at that address.")));
    }

    @Test
    void handleIllegalArgumentException() throws Exception {
        mvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"testing":"Shouldn't work", "hopes":"dreams"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.request", is("uri=/books")))
                .andExpect(jsonPath("$.error", is("That isn't a valid book.")))
                .andExpect(jsonPath("$.message", is("The data you provided wasn't valid.")));

        mvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.request", is("uri=/books")))
                .andExpect(jsonPath("$.error", is("Every book needs a title.")))
                .andExpect(jsonPath("$.message", is("The data you provided wasn't valid.")));
    }

    @Test
    void handleException() throws Exception {
        mvc.perform(post("/books")
                        .content("{'test':'This is only a test.'}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.request", is("uri=/books")))
                .andExpect(jsonPath("$.message", is("That didn't seem to work. Try something different.")));
    }
}