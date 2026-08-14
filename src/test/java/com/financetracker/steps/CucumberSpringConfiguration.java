package com.financetracker.steps;

import com.financetracker.FinanceTrackerApplication;
import com.financetracker.testsupport.WebDriverTestConfig;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@ActiveProfiles("test")
@SpringBootTest(
        classes = {FinanceTrackerApplication.class, WebDriverTestConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
public class CucumberSpringConfiguration {
}
