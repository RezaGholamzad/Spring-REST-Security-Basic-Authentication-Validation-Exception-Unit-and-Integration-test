package com.mkyong.springRestSecurity;

import com.mkyong.springRestSecurity.domain.Book;
import com.mkyong.springRestSecurity.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;

@SpringBootApplication
@Slf4j
public class SpringRestSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringRestSecurityApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(BookRepository repository) {
        return args -> {
            log.info("Preloading " + repository.save(new Book("A Guide to the Bodhisattva Way of Life", "Santideva",
                    new BigDecimal("15.41"))));
            log.info("Preloading " + repository.save(new Book("The Life-Changing Magic of Tidying Up", "Marie Kondo",
                    new BigDecimal("9.69"))));
            log.info("Preloading " + repository.save(new Book("Refactoring: Improving the Design of Existing Code", "Martin Fowler",
                    new BigDecimal("47.99"))));
        };
    }

}
