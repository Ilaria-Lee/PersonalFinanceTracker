# FILE: src/test/resources/features/UC04_add_expense.feature
Feature: UC04 - Add expense
  As an authenticated user
  I want to add an expense
  So that I can track daily spending

  Background:
    Given the application is running on "http://localhost:8080"
    And a user exists with email "expense.add@example.com" and password "SecurePass123!"
    And I am logged in as "expense.add@example.com" with password "SecurePass123!"
    And category "Groceries" exists for the logged-in user
    And I am on the "Add Expense" page at "/expenses/new"

  Scenario: Add expense successfully
    When I enter "45.90" into field "Amount"
    And I select "Groceries" from dropdown "Category"
    And I set date field "Date" to "2024-01-20"
    And I enter "Weekly supermarket" into field "Note"
    And I click the "Save Expense" button
    Then I should be redirected to "/expenses"
    And I should see text "Expense added successfully"
    And I should see an expense row with category "Groceries" amount "45.90" and note "Weekly supermarket"

  Scenario: Add expense fails when amount is zero
    When I enter "0.00" into field "Amount"
    And I select "Groceries" from dropdown "Category"
    And I set date field "Date" to "2024-01-20"
    And I click the "Save Expense" button
    Then I should remain on "/expenses/new"
    And I should see validation message "Amount must be greater than 0"
