package com.learnkafka.integration;

import com.learnkafka.IntegrationTest;
import com.learnkafka.domain.Book;
import com.learnkafka.domain.LibraryEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LibraryEventsControllerIntegrationTest implements IntegrationTest {

    @Autowired
    TestRestTemplate testRestTemplate;

    @Test
    void postLibraryEvent() {
        Book book = Book.builder()
                .bookId(123).bookAuthor("Andrew")
                .bookName("House of Dragons")
                .build();
        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(null)
                .book(book).build();

        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", MediaType.APPLICATION_JSON.toString());

        HttpEntity<LibraryEvent> request =
                new HttpEntity<>(libraryEvent,headers);

        ResponseEntity<LibraryEvent> responseEntity = testRestTemplate
                .exchange("/v1/libraryevent",
                HttpMethod.POST, request, LibraryEvent.class
                );
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }
}
