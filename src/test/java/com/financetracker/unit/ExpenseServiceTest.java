// FILE: src/test/java/com/financetracker/unit/ExpenseServiceTest.java
package com.financetracker.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.financetracker.dto.ExpenseDto;
import com.financetracker.exception.ResourceNotFoundException;
import com.financetracker.exception.UnauthorizedAccessException;
import com.financetracker.exception.ValidationException;
import com.financetracker.model.Category;
import com.financetracker.model.Expense;
import com.financetracker.model.User;
import com.financetracker.repository.ExpenseRepository;
import com.financetracker.service.CategoryService;
import com.financetracker.service.ExpenseService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private ExpenseService expenseService;

    @SuppressWarnings("null")
    @Test
    void createExpense_success() {
        User user = User.builder().id(1L).email("user@test.com").build();
        Category category = Category.builder().id(10L).name("Food").user(user).build();
        ExpenseDto dto = new ExpenseDto(null, new BigDecimal("25.50"), LocalDate.of(2024, 1, 10), 10L, "Lunch");

        Expense savedExpense = Expense.builder()
                .id(100L)
                .amount(dto.getAmount())
                .date(dto.getDate())
                .note(dto.getNote())
                .category(category)
                .user(user)
                .build();

        when(categoryService.findByIdAndUser(10L, user)).thenReturn(category);
        when(expenseRepository.save(org.mockito.ArgumentMatchers.any(Expense.class))).thenReturn(savedExpense);

        Expense result = expenseService.createExpense(dto, user);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        verify(expenseRepository).save(org.mockito.ArgumentMatchers.any(Expense.class));
    }

    @Test
    void createExpense_zeroAmount() {
        User user = User.builder().id(1L).build();
        ExpenseDto dto = new ExpenseDto(null, new BigDecimal("0.00"), LocalDate.of(2024, 1, 10), 10L, "");

        assertThrows(ValidationException.class, () -> expenseService.createExpense(dto, user));
    }

    @Test
    void createExpense_categoryNotFound() {
        User user = User.builder().id(1L).build();
        ExpenseDto dto = new ExpenseDto(null, new BigDecimal("10.00"), LocalDate.of(2024, 1, 10), 999L, "");

        when(categoryService.findByIdAndUser(999L, user)).thenThrow(new ResourceNotFoundException("Category not found"));

        assertThrows(ResourceNotFoundException.class, () -> expenseService.createExpense(dto, user));
    }

    @Test
    void createExpense_categoryWrongUser() {
        User user = User.builder().id(1L).build();
        ExpenseDto dto = new ExpenseDto(null, new BigDecimal("10.00"), LocalDate.of(2024, 1, 10), 999L, "");

        when(categoryService.findByIdAndUser(999L, user)).thenThrow(new UnauthorizedAccessException("Unauthorized"));

        assertThrows(UnauthorizedAccessException.class, () -> expenseService.createExpense(dto, user));
    }

    @Test
    void deleteExpense_notOwner() {
        User currentUser = User.builder().id(1L).build();
        User owner = User.builder().id(2L).build();
        Expense expense = Expense.builder().id(50L).user(owner).build();

        when(expenseRepository.findById(50L)).thenReturn(Optional.of(expense));

        assertThrows(UnauthorizedAccessException.class, () -> expenseService.deleteExpense(50L, currentUser));
    }

    @Test
    void getExpensesFiltered_monthFilter() {
        User user = User.builder().id(1L).build();
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);

        when(expenseRepository.findByUserAndDateBetweenOrderByDateDesc(user, from, to))
                .thenReturn(Collections.emptyList());

        expenseService.getExpensesFiltered(user, null, 1, 2024);

        verify(expenseRepository).findByUserAndDateBetweenOrderByDateDesc(user, from, to);
    }
}
