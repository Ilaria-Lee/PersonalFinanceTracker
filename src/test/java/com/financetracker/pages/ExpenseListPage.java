// FILE: src/test/java/com/financetracker/pages/ExpenseListPage.java
package com.financetracker.pages;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ExpenseListPage extends BasePage {

    private static final By CATEGORY_FILTER = By.name("categoryId");
    private static final By MONTH_FILTER = By.name("month");
    private static final By YEAR_FILTER = By.name("year");
    private static final By FILTER_BUTTON = By.cssSelector("button[type='submit'].btn-outline-primary");
    private static final By EXPENSE_TABLE_ROWS = By.cssSelector("table tbody tr");
    private static final By EDIT_ACTION_IN_ROW = By.cssSelector("a.btn.btn-outline-primary");
    private static final By DELETE_ACTION_IN_ROW = By.cssSelector("button[type='submit'].btn-outline-danger");

    public ExpenseListPage(WebDriver driver) {
        super(driver);
    }

    public void selectCategoryFilter(String categoryName) {
        new Select(waitForElement(CATEGORY_FILTER)).selectByVisibleText(categoryName);
    }

    public void selectMonthFilter(String month) {
        new Select(waitForElement(MONTH_FILTER)).selectByValue(String.valueOf(Integer.parseInt(month)));
    }

    public void setYearFilter(String year) {
        fillField(YEAR_FILTER, year);
    }

    public void clickFilter() {
        List<WebElement> rowsBeforeFilter = driver.findElements(EXPENSE_TABLE_ROWS);
        click(FILTER_BUTTON);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        if (!rowsBeforeFilter.isEmpty()) {
            // The first row becoming stale is a reliable signal that the table was refreshed.
            wait.until(d -> {
                try {
                    rowsBeforeFilter.get(0).isDisplayed();
                    return false;
                } catch (StaleElementReferenceException ex) {
                    return true;
                }
            });
        }

        wait.until(d -> d.getCurrentUrl().contains("/expenses"));
    }

    public void clickEditActionForExpenseNote(String note) {
        for (WebElement row : waitForElements(EXPENSE_TABLE_ROWS)) {
            try {
                if (row.getText().contains(note)) {
                    clickElement(row.findElement(EDIT_ACTION_IN_ROW));
                    return;
                }
            } catch (StaleElementReferenceException ignored) {
                // Retry on next row snapshot.
            }
        }
        throw new IllegalStateException("Expense row not found for note: " + note);
    }

    public void clickDeleteActionForExpenseNote(String note) {
        for (WebElement row : waitForElements(EXPENSE_TABLE_ROWS)) {
            try {
                if (row.getText().contains(note)) {
                    clickElement(row.findElement(DELETE_ACTION_IN_ROW));
                    return;
                }
            } catch (StaleElementReferenceException ignored) {
                // Retry on next row snapshot.
            }
        }
        throw new IllegalStateException("Expense row not found for note: " + note);
    }

    public boolean hasExpenseNote(String note) {
        List<WebElement> rows = waitForElements(EXPENSE_TABLE_ROWS);
        for (WebElement row : rows) {
            try {
                if (row.getText().contains(note)) {
                    return true;
                }
            } catch (StaleElementReferenceException ignored) {
                // Ignore stale rows that were detached during filter refresh.
            }
        }
        return false;
    }

    public boolean hasExpenseNotesInOrder(List<String> expectedNotes) {
        int nextExpectedNote = 0;
        for (WebElement row : waitForElements(EXPENSE_TABLE_ROWS)) {
            if (nextExpectedNote < expectedNotes.size()
                    && row.getText().contains(expectedNotes.get(nextExpectedNote))) {
                nextExpectedNote++;
            }
        }
        return nextExpectedNote == expectedNotes.size();
    }

    public boolean hasExpenseRow(String category, String amount, String note) {
        List<WebElement> rows = waitForElements(EXPENSE_TABLE_ROWS);
        for (WebElement row : rows) {
            String text = row.getText();
            if (text.contains(category) && text.contains(amount) && text.contains(note)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasExpenseRowAmountAndNote(String amount, String note) {
        List<WebElement> rows = waitForElements(EXPENSE_TABLE_ROWS);
        for (WebElement row : rows) {
            String text = row.getText();
            if (text.contains(amount) && text.contains(note)) {
                return true;
            }
        }
        return false;
    }
}
