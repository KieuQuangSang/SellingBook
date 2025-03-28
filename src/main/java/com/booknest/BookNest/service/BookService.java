package com.booknest.BookNest.service;

import com.booknest.BookNest.model.Book;
import com.booknest.BookNest.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
    @Autowired
    private BookRepository repository;

    @Autowired
    private OrderService orderService;

    public Book addBook(Book book) {
        return repository.save(book);
    }

    public Book updateBook(Long id, Book book) {
        book.setId(id);
        return repository.save(book);
    }

    public void deleteBook(Long id) {
        repository.deleteById(id);
    }

    public Book getBookById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Book> getAllBooks() {
        return repository.findAll();
    }

    public List<Book> searchBooksByTitle(String title) {
        return repository.findByTitleContaining(title);
    }

    public List<Book> getBooksByCategoryId(Long categoryId) {
        return repository.findByCategoriesId(categoryId);
    }

    public List<Book> getBooksByUserId(Long userId) {
        return orderService.getBooksByUserId(userId);
    }
}
