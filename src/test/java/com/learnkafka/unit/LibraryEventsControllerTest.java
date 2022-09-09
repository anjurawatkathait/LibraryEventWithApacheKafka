package com.learnkafka.unit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.domain.Book;
import com.learnkafka.domain.LibraryEvent;
import com.learnkafka.producer.LibraryEventProducer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//Unit Test Case
@WebMvcTest
@AutoConfigureMockMvc
public class LibraryEventsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    LibraryEventProducer libraryEventProducer;

    @Autowired
    TestRestTemplate restTemplate;

    ObjectMapper objectMapper = new ObjectMapper();

    private Consumer<Integer, String> consumer;

    @Test
    void postLibraryEvent() throws Exception {

        Book book = Book.builder()
                .bookId(123).bookAuthor("Andrew")
                .bookName("House of Dragons")
                .build();
        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(null)
                .book(book).build();
        String json = objectMapper.writeValueAsString(libraryEvent);

        //mocking this behaviour " libraryEventProducer.sendLibraryEventApproach2(libraryEvent);"
        //doNothing().when(libraryEventProducer).sendLibraryEventApproach2(isA(LibraryEvent.class));
        when(libraryEventProducer.sendLibraryEventApproach2(isA(LibraryEvent.class))).thenReturn(null);

        mockMvc.perform(post("/v1/libraryevent")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());

    }

    @Test
    void postLibraryEvent_4XX() throws Exception {

        Book book = Book.builder()
                .bookId(null).bookAuthor(null)
                .bookName("House of Dragons")
                .build();
        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(null)
                .book(book).build();
        String json = objectMapper.writeValueAsString(libraryEvent);
        String expectedErrorMessage = "book.bookAuthor - must not be blank && book.bookId - must not be null";
        //mocking this behaviour " libraryEventProducer.sendLibraryEventApproach2(libraryEvent);"
        //doNothing().when(libraryEventProducer).sendLibraryEventApproach2(isA(LibraryEvent.class)); // was doing it when Approach2 method was returning void
        when(libraryEventProducer.sendLibraryEventApproach2(isA(LibraryEvent.class))).thenReturn(null);

        mockMvc.perform(post("/v1/libraryevent")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status()
                .is4xxClientError())
                .andExpect(content().string(expectedErrorMessage));

    }

    @Test
    void putLibraryEvent() throws JsonProcessingException {
        Book book = Book.builder()
                .bookId(123).bookAuthor("Andrew")
                .bookName("House of Dragons")
                .build();
        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(111)
                .book(book).build();

        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", MediaType.APPLICATION_JSON.toString());
        HttpEntity<LibraryEvent> request = new HttpEntity<>(libraryEvent,headers);

        ResponseEntity<LibraryEvent> responseEntity = restTemplate.exchange("/v1/libraryevent", HttpMethod.PUT, request, LibraryEvent.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        ConsumerRecords<Integer, String> consumerRecords = KafkaTestUtils.getRecords(consumer);
        assert consumerRecords.count() == 2;

        consumerRecords.forEach(record-> {
            if(record.key()!=null){
                String expectedRecord = "{\"libraryEventId\":123,\"libraryEventType\":\"UPDATE\",\"book\":{\"bookId\":111,\"bookName\":\"ouse of Dragons\",\"bookAuthor\":\"Andrew\"}}";
                String value = record.value();
                assertEquals(expectedRecord, value);
            }
        });

    }
}
