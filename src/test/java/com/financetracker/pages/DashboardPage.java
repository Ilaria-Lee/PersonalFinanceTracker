// FILE: src/test/java/com/financetracker/pages/DashboardPage.java
package com.financetracker.pages;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class DashboardPage extends BasePage {

    private static final By PAGE_TITLE = By.cssSelector("h1");
    private static final By CREATE_GOAL_NAME_INPUT = By.name("name");
    private static final By CREATE_GOAL_TARGET_INPUT = By.name("targetAmount");
    private static final By CREATE_GOAL_DEADLINE_INPUT = By.name("deadline");
    private static final By CREATE_GOAL_BUTTON = By.cssSelector("button.btn-primary");
    private static final By GOAL_CARDS = By.cssSelector("main div.row.g-3 > div > .card");
    private static final By GOAL_TITLE_IN_CARD = By.cssSelector("h3.h5");
    private static final By GOAL_DELETE_BUTTON = By.cssSelector("button.btn-outline-danger");
    private static final By GOAL_CONTRIBUTION_AMOUNT_INPUT = By.cssSelector("input[name='amount']");
    private static final By GOAL_CONTRIBUTION_DATE_INPUT = By.cssSelector("input[name='date']");
    private static final By GOAL_ADD_CONTRIBUTION_BUTTON = By.cssSelector("button.btn-outline-primary");
    private static final By GOAL_CONTRIBUTION_ITEMS = By.cssSelector("ul.list-group li");
    private static final By GOAL_EDIT_NAME_INPUT = By.cssSelector("form[action$='/edit'] input[name='name']");
    private static final By GOAL_EDIT_TARGET_INPUT = By.cssSelector("form[action$='/edit'] input[name='targetAmount']");
    private static final By GOAL_EDIT_DEADLINE_INPUT = By.cssSelector("form[action$='/edit'] input[name='deadline']");
    private static final By GOAL_UPDATE_BUTTON = By.cssSelector("form[action$='/edit'] button.btn-outline-secondary");

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    public String readTitle() {
        return getText(PAGE_TITLE);
    }

    public void enterGoalName(String value) {
        fillField(CREATE_GOAL_NAME_INPUT, value);
    }

    public void enterGoalTarget(String value) {
        fillField(CREATE_GOAL_TARGET_INPUT, value);
    }

    public void enterGoalDeadline(String value) {
        WebElement element = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(CREATE_GOAL_DEADLINE_INPUT));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].removeAttribute('required');" +
                "arguments[0].value = arguments[1];" +
                "arguments[0].dispatchEvent(new Event('change', {bubbles:true}));",
                element, value);
    }

    public void clickCreateGoal() {
        click(CREATE_GOAL_BUTTON);
    }

    public boolean hasGoalCard(String goalName) {
        // Verify it's there
        for (WebElement card : waitForElements(GOAL_CARDS)) {
            if (card.findElements(GOAL_TITLE_IN_CARD).isEmpty()) {
                continue;
            }
            if (card.findElement(GOAL_TITLE_IN_CARD).getText().contains(goalName)) {
                return true;
            }
        }
        return false;
    }

    public void clickDeleteGoal(String goalName) {
        for (WebElement card : waitForElements(GOAL_CARDS)) {
            if (card.findElements(GOAL_TITLE_IN_CARD).isEmpty()) {
                continue;
            }
            if (card.findElement(GOAL_TITLE_IN_CARD).getText().contains(goalName)) {
                card.findElement(GOAL_DELETE_BUTTON).click();
                return;
            }
        }
        throw new IllegalStateException("Goal card not found: " + goalName);
    }

    public void enterContributionAmountForGoal(String goalName, String amount) {
        WebElement card = findGoalCard(goalName);
        WebElement amountInput = card.findElement(GOAL_CONTRIBUTION_AMOUNT_INPUT);
        amountInput.clear();
        amountInput.sendKeys(amount);
    }

    public void enterContributionDateForGoal(String goalName, String date) {
        WebElement card = findGoalCard(goalName);
        WebElement dateInput = card.findElement(GOAL_CONTRIBUTION_DATE_INPUT);
        
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].value = arguments[1];", dateInput, date);
        
        String actual = dateInput.getAttribute("value");
        if (!date.equals(actual)) {
            js.executeScript("arguments[0].setAttribute('value', arguments[1]);", dateInput, date);
        }
        
        js.executeScript("arguments[0].dispatchEvent(new Event('change', {bubbles:true}));", dateInput);
    }

    public void clickAddContributionForGoal(String goalName) {
        WebElement card = findGoalCard(goalName);
        card.findElement(GOAL_ADD_CONTRIBUTION_BUTTON).click();
    }

    public boolean hasContributionAmountUnderGoal(String goalName, String amount) {
        WebElement card;
        try { card = findGoalCard(goalName); } catch (Exception e) { return false; }
        List<WebElement> items = card.findElements(GOAL_CONTRIBUTION_ITEMS);
        for (WebElement item : items) {
            if (item.getText().contains(amount)) {
                return true;
            }
        }
        return false;
    }

    public void changeGoal(String currentName, String newName, String targetAmount, String deadline) {
        WebElement card = findGoalCard(currentName);
        WebElement nameInput = card.findElement(GOAL_EDIT_NAME_INPUT);
        WebElement targetInput = card.findElement(GOAL_EDIT_TARGET_INPUT);
        WebElement deadlineInput = card.findElement(GOAL_EDIT_DEADLINE_INPUT);

        nameInput.clear();
        nameInput.sendKeys(newName);
        targetInput.clear();
        targetInput.sendKeys(targetAmount);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1];"
                        + "arguments[0].dispatchEvent(new Event('change', {bubbles:true}));",
                deadlineInput,
                deadline
        );
    }

    public void clickUpdateGoal(String goalName) {
        findGoalCard(goalName).findElement(GOAL_UPDATE_BUTTON).click();
    }

    private WebElement findGoalCard(String goalName) {
        for (WebElement card : waitForElements(GOAL_CARDS)) {
            if (card.findElements(GOAL_TITLE_IN_CARD).isEmpty()) {
                continue;
            }
            if (card.findElement(GOAL_TITLE_IN_CARD).getText().contains(goalName)) {
                return card;
            }
        }
        throw new IllegalStateException("Goal card not found: " + goalName);
    }
}
