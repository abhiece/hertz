package com.hertz.service;

import com.hertz.exception.LibraryException;
import com.hertz.model.Book;
import com.hertz.model.Member;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.hertz.model.Library.LIBRARY_SINGLETON_INSTANCE;
import static com.hertz.util.SanityCheckUtil.*;

@Service
public class LibraryService {

    public int addBooks(List<Book> books) {
        int numOfBooksAdded = 0;
        if (validateBooks(books)) {
            numOfBooksAdded = LIBRARY_SINGLETON_INSTANCE.addBooks(books);
        }
        return numOfBooksAdded;
    }

    public int removeBooks(List<Book> books) {
        int numOfBooksRemoved = 0;
        if (validateBooks(books)) {
            numOfBooksRemoved = LIBRARY_SINGLETON_INSTANCE.removeBooks(books);
        }
        return numOfBooksRemoved;
    }

    public List<Book> loanBooks(String name, List<String> bookTitles) {
        List<Book> loanedBookList = new ArrayList<>();
        Member member = validateMember(name);
        validateMemberAndBooks(member, bookTitles);
        //- maximum number of books loaned at any time is 3 per user
        if (bookTitles.size() > 3) {
            throw new LibraryException("More than 3 Books are not given on loan!");
        }

        List<Book> listOfBooksLoaned = member.getListOfBooksLoaned();
        //if a member has any outstanding loaned books, they cannot loan any more until all books returned.
        if (!CollectionUtils.isEmpty(listOfBooksLoaned)) {
            throw new LibraryException("You have an outstanding loaned book(s). Please return before requesting another loan of book!");
        } else {
            loanedBookList.addAll(LIBRARY_SINGLETON_INSTANCE.getBookSet()
                    .stream()
                    .filter(book -> bookTitles.contains(book.getTitle()))
                    .collect(Collectors.toList()));
            if (loanedBookList.size() > 0) {
                member.setListOfBooksLoaned(loanedBookList);
                LIBRARY_SINGLETON_INSTANCE.getBookSet().removeAll(loanedBookList);
            } else {
                throw new LibraryException(bookTitles + " Book(s) Not found");
            }
        }

        return loanedBookList;
    }

    public List<Book> returnBooks(String name, List<String> bookTitles) {
        Member member = validateMember(name);
        validateMemberAndBooks(member, bookTitles);

        List<Book> filteredLoanedBookList = member.getListOfBooksLoaned()
                .stream()
                .filter(x -> bookTitles.contains(x.getTitle()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(filteredLoanedBookList)) {
            throw new LibraryException(bookTitles + " Returned Book(s) are not matching!");
        } else {
            member.getListOfBooksLoaned().removeAll(filteredLoanedBookList);
            LIBRARY_SINGLETON_INSTANCE.getBookSet().addAll(filteredLoanedBookList);
        }

        return filteredLoanedBookList;
    }


}
