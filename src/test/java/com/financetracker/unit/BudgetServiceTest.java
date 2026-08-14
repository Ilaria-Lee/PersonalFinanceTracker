// FILE: src/test/java/com/financetracker/unit/BudgetServiceTest.java
package com.financetracker.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.financetracker.dto.BudgetDto;
import com.financetracker.model.Budget;
import com.financetracker.model.Category;
import com.financetracker.model.User;
import com.financetracker.repository.BudgetRepository;
import com.financetracker.repository.ExpenseRepository;
import com.financetracker.service.BudgetService;
import com.financetracker.service.CategoryService;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private BudgetService budgetService;

    @SuppressWarnings("null")
@Test
    void setBudget_createsNew() {
        User user = User.builder().id(1L).email("user@test.com").build();
        Category category = Category.builder().id(2L).name("Food").user(user).build();
        BudgetDto dto = new BudgetDto(2L, 1, 2024, new BigDecimal("300.00"));

        when(categoryService.findByIdAndUser(2L, user)).thenReturn(category);
        when(budgetRepository.findByUserAndCategoryAndMonthAndYear(user, category, 1, 2024))
                .thenReturn(Optional.empty());

        budgetService.setBudget(dto, user);

        ArgumentCaptor<Budget> captor = ArgumentCaptor.forClass(Budget.class);
        verify(budgetRepository).save(captor.capture());
        Budget saved = captor.getValue();
        assertNotNull(saved);
        assertEquals(dto.getLimitAmount(), saved.getLimitAmount());
        assertEquals(dto.getMonth(), saved.getMonth());
        assertEquals(dto.getYear(), saved.getYear());
        assertEquals(category, saved.getCategory());
        assertEquals(user, saved.getUser());
    }

    @SuppressWarnings("null")
@Test
    void setBudget_updatesExisting() {
        User user = User.builder().id(1L).email("user@test.com").build();
        Category category = Category.builder().id(2L).name("Food").user(user).build();
        Budget existing = Budget.builder()
                .id(5L)
                .limitAmount(new BigDecimal("200.00"))
                .month(1)
                .year(2024)
                .category(category)
                .user(user)
                .build();
        BudgetDto dto = new BudgetDto(2L, 1, 2024, new BigDecimal("350.00"));

        when(categoryService.findByIdAndUser(2L, user)).thenReturn(category);
        when(budgetRepository.findByUserAndCategoryAndMonthAndYear(user, category, 1, 2024))
                .thenReturn(Optional.of(existing));

        budgetService.setBudget(dto, user);

        verify(budgetRepository).save(existing);
        assertEquals(new BigDecimal("350.00"), existing.getLimitAmount());
    }
}
