package com.booknest.BookNest.repository;

import com.booknest.BookNest.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleContaining(String title);
    List<Book> findByCategoriesId(Long categoryId);
}
