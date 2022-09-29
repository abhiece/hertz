package com.hertz.service;

import com.hertz.exception.LibraryException;
import com.hertz.model.Book;
import com.hertz.model.Category;
import com.hertz.model.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.hertz.model.Library.LIBRARY_SINGLETON_INSTANCE;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LibraryServiceTest {

    LibraryService service = new LibraryService();
    private Set<Member> memberSet;
    private Book book1;
    private Book book2;

    @BeforeAll
    public void setUp() {
        log.info("LibraryApp application started");
        log.info("{} Library Members added", addMembersWithEmptyListOfLoanedBooks());
    }

    @Test
    @Order(1)
    void when2BooksAddedSizeofLibraryBooks() {
        log.info("{} Sample books added", addSampleBooks("Title1", "Title2"));
        Set<Book> bookSet = LIBRARY_SINGLETON_INSTANCE.getBookSet();
        int actualSize = bookSet.size();
        assertEquals(2, actualSize);
        assertTrue(bookSet.contains(book1));
        assertTrue(bookSet.contains(book2));
    }

    @Test
    void when2BooksAddedAndTryingToAddSameBookThrowsException() {
        log.info("{} Sample books added", addSampleBooks("Title3", "Title4"));
        List<Book> books = new ArrayList<>();
        books.add(book1);

        LibraryException libraryException = Assertions.assertThrows(LibraryException.class, () -> service.addBooksService(books));
        assertEquals(" Book(s) are already there in the library.", libraryException.getMessage());
    }

    @Test
    void WhenBookRemovedSuccessfully() {
        log.info("{} Sample books added", addSampleBooks("Title5", "Title6"));
        List<Book> books = new ArrayList<>();
        books.add(book1);
        service.removeBooksService(books);

        Set<Book> bookSet = LIBRARY_SINGLETON_INSTANCE.getBookSet();
        assertFalse(bookSet.contains(book1));
    }

    @Test
    void whenMoreThan3BooksLoanByAMemberThrowException() {
        log.info("{} Library Members added", addMembersWithEmptyListOfLoanedBooks());

        List<String> bookTitles = new ArrayList<>();
        bookTitles.add("title1");
        bookTitles.add("title2");
        bookTitles.add("title3");
        bookTitles.add("title4");
        LibraryException libraryException = Assertions.assertThrows(LibraryException.class, () -> service.loanBooksService("John", bookTitles));
        assertEquals("More than 3 Books are not given on loan!", libraryException.getMessage());
    }

    @Test
    void whenAnOutstandingBookLoanByAMemberThrowException() {
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

        LibraryException libraryException = Assertions.assertThrows(LibraryException.class, () -> service.loanBooksService("John", bookTitles));
        assertEquals("You have an outstanding loaned book(s). Please return before requesting another loan of book!", libraryException.getMessage());
    }

    @Test
    void whenBookNotFoundThrowException() {
        List<String> bookTitles = new ArrayList<>();
        bookTitles.add("title4");

        LibraryException libraryException = Assertions.assertThrows(LibraryException.class, () -> service.loanBooksService("John", bookTitles));
        assertEquals("[title4] Book(s) Not found", libraryException.getMessage());
    }

    @Test
    void whenBookFoundLoanTheBookAndCheckLibraryStatus() {
        log.info("{} Sample books added", addSampleBooks("Title7", "Title8"));
        List<String> bookTitles = new ArrayList<>();
        bookTitles.add("Title7");

        List<Book> loanedBooks = service.loanBooksService("Harry", bookTitles);
        Book book = loanedBooks.get(0);
        assertEquals("Title7", book.getTitle());
        assertEquals("Author1", book.getAuthor());
        assertEquals(2, book.getCategories().size());
        assertEquals("poetry", book.getCategories().get(0).getName());

        Member member = LIBRARY_SINGLETON_INSTANCE.getMemberSet()
                .stream()
                .filter(x -> x.getName().equals("Harry"))
                .collect(Collectors.toList()).get(0);
        assertEquals("Harry", member.getName());
        assertEquals("Title7", member.getListOfBooksLoaned().get(0).getTitle());

    }

    @Test
    void whenUnknownBookIsReturnedThrowException() {
        log.info("{} Sample books added", addSampleBooks("Title5", "Title6"));
        List<String> bookTitles1 = new ArrayList<>();
        bookTitles1.add("Title5");
        List<String> bookTitles2 = new ArrayList<>();
        bookTitles2.add("Title6");

        service.loanBooksService("John", bookTitles1);
        LibraryException libraryException = assertThrows(LibraryException.class, () -> service.returnBooksService("John", bookTitles2));

        assertEquals("[Title6] Returned Book(s) are not matching!", libraryException.getMessage());
    }

    @Test
    void whenBookIsReturnedCheckLibraryAndMemberStatus() {
        log.info("{} Sample books added", addSampleBooks("Title9", "Title10"));
        List<String> bookTitles1 = new ArrayList<>();
        bookTitles1.add("Title9");
        bookTitles1.add("Title10");
        List<String> bookTitles2 = new ArrayList<>();
        bookTitles2.add("Title9");

        service.loanBooksService("Larry", bookTitles1);
        List<Book> returnedBooks = service.returnBooksService("Larry", bookTitles2);
        assertEquals("Title9", returnedBooks.get(0).getTitle());

        Member member = LIBRARY_SINGLETON_INSTANCE.getMemberSet()
                .stream()
                .filter(x -> x.getName().equals("Larry"))
                .collect(Collectors.toList()).get(0);
        assertEquals(1, member.getListOfBooksLoaned().size());
        assertEquals("Title10", member.getListOfBooksLoaned().get(0).getTitle());

    }

    private int addSampleBooks(String title1, String title2) {
        List<Category> categories1 = new ArrayList<>();
        categories1.add(Category.POETRY);
        categories1.add(Category.MYSTERY);
        book1 = Book.builder()
                .title(title1)
                .author("Author1")
                .categories(categories1)
                .build();

        List<Category> categories2 = new ArrayList<>();
        categories2.add(Category.THRILLER);
        categories2.add(Category.SCIENCE_FICTION);
        book2 = Book.builder()
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
        memberSet = new HashSet<>();
        memberSet.add(member1);
        memberSet.add(member2);
        memberSet.add(member3);
        LIBRARY_SINGLETON_INSTANCE.setMemberSet(memberSet);

        return memberSet.size();
    }

}