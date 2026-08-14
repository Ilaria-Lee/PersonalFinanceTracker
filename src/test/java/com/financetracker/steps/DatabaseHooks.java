package com.financetracker.steps;

import com.financetracker.repository.BudgetRepository;
import com.financetracker.repository.CategoryRepository;
import com.financetracker.repository.ExpenseRepository;
import com.financetracker.repository.SavingsContributionRepository;
import com.financetracker.repository.SavingsGoalRepository;
import com.financetracker.repository.UserRepository;
import io.cucumber.java.Before;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;

import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor
public class DatabaseHooks {

    private final SavingsContributionRepository savingsContributionRepository;
    private final SavingsGoalRepository savingsGoalRepository;
    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Autowired
    private WebDriver webDriver;

    @Before(order = 0)
    public void cleanDatabase() {
        savingsContributionRepository.deleteAllInBatch();
        savingsGoalRepository.deleteAllInBatch();
        budgetRepository.deleteAllInBatch();
        expenseRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Before(order = 1)
    public void cleanBrowserState() {
        if (webDriver != null) {
            webDriver.manage().deleteAllCookies();
            webDriver.navigate().to("about:blank");
        }
        CommonSteps.resetCurrentUserContext();
    }
}
