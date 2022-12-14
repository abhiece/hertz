package com.hertz.model;

import com.hertz.exception.LibraryException;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public enum Library {
    LIBRARY_SINGLETON_INSTANCE;

    //- there is only 1 copy of each book
    private final Set<Book> bookSet;
    private Set<Member> memberSet;


    Library() {
        bookSet = ConcurrentHashMap.newKeySet();
        memberSet = ConcurrentHashMap.newKeySet();
    }

    public int addBooks(List<Book> books) {
        if (!bookSet.containsAll(books)) {
            bookSet.addAll(books);
        } else {
            throw new LibraryException(" Book(s) are already there in the library.");
        }

        return books.size();
    }

    public int removeBooks(List<Book> books) {
        if (bookSet.containsAll(books)) {
            bookSet.removeAll(books);
        } else {
            throw new LibraryException(" Book(s) doesn't belong to library.");
        }

        return books.size();
    }

    public Set<Member> getMemberSet() {
        return memberSet;
    }

    public void setMemberSet(Set<Member> memberSet) {
        this.memberSet = memberSet;
    }

    public Set<Book> getBookSet() {
        return bookSet;
    }
}
