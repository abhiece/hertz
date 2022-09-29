package com.hertz.controller;

import com.hertz.model.Book;
import com.hertz.service.LibraryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/library", produces = MediaType.APPLICATION_JSON_VALUE)
public class LibraryController {

    @Autowired
    LibraryService libraryService;

    /*
     * end point to add new books
     *
     */
    @PostMapping(path = "/addBooks", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addBooks(@RequestBody List<Book> books) {
        HttpStatus status = HttpStatus.CREATED;
        int numBooksAdded;
        String message = " no. of books Added to Library!";
        try {
            numBooksAdded = libraryService.addBooksService(books);
        } catch (Exception e) {
            String eMessage = e.getMessage();
            log.error(eMessage, e);
            message = eMessage;
            status = HttpStatus.BAD_REQUEST;
            return new ResponseEntity<>(message, status);
        }
        return new ResponseEntity<>(numBooksAdded + message, status);
    }

    /*
     * end point to removing books
     *
     */
    @DeleteMapping(path = "/removeBooks", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> removeBooks(@RequestBody List<Book> books) {
        HttpStatus status = HttpStatus.OK;
        int numBooksRemoved = 0;
        String message = " no. of books removed from Library!";
        try {
            numBooksRemoved = libraryService.removeBooksService(books);
        } catch (Exception e) {
            String eMessage = e.getMessage();
            log.error(eMessage, e);
            message = eMessage;
            status = HttpStatus.BAD_REQUEST;
            return new ResponseEntity<>(message, status);
        }
        return new ResponseEntity<>(numBooksRemoved + message, status);
    }

    /*
     * end point to allowing members to loan books
     *
     */
    @GetMapping(path = "/loanBooks")
    public ResponseEntity<Object> loanBooks(@RequestParam(value = "name") String name, @RequestParam("title") List<String> bookTitles) {
        HttpStatus status = HttpStatus.OK;
        List<Book> bookList = new ArrayList<>();
        try {
            bookList = libraryService.loanBooksService(name, bookTitles);
        } catch (Exception e) {
            String eMessage = e.getMessage();
            log.error(eMessage, e);
            status = HttpStatus.BAD_REQUEST;
            return new ResponseEntity<>(eMessage, status);
        }

        return new ResponseEntity<>(bookList, status);
    }

    /*
     * end point to allowing members to return books
     *
     */
    @PutMapping(path = "/returnBooks")
    public ResponseEntity<String> returnBooks(@RequestParam(value = "name") String name, @RequestParam("title") List<String> bookTitles) {
        HttpStatus status = HttpStatus.OK;
        String message = "Thank you for returning book(s) ==>>";
        try {
            libraryService.returnBooksService(name, bookTitles);
        } catch (Exception e) {
            String eMessage = e.getMessage();
            log.error(eMessage, e);
            message = eMessage;
            status = HttpStatus.BAD_REQUEST;
            return new ResponseEntity<>(message, status);
        }
        return new ResponseEntity<>(message + bookTitles.toString(), status);
    }
}
