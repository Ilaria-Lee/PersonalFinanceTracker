// FILE: src/main/java/com/financetracker/service/DashboardService.java
package com.financetracker.service;

import com.financetracker.dto.DashboardDto;
import com.financetracker.model.Expense;
import com.financetracker.model.User;
import com.financetracker.repository.ExpenseRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardService {

    private final ExpenseRepository expenseRepository;
    private final BudgetService budgetService;

    public DashboardDto getDashboardData(User user) {
        LocalDate now = LocalDate.now();

        LocalDate currentMonthStart = now.withDayOfMonth(1);
        LocalDate currentMonthEnd = currentMonthStart.withDayOfMonth(currentMonthStart.lengthOfMonth());

        LocalDate previousMonthRef = now.minusMonths(1);
        LocalDate previousMonthStart = previousMonthRef.withDayOfMonth(1);
        LocalDate previousMonthEnd = previousMonthStart.withDayOfMonth(previousMonthStart.lengthOfMonth());

        BigDecimal currentMonthTotal = expenseRepository.sumByUserAndDateBetween(user, currentMonthStart, currentMonthEnd);
        if (currentMonthTotal == null) {
            currentMonthTotal = BigDecimal.ZERO;
        }

        BigDecimal previousMonthTotal = expenseRepository.sumByUserAndDateBetween(user, previousMonthStart, previousMonthEnd);
        if (previousMonthTotal == null) {
            previousMonthTotal = BigDecimal.ZERO;
        }

        BigDecimal monthOverMonthDifference = currentMonthTotal.subtract(previousMonthTotal);

        Map<String, BigDecimal> expensesByCategory = new LinkedHashMap<>();
        List<Expense> currentMonthExpenses = expenseRepository.findByUserAndDateBetweenOrderByDateDesc(user, currentMonthStart, currentMonthEnd);
        for (Expense expense : currentMonthExpenses) {
            String categoryName = expense.getCategory().getName();
            BigDecimal existing = expensesByCategory.getOrDefault(categoryName, BigDecimal.ZERO);
            expensesByCategory.put(categoryName, existing.add(expense.getAmount()));
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
        List<DashboardDto.MonthlyTotal> last6MonthsTotals = new java.util.ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            LocalDate monthDate = now.minusMonths(i);
            LocalDate from = monthDate.withDayOfMonth(1);
            LocalDate to = from.withDayOfMonth(from.lengthOfMonth());
            BigDecimal total = expenseRepository.sumByUserAndDateBetween(user, from, to);
            if (total == null) {
                total = BigDecimal.ZERO;
            }
            last6MonthsTotals.add(new DashboardDto.MonthlyTotal(monthDate.format(formatter), total));
        }

        DashboardDto dashboardDto = new DashboardDto();
        dashboardDto.setCurrentMonthTotal(currentMonthTotal);
        dashboardDto.setPreviousMonthTotal(previousMonthTotal);
        dashboardDto.setMonthOverMonthDifference(monthOverMonthDifference);
        dashboardDto.setExpensesByCategory(expensesByCategory);
        dashboardDto.setLast6MonthsTotals(last6MonthsTotals);
        dashboardDto.setBudgetProgress(budgetService.getBudgetProgressForCurrentMonth(user));

        return dashboardDto;
    }
}
