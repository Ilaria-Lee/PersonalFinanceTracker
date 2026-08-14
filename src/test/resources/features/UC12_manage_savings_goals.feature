# FILE: src/test/resources/features/UC12_manage_savings_goals.feature
Feature: UC12 - Manage savings goals
  As an authenticated user
  I want to create, update, contribute to, and delete savings goals
  So that I can track long-term objectives

  Background:
    Given the application is running on "http://localhost:8080"
    And a user exists with email "savings.user@example.com" and password "SecurePass123!"
    And I am logged in as "savings.user@example.com" with password "SecurePass123!"
    And I am on the "Savings" page at "/savings"

  Scenario: Create a new savings goal
    When I enter "Vacation 2025" into field "Name"
    And I enter "2000.00" into field "Target Amount (€)"
    And I set date field "Deadline" to "2027-08-01"
    And I click the "Create Goal" button
    Then I should be redirected to "/savings"
    And I should see text "Savings goal created successfully"
    And I should see goal card "Vacation 2025"

  Scenario: Add contribution to an existing goal
    Given a savings goal exists with name "Vacation 2025" target "2000.00" deadline "2027-08-01"
    When I enter "150.00" into contribution field "Amount (€)" for goal "Vacation 2025"
    And I set contribution date field "Date" to "2024-02-10" for goal "Vacation 2025"
    And I click the "Add Contribution" button for goal "Vacation 2025"
    Then I should be redirected to "/savings"
    And I should see text "Contribution added successfully"
    And I should see contribution amount "150.00" under goal "Vacation 2025"

  Scenario: Delete a savings goal
    Given a savings goal exists with name "Old Goal" target "1000.00" deadline "2027-12-31"
    When I click the "Delete" action for goal "Old Goal"
    And I accept the browser confirmation dialog
    Then I should be redirected to "/savings"
    And I should see text "Savings goal deleted successfully"
    And I should not see goal card "Old Goal"
