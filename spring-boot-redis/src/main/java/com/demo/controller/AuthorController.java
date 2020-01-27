package com.demo.controller;

import com.demo.model.Author;
import com.demo.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @PutMapping(value = "/authors")
    public ResponseEntity<Author> add(@RequestBody Author author) {
        authorService.save(author);
        return new ResponseEntity<>(author, HttpStatus.CREATED);
    }

    @GetMapping(value = "/authors")
    public ResponseEntity<List<Author>> getAll() {
        List<Author> authorsList = authorService.findAll();
        return new ResponseEntity<>(authorsList, HttpStatus.OK);
    }
}
