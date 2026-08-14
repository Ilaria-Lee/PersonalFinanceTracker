package com.financetracker.utils;

import io.cucumber.java.Scenario;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility per catturare screenshot e debug info in caso di errore nei test.
 * Salva screenshot con nome utile (nomeTest_timestamp) + URL + HTML della pagina.
 */
@Slf4j
public class ScreenshotUtil {

    private static final String SCREENSHOT_DIR = "target/test-screenshots";
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    /**
     * Cattura screenshot + URL + HTML della pagina in caso di errore.
     * Salva con nome: scenario_timestamp.png
     * 
     * @param driver WebDriver Selenium
     * @param scenario Scenario Cucumber fallito
     */
    public static void captureScreenshot(WebDriver driver, Scenario scenario) {
        if (driver == null || scenario == null) {
            log.warn("Driver o Scenario null, impossibile catturare screenshot");
            return;
        }

        try {
            // Crea directory se non esiste
            Files.createDirectories(Paths.get(SCREENSHOT_DIR));

            String scenarioName = scenario.getName()
                    .replaceAll("\\s+", "_")
                    .replaceAll("[^a-zA-Z0-9_]", "");
            
            scenarioName = scenarioName.substring(0, Math.min(50, scenarioName.length()));
            
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            String filename = scenarioName + "_" + timestamp;

            // 1. Cattura screenshot
            captureScreenshotFile(driver, filename);

            // 2. Log URL corrente
            logCurrentUrl(driver, filename);

            // 3. Log HTML pagina
            logPageSource(driver, filename);

            log.info("Debug info salvati per lo scenario fallito: {}", scenarioName);

        } catch (Exception e) {
            log.error("Errore durante la cattura dello screenshot", e);
        }
    }

    /**
     * Salva il file screenshot PNG
     */
    private static void captureScreenshotFile(WebDriver driver, String filename) throws IOException {
        if (!(driver instanceof TakesScreenshot)) {
            log.warn("WebDriver non supporta screenshot");
            return;
        }

        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String filepath = SCREENSHOT_DIR + File.separator + filename + ".png";
        Files.copy(screenshot.toPath(), Paths.get(filepath));
        log.info("Screenshot salvato: {}", filepath);
    }

    /**
     * Salva URL corrente in file di testo
     */
    private static void logCurrentUrl(WebDriver driver, String filename) throws IOException {
        String currentUrl = driver.getCurrentUrl();
        String filepath = SCREENSHOT_DIR + File.separator + filename + "_url.txt";
        Files.write(Paths.get(filepath), ("URL: " + currentUrl).getBytes());
        log.info("URL salvato: {}", filepath);
    }

    /**
     * Salva HTML della pagina in file di testo
     */
    private static void logPageSource(WebDriver driver, String filename) throws IOException {
        String pageSource = driver.getPageSource();
        String filepath = SCREENSHOT_DIR + File.separator + filename + "_source.html";
        Files.write(Paths.get(filepath), pageSource.getBytes());
        log.info("HTML pagina salvato: {}", filepath);
    }
}
