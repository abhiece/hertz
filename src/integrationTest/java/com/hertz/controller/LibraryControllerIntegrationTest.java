package com.hertz.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hertz.LibraryTestApplication;
import com.hertz.model.Book;
import com.hertz.model.Category;
import com.hertz.model.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = LibraryTestApplication.class)
@WebAppConfiguration
@Slf4j
class LibraryControllerIntegrationTest {

    @Resource
    protected WebApplicationContext webApplicationContext;

    protected MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
        addMembersWithEmptyListOfLoanedBooks();
        log.info("{} Library Members added", addMembersWithEmptyListOfLoanedBooks());

    }

    @Test
    void addBooksSuccessfully() throws Exception {
        mockMvc
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
        Set<Book> bookSet = LIBRARY_SINGLETON_INSTANCE.getBookSet();
        assertEquals(2, bookSet.size());
        assertTrue(bookSet.stream().anyMatch(x -> x.getTitle().equals("Title1")));
        assertTrue(bookSet.stream().anyMatch(x -> x.getTitle().equals("Title2")));
    }

    @Test
    void removeBooksSuccessfully() throws Exception {
        addSampleBooks();
        mockMvc
                .perform(delete("/library/removeBooks")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("[{\n" +
                                "\t\t\"title\": \"Title1\",\n" +
                                "\t\t\"author\": \"Author1\",\n" +
                                "\t\t\"categories\": [\n" +
                                "\t\t\t\"POETRY\",\n" +
                                "\t\t\t\"MYSTERY\"\n" +
                                "\t\t]\n" +
                                "\n" +
                                "\t}]\n")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print()).andExpect(status().is(200)).andReturn();
        Set<Book> bookSet = LIBRARY_SINGLETON_INSTANCE.getBookSet();
        assertEquals(1, bookSet.size());
        assertFalse(bookSet.stream().anyMatch(x -> x.getTitle().equals("Title1")));
        assertTrue(bookSet.stream().anyMatch(x -> x.getTitle().equals("Title2")));
    }

    @Test
    public void testLoanBooksSuccessfully() throws Exception {
        addSampleBooks();
        MvcResult result = mockMvc
                .perform(get("/library/loanBooks?name=John&title=Title1&title=Title2")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print()).andExpect(status().is(200)).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String contentAsString = response.getContentAsString();
        Set<Book> bookSet = LIBRARY_SINGLETON_INSTANCE.getBookSet();
        assertEquals(0, bookSet.size());
        ObjectMapper objectMapper = new ObjectMapper();
        List<Book> books = objectMapper.readValue(contentAsString,new TypeReference<List<Book>>(){});
        assertEquals(2, books.size());
        assertTrue(books.stream().anyMatch(x -> x.getTitle().equals("Title1")));
        assertTrue(books.stream().anyMatch(x -> x.getTitle().equals("Title2")));
    }

    @Test
    void returnBooks() throws Exception {
        addSampleBooks();
        mockMvc
                .perform(get("/library/loanBooks?name=John&title=Title1&title=Title2")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print()).andExpect(status().is(200)).andReturn();
        Set<Book> bookSetAfterLoan = LIBRARY_SINGLETON_INSTANCE.getBookSet();
        assertEquals(0, bookSetAfterLoan.size());
        MvcResult result = mockMvc
                .perform(put("/library/returnBooks?name=John&title=Title1")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print()).andExpect(status().is(200)).andReturn();
        Set<Book> bookSetAfterReturn = LIBRARY_SINGLETON_INSTANCE.getBookSet();
        assertEquals(1, bookSetAfterReturn.size());
        assertTrue(bookSetAfterReturn.stream().anyMatch(x -> x.getTitle().equals("Title1")));
        assertFalse(bookSetAfterReturn.stream().anyMatch(x -> x.getTitle().equals("Title2")));
    }

    private void addSampleBooks() {
        List<Book> books = getBooks();
        LIBRARY_SINGLETON_INSTANCE.addBooks(books);
    }

    private List<Book> getBooks() {
        List<Category> categories1 = new ArrayList<>();
        categories1.add(Category.POETRY);
        categories1.add(Category.MYSTERY);
        Book book1 = Book.builder()
                .title("Title1")
                .author("Author1")
                .categories(categories1)
                .build();

        List<Category> categories2 = new ArrayList<>();
        categories2.add(Category.THRILLER);
        categories2.add(Category.SCIENCE_FICTION);
        Book book2 = Book.builder()
                .title("Title2")
                .author("Author2")
                .categories(categories2)
                .build();

        List<Book> books = new ArrayList<>();
        books.add(book1);
        books.add(book2);
        return books;
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