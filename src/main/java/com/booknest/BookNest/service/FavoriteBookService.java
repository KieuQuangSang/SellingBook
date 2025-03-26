package com.booknest.BookNest.service;

import com.booknest.BookNest.model.Book;
import com.booknest.BookNest.model.FavoriteBook;
import com.booknest.BookNest.model.User;
import com.booknest.BookNest.repository.BookRepository;
import com.booknest.BookNest.repository.FavoriteBookRepository;
import com.booknest.BookNest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FavoriteBookService {
    @Autowired
    private FavoriteBookRepository favoriteBookRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    public FavoriteBook addToFavorites(Long userId, Long bookId) {
        Optional<FavoriteBook> existingFavorite = favoriteBookRepository.findByUserIdAndBookId(userId, bookId);
        if (existingFavorite.isPresent()) {
            return existingFavorite.get();
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        FavoriteBook favoriteBook = new FavoriteBook();
        favoriteBook.setUser(user);
        favoriteBook.setBook(book);
        favoriteBook.setCreatedAt(LocalDateTime.now());

        return favoriteBookRepository.save(favoriteBook);
    }

    public void removeFromFavorites(Long userId, Long bookId) {
        FavoriteBook favoriteBook = favoriteBookRepository.findByUserIdAndBookId(userId, bookId)
                .orElseThrow(() -> new RuntimeException("Favorite book not found"));
        favoriteBookRepository.delete(favoriteBook);
    }

    public List<Book> getFavoriteBooks(Long userId) {
        return favoriteBookRepository.findByUserId(userId)
                .stream()
                .map(FavoriteBook::getBook)
                .collect(Collectors.toList());
    }

    public List<Book> getRecommendedBooks(Long userId) {
        // Lấy danh sách thể loại yêu thích
        List<Long> favoriteCategories = favoriteBookRepository.findFavoriteCategoriesByUserId(userId);
        
        // Lấy danh sách tác giả yêu thích
        List<String> favoriteAuthors = favoriteBookRepository.findFavoriteAuthorsByUserId(userId);
        
        // Lấy danh sách sách đã yêu thích
        Set<Long> favoriteBookIds = favoriteBookRepository.findByUserId(userId)
                .stream()
                .map(fb -> fb.getBook().getId())
                .collect(Collectors.toSet());

        // Lấy tất cả sách
        List<Book> allBooks = bookRepository.findAll();
        
        // Tính điểm cho mỗi cuốn sách
        return allBooks.stream()
                .filter(book -> !favoriteBookIds.contains(book.getId())) // Loại bỏ sách đã yêu thích
                .map(book -> {
                    int score = 0;
                    
                    // Cộng điểm nếu sách cùng thể loại
                    if (favoriteCategories.contains(book.getCategories().getId())) {
                        score += 2;
                    }
                    
                    // Cộng điểm nếu sách cùng tác giả
                    if (favoriteAuthors.contains(book.getAuthor())) {
                        score += 3;
                    }
                    
                    return Map.entry(book, score);
                })
                .filter(entry -> entry.getValue() > 0) // Chỉ giữ lại sách có điểm > 0
                .sorted(Map.Entry.<Book, Integer>comparingByValue().reversed()) // Sắp xếp theo điểm giảm dần
                .limit(10) // Giới hạn 10 cuốn sách
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
} 