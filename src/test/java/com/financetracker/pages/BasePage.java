// FILE: src/test/java/com/financetracker/pages/BasePage.java
package com.financetracker.pages;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.time.Duration;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BasePage {

    protected static final String BASE_URL = "http://localhost:8080";
    protected final WebDriver driver;

    public BasePage(WebDriver driver) {
        this.driver = driver;
    }

    public void navigateTo(String path) {
        String target = path.startsWith("http") ? path : BASE_URL + path;
        driver.get(target);
    }

    public void click(By locator) {
        WebElement element = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(locator));
        clickElement(element);
    }

    public void fillField(By locator, String value) {
        clearAndType(locator, value);
    }

    public void clearAndType(By locator, String value) {
        WebElement element = waitForElement(locator);
        element.clear();
        element.sendKeys(value);
    }

    public String getText(By locator) {
        return waitForElement(locator).getText();
    }

    public boolean isPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    public WebElement waitForElement(By locator) {
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public List<WebElement> waitForElements(By locator) {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
        } catch (Exception e) {
            return driver.findElements(locator);
        }
    }

    protected void clickElement(WebElement element) {
        try {
            // First attempt: scroll into view and click normally
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center', behavior:'smooth'});", element);
            
            // Wait a moment for scroll animation
            Thread.sleep(200);
            
            element.click();
        } catch (ElementClickInterceptedException ex) {
            // Element is hidden behind another element, use JavaScript click
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            } catch (Exception jsEx) {
                throw new RuntimeException("Failed to click element via JavaScript: " + jsEx.getMessage(), jsEx);
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while clicking element", ex);
        }
    }

    // Utility method: click a button/link by its visible text
    public void clickButtonByText(String buttonText) {
        By locator = By.xpath("//button[normalize-space()='" + buttonText + "'] | //a[normalize-space()='" + buttonText + "']");
        click(locator);
    }

    // Utility method: click a button by its CSS class
    public void clickButtonByClass(String buttonClass) {
        By locator = By.xpath("//button[contains(@class, '" + buttonClass + "')]");
        click(locator);
    }

    public Path takeScreenshot(String prefix) {
        if (!(driver instanceof TakesScreenshot)) {
            throw new IllegalStateException("Current driver does not support screenshots");
        }

        File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        Path target = Path.of("target", "screenshots", prefix + "-" + System.currentTimeMillis() + ".png");

        try {
            Files.createDirectories(target.getParent());
            Files.copy(source.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
            return target;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to save screenshot to " + target, ex);
        }
    }
}
