package com.mkyong.springRestSecurity.repository;

import com.mkyong.springRestSecurity.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BookRepository extends JpaRepository<Book, Long> {
}
