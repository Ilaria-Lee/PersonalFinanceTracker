// FILE: src/test/java/com/financetracker/steps/ExpenseSteps.java
package com.financetracker.steps;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.financetracker.pages.BudgetPage;
import com.financetracker.pages.ExpenseFormPage;
import com.financetracker.pages.ExpenseListPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.datatable.DataTable;
import java.time.Month;
import java.util.Locale;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;

public class ExpenseSteps {

    @Autowired
    private WebDriver webDriver;

    @When("I select {string} from dropdown {string}")
    public void iSelectFromDropdown(String value, String dropdownLabel) {
        String url = webDriver.getCurrentUrl();

        if ("Category".equals(dropdownLabel) && url.contains("/expenses")) {
            if (url.contains("/new") || url.contains("/edit")) {
                new ExpenseFormPage(webDriver).selectCategory(value);
            } else {
                new ExpenseListPage(webDriver).selectCategoryFilter(value);
            }
            return;
        }

        if ("Category".equals(dropdownLabel) && url.contains("/budgets")) {
            new BudgetPage(webDriver).selectCategory(value);
            return;
        }

        throw new IllegalStateException("Unsupported dropdown: " + dropdownLabel + " on " + url);
    }

    @When("I select month {string} in field {string}")
    public void iSelectMonthInField(String month, String fieldLabel) {
        if (!"Month".equals(fieldLabel)) {
            throw new IllegalArgumentException("Unsupported field label: " + fieldLabel);
        }

        String url = webDriver.getCurrentUrl();
        if (url.contains("/expenses") && !url.contains("/new") && !url.contains("/edit")) {
            new ExpenseListPage(webDriver).selectMonthFilter(month);
            return;
        }

        if (url.contains("/budgets")) {
            new BudgetPage(webDriver).selectMonth(month);
            return;
        }

        throw new IllegalStateException("Unsupported page for month selection: " + url);
    }

    @When("I select period {string}")
    public void iSelectPeriod(String period) {
        String[] parts = period.split(" ");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Unsupported period: " + period);
        }

        Month month = Month.valueOf(parts[0].toUpperCase(Locale.ENGLISH));
        String year = parts[1];

        String url = webDriver.getCurrentUrl();
        if (url.contains("/budgets")) {
            BudgetPage page = new BudgetPage(webDriver);
            page.enterYear(year);
            page.selectMonth(String.valueOf(month.getValue()));
            return;
        }

        throw new IllegalStateException("Unsupported page for period selection: " + url);
    }

    @When("I set date field {string} to {string}")
    public void iSetDateFieldTo(String fieldLabel, String value) {
        String url = webDriver.getCurrentUrl();

        if ((url.contains("/expenses/new") || url.contains("/edit")) && "Date".equals(fieldLabel)) {
            new ExpenseFormPage(webDriver).setDate(value);
            return;
        }

        if (url.contains("/savings") && "Deadline".equals(fieldLabel)) {
            new com.financetracker.pages.DashboardPage(webDriver).enterGoalDeadline(value);
            return;
        }

        throw new IllegalStateException("Unsupported date step: " + fieldLabel + " on " + url);
    }

    @When("I click the {string} action for expense note {string}")
    public void iClickTheActionForExpenseNote(String action, String note) {
        ExpenseListPage page = new ExpenseListPage(webDriver);
        if ("Edit".equals(action)) {
            page.clickEditActionForExpenseNote(note);
        } else if ("Delete".equals(action)) {
            page.clickDeleteActionForExpenseNote(note);
        } else {
            throw new IllegalArgumentException("Unsupported expense action: " + action);
        }
    }

    @When("I change field {string} to {string}")
    public void iChangeFieldTo(String fieldLabel, String value) {
        ExpenseFormPage page = new ExpenseFormPage(webDriver);
        if ("Amount".equals(fieldLabel)) {
            page.enterAmount(value);
        } else if ("Note".equals(fieldLabel)) {
            page.enterNote(value);
        } else {
            throw new IllegalArgumentException("Unsupported change field: " + fieldLabel);
        }
    }

    @Then("I should see an expense row with category {string} amount {string} and note {string}")
    public void iShouldSeeAnExpenseRowWithCategoryAmountAndNote(String category, String amount, String note) {
        assertTrue(new ExpenseListPage(webDriver).hasExpenseRow(category, amount, note));
    }

    @Then("I should see an expense row with amount {string} and note {string}")
    public void iShouldSeeAnExpenseRowWithAmountAndNote(String amount, String note) {
        assertTrue(new ExpenseListPage(webDriver).hasExpenseRowAmountAndNote(amount, note));
    }

    @Then("I should see expense note {string} in the expense table")
    public void iShouldSeeExpenseNoteInTheExpenseTable(String note) {
        assertTrue(new ExpenseListPage(webDriver).hasExpenseNote(note));
    }

    @Then("I should not see expense note {string} in the expense table")
    public void iShouldNotSeeExpenseNoteInTheExpenseTable(String note) {
        assertFalse(new ExpenseListPage(webDriver).hasExpenseNote(note));
    }

    @Then("the expense notes should appear in this order:")
    public void theExpenseNotesShouldAppearInThisOrder(DataTable dataTable) {
        java.util.List<String> expectedNotes = dataTable.asLists().stream()
                .map(row -> row.get(0))
                .toList();
        assertTrue(new ExpenseListPage(webDriver).hasExpenseNotesInOrder(expectedNotes));
    }

    @And("I click the {string} button for goal {string}")
    public void iClickTheButtonForGoal(String buttonText, String goalName) {
        if (!"Add Contribution".equals(buttonText)) {
            throw new IllegalArgumentException("Unsupported goal button: " + buttonText);
        }
        new com.financetracker.pages.DashboardPage(webDriver).clickAddContributionForGoal(goalName);
    }
}
