// FILE: src/test/java/com/financetracker/pages/ExpenseFormPage.java
package com.financetracker.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ExpenseFormPage extends BasePage {

    private static final By AMOUNT_INPUT = By.name("amount");
    private static final By DATE_INPUT = By.name("date");
    private static final By CATEGORY_SELECT = By.name("categoryId");
    private static final By NOTE_TEXTAREA = By.name("note");
    // Specific selector: button with type='submit' and text 'Save Expense'
    private static final By SAVE_BUTTON = By.xpath("//button[@type='submit' and contains(text(), 'Save Expense')]");

    public ExpenseFormPage(WebDriver driver) {
        super(driver);
    }

    public void enterAmount(String value) {
        fillField(AMOUNT_INPUT, value);
    }

    public void setDate(String value) {
        WebElement element = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(DATE_INPUT));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].removeAttribute('required');" +
                "arguments[0].value = arguments[1];" +
                "arguments[0].dispatchEvent(new Event('change', {bubbles:true}));",
                element, value);
    }

    public void selectCategory(String category) {
        new Select(waitForElement(CATEGORY_SELECT)).selectByVisibleText(category);
    }

    public void enterNote(String note) {
        fillField(NOTE_TEXTAREA, note);
    }

    public void clickSave() {
        click(SAVE_BUTTON);
    }
}
