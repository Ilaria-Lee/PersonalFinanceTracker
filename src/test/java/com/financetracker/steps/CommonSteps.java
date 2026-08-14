// FILE: src/test/java/com/financetracker/steps/CommonSteps.java
package com.financetracker.steps;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.financetracker.model.Budget;
import com.financetracker.model.Category;
import com.financetracker.model.Expense;
import com.financetracker.model.SavingsGoal;
import com.financetracker.model.User;
import com.financetracker.pages.BasePage;
import com.financetracker.pages.DashboardPage;
import com.financetracker.pages.LoginPage;
import com.financetracker.repository.BudgetRepository;
import com.financetracker.repository.CategoryRepository;
import com.financetracker.service.CategoryService;
import com.financetracker.repository.ExpenseRepository;
import com.financetracker.repository.SavingsGoalRepository;
import com.financetracker.repository.UserRepository;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class CommonSteps {

    private static WebDriver sharedDriver;
    private static String currentUserEmail;

    @Autowired
    private WebDriver webDriver;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private SavingsGoalRepository savingsGoalRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public void setSharedDriver(WebDriver driver) {
        sharedDriver = driver;
    }

    public static void resetCurrentUserContext() {
        currentUserEmail = null;
    }

    @Given("the application is running on {string}")
    public void applicationIsRunningOn(String baseUrl) {
        assertTrue(baseUrl.startsWith("http://localhost:8080"));
        new BasePage(webDriver).navigateTo(baseUrl);
        new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("localhost"));
    }

    @Given("I am on the {string} page at {string}")
    public void iAmOnThePageAt(String pageName, String path) {
        new BasePage(webDriver).navigateTo(path);
        
        // Verify we actually reached the intended page
        String currentUrl = webDriver.getCurrentUrl();
        if (!currentUrl.contains(path)) {
            throw new IllegalStateException(
                    "Navigation failed: Expected to be on '" + path + "' but got '" + currentUrl + "'. " +
                    "Page may require permission, or server may have redirected due to error."
            );
        }
    }

    @SuppressWarnings("null")
    @Given("a user exists with email {string} and password {string}")
    public void aUserExistsWithEmailAndPassword(String email, String password) {
        Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isPresent()) {
            return;
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(user);
        categoryService.createDefaultCategories(user);
    }

    @Given("I am logged in as {string} with password {string}")
    public void iAmLoggedInAsWithPassword(String email, String password) {
        currentUserEmail = email;
        LoginPage loginPage = new LoginPage(webDriver);
        loginPage.navigateTo("/auth/login");
        loginPage.enterEmail(email);
        loginPage.enterPassword(password);
        loginPage.clickLogin();
        new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("/dashboard"));
    }

    @SuppressWarnings("null")
    @Given("category {string} exists for the logged-in user")
    public void categoryExistsForTheLoggedInUser(String categoryName) {
        User user = getCurrentUserFromDb();
        if (categoryRepository.findByNameAndUser(categoryName, user).isPresent()) {
            return;
        }

        Category category = Category.builder().name(categoryName).user(user).build();
        categoryRepository.save(category);

        // Refresh if already on categories page to show the new data
        if (webDriver != null && webDriver.getCurrentUrl().contains("/categories")) {
            webDriver.navigate().refresh();
        }
    }

    @SuppressWarnings("null")
    @Given("an expense exists for the logged-in user with category {string} amount {string} date {string} note {string}")
    public void anExpenseExistsForTheLoggedInUser(String categoryName, String amount, String date, String note) {
        User user = getCurrentUserFromDb();
        Category category = categoryRepository.findByNameAndUser(categoryName, user)
                .orElseGet(() -> categoryRepository.save(Category.builder().name(categoryName).user(user).build()));

        Expense expense = Expense.builder()
                .amount(new BigDecimal(amount))
                .date(LocalDate.parse(date))
                .note(note)
                .category(category)
                .user(user)
                .build();
        expenseRepository.save(expense);

        // Refresh if already on expenses page to show the new data
        if (webDriver != null && webDriver.getCurrentUrl().contains("/expenses")) {
            webDriver.navigate().refresh();
        }
    }

    @Given("an expense exists for the logged-in user with category {string} amount {string} current month note {string}")
    public void anExpenseExistsCurrentMonth(String categoryName, String amount, String note) {
        String date = LocalDate.now().withDayOfMonth(1).toString();
        anExpenseExistsForTheLoggedInUser(categoryName, amount, date, note);
    }

    @Given("a budget exists for category {string} month {string} year {string} limit {string}")
    public void aBudgetExistsForCategoryMonthYearLimit(String categoryName, String month, String year, String limit) {
        User user = getCurrentUserFromDb();
        @SuppressWarnings("null")
        Category category = categoryRepository.findByNameAndUser(categoryName, user)
                .orElseGet(() -> categoryRepository.save(Category.builder().name(categoryName).user(user).build()));

        Budget budget = budgetRepository.findByUserAndCategoryAndMonthAndYear(
                        user,
                        category,
                        Integer.parseInt(month),
                        Integer.parseInt(year)
                )
                .orElseGet(() -> Budget.builder()
                        .category(category)
                        .user(user)
                        .month(Integer.parseInt(month))
                        .year(Integer.parseInt(year))
                        .build());

        budget.setLimitAmount(new BigDecimal(limit));
        budgetRepository.save(budget);

        // Refresh if already on budgets page to show the new data
        if (webDriver != null && webDriver.getCurrentUrl().contains("/budgets")) {
            webDriver.navigate().refresh();
        }
    }

    @Given("a budget exists for category {string} current month limit {string}")
    public void aBudgetExistsForCurrentMonthLimit(String categoryName, String limit) {
        int month = LocalDate.now().getMonthValue();
        int year = LocalDate.now().getYear();
        aBudgetExistsForCategoryMonthYearLimit(categoryName, 
            String.valueOf(month), String.valueOf(year), limit);
    }

    @SuppressWarnings("null")
    @Given("a savings goal exists with name {string} target {string} deadline {string}")
    public void aSavingsGoalExistsWithNameTargetDeadline(String name, String target, String deadline) {
        User user = getCurrentUserFromDb();
        SavingsGoal goal = SavingsGoal.builder()
                .name(name)
                .targetAmount(new BigDecimal(target))
                .deadline(LocalDate.parse(deadline))
                .user(user)
                .build();
        savingsGoalRepository.save(goal);
        // Ensure we're on the /savings page and refresh to load the newly created goal
        if (!webDriver.getCurrentUrl().contains("/savings")) {
            new BasePage(webDriver).navigateTo("/savings");
        }
        webDriver.navigate().refresh();
        new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("/savings"));
    }

    @Then("I should be redirected to {string}")
    public void iShouldBeRedirectedTo(String path) {
        // Wait up to 10 seconds for the page to load and the URL to change
        new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains(path));
        assertTrue(webDriver.getCurrentUrl().contains(path));
    }

    @Then("I should remain on {string}")
    public void iShouldRemainOn(String path) {
        assertTrue(webDriver.getCurrentUrl().contains(path));
    }

    @Then("I should see text {string}")
    public void iShouldSeeText(String text) {
        new WebDriverWait(webDriver, Duration.ofSeconds(10))
            .until(d -> d.getPageSource().contains(text));
        assertTrue(webDriver.getPageSource().contains(text));
    }

    @Then("I should see validation message {string}")
    public void iShouldSeeValidationMessage(String text) {
        new WebDriverWait(webDriver, Duration.ofSeconds(10))
            .until(d -> d.getPageSource().contains(text));
        assertTrue(webDriver.getPageSource().contains(text));
    }

    @And("I accept the browser confirmation dialog")
    public void iAcceptTheBrowserConfirmationDialog() {
        try {
            new WebDriverWait(webDriver, Duration.ofSeconds(2)).until(ExpectedConditions.alertIsPresent());
            webDriver.switchTo().alert().accept();
        } catch (Exception e) {
            // Alert già accettato automaticamente o non apparso, procediamo
        }
    }

    @Then("I should see goal card {string}")
    public void iShouldSeeGoalCard(String goalName) {
        DashboardPage dashboardPage = new DashboardPage(webDriver);
        assertTrue(dashboardPage.hasGoalCard(goalName));
    }

    @Then("I should not see goal card {string}")
    public void iShouldNotSeeGoalCard(String goalName) {
        DashboardPage dashboardPage = new DashboardPage(webDriver);
        assertFalse(dashboardPage.hasGoalCard(goalName));
    }

    @Then("I should see contribution amount {string} under goal {string}")
    public void iShouldSeeContributionAmountUnderGoal(String amount, String goalName) {
        DashboardPage dashboardPage = new DashboardPage(webDriver);
        assertTrue(dashboardPage.hasContributionAmountUnderGoal(goalName, amount));
    }

    @After(order = 0)
    public void afterScenario() {
        if (webDriver != null) {
            webDriver.manage().deleteAllCookies();
            webDriver.navigate().to("about:blank");
        }
        currentUserEmail = null;
    }

    @AfterAll
    public static void afterAll() {
        if (sharedDriver != null) {
            sharedDriver.quit();
            sharedDriver = null;
        }
    }

    private User getCurrentUserFromDb() {
        if (currentUserEmail == null) {
            throw new IllegalStateException("No logged-in user context available for test data setup");
        }
        return userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new IllegalStateException("Current user not found in database: " + currentUserEmail));
    }
}
