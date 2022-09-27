package com.hertz.util;

import com.hertz.exception.LibraryException;
import com.hertz.model.Book;
import com.hertz.model.Category;
import com.hertz.model.Member;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.hertz.util.SanityCheckUtil.validateBooks;
import static com.hertz.util.SanityCheckUtil.validateMemberAndBooks;
import static org.junit.jupiter.api.Assertions.*;

class SanityCheckUtilTest {

    @Test
    void whenNullBooksValidationFailed() {
        boolean isValid = validateBooks(null);
        assertFalse(isValid);
    }

    @Test
    void whenEmptyBooksTitleValidationFailed() {
        List<Category> categories = new ArrayList<>();
        categories.add(Category.MYSTERY);
        Book book = Book.builder()
                .author("Author1")
                .categories(categories)
                .build();

        List<Book> books = new ArrayList<>();
        books.add(book);

        boolean isValid = validateBooks(books);
        assertFalse(isValid);
    }

    @Test
    void whenEmptyBooksAuthorValidationFailed() {
        List<Category> categories = new ArrayList<>();
        categories.add(Category.MYSTERY);
        Book book = Book.builder()
                .title("title1")
                .categories(categories)
                .build();

        List<Book> books = new ArrayList<>();
        books.add(book);

        boolean isValid = validateBooks(books);
        assertFalse(isValid);
    }

    @Test
    void whenEmptyBooksCategoryValidationFailed() {
        List<Category> categories = new ArrayList<>();
        Book book = Book.builder()
                .title("title1")
                .author("Author1")
                .categories(categories)
                .build();

        List<Book> books = new ArrayList<>();
        books.add(book);

        boolean isValid = validateBooks(books);
        assertFalse(isValid);
    }

    @Test
    void whenListOfBooksCategoryValidationPassed() {
        List<Category> categories = new ArrayList<>();
        categories.add(Category.MYSTERY);
        Book book = Book.builder()
                .title("title1")
                .author("Author1")
                .categories(categories)
                .build();

        List<Book> books = new ArrayList<>();
        books.add(book);

        boolean isValid = validateBooks(books);
        assertTrue(isValid);
    }

    @Test()
    void whenMemberNullValidateMemberAndBooksThrowException() {
        LibraryException thrown = Assertions.assertThrows(LibraryException.class, () -> validateMemberAndBooks(null, null));
        assertEquals("Member doesn't exist!", thrown.getMessage());

    }

    @Test
    void whenBooksNullValidateMemberAndBooksThrowException() {
        Member member = Member.builder()
                .name("John")
                .build();
        LibraryException thrown = Assertions.assertThrows(LibraryException.class, () -> validateMemberAndBooks(member, null));

        assertEquals("Book(s) title is  not valid!", thrown.getMessage());

    }

    @Test
    void whenMemberBooksCorrectValidateMemberAndBooksPassed() {
        Member member = Member.builder()
                .name("John")
                .build();

        List<String> bookTitles = new ArrayList<>();
        bookTitles.add("title1");
        validateMemberAndBooks(member, bookTitles);
        Assertions.assertDoesNotThrow(() -> validateMemberAndBooks(member, bookTitles));

    }
}