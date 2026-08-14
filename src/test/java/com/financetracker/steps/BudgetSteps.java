// FILE: src/test/java/com/financetracker/steps/BudgetSteps.java
package com.financetracker.steps;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.financetracker.pages.BudgetPage;
import com.financetracker.pages.DashboardPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;

public class BudgetSteps {

    @Autowired
    private WebDriver webDriver;

    @Then("I should see budget row with category {string} and limit {string}")
    public void iShouldSeeBudgetRowWithCategoryAndLimit(String category, String limit) {
        assertTrue(new BudgetPage(webDriver).hasBudgetRow(category, limit));
    }

    @When("I enter {string} into contribution field {string} for goal {string}")
    public void iEnterIntoContributionFieldForGoal(String value, String fieldLabel, String goalName) {
        if (!"Amount (€)".equals(fieldLabel)) {
            throw new IllegalArgumentException("Unsupported contribution field: " + fieldLabel);
        }
        new DashboardPage(webDriver).enterContributionAmountForGoal(goalName, value);
    }

    @And("I set contribution date field {string} to {string} for goal {string}")
    public void iSetContributionDateFieldToForGoal(String fieldLabel, String value, String goalName) {
        if (!"Date".equals(fieldLabel)) {
            throw new IllegalArgumentException("Unsupported contribution date field: " + fieldLabel);
        }
        new DashboardPage(webDriver).enterContributionDateForGoal(goalName, value);
    }

    @When("I click the {string} action for goal {string}")
    public void iClickTheActionForGoal(String action, String goalName) {
        if (!"Delete".equals(action)) {
            throw new IllegalArgumentException("Unsupported goal action: " + action);
        }
        new DashboardPage(webDriver).clickDeleteGoal(goalName);
    }
}
