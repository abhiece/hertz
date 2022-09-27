package com.hertz.service;

import com.hertz.model.Book;
import com.hertz.model.Category;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.hertz.model.Library.LIBRARY_SINGLETON_INSTANCE;
import static org.junit.jupiter.api.Assertions.*;

class LibraryServiceTest {

    LibraryService service = new LibraryService();
    Book book1;
    Book book2;

    @BeforeEach
    void setUp() {
        List<Category> categories1 = new ArrayList<>();
        categories1.add(Category.MYSTERY);
        book1 = Book.builder()
                .title("title1")
                .author("Author1")
                .categories(categories1)
                .build();

        List<Category> categories2 = new ArrayList<>();
        categories2.add(Category.POETRY);
        categories2.add(Category.THRILLER);
        book2 = Book.builder()
                .title("title2")
                .author("Author2")
                .categories(categories2)
                .build();

        List<Book> books = new ArrayList<>();
        books.add(book1);
        books.add(book2);

        service.addBooks(books);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void when2BooksAddedSizeofLibraryBooks() {
        Set<Book> bookSet = LIBRARY_SINGLETON_INSTANCE.getBookSet();
        int actualSize = bookSet.size();
        assertEquals(2, actualSize);
        assertTrue(bookSet.contains(book1));
        assertTrue(bookSet.contains(book2));
    }

    @Test
    void when2BooksAddedAndTryingToAddSameBookAgainSizeDoesntChange() {
        List<Book> books = new ArrayList<>();
        books.add(book1);

        service.addBooks(books);

        Set<Book> bookSet = LIBRARY_SINGLETON_INSTANCE.getBookSet();
        int actualSize = bookSet.size();
        assertEquals(2, actualSize);
        assertTrue(bookSet.contains(book1));
        assertTrue(bookSet.contains(book2));
    }

    @Test
    void When2BooksAddedAnd1BookRemovedCheckSizeOfLibrary() {
        List<Book> books = new ArrayList<>();
        books.add(book1);
        service.removeBooks(books);

        Set<Book> bookSet = LIBRARY_SINGLETON_INSTANCE.getBookSet();
        int actualSize = bookSet.size();
        assertEquals(1, actualSize);
        assertFalse(bookSet.contains(book1));
        assertTrue(bookSet.contains(book2));

    }

    @Test
    void When2BooksAddedAnd1BookRemoved2timesCheckSizeOfLibrary() {
        List<Book> books = new ArrayList<>();
        books.add(book1);
        service.removeBooks(books);
        service.removeBooks(books);

        Set<Book> bookSet = LIBRARY_SINGLETON_INSTANCE.getBookSet();
        int actualSize = bookSet.size();
        assertEquals(1, actualSize);
        assertFalse(bookSet.contains(book1));
        assertTrue(bookSet.contains(book2));

    }

    @Test
    void loanBooks() {
    }

    @Test
    void returnBooks() {
    }
}