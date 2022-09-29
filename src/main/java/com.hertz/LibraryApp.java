package com.hertz;


import com.hertz.model.Book;
import com.hertz.model.Category;
import com.hertz.model.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.*;

import static com.hertz.model.Library.LIBRARY_SINGLETON_INSTANCE;


@SpringBootApplication
@Slf4j
public class LibraryApp {
    public static ApplicationContext applicationContext;

    public static void main(String[] args) {
        try {
            applicationContext = SpringApplication.run(LibraryApp.class, args);
            log.info("LibraryApp application started");
            log.info("{} Library Members added", addMembersWithEmptyListOfLoanedBooks());
            log.info("{} Sample books added", addSampleBooks());

        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
        }
    }

    private static int addSampleBooks() {
        List<Category> categories1 = new ArrayList<>();
        categories1.add(Category.MYSTERY);
        categories1.add(Category.SCIENCE_FICTION);
        Book book1 = Book.builder()
                .title("Title1")
                .author("Author1")
                .categories(categories1)
                .build();

        List<Category> categories2 = new ArrayList<>();
        categories2.add(Category.POETRY);
        categories2.add(Category.THRILLER);
        Book book2 = Book.builder()
                .title("Title2")
                .author("Author2")
                .categories(categories2)
                .build();

        List<Book> books = new ArrayList<>();
        books.add(book1);
        books.add(book2);
        LIBRARY_SINGLETON_INSTANCE.addBooks(books);

        return books.size();
    }

    private static int addMembersWithEmptyListOfLoanedBooks() {
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
