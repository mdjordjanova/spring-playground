package com.demo.controller;

import com.demo.model.Review;
import com.demo.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PutMapping(value = "/reviews")
    public ResponseEntity<Review> add(@RequestBody Review review) {
        reviewService.save(review);
        return new ResponseEntity<>(review, HttpStatus.CREATED);
    }

    @GetMapping(value = "/reviews")
    public ResponseEntity<List<Review>> getAll() {
        List<Review> reviewsList = reviewService.findAll();
        return new ResponseEntity<>(reviewsList, HttpStatus.OK);
    }
}
