package com.hertz.service;

import com.hertz.exception.LibraryException;
import com.hertz.model.Book;
import com.hertz.model.Category;
import com.hertz.model.Member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static com.hertz.model.Library.LIBRARY_SINGLETON_INSTANCE;
import static org.junit.jupiter.api.Assertions.*;

class LibraryServiceTest {

    LibraryService service = new LibraryService();
    Set<Member> memberSet;
    Book book1;
    Book book2;

    @BeforeEach
    void setUp() {
        addSampleBooks();
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
    void whenMoreThan3BooksLoanByAMemberThrowException() {
        setSampleMembers();

        List<String> bookTitles = new ArrayList<>();
        bookTitles.add("title1");
        bookTitles.add("title2");
        bookTitles.add("title3");
        bookTitles.add("title4");
        LibraryException libraryException = Assertions.assertThrows(LibraryException.class, () -> service.loanBooks("John", bookTitles));
        assertEquals("More than 3 Books are not given on loan!", libraryException.getMessage());
    }

    @Test
    void whenAnOutstandingBookLoanByAMemberThrowException() {
        setSampleMembers();

        List<String> bookTitles = new ArrayList<>();
        bookTitles.add("title2");

        Optional<Member> firstMember = memberSet.stream().findFirst();
        List<Category> categories1 = new ArrayList<>();
        categories1.add(Category.MYSTERY);
        book1 = Book.builder()
                .title("title1")
                .author("Author1")
                .categories(categories1)
                .build();
        List<Book> books = new ArrayList<>();
        books.add(book1);
        firstMember.get().setListOfBooksLoaned(books);

        LibraryException libraryException = Assertions.assertThrows(LibraryException.class, () -> service.loanBooks("John", bookTitles));
        assertEquals("You have an outstanding loaned book(s). Please return before requesting another loan of book!", libraryException.getMessage());
    }

    @Test
    void whenBookNotFoundThrowException() {
        setSampleMembers();

        List<String> bookTitles = new ArrayList<>();
        bookTitles.add("title4");

        LibraryException libraryException = Assertions.assertThrows(LibraryException.class, () -> service.loanBooks("John", bookTitles));
        assertEquals("[title4] Book(s) Not found", libraryException.getMessage());
    }

    @Test
    void whenBookFoundLoanTheBookAndCheckLibraryStatus() {
        setSampleMembers();

        List<String> bookTitles = new ArrayList<>();
        bookTitles.add("title1");

        List<Book> loanedBooks = service.loanBooks("John", bookTitles);
        Book book = loanedBooks.get(0);
        assertEquals("title1", book.getTitle());
        assertEquals("Author1", book.getAuthor());
        assertEquals(2, book.getCategories().size());
        assertEquals("mystery", book.getCategories().get(0).getName());

        Member member = LIBRARY_SINGLETON_INSTANCE.getMemberSet()
                .stream()
                .filter(x -> x.getName().equals("John"))
                .collect(Collectors.toList()).get(0);
        assertEquals("John", member.getName());
        assertEquals("title1", member.getListOfBooksLoaned().get(0).getTitle());

    }

    @Test
    void whenUnknownBookIsReturnedThrowException() {
        setSampleMembers();

        List<String> bookTitles1 = new ArrayList<>();
        bookTitles1.add("title1");
        List<String> bookTitles2 = new ArrayList<>();
        bookTitles2.add("title2");

        service.loanBooks("John", bookTitles1);
        LibraryException libraryException = assertThrows(LibraryException.class, () -> service.returnBooks("John", bookTitles2));

        assertEquals("[title2] Returned Book(s) are not matching!", libraryException.getMessage());
    }

    @Test
    void whenBookIsReturnedCheckLibraryAndMemberStatus() {
        setSampleMembers();

        List<String> bookTitles1 = new ArrayList<>();
        bookTitles1.add("title1");
        bookTitles1.add("title2");
        List<String> bookTitles2 = new ArrayList<>();
        bookTitles2.add("title1");

        service.loanBooks("John", bookTitles1);
        List<Book> returnedBooks = service.returnBooks("John", bookTitles2);
        assertEquals("title1", returnedBooks.get(0).getTitle());

        Member member = LIBRARY_SINGLETON_INSTANCE.getMemberSet()
                .stream()
                .filter(x -> x.getName().equals("John"))
                .collect(Collectors.toList()).get(0);
        assertEquals(1, member.getListOfBooksLoaned().size());
        assertEquals("title2", member.getListOfBooksLoaned().get(0).getTitle());

    }

    private void addSampleBooks() {
        List<Category> categories1 = new ArrayList<>();
        categories1.add(Category.MYSTERY);
        categories1.add(Category.SCIENCE_FICTION);
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

    private void setSampleMembers() {
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
        memberSet = new HashSet<>();
        memberSet.add(member1);
        memberSet.add(member2);
        memberSet.add(member3);
        LIBRARY_SINGLETON_INSTANCE.setMemberSet(memberSet);
    }

}