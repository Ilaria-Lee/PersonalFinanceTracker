package com.financetracker.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.financetracker.dto.BudgetProgressDto;
import com.financetracker.dto.DashboardDto;
import com.financetracker.model.Category;
import com.financetracker.model.Expense;
import com.financetracker.model.User;
import com.financetracker.repository.ExpenseRepository;
import com.financetracker.service.BudgetService;
import com.financetracker.service.DashboardService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private BudgetService budgetService;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void getDashboardData_aggregatesTotalsCategoriesAndBudgetProgress() {
        User user = User.builder().id(1L).build();
        Category groceries = Category.builder().id(10L).name("Groceries").user(user).build();
        Category transport = Category.builder().id(11L).name("Transport").user(user).build();

        when(expenseRepository.sumByUserAndDateBetween(eq(user), any(), any()))
                .thenReturn(new BigDecimal("120.00"), new BigDecimal("80.00"), BigDecimal.ZERO);
        when(expenseRepository.findByUserAndDateBetweenOrderByDateDesc(eq(user), any(), any()))
                .thenReturn(List.of(
                        Expense.builder().amount(new BigDecimal("20.00")).category(groceries).user(user).build(),
                        Expense.builder().amount(new BigDecimal("30.00")).category(groceries).user(user).build(),
                        Expense.builder().amount(new BigDecimal("40.00")).category(transport).user(user).build()
                ));
        BudgetProgressDto progress = new BudgetProgressDto(
                5L,
                "Groceries",
                new BigDecimal("100.00"),
                new BigDecimal("85.00"),
                new BigDecimal("85.0")
        );
        when(budgetService.getBudgetProgressForCurrentMonth(user)).thenReturn(List.of(progress));

        DashboardDto result = dashboardService.getDashboardData(user);

        assertEquals(new BigDecimal("120.00"), result.getCurrentMonthTotal());
        assertEquals(new BigDecimal("80.00"), result.getPreviousMonthTotal());
        assertEquals(new BigDecimal("40.00"), result.getMonthOverMonthDifference());
        assertEquals(new BigDecimal("50.00"), result.getExpensesByCategory().get("Groceries"));
        assertEquals(new BigDecimal("40.00"), result.getExpensesByCategory().get("Transport"));
        assertEquals(6, result.getLast6MonthsTotals().size());
        assertEquals(progress, result.getBudgetProgress().get(0));
    }
}
