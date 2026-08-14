// FILE: src/main/java/com/financetracker/service/ExpenseService.java
package com.financetracker.service;

import com.financetracker.dto.ExpenseDto;
import com.financetracker.exception.ResourceNotFoundException;
import com.financetracker.exception.UnauthorizedAccessException;
import com.financetracker.exception.ValidationException;
import com.financetracker.model.Category;
import com.financetracker.model.Expense;
import com.financetracker.model.User;
import com.financetracker.repository.ExpenseRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryService categoryService;

    public List<Expense> getExpensesForUser(User user) {
        return expenseRepository.findByUserOrderByDateDesc(user);
    }

    public List<Expense> getExpensesFiltered(User user, Long categoryId, Integer month, Integer year) {
        Category category = null;
        if (categoryId != null) {
            category = categoryService.findByIdAndUser(categoryId, user);
        }

        boolean hasCategoryFilter = category != null;
        boolean hasDateFilter = month != null && year != null;

        if (!hasCategoryFilter && !hasDateFilter) {
            return expenseRepository.findByUserOrderByDateDesc(user);
        }

        if (hasCategoryFilter && !hasDateFilter) {
            return expenseRepository.findByUserAndCategoryOrderByDateDesc(user, category);
        }

        @SuppressWarnings("null")
        LocalDate from = LocalDate.of(year, month, 1);
        LocalDate to = from.withDayOfMonth(from.lengthOfMonth());

        if (!hasCategoryFilter) {
            return expenseRepository.findByUserAndDateBetweenOrderByDateDesc(user, from, to);
        }

        return expenseRepository.findByUserAndCategoryAndDateBetweenOrderByDateDesc(user, category, from, to);
    }

    @SuppressWarnings("null")
    public Expense createExpense(ExpenseDto dto, User user) {
        validateAmount(dto.getAmount());
        Category category = categoryService.findByIdAndUser(dto.getCategoryId(), user);

        Expense expense = Expense.builder()
                .amount(dto.getAmount())
                .date(dto.getDate())
                .note(dto.getNote())
                .category(category)
                .user(user)
                .build();

        Expense savedExpense = expenseRepository.save(expense);
        log.info("Expense created: {} for user {}", savedExpense.getId(), user.getEmail());
        return savedExpense;
    }

    public Expense updateExpense(Long id, ExpenseDto dto, User user) {
        Expense expense = findByIdAndUser(id, user);
        validateAmount(dto.getAmount());
        Category category = categoryService.findByIdAndUser(dto.getCategoryId(), user);

        expense.setAmount(dto.getAmount());
        expense.setDate(dto.getDate());
        expense.setNote(dto.getNote());
        expense.setCategory(category);

        Expense updatedExpense = expenseRepository.save(expense);
        log.info("Expense updated: {} for user {}", updatedExpense.getId(), user.getEmail());
        return updatedExpense;
    }

    @SuppressWarnings("null")
    public void deleteExpense(Long id, User user) {
        Expense expense = findByIdAndUser(id, user);
        expenseRepository.delete(expense);
        log.info("Expense deleted: {} for user {}", id, user.getEmail());
    }

    public Expense findByIdAndUser(Long id, User user) {
        @SuppressWarnings("null")
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        if (!Objects.equals(expense.getUser().getId(), user.getId())) {
            throw new UnauthorizedAccessException("Unauthorized access to expense");
        }

        return expense;
    }

    public ExpenseDto toDto(Expense expense) {
        return new ExpenseDto(
                expense.getId(),
                expense.getAmount(),
                expense.getDate(),
                expense.getCategory().getId(),
            expense.getNote()
        );
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Amount must be greater than 0");
        }
    }
}
