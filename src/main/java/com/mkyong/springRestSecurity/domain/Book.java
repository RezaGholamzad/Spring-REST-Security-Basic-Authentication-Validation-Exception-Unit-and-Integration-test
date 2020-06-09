package com.mkyong.springRestSecurity.domain;

import com.mkyong.springRestSecurity.exception.validator.Author;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Book {
    @Id
    @GeneratedValue
    private Long id;

    @NotEmpty(message = "please provide a name")
    private String name;

    @Author // custom annotation
    @NotEmpty(message = "please provide a author")
    private String author;

    @NotNull(message = "please provide a price")
    @DecimalMin("1.00")
    private BigDecimal price;

    public Book(String name, String author, BigDecimal price) {
        this.name = name;
        this.author = author;
        this.price = price;
    }

}
