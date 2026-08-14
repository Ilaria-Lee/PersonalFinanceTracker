package com.financetracker.testsupport;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

@Slf4j
@TestConfiguration
public class WebDriverTestConfig {

    @Bean
    @Scope("cucumber-glue")
    public WebDriver webDriver() {
        // Setup ChromeDriver - will automatically download matching version
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        
        // Headless mode for CI/automated testing
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");  // ✅ FIX: screenshot neri in headless
        options.addArguments("--window-size=1920,1080");  // ✅ FIX: specifica dimensioni
        
        // Security and resource optimizations
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-background-networking");
        options.addArguments("--disable-web-resources");
        
        // CDP protocol stability improvements
        options.addArguments("--disable-blink-features=AutomationControlled");
        
        // Disable various Chrome features that can cause connection issues
        options.addArguments("--disable-component-extensions-with-background-pages");
        options.addArguments("--disable-default-apps");
        options.addArguments("--disable-sync");
        
        // Network and communication improvements
        options.addArguments("--enable-logging");
        options.addArguments("--v=1");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);
        options.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.ACCEPT);
        
        ChromeDriver driver = new ChromeDriver(options);
        
        // Log driver version for debugging
        String capabilities = driver.getCapabilities().toString();
        log.info("ChromeDriver initialized with capabilities: {}", capabilities);
        
        return driver;
    }
}

