package com.hertz;


import com.hertz.model.Library;
import com.hertz.model.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


@SpringBootApplication
@Slf4j
public class LibraryApp {
    public static ApplicationContext applicationContext;


    public static void main(String[] args) {
        try {
            applicationContext = SpringApplication.run(LibraryApp.class, args);
            log.info("LibraryApp application started");
            addMembersWithEmptyListOfLoanedBooks();
            log.info("{} Library Members added", addMembersWithEmptyListOfLoanedBooks());

        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
        }
    }

    private static int addMembersWithEmptyListOfLoanedBooks(){
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

        return memberSet.size();
    }
}
