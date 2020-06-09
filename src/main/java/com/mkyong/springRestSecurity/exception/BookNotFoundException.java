package com.mkyong.springRestSecurity.exception;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(Long id) {
        super("book not found id : " + id);
    }
}
