# FILE: src/test/resources/features/UC06_delete_expense.feature
Feature: UC06 - Delete expense
  As an authenticated user
  I want to delete an incorrect expense
  So that I can keep the list clean

  Background:
    Given the application is running on "http://localhost:8080"
    And a user exists with email "expense.delete@example.com" and password "SecurePass123!"
    And I am logged in as "expense.delete@example.com" with password "SecurePass123!"
    And category "Groceries" exists for the logged-in user
    And an expense exists for the logged-in user with category "Groceries" amount "45.90" date "2024-01-20" note "To be removed"
    And I am on the "Expenses" page at "/expenses"

  Scenario: Delete expense from list
    When I click the "Delete" action for expense note "To be removed"
    And I accept the browser confirmation dialog
    Then I should be redirected to "/expenses"
    And I should see text "Expense deleted successfully"
    And I should not see expense note "To be removed" in the expense table
