package com.booknest.BookNest.controller;

import com.booknest.BookNest.model.Book;
import com.booknest.BookNest.model.FavoriteBook;
import com.booknest.BookNest.service.FavoriteBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteBookController {
    @Autowired
    private FavoriteBookService favoriteBookService;

    @PostMapping("/{userId}/books/{bookId}")
    public ResponseEntity<FavoriteBook> addToFavorites(
            @PathVariable Long userId,
            @PathVariable Long bookId) {
        return ResponseEntity.ok(favoriteBookService.addToFavorites(userId, bookId));
    }

    @DeleteMapping("/{userId}/books/{bookId}")
    public ResponseEntity<Void> removeFromFavorites(
            @PathVariable Long userId,
            @PathVariable Long bookId) {
        favoriteBookService.removeFromFavorites(userId, bookId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/books")
    public ResponseEntity<List<Book>> getFavoriteBooks(@PathVariable Long userId) {
        return ResponseEntity.ok(favoriteBookService.getFavoriteBooks(userId));
    }

    @GetMapping("/{userId}/recommendations")
    public ResponseEntity<List<Book>> getRecommendedBooks(@PathVariable Long userId) {
        return ResponseEntity.ok(favoriteBookService.getRecommendedBooks(userId));
    }
} 