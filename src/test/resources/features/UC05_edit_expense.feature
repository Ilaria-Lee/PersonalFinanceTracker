# FILE: src/test/resources/features/UC05_edit_expense.feature
Feature: UC05 - Edit expense
  As an authenticated user
  I want to edit a previously saved expense
  So that my records stay accurate

  Background:
    Given the application is running on "http://localhost:8080"
    And a user exists with email "expense.edit@example.com" and password "SecurePass123!"
    And I am logged in as "expense.edit@example.com" with password "SecurePass123!"
    And category "Groceries" exists for the logged-in user
    And an expense exists for the logged-in user with category "Groceries" amount "45.90" date "2024-01-20" note "Weekly supermarket"

  Scenario: Edit expense successfully
    Given I am on the "Expenses" page at "/expenses"
    When I click the "Edit" action for expense note "Weekly supermarket"
    And I change field "Amount" to "39.50"
    And I change field "Note" to "Discounted groceries"
    And I click the "Save Expense" button
    Then I should be redirected to "/expenses"
    And I should see text "Expense updated successfully"
    And I should see an expense row with amount "39.50" and note "Discounted groceries"
