# FILE: src/test/resources/features/UC03_logout.feature
Feature: UC03 - Logout
  As an authenticated user
  I want to log out
  So that my session ends securely

  Background:
    Given the application is running on "http://localhost:8080"
    And a user exists with email "logout.user@example.com" and password "SecurePass123!"
    And I am logged in as "logout.user@example.com" with password "SecurePass123!"
    And I am on the "Dashboard" page at "/dashboard"

  Scenario: Logout successfully
    When I click the "Logout" button
    Then I should be redirected to "/auth/login?logout=true"
    And I should see text "Logged out successfully"
