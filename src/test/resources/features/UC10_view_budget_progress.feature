# FILE: src/test/resources/features/UC10_view_budget_progress.feature
Feature: UC10 - View budget progress
  As an authenticated user
  I want to see spending progress for each budget
  So that I can monitor how close I am to the limit

  Background:
    Given the application is running on "http://localhost:8080"
    And a user exists with email "budget.progress@example.com" and password "SecurePass123!"
    And I am logged in as "budget.progress@example.com" with password "SecurePass123!"
    And category "Groceries" exists for the logged-in user
    And a budget exists for category "Groceries" current month limit "100.00"
    And an expense exists for the logged-in user with category "Groceries" amount "55.00" current month note "Weekly shopping"
    And I am on the "Budgets" page at "/budgets"

  Scenario: View budget progress for current month
    Then I should see budget progress for category "Groceries" with limit "100.00" spent "55.00" remaining "45.00" and percentage "55.0"
