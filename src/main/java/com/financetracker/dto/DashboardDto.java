// FILE: src/main/java/com/financetracker/dto/DashboardDto.java
package com.financetracker.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDto {

    private BigDecimal currentMonthTotal = BigDecimal.ZERO;
    private BigDecimal previousMonthTotal = BigDecimal.ZERO;
    private BigDecimal monthOverMonthDifference = BigDecimal.ZERO;
    private Map<String, BigDecimal> expensesByCategory = new LinkedHashMap<>();
    private List<MonthlyTotal> last6MonthsTotals = new ArrayList<>();
    private List<BudgetProgressDto> budgetProgress = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyTotal {

        private String monthLabel;
        private BigDecimal total;
    }
}
