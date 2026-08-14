// FILE: src/main/java/com/financetracker/repository/ExpenseRepository.java
package com.financetracker.repository;

import com.financetracker.model.Category;
import com.financetracker.model.Expense;
import com.financetracker.model.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUserOrderByDateDesc(User user);

    List<Expense> findByUserAndCategoryOrderByDateDesc(User user, Category category);

    List<Expense> findByUserAndDateBetweenOrderByDateDesc(User user, LocalDate from, LocalDate to);

    List<Expense> findByUserAndCategoryAndDateBetweenOrderByDateDesc(User user, Category category, LocalDate from, LocalDate to);

    boolean existsByCategory(Category category);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user = :user AND e.category = :category AND e.date >= :from AND e.date <= :to")
    BigDecimal sumByUserAndCategoryAndDateBetween(
            @Param("user") User user,
            @Param("category") Category category,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user = :user AND e.date >= :from AND e.date <= :to")
    BigDecimal sumByUserAndDateBetween(
            @Param("user") User user,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );
}
