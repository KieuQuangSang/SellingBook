package com.booknest.BookNest.repository;

import com.booknest.BookNest.model.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    List<Promotion> findByPromotionNameContaining(String name);
}
