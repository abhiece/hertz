package com.hertz.util;

import com.hertz.exception.LibraryException;
import com.hertz.model.Book;
import com.hertz.model.Category;
import com.hertz.model.Library;
import com.hertz.model.Member;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

import static com.hertz.util.SanityCheckUtil.*;
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
        LibraryException libraryException = Assertions.assertThrows(LibraryException.class, () -> validateMemberAndBooks(member, null));

        assertEquals("Book(s) title is empty!", libraryException.getMessage());

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

    @Test
    void whenANonMemberThrowException() {
        setMembers();
        LibraryException libraryException = Assertions.assertThrows(LibraryException.class, () -> validateMember("Jane"));
        assertEquals("You are not a member of this Library. Please register. Thank you!", libraryException.getMessage());
    }

    @Test
    void whenAMemberPass() {
        setMembers();
        Member member = validateMember("Harry");
        assertEquals("Harry", member.getName());
    }

    private void setMembers() {
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
        Library.LIBRARY_SINGLETON_INSTANCE.setMemberSet(memberSet);
    }

}