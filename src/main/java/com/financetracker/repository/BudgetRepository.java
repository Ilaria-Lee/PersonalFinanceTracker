// FILE: src/main/java/com/financetracker/repository/BudgetRepository.java
package com.financetracker.repository;

import com.financetracker.model.Budget;
import com.financetracker.model.Category;
import com.financetracker.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUserAndMonthAndYear(User user, int month, int year);

    Optional<Budget> findByUserAndCategoryAndMonthAndYear(User user, Category category, int month, int year);
}
