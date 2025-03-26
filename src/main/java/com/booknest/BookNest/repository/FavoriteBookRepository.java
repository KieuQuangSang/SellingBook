package com.booknest.BookNest.repository;

import com.booknest.BookNest.model.FavoriteBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FavoriteBookRepository extends JpaRepository<FavoriteBook, Long> {
    List<FavoriteBook> findByUserId(Long userId);
    Optional<FavoriteBook> findByUserIdAndBookId(Long userId, Long bookId);
    
    @Query("SELECT DISTINCT f.book.categories.id FROM FavoriteBook f WHERE f.user.id = :userId")
    List<Long> findFavoriteCategoriesByUserId(@Param("userId") Long userId);
    
    @Query("SELECT DISTINCT f.book.author FROM FavoriteBook f WHERE f.user.id = :userId")
    List<String> findFavoriteAuthorsByUserId(@Param("userId") Long userId);
} 