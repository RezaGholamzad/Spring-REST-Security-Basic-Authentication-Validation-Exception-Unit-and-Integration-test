package com.mkyong.springRestSecurity.controller;

import com.mkyong.springRestSecurity.domain.Book;
import com.mkyong.springRestSecurity.exception.BookNotFoundException;
import com.mkyong.springRestSecurity.exception.BookUnSupportedFieldPatchException;
import com.mkyong.springRestSecurity.repository.BookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Map;

@RestController
@Validated // class level, can apply the javax.validation.constraints.* annotations on
            // the path variable or even the request parameter directly.
public class BookController {

    private BookRepository repository;

    public BookController(BookRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/books")
    public List<Book> findAll(){
        return repository.findAll();
    }

    @PostMapping("/books")
    @ResponseStatus(HttpStatus.CREATED)
    public Book newBook(@Valid @RequestBody Book newBook) {
        return repository.save(newBook);
    }

    @GetMapping("/books/{id}")
    public Book findBook(@PathVariable @Min(1) Long id){
        return repository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    }

    @PutMapping("/books/{id}")
    public Book saveOrUpdate(@RequestBody Book newBook, @PathVariable Long id){
        return repository.findById(id)
                .map( book -> {
                    book.setName(newBook.getName());
                    book.setAuthor(newBook.getAuthor());
                    book.setPrice(newBook.getPrice());
                    return repository.save(book);
                })
                .orElseGet(() -> {
                    newBook.setId(id);
                    return repository.save(newBook);
                });
    }

    //update author only
    @PatchMapping("/books/{id}")
    public Book patch(@RequestBody Map<String,String> update, @PathVariable Long id){
        return repository.findById(id)
                .map(book -> {
                    String author = update.get("author");
                    if (!StringUtils.isEmpty(author)){
                        book.setAuthor(author);
                        return repository.save(book);
                    }else {
                        throw new BookUnSupportedFieldPatchException(update.keySet());
                    }
                })
                .orElseGet(() -> {
                    throw new BookNotFoundException(id);
                });
    }

    @DeleteMapping("/books/{id}")
    public void deleteBook(@PathVariable Long id){
        repository.deleteById(id);
    }
}
