package com.hertz.controller;

import com.hertz.model.Book;
import com.hertz.service.LibraryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
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
        try {
            libraryService.addBooks(books);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            status = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(status);
    }

    /*
     * end point to removing books
     *
     */
    @DeleteMapping(path = "/removeBooks", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> removeBooks(@RequestBody List<Book> books) {
        HttpStatus status = HttpStatus.OK;
        try {
            libraryService.removeBooks(books);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            status = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(status);
    }

    /*
     * end point to allowing members to loan books
     *
     */
    @GetMapping(path = "/loanBooks")
    @ResponseBody
    public List<Book> loanBooks(@RequestParam(value = "name") String name, @RequestParam("title") List<String> bookTitles) {
        return libraryService.loanBooks(name, bookTitles);
    }

    /*
     * end point to allowing members to return books
     *
     */
    @PutMapping(path = "/returnBooks")
    public ResponseEntity<String> returnBooks(@RequestParam(value = "name") String name, @RequestParam("title") List<String> bookTitles) {
        HttpStatus status = HttpStatus.OK;
        try {
            libraryService.returnBooks(name, bookTitles);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            status = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(status);
    }
}
