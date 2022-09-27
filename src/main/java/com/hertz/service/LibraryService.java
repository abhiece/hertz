package com.hertz.service;

import com.hertz.exception.LibraryException;
import com.hertz.model.Book;
import com.hertz.model.Member;
import com.hertz.util.SanityCheckUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.hertz.model.Library.LIBRARY_SINGLETON_INSTANCE;

@Service
public class LibraryService {

    public void addBooks(List<Book> books) {
        if (SanityCheckUtil.validateBooks(books)) {
            LIBRARY_SINGLETON_INSTANCE.addBooks(books);
        }
    }

    public void removeBooks(List<Book> books) {
        if (SanityCheckUtil.validateBooks(books)) {
            LIBRARY_SINGLETON_INSTANCE.removeBooks(books);
        }
    }

    public List<Book> loanBooks(String name, List<String> bookTitles) {
        List<Book> filteredBookList = new ArrayList<>();
        Member member = LIBRARY_SINGLETON_INSTANCE.getMemberSet()
                .stream()
                .filter(x -> x.getName().equals(name))
                .collect(Collectors.toList()).get(0);

        SanityCheckUtil.validateMemberAndBooks(member, bookTitles);
        //- maximum number of books loaned at any time is 3 per user
        if (bookTitles.size() > 3) {
            throw new LibraryException("More than 3 Books are not given on loan!");
        }

        List<Book> listOfBooksLoaned = member.getListOfBooksLoaned();
        //if a member has any outstanding loaned books, they cannot loan any more until all books returned.
        if (!CollectionUtils.isEmpty(listOfBooksLoaned)) {
            throw new LibraryException("You have an outstanding loaned book(s)!");
        } else {
            filteredBookList.addAll(LIBRARY_SINGLETON_INSTANCE.getBookSet()
                    .stream()
                    .filter(book -> bookTitles.contains(book.getTitle()))
                    .collect(Collectors.toList()));
            if (filteredBookList.size() > 0) {
                member.setListOfBooksLoaned(filteredBookList);
                LIBRARY_SINGLETON_INSTANCE.getBookSet().removeAll(filteredBookList);
            }
        }

        return filteredBookList;
    }

    public void returnBooks(String name, List<String> bookTitles) {
        Member member = LIBRARY_SINGLETON_INSTANCE.getMemberSet()
                .stream()
                .filter(x -> x.getName().equals(name))
                .collect(Collectors.toList()).get(0);

        SanityCheckUtil.validateMemberAndBooks(member, bookTitles);

        List<Book> filteredBookList = new ArrayList<>();
        filteredBookList.addAll(LIBRARY_SINGLETON_INSTANCE.getBookSet()
                .stream()
                .filter(book -> bookTitles.contains(book.getTitle()))
                .collect(Collectors.toList()));

        if (filteredBookList.size() > 0) {
            member.getListOfBooksLoaned().removeAll(filteredBookList);
            LIBRARY_SINGLETON_INSTANCE.getBookSet().addAll(filteredBookList);
        }
    }


}
