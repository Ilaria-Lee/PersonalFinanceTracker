# FILE: src/test/resources/features/UC09_set_budget.feature
Feature: UC09 - Set budget
  As an authenticated user
  I want to create, update, and delete a monthly budget by category
  So that I can monitor spending limits

  Background:
    Given the application is running on "http://localhost:8080"
    And a user exists with email "budget.user@example.com" and password "SecurePass123!"
    And I am logged in as "budget.user@example.com" with password "SecurePass123!"
    And category "Groceries" exists for the logged-in user
    And I am on the "Budgets" page at "/budgets"

  Scenario: Create budget successfully
    When I select "Groceries" from dropdown "Category"
    And I select month "1" in field "Month"
    And I enter "2024" into field "Year"
    And I enter "300.00" into field "Limit Amount (€)"
    And I click the "Save" button
    Then I should be redirected to "/budgets"
    And I should see text "Budget saved successfully"
    And I should see budget row with category "Groceries" and limit "300.00"

  Scenario: Update existing budget for same category and month
    Given a budget exists for category "Groceries" month "1" year "2024" limit "300.00"
    When I select "Groceries" from dropdown "Category"
    And I select month "1" in field "Month"
    And I enter "2024" into field "Year"
    And I enter "350.00" into field "Limit Amount (€)"
    And I click the "Save" button
    Then I should be redirected to "/budgets"
    And I should see budget row with category "Groceries" and limit "350.00"

  Scenario: Delete an existing budget
    Given a budget exists for category "Groceries" current month limit "300.00"
    When I click the "Delete" action for budget category "Groceries"
    And I accept the browser confirmation dialog
    Then I should be redirected to "/budgets"
    And I should see text "Budget deleted successfully"
    And I should not see a budget row for category "Groceries"
