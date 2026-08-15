// FILE: src/test/java/com/financetracker/steps/CategorySteps.java
package com.financetracker.steps;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.financetracker.pages.CategoryPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;

public class CategorySteps {

    @Autowired
    private WebDriver webDriver;

    @When("I click the {string} action for category {string}")
    public void iClickTheActionForCategory(String action, String categoryName) {
        if (!"Delete".equals(action)) {
            throw new IllegalArgumentException("Unsupported category action: " + action);
        }
        new CategoryPage(webDriver).clickDeleteActionForCategory(categoryName);
    }

    @Then("I should see category {string} in the category table")
    public void iShouldSeeCategoryInTheCategoryTable(String categoryName) {
        assertTrue(new CategoryPage(webDriver).hasCategory(categoryName));
    }

    @Then("I should not see category {string} in the category table")
    public void iShouldNotSeeCategoryInTheCategoryTable(String categoryName) {
        assertFalse(new CategoryPage(webDriver).hasCategory(categoryName));
    }
}
