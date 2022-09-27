package com.hertz.util;

import com.hertz.exception.LibraryException;
import com.hertz.model.Book;
import com.hertz.model.Member;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;

public class SanityCheckUtil {

    private SanityCheckUtil(){
     throw new UnsupportedOperationException("Util class shouldn't be instantiated!");
    }

    public static boolean validateBooks(List<Book> books) {
        Optional<List<Book>> bookList = Optional.ofNullable(books);
        return bookList.filter(list -> list
                .stream().noneMatch(x -> Optional.ofNullable(x.getTitle()).isEmpty())).isPresent()
                && bookList.filter(list -> list
                .stream().noneMatch(x -> Optional.ofNullable(x.getAuthor()).isEmpty())).isPresent()
                && bookList.filter(list -> list
                .stream().noneMatch(x -> CollectionUtils.isEmpty(x.getCategories()))).isPresent();

    }

    public static void validateMemberAndBooks(Member member, List<String> bookTitles) {
        if (ObjectUtils.isEmpty(member)) {
            throw new LibraryException("Member doesn't exist!");
        }

        if (CollectionUtils.isEmpty(bookTitles)) {
            throw new LibraryException("Book(s) title is  not valid!");
        }
    }
}
