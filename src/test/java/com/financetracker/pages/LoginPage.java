// FILE: src/test/java/com/financetracker/pages/LoginPage.java
package com.financetracker.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private static final By EMAIL_INPUT = By.name("email");
    private static final By PASSWORD_INPUT = By.name("password");
    // Specific selector: button with text 'Login'
    private static final By LOGIN_BUTTON = By.xpath("//button[@type='submit' and contains(text(), 'Login')]");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void enterEmail(String email) {
        fillField(EMAIL_INPUT, email);
    }

    public void enterPassword(String password) {
        fillField(PASSWORD_INPUT, password);
    }

    public void clickLogin() {
        click(LOGIN_BUTTON);
    }

    public String getEmailValue() {
        return waitForElement(EMAIL_INPUT).getAttribute("value");
    }
}
