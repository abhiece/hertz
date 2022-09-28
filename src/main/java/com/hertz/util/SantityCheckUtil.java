package com.hertz.util;

import com.hertz.exception.LibraryException;
import com.hertz.model.Book;
import com.hertz.model.Member;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.hertz.model.Library.LIBRARY_SINGLETON_INSTANCE;

public class SantityCheckUtil {

    private SantityCheckUtil() {
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
            throw new LibraryException("Book(s) title is empty!");
        }
    }

    public static Member validateMember(String name) {
        Member member;
        try {
            member = LIBRARY_SINGLETON_INSTANCE.getMemberSet()
                    .stream()
                    .filter(x -> x.getName().equals(name))
                    .collect(Collectors.toList()).get(0);
        } catch (IndexOutOfBoundsException ex) {
            throw new LibraryException("You are not a member of this Library. Please register. Thank you!");
        }
        return member;
    }
}
