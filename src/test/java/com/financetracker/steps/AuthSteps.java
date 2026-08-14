// FILE: src/test/java/com/financetracker/steps/AuthSteps.java
package com.financetracker.steps;

import com.financetracker.pages.BudgetPage;
import com.financetracker.pages.BasePage;
import com.financetracker.pages.CategoryPage;
import com.financetracker.pages.DashboardPage;
import com.financetracker.pages.ExpenseFormPage;
import com.financetracker.pages.ExpenseListPage;
import com.financetracker.pages.LoginPage;
import com.financetracker.pages.RegisterPage;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class AuthSteps {

    @Autowired
    private WebDriver webDriver;

    @When("I enter {string} into field {string}")
    public void iEnterIntoField(String value, String fieldLabel) {
        String url = webDriver.getCurrentUrl();

        if (url.contains("/auth/register")) {
            RegisterPage registerPage = new RegisterPage(webDriver);
            if ("Email".equals(fieldLabel)) {
                registerPage.enterEmail(value);
            } else if ("Password".equals(fieldLabel)) {
                registerPage.enterPassword(value);
            } else if ("Confirm Password".equals(fieldLabel)) {
                registerPage.enterConfirmPassword(value);
            } else {
                throw new IllegalArgumentException("Unsupported register field: " + fieldLabel);
            }
            return;
        }

        if (url.contains("/auth/login")) {
            LoginPage loginPage = new LoginPage(webDriver);
            if ("Email".equals(fieldLabel)) {
                loginPage.enterEmail(value);
            } else if ("Password".equals(fieldLabel)) {
                loginPage.enterPassword(value);
            } else {
                throw new IllegalArgumentException("Unsupported login field: " + fieldLabel);
            }
            return;
        }

        if (url.contains("/expenses/new") || url.contains("/expenses/") && url.contains("/edit")) {
            ExpenseFormPage expenseFormPage = new ExpenseFormPage(webDriver);
            if ("Amount".equals(fieldLabel)) {
                expenseFormPage.enterAmount(value);
            } else if ("Note".equals(fieldLabel)) {
                expenseFormPage.enterNote(value);
            } else {
                throw new IllegalArgumentException("Unsupported expense form field: " + fieldLabel);
            }
            return;
        }

        if (url.contains("/budgets")) {
            BudgetPage budgetPage = new BudgetPage(webDriver);
            if ("Year".equals(fieldLabel)) {
                budgetPage.enterYear(value);
            } else if ("Limit Amount (€)".equals(fieldLabel)) {
                budgetPage.enterLimitAmount(value);
            } else {
                throw new IllegalArgumentException("Unsupported budget field: " + fieldLabel);
            }
            return;
        }

        if (url.contains("/expenses") && !url.contains("/new") && !url.contains("/edit")) {
            ExpenseListPage expenseListPage = new ExpenseListPage(webDriver);
            if ("Year".equals(fieldLabel)) {
                expenseListPage.setYearFilter(value);
            } else {
                throw new IllegalArgumentException("Unsupported expenses list field: " + fieldLabel);
            }
            return;
        }

        if (url.contains("/categories")) {
            CategoryPage categoryPage = new CategoryPage(webDriver);
            if ("New Category".equals(fieldLabel)) {
                categoryPage.enterCategoryName(value);
            } else {
                throw new IllegalArgumentException("Unsupported category field: " + fieldLabel);
            }
            return;
        }

        if (url.contains("/savings")) {
            DashboardPage dashboardPage = new DashboardPage(webDriver);
            if ("Name".equals(fieldLabel)) {
                dashboardPage.enterGoalName(value);
            } else if ("Target Amount (€)".equals(fieldLabel)) {
                dashboardPage.enterGoalTarget(value);
            } else {
                throw new IllegalArgumentException("Unsupported savings field: " + fieldLabel);
            }
            return;
        }

        throw new IllegalStateException("Unsupported page for generic input step: " + url);
    }

    @When("I click the {string} button")
    public void iClickTheButton(String buttonText) {
        String url = webDriver.getCurrentUrl();

        if ("Register".equals(buttonText) && url.contains("/auth/register")) {
            log.info("Submitting registration form from {}", url);
            new RegisterPage(webDriver).clickRegister();
            return;
        }

        if ("Login".equals(buttonText) && url.contains("/auth/login")) {
            new LoginPage(webDriver).clickLogin();
            return;
        }

        if ("Save Expense".equals(buttonText) && (url.contains("/expenses/new") || url.contains("/expenses/") && url.contains("/edit"))) {
            new ExpenseFormPage(webDriver).clickSave();
            return;
        }

        if ("Save".equals(buttonText) && url.contains("/budgets")) {
            new BudgetPage(webDriver).clickSave();
            return;
        }

        if ("Filter".equals(buttonText) && url.contains("/expenses") && !url.contains("/new") && !url.contains("/edit")) {
            new ExpenseListPage(webDriver).clickFilter();
            return;
        }

        if ("Add Category".equals(buttonText) && url.contains("/categories")) {
            new CategoryPage(webDriver).clickAddCategory();
            return;
        }

        if ("Create Goal".equals(buttonText) && url.contains("/savings")) {
            new DashboardPage(webDriver).clickCreateGoal();
            return;
        }

        if ("Logout".equals(buttonText)) {
            log.info("Checking logout availability at current URL: {}", webDriver.getCurrentUrl());
            if (!webDriver.getCurrentUrl().contains("/dashboard")) {
                throw new IllegalStateException(
                        "Logout is not available because the user is not on the dashboard. Current URL: "
                                + webDriver.getCurrentUrl()
                );
            }

            new BasePage(webDriver).clickButtonByText("Logout");
            return;
        }

        throw new IllegalStateException("Unsupported button step: " + buttonText + " on " + url);
    }
}
