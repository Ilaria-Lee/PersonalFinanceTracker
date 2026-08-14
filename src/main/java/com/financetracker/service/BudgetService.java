// FILE: src/main/java/com/financetracker/service/BudgetService.java
package com.financetracker.service;

import com.financetracker.dto.BudgetDto;
import com.financetracker.dto.BudgetProgressDto;
import com.financetracker.exception.ResourceNotFoundException;
import com.financetracker.exception.UnauthorizedAccessException;
import com.financetracker.model.Budget;
import com.financetracker.model.Category;
import com.financetracker.model.User;
import com.financetracker.repository.BudgetRepository;
import com.financetracker.repository.ExpenseRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BudgetService {

    private static final BigDecimal HUNDRED = new BigDecimal("100");
    private static final BigDecimal ALERT_THRESHOLD = new BigDecimal("80");

    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;
    private final CategoryService categoryService;

    @Transactional(readOnly = true)
    public List<BudgetProgressDto> getBudgetProgressForCurrentMonth(User user) {
        LocalDate now = LocalDate.now();
        return getBudgetProgress(user, now.getMonthValue(), now.getYear());
    }

    @Transactional(readOnly = true)
    public List<BudgetProgressDto> getBudgetProgress(User user, int month, int year) {
        List<Budget> budgets = budgetRepository.findByUserAndMonthAndYear(user, month, year);
        List<BudgetProgressDto> progress = new ArrayList<>();

        LocalDate from = LocalDate.of(year, month, 1);
        LocalDate to = from.withDayOfMonth(from.lengthOfMonth());

        for (Budget budget : budgets) {
            String categoryName = budget.getCategory().getName();

            BigDecimal spent = expenseRepository.sumByUserAndCategoryAndDateBetween(user, budget.getCategory(), from, to);
            if (spent == null) {
                spent = BigDecimal.ZERO;
            }

            BigDecimal percentage = calculatePercentage(spent, budget.getLimitAmount());

            progress.add(new BudgetProgressDto(
                    categoryName,
                    budget.getLimitAmount(),
                    spent,
                    percentage
            ));
        }

        return progress;
    }

    @SuppressWarnings("null")
    public void setBudget(BudgetDto dto, User user) {
        Category category = categoryService.findByIdAndUser(dto.getCategoryId(), user);

        Optional<Budget> existingBudget = budgetRepository.findByUserAndCategoryAndMonthAndYear(
                user,
                category,
                dto.getMonth(),
                dto.getYear()
        );

        if (existingBudget.isPresent()) {
            Budget budget = existingBudget.get();
            budget.setLimitAmount(dto.getLimitAmount());
            budgetRepository.save(budget);
            log.info("Budget updated for category {} user {}", category.getName(), user.getEmail());
            return;
        }

        Budget newBudget = Budget.builder()
                .limitAmount(dto.getLimitAmount())
                .month(dto.getMonth())
                .year(dto.getYear())
                .category(category)
                .user(user)
                .build();

        budgetRepository.save(newBudget);
        log.info("Budget created for category {} user {}", category.getName(), user.getEmail());
    }

    public void deleteBudget(Long id, User user) {
        @SuppressWarnings("null")
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

        if (!Objects.equals(budget.getUser().getId(), user.getId())) {
            throw new UnauthorizedAccessException("Unauthorized access to budget");
        }

        budgetRepository.delete(budget);
        log.info("Budget deleted: {} for user {}", id, user.getEmail());
    }

    private BigDecimal calculatePercentage(BigDecimal spent, BigDecimal limitAmount) {
        if (limitAmount == null || limitAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO.setScale(1, RoundingMode.HALF_UP);
        }

        return spent.multiply(HUNDRED).divide(limitAmount, 1, RoundingMode.HALF_UP);
    }
}
