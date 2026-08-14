// FILE: src/main/java/com/financetracker/dto/BudgetDto.java
package com.financetracker.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetDto {

    private Long categoryId;

    @Min(1)
    @Max(12)
    private int month;

    @Min(2000)
    private int year;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal limitAmount;
}
