package com.financetracker.steps;

import com.financetracker.utils.ScreenshotUtil;
import io.cucumber.java.After;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.Scenario;

/**
 * Hook Cucumber per catturare screenshot automaticamente SOLO in caso di errore.
 * Non cattura screenshot per test passati = risparmio spazio + debug più pulito.
 */
@Slf4j
public class ErrorScreenshotHooks {

    @Autowired(required = false)
    private WebDriver webDriver;

    /**
     * Scatta screenshot + URL + HTML SOLO se lo scenario è fallito.
     * Order alto = eseguito prima del cleanup @After con order più basso.
     */
    @After(order = 1000)
    public void captureScreenshotOnFailure(Scenario scenario) {
        if (scenario.isFailed()) {
            log.info("❌ Test fallito: {}. Catturando screenshot...", scenario.getName());
            ScreenshotUtil.captureScreenshot(webDriver, scenario);
        } else {
            log.info("✅ Test passato: {}. Nessuno screenshot necessario.", scenario.getName());
        }
    }
}
