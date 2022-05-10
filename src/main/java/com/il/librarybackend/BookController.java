package com.il.librarybackend;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/books")
@CrossOrigin("http://localhost:3000")
public class BookController {

    public final BookRepository repository;

    public BookController(BookRepository repository) {
        this.repository = repository;
    }


    @GetMapping
    public List<Book> getAllBooks() {
        return repository.findAll();
    }

    @PostMapping
    public Book postNewBook(@RequestBody Book book) {
        return repository.save(book);
    }

    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Long id) {
        if (repository.findById(id).isPresent()) {
            return repository.findById(id).get();
        } else throw new NoSuchElementException("That book isn't in the database");
    }

    @DeleteMapping("/{id}")
    public String deleteBookById(@PathVariable Long id) {
        repository.deleteById(id);
        return String.format("Book %d deleted from database", id);
    }

    @PatchMapping("/{id}")
    public Book patchUpdateBookById(@PathVariable Long id, @RequestBody HashMap<String, Object> updates) {

        Book updatedBook;

        if (repository.findById(id).isPresent()) {
            updatedBook = repository.findById(id).get();
        } else throw new NoSuchElementException("That book isn't in the database");


        updates.forEach((key, value) -> {
            switch (key) {
                case "title" -> updatedBook.setTitle((String) value);
                case "author" -> updatedBook.setAuthor((String) value);
                case "favorite" -> updatedBook.setFavorite((Boolean) value);
            }
        });

        return repository.save(updatedBook);
    }
}
