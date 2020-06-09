package com.mkyong.springRestSecurity.integrationTestWithSecurity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mkyong.springRestSecurity.domain.Book;
import com.mkyong.springRestSecurity.repository.BookRepository;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class BookControllerRestTemplateTest {
    private static final ObjectMapper om = new ObjectMapper();

    //@WithMockUser is not working with TestRestTemplate
    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private BookRepository mockRepository;

    @BeforeEach
    public void init(){
        Book book = new Book(1L, "A Guide to the Bodhisattva Way of Life", "Santideva", new BigDecimal("15.41"));
        when(mockRepository.findById(1L)).thenReturn(Optional.of(book));
    }

    @Test
    public void find_login_ok() throws JSONException {
        String expected = "{id:1,name:\"A Guide to the Bodhisattva Way of Life\",author:\"Santideva\",price:15.41}";

        ResponseEntity<String> response = restTemplate.withBasicAuth("user", "password")
                .getForEntity("/books/1" , String.class);

        printJSON(response);

        assertThat(MediaType.APPLICATION_JSON).isEqualByComparingTo(response.getHeaders().getContentType());
        assertThat(HttpStatus.OK).isEqualByComparingTo(response.getStatusCode());

        JSONAssert.assertEquals(expected,response.getBody(), false);

    }

    @Test
    public void find_noLogin_401() throws JSONException {
        String expected = "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Unauthorized\",\"path\":\"/books/1\"}";

        ResponseEntity<String> response = restTemplate.getForEntity("/books/1", String.class);

        printJSON(response);

        assertThat(MediaType.APPLICATION_JSON).isEqualByComparingTo(response.getHeaders().getContentType());
        assertThat(HttpStatus.UNAUTHORIZED).isEqualByComparingTo(response.getStatusCode());

        JSONAssert.assertEquals(expected, response.getBody(), false);

    }

    private static void printJSON(Object object) {
        String result;
        try {
            result = om.writerWithDefaultPrettyPrinter().writeValueAsString(object);
            System.out.println(result);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
