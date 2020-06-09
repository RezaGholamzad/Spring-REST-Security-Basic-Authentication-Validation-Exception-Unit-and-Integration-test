package com.mkyong.springRestSecurity.exception.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Target({FIELD})
@Retention(RUNTIME) // if it should be available at runtime, for inspection via reflection
@Constraint(validatedBy = AuthorValidator.class)
@Documented // indicates that elements using this annotation should be documented by JavaDoc
public @interface Author {

    String message() default "Author is not valid.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
