// FILE: src/test/java/com/financetracker/pages/CategoryPage.java
package com.financetracker.pages;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CategoryPage extends BasePage {

    private static final By NAME_INPUT = By.name("name");
    private static final By ADD_CATEGORY_BUTTON = By.cssSelector("button.btn-primary");
    private static final By CATEGORY_TABLE_ROWS = By.cssSelector("table tbody tr");
    private static final By DELETE_BUTTON_IN_ROW = By.cssSelector("button.btn-outline-danger");

    public CategoryPage(WebDriver driver) {
        super(driver);
    }

    public void enterCategoryName(String name) {
        fillField(NAME_INPUT, name);
    }

    public void clickAddCategory() {
        click(ADD_CATEGORY_BUTTON);
    }

    public boolean hasCategory(String name) {
        List<WebElement> rows = waitForElements(CATEGORY_TABLE_ROWS);
        for (WebElement row : rows) {
            if (row.getText().contains(name)) {
                return true;
            }
        }
        return false;
    }

    public void clickDeleteActionForCategory(String name) {
        // Wait up to 10 seconds for the category row to be present before clicking delete
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> d.findElements(CATEGORY_TABLE_ROWS).stream()
                        .anyMatch(row -> row.getText().contains(name)));

        for (WebElement row : driver.findElements(CATEGORY_TABLE_ROWS)) {
            if (row.getText().contains(name)) {
                clickElement(row.findElement(DELETE_BUTTON_IN_ROW));
                return;
            }
        }
        throw new IllegalStateException("Category row not found: " + name);
    }
}
