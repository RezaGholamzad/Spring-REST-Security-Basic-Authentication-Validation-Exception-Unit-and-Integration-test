package com.mkyong.springRestSecurity.integrationTestWithoutSecurity;

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
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

//import static org.junit.Assert.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // for restTemplate
@ActiveProfiles("test")
public class BookControllerRestTemplateTest {
    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private BookRepository mockRepository;

    @BeforeEach
    public void init(){
        Book book = new Book(1L, "Book Name", "Mkyong", new BigDecimal("9.99"));
        when(mockRepository.findById(1L)).thenReturn(Optional.of(book));
    }

    @Test
    public void find_bookId_OK() throws JSONException {
        String expected = "{id:1,name:\"Book Name\",author:\"Mkyong\",price:9.99}";

        ResponseEntity<String> response = restTemplate.getForEntity("/books/1", String.class);

        assertThat(HttpStatus.OK).isEqualByComparingTo(response.getStatusCode());
        assertThat(MediaType.APPLICATION_JSON).isEqualByComparingTo(response.getHeaders().getContentType());

        JSONAssert.assertEquals(expected, response.getBody(), false);

        verify(mockRepository, times(1)).findById(1L);
    }

    @Test
    public void find_allBook_OK() throws Exception {

        List<Book> books = Arrays.asList(
                new Book(1L, "Book A", "Ah Pig", new BigDecimal("1.99")),
                new Book(2L, "Book B", "Ah Dog", new BigDecimal("2.99")));

        when(mockRepository.findAll()).thenReturn(books);

        String expected = om.writeValueAsString(books);

        ResponseEntity<String> response = restTemplate.getForEntity("/books", String.class);

        assertThat(HttpStatus.OK).isEqualByComparingTo(response.getStatusCode());
        JSONAssert.assertEquals(expected, response.getBody(), false);

        verify(mockRepository, times(1)).findAll();
    }

    @Test
    public void find_bookIdNotFound_404() throws Exception {

        String expected = "{status:404,error:\"Not Found\",message:\"Book id not found : 5\",path:\"/books/5\"}";

        ResponseEntity<String> response = restTemplate.getForEntity("/books/5", String.class);

        assertThat(HttpStatus.NOT_FOUND).isEqualByComparingTo(response.getStatusCode());
        JSONAssert.assertEquals(expected, response.getBody(), false);

    }

    @Test
    public void save_book_OK() throws JsonProcessingException, JSONException {
        Book newBook = new Book(1L, "Spring Boot Guide", "mkyong", new BigDecimal("2.99"));

        when(mockRepository.save(any(Book.class))).thenReturn(newBook);

        String expected = om.writeValueAsString(newBook);

        ResponseEntity<String> response = restTemplate.postForEntity("/books", newBook, String.class);

        assertThat(HttpStatus.CREATED).isEqualByComparingTo(response.getStatusCode());
        JSONAssert.assertEquals(expected, response.getBody(), false);

        verify(mockRepository, times(4)).save(any(Book.class));
    }

    @Test
    public void update_book_OK() throws JsonProcessingException, JSONException {
        Book updateBook = new Book(1L, "ABC", "mkyong", new BigDecimal("19.99"));

        when(mockRepository.save(any(Book.class))).thenReturn(updateBook);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(om.writeValueAsString(updateBook), headers);

        ResponseEntity<String> response = restTemplate.exchange("/books/1", HttpMethod.PUT, entity, String.class);
        assertThat(HttpStatus.OK).isEqualByComparingTo(response.getStatusCode());
        JSONAssert.assertEquals(om.writeValueAsString(updateBook), response.getBody(), false);

        verify(mockRepository, times(1)).findById(1L);
        verify(mockRepository, times(4)).save(any(Book.class));
    }

    @Test
    public void patch_bookAuthor_OK(){
        when(mockRepository.save(any(Book.class))).thenReturn(new Book());
        String patchInJson = "{\"author\":\"ultraman\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(patchInJson, headers);

        ResponseEntity<String> response = restTemplate.exchange("/books/1", HttpMethod.PUT, entity, String.class);

        assertThat(HttpStatus.OK).isEqualByComparingTo(response.getStatusCode());

        verify(mockRepository, times(1)).findById(1L);
        verify(mockRepository, times(4)).save(any(Book.class));
    }

    @Test
    public void patch_bookPrice_405() {

        String expectedBody = "Field [price] update is not allow.";

        String patchInJson = "{\"price\":\"99.99\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(patchInJson, headers);

        ResponseEntity<String> response = restTemplate.exchange("/books/1", HttpMethod.PATCH, entity, String.class);
        assertThat(HttpStatus.METHOD_NOT_ALLOWED).isEqualByComparingTo(response.getStatusCode());

        assertThat(expectedBody).isEqualTo(response.getBody());

        verify(mockRepository, times(1)).findById(1L);
        verify(mockRepository, times(3)).save(any(Book.class));
    }

    @Test
    public void delete_book_OK() {

        doNothing().when(mockRepository).deleteById(1L);

        HttpEntity<String> entity = new HttpEntity<>(null, new HttpHeaders());
        ResponseEntity<String> response = restTemplate.exchange("/books/1", HttpMethod.DELETE, entity, String.class);

        assertThat(HttpStatus.OK).isEqualByComparingTo(response.getStatusCode());

        verify(mockRepository, times(1)).deleteById(1L);
    }

    //    **************************** Test for Spring REST Validation ************************

    /*
        {
            "timestamp":"2019-03-05T09:34:13.280+0000",
            "status":400,
            "errors":["Author is not allowed.","Please provide a price","Please provide a author"]
        }
     */
    @Test
    public void save_emptyAuthor_emptyPrice_400() throws JSONException {
        String bookInJson = "{\"name\":\"ABC\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(bookInJson, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/books", entity, String.class);
        String expectedJson = "{\"status\":400,\"errors\":[\"Author is not valid.\",\"please provide a price\",\"please provide a author\"]}";

        printJSON(response);

        assertThat(HttpStatus.BAD_REQUEST).isEqualByComparingTo(response.getStatusCode());
        //use of Junit
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()); // import static org.junit.Assert.assertEquals;

        JSONAssert.assertEquals(expectedJson, response.getBody(), false);

        verify(mockRepository, times(0)).save(any(Book.class));
    }

     /*
        {
            "timestamp":"2019-03-05T09:34:13.207+0000",
            "status":400,
            "errors":["Author is not allowed."]
        }
     */
     @Test
    public void save_invalidAuthor_400() throws JSONException {
        String bookInJson = "{\"name\":\" Spring REST tutorials\", \"author\":\"abc\",\"price\":\"9.99\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(bookInJson, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/books", entity, String.class);
        String expectedJson = "{\"status\":400,\"errors\":[\"Author is not valid.\"]}";

         printJSON(response);

        assertThat(HttpStatus.BAD_REQUEST).isEqualByComparingTo(response.getStatusCode());

        JSONAssert.assertEquals(expectedJson, response.getBody(), false);

        verify(mockRepository, times(0)).save(any(Book.class));
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
