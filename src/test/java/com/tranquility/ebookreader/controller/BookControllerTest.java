package com.tranquility.ebookreader.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranquility.ebookreader.config.TestConfig;
import com.tranquility.ebookreader.dto.BookInfoDto;
import com.tranquility.ebookreader.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(BookController.class)
@Import(TestConfig.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookInfoDto mockBookInfoDto;

//    @BeforeEach
//    void setUp() {
//        mockBookDto = new BookDto();
//        mockBookDto.setId("1");
//        mockBookDto.setName("Test PG");
//        mockBookDto.setLocation("Test Location");
//        mockBookDto.setOwnerName("Test Owner");
//        mockBookDto.setTypeOfStay("Single");
//        mockBookDto.setRoomSize(1);
//        mockBookDto.setSuspended(false);
//    }
//
//    @Test
//    void testAddBook() throws Exception {
//        BookRequest pgRequest = new BookRequest();
//        pgRequest.setTitle("Test PG");
//        pgRequest.setLocation("Test Location");
//        pgRequest.setOwnerName("Test Owner");
//        pgRequest.setTypeOfStay("Single");
//        pgRequest.setRoomSize(1);
//
//        when(bookService.addBook(any(BookRequest.class))).thenReturn(mockBookDto);
//
//        mockMvc.perform(post("/api/book")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(pgRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name").value("Test PG"))
//                .andExpect(jsonPath("$.location").value("Test Location"));
//    }
//
//    @Test
//    void testUpdateBook() throws Exception {
//        BookRequest pgRequest = new BookRequest();
//        pgRequest.setTitle("Updated PG");
//        pgRequest.setLocation("Updated Location");
//        pgRequest.setOwnerName("Updated Owner");
//        pgRequest.setTypeOfStay("Double");
//        pgRequest.setRoomSize(2);
//
//        BookDto updatedBookDto = new BookDto();
//        updatedBookDto.setId("1");
//        updatedBookDto.setName("Updated PG");
//        updatedBookDto.setLocation("Updated Location");
//        updatedBookDto.setOwnerName("Updated Owner");
//        updatedBookDto.setTypeOfStay("Double");
//        updatedBookDto.setRoomSize(2);
//        updatedBookDto.setSuspended(false);
//
//        when(bookService.updateBook(anyString(), any(BookRequest.class))).thenReturn(updatedBookDto);
//
//        mockMvc.perform(put("/api/pg/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(pgRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name").value("Updated PG"))
//                .andExpect(jsonPath("$.location").value("Updated Location"));
//    }
//
//    @Test
//    void testSearchBooks() throws Exception {
//        List<BookDto> bookList = Arrays.asList(mockBookDto);
//        when(bookService.searchBooks(anyString(), anyString())).thenReturn(bookList);
//
//        mockMvc.perform(get("/api/book/search")
//                        .param("title", "Test")
//                        .param("genre", "Location"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].name").value("Test PG"))
//                .andExpect(jsonPath("$[0].location").value("Test Location"));
//    }
//
//    @Test
//    void testGetBookInfo() throws Exception {
//        when(bookService.getBookInfo(anyString())).thenReturn(mockBookDto);
//
//        mockMvc.perform(get("/api/pg/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name").value("Test PG"))
//                .andExpect(jsonPath("$.location").value("Test Location"));
//    }
}

