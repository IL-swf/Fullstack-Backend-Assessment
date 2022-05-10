package com.il.librarybackend;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

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
        if (book.getTitle() ==  null) throw new IllegalArgumentException("That isn't a valid book.");
        if (book.getTitle().equals("")) throw new IllegalArgumentException("Every book needs a title.");

        try {
            return repository.save(book);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("That isn't a valid book.");
        }
    }

    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Long id) {
        try {
            if (repository.findById(id).isPresent()) return repository.findById(id).get();
            else throw new NoSuchElementException("That book isn't in the database.");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("That isn't a valid book id.");
        }
    }

    @DeleteMapping("/{id}")
    public String deleteBookById(@PathVariable Long id) {

        try {
            repository.deleteById(id);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("That isn't a valid book id.");
        }

        return String.format("Book %d deleted from database", id);
    }

    @PatchMapping("/{id}")
    public Book patchUpdateBookById(@PathVariable Long id, @RequestBody HashMap<String, Object> updates) {

        Book updatedBook;

        try {
            if (repository.findById(id).isPresent()) updatedBook = repository.findById(id).get();
            else throw new NoSuchElementException("That book isn't in the database.");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("That isn't a valid book id.");
        }

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
