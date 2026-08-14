// FILE: src/main/java/com/financetracker/dto/BudgetProgressDto.java
package com.financetracker.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetProgressDto {

    private String categoryName;
    private BigDecimal limitAmount;
    private BigDecimal spent;
    private BigDecimal percentage;

    public boolean isAlertThresholdReached() {
        return percentage != null && percentage.compareTo(new BigDecimal("80")) >= 0;
    }
}
