// FILE: src/test/java/com/financetracker/pages/RegisterPage.java
package com.financetracker.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class RegisterPage extends BasePage {

    private static final By EMAIL_INPUT = By.name("email");
    private static final By PASSWORD_INPUT = By.name("password");
    private static final By CONFIRM_PASSWORD_INPUT = By.name("confirmPassword");
    // Specific selector: button with text 'Register'
    private static final By REGISTER_BUTTON = By.xpath("//button[@type='submit' and contains(text(), 'Register')]");

    public RegisterPage(WebDriver driver) {
        super(driver);
    }

    public void enterEmail(String value) {
        fillField(EMAIL_INPUT, value);
    }

    public void enterPassword(String value) {
        fillField(PASSWORD_INPUT, value);
    }

    public void enterConfirmPassword(String value) {
        fillField(CONFIRM_PASSWORD_INPUT, value);
    }

    public void clickRegister() {
        click(REGISTER_BUTTON);
    }
}
