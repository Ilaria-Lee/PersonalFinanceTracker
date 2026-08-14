# FILE: src/test/resources/features/UC11_budget_alert.feature
Feature: UC11 - Budget Alert
  As an authenticated user
  I want to be warned when I exceed most of my budget
  So that I can react before overspending

  Background:
    Given the application is running on "http://localhost:8080"
    And a user exists with email "budget.alert@example.com" and password "SecurePass123!"
    And I am logged in as "budget.alert@example.com" with password "SecurePass123!"
    And category "Groceries" exists for the logged-in user
    And a budget exists for category "Groceries" current month limit "100.00"
    And an expense exists for the logged-in user with category "Groceries" amount "85.00" current month note "Market"
    And I am on the "Dashboard" page at "/dashboard"

  Scenario: Show alert when budget threshold is reached
    Then I should see text "Budget Alerts"
    And I should see text "Groceries"
    And I should see text "85.0% of budget used."
