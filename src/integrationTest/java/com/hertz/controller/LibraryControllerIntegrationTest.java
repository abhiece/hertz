package com.hertz.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hertz.LibraryTestApplication;
import com.hertz.model.Book;
import com.hertz.model.Category;
import com.hertz.model.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import java.util.*;

import static com.hertz.model.Library.LIBRARY_SINGLETON_INSTANCE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = LibraryTestApplication.class)
@WebAppConfiguration
@Slf4j
@TestInstance(Lifecycle.PER_CLASS)
class LibraryControllerIntegrationTest {

    @Resource
    protected WebApplicationContext webApplicationContext;

    protected MockMvc mockMvc;

    @BeforeAll
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
        log.info("LibraryApp application started");
        log.info("{} Library Members added", addMembersWithEmptyListOfLoanedBooks());
    }

    @Test
    void testAddBooksSuccessfully() throws Exception {
        MvcResult mvcResult = mockMvc
                .perform(post("/library/addBooks")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("[{\n" +
                                "\t\t\"title\": \"Title1\",\n" +
                                "\t\t\"author\": \"Author1\",\n" +
                                "\t\t\"categories\": [\n" +
                                "\t\t\t\"POETRY\",\n" +
                                "\t\t\t\"MYSTERY\"\n" +
                                "\t\t]\n" +
                                "\n" +
                                "\t}, {\n" +
                                "\t\t\"title\": \"Title2\",\n" +
                                "\t\t\"author\": \"Author2\",\n" +
                                "\t\t\"categories\": [\n" +
                                "\t\t\t\"THRILLER\",\n" +
                                "\t\t\t\"SCIENCE_FICTION\"\n" +
                                "\t\t]\n" +
                                "\n" +
                                "\t}]\n")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print()).andExpect(status().is(201)).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertEquals("2 no. of books Added to Library!", contentAsString);
        Set<Book> bookSet = LIBRARY_SINGLETON_INSTANCE.getBookSet();
        assertEquals(2, bookSet.size());
        assertTrue(bookSet.stream().anyMatch(x -> x.getTitle().equals("Title1")));
        assertTrue(bookSet.stream().anyMatch(x -> x.getTitle().equals("Title2")));
    }

    @Test
    void testAddSameBookThrowsException() throws Exception {
        log.info("{} Sample books added", addSampleBooks("Title9", "Title10"));
        MvcResult mvc2ndResult = mockMvc
                .perform(post("/library/addBooks")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("[{\n" +
                                "\t\t\"title\": \"Title9\",\n" +
                                "\t\t\"author\": \"Author1\",\n" +
                                "\t\t\"categories\": [\n" +
                                "\t\t\t\"POETRY\",\n" +
                                "\t\t\t\"MYSTERY\"\n" +
                                "\t\t]\n" +
                                "\n" +
                                "\t}]\n")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print()).andExpect(status().is(400)).andReturn();
        String contentAsString = mvc2ndResult.getResponse().getContentAsString();
        assertEquals(" Book(s) are already there in the library.", contentAsString);
    }

    @Test
    void testAddBooksWithoutAllDetailsThrowsException() throws Exception {
        MvcResult mvcResult = mockMvc
                .perform(post("/library/addBooks")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("[{\n" +
                                "\t\t\"title\": \"Title1\",\n" +
                                "\t\t\"categories\": [\n" +
                                "\t\t\t\"POETRY\",\n" +
                                "\t\t\t\"MYSTERY\"\n" +
                                "\t\t]\n" +
                                "\n" +
                                "\t}]\n")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print()).andExpect(status().is(400)).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertEquals("[Book(title=Title1, author=null, categories=[POETRY, MYSTERY])] Book(s) is not valid!", contentAsString);
    }

    @Test
    void testRemoveBooksSuccessfully() throws Exception {
        log.info("{} Sample books added", addSampleBooks("Title3", "Title4"));
        MvcResult mvcResult = mockMvc
                .perform(delete("/library/removeBooks")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("[{\n" +
                                "\t\t\"title\": \"Title3\",\n" +
                                "\t\t\"author\": \"Author1\",\n" +
                                "\t\t\"categories\": [\n" +
                                "\t\t\t\"POETRY\",\n" +
                                "\t\t\t\"MYSTERY\"\n" +
                                "\t\t]\n" +
                                "\n" +
                                "\t}]\n")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print()).andExpect(status().is(200)).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertEquals("1 no. of books removed from Library!", contentAsString);
    }

    @Test
    void testRemoveBookWhichIsNotThereThrowsException() throws Exception {
        MvcResult mvcResult = mockMvc
                .perform(delete("/library/removeBooks")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("[{\n" +
                                "\t\t\"title\": \"Title5\",\n" +
                                "\t\t\"author\": \"Author1\",\n" +
                                "\t\t\"categories\": [\n" +
                                "\t\t\t\"POETRY\",\n" +
                                "\t\t\t\"MYSTERY\"\n" +
                                "\t\t]\n" +
                                "\n" +
                                "\t}]\n")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print()).andExpect(status().is(400)).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertEquals(" Book(s) doesn't belong to library.", contentAsString);
    }

    @Test
    public void testLoanBookSuccessfully() throws Exception {
        log.info("{} Sample books added", addSampleBooks("Title5", "Title6"));
        MvcResult mvcResult = mockMvc
                .perform(get("/library/loanBooks?name=Harry&title=Title5&title=Title6")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print()).andExpect(status().is(200)).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        String contentAsString = response.getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        List<Book> books = objectMapper.readValue(contentAsString,new TypeReference<>(){});
        assertEquals(2, books.size());
        assertTrue(books.stream().anyMatch(x -> x.getTitle().equals("Title5")));
        assertTrue(books.stream().anyMatch(x -> x.getTitle().equals("Title6")));
    }

    @Test
    public void testLoanBookWhichIsNotThereThrowsException() throws Exception {
        MvcResult mvcResult = mockMvc
                .perform(get("/library/loanBooks?name=John&title=Title15")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print()).andExpect(status().is(400)).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertEquals("[Title15] Book(s) Not found", contentAsString);
    }

    @Test
    public void testLoanBooksWhenHaveAnOutstandingBookThrowsException() throws Exception {
        log.info("{} Sample books added", addSampleBooks("Title7", "Title8"));
        MvcResult mvcResult = mockMvc
                .perform(get("/library/loanBooks?name=Larry&title=Title7&title=Title8")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print()).andExpect(status().is(200)).andReturn();

        MvcResult mvcResult2 = mockMvc
                .perform(get("/library/loanBooks?name=John&title=Title5")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print()).andExpect(status().is(400)).andReturn();
        String contentAsString2 = mvcResult2.getResponse().getContentAsString();
        assertEquals("You have an outstanding loaned book(s). Please return before requesting another loan of book!", contentAsString2);
    }

    @Test
    void testReturnBookSuccessfully() throws Exception {
        log.info("{} Sample books added", addSampleBooks("Title11", "Title12"));
        mockMvc
                .perform(get("/library/loanBooks?name=John&title=Title11&title=Title12")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print()).andExpect(status().is(200)).andReturn();
        MvcResult mvcResult = mockMvc
                .perform(put("/library/returnBooks?name=John&title=Title11")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print()).andExpect(status().is(200)).andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        String contentAsString = response.getContentAsString();
        assertEquals("Thank you for returning book(s) ==>>[Title11]", contentAsString);
    }

    private int addSampleBooks(String title1, String title2) {
        List<Category> categories1 = new ArrayList<>();
        categories1.add(Category.POETRY);
        categories1.add(Category.MYSTERY);
        Book book1 = Book.builder()
                .title(title1)
                .author("Author1")
                .categories(categories1)
                .build();

        List<Category> categories2 = new ArrayList<>();
        categories2.add(Category.THRILLER);
        categories2.add(Category.SCIENCE_FICTION);
        Book book2 = Book.builder()
                .title(title2)
                .author("Author2")
                .categories(categories2)
                .build();

        List<Book> books = new ArrayList<>();
        books.add(book1);
        books.add(book2);
        LIBRARY_SINGLETON_INSTANCE.addBooks(books);

        return books.size();
    }

    private int addMembersWithEmptyListOfLoanedBooks() {
        Member member1 = Member.builder()
                .name("John")
                .listOfBooksLoaned(Collections.emptyList())
                .build();
        Member member2 = Member.builder()
                .name("Harry")
                .listOfBooksLoaned(Collections.emptyList())
                .build();
        Member member3 = Member.builder()
                .name("Larry")
                .listOfBooksLoaned(Collections.emptyList())
                .build();
        Set<Member> memberSet = new HashSet<>();
        memberSet.add(member1);
        memberSet.add(member2);
        memberSet.add(member3);
        LIBRARY_SINGLETON_INSTANCE.setMemberSet(memberSet);

        return memberSet.size();
    }
}