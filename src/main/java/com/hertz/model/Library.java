package com.hertz.model;

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
    }

    public int addBooks(List<Book> books) {
        bookSet.addAll(books);

        return bookSet.size();
    }

    public int removeBooks(List<Book> books) {
        if (!bookSet.containsAll(books)) {
            return 0;
        } else if (bookSet.removeAll(books)) {
            return bookSet.size();
        }
        return 0;
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
