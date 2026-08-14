# FILE: src/test/resources/features/UC07_view_expense_list.feature
Feature: UC07 - View and filter expense list
  As an authenticated user
  I want to view and filter my expenses
  So that I can inspect spending by period and category

  Background:
    Given the application is running on "http://localhost:8080"
    And a user exists with email "expense.view@example.com" and password "SecurePass123!"
    And I am logged in as "expense.view@example.com" with password "SecurePass123!"
    And category "Groceries" exists for the logged-in user
    And category "Transport" exists for the logged-in user
    And an expense exists for the logged-in user with category "Groceries" amount "45.90" date "2024-01-15" note "Market"
    And an expense exists for the logged-in user with category "Transport" amount "12.00" date "2024-01-16" note "Bus pass"
    And an expense exists for the logged-in user with category "Groceries" amount "30.00" date "2024-02-01" note "Weekly shopping"
    And I am on the "Expenses" page at "/expenses"

  Scenario: View full expense list ordered by date
    Then I should see expense note "Weekly shopping" in the expense table
    And I should see expense note "Bus pass" in the expense table
    And I should see expense note "Market" in the expense table

  Scenario: Filter by category and month/year
    When I select "Groceries" from dropdown "Category"
    And I select month "1" in field "Month"
    And I enter "2024" into field "Year"
    And I click the "Filter" button
    Then I should see expense note "Market" in the expense table
    And I should not see expense note "Bus pass" in the expense table
    And I should not see expense note "Weekly shopping" in the expense table
