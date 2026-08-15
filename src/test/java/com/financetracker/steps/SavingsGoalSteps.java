package com.financetracker.steps;

import com.financetracker.pages.DashboardPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;

public class SavingsGoalSteps {

    @Autowired
    private WebDriver webDriver;

    @When("I change goal {string} to name {string} target {string} deadline {string}")
    public void iChangeGoal(String currentName, String newName, String target, String deadline) {
        new DashboardPage(webDriver).changeGoal(currentName, newName, target, deadline);
    }

    @And("I submit the update for goal {string}")
    public void iSubmitTheUpdateForGoal(String currentName) {
        new DashboardPage(webDriver).clickUpdateGoal(currentName);
    }
}
