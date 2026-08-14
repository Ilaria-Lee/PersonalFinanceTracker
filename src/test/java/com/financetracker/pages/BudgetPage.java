// FILE: src/test/java/com/financetracker/pages/BudgetPage.java
package com.financetracker.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class BudgetPage extends BasePage {

    private static final By CATEGORY_SELECT = By.name("categoryId");
    private static final By MONTH_SELECT = By.name("month");
    private static final By YEAR_INPUT = By.name("year");
    private static final By LIMIT_AMOUNT_INPUT = By.name("limitAmount");
    private static final By SAVE_BUTTON = By.cssSelector("button.btn-primary");
    private static final By BUDGET_TABLE_ROWS = By.cssSelector("table tbody tr");

    public BudgetPage(WebDriver driver) {
        super(driver);
    }

    public void selectCategory(String categoryName) {
        new Select(waitForElement(CATEGORY_SELECT)).selectByVisibleText(categoryName);
    }

    public void selectMonth(String month) {
        new Select(waitForElement(MONTH_SELECT)).selectByValue(String.valueOf(Integer.parseInt(month)));
    }

    public void enterYear(String year) {
        fillField(YEAR_INPUT, year);
    }

    public void enterLimitAmount(String amount) {
        fillField(LIMIT_AMOUNT_INPUT, amount);
    }

    public void clickSave() {
        click(SAVE_BUTTON);
    }

    public boolean hasBudgetRow(String categoryName, String limit) {
        List<WebElement> rows = waitForElements(BUDGET_TABLE_ROWS);
        for (WebElement row : rows) {
            String text = row.getText();
            if (text.contains(categoryName) && text.contains(limit)) {
                return true;
            }
        }
        return false;
    }
}
