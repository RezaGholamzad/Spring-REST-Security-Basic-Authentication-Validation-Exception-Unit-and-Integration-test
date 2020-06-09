package com.mkyong.springRestSecurity.integrationTestWithSecurity;

import com.mkyong.springRestSecurity.domain.Book;
import com.mkyong.springRestSecurity.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//first way: In this test, the full Spring application context is started but without the server
@SpringBootTest
@AutoConfigureMockMvc

//second way: in this test, Spring Boot instantiates only the web layer rather than the whole context
//@WebMvcTest
//In an application with multiple controllers, you can even ask for only one to be instantiated by using
//@WebMvcTest(BookController.class)


//used to declare which active bean definition profiles should be used
// when loading an ApplicationContext for test classes.
@ActiveProfiles("test")
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    //create and inject a mock
    @MockBean
    private BookRepository mockRepository;

    @BeforeEach
    public void init(){
        Book book = new Book(1L, "A Guide to the Bodhisattva Way of Life", "Santideva", new BigDecimal("15.41"));

//       A unit test should test a class in isolation.
//       Mockito simplifies the development of tests for classes with external dependencies significantly.
//       The thenReturn() methods lets you define the return value when a particular method
//       of the mocked object is been called.
        when(mockRepository.findById(1L)).thenReturn(Optional.of(book));
    }

//    ****************************** Test for Spring REST Security ****************************

    @WithMockUser("USER")
    @Test
    public void find_login_ok() throws Exception{
        mockMvc.perform(get("/books/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("A Guide to the Bodhisattva Way of Life")))
                .andExpect(jsonPath("$.author", is("Santideva")))
                .andExpect(jsonPath("$.price", is(15.41)));
    }

    @Test
    public void find_noLogin_401() throws Exception {
        mockMvc.perform(get("/books/1"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
