# FILE: src/test/resources/features/UC02_login.feature
Feature: UC02 - User login
  As a registered user
  I want to log in with my credentials
  So that I can access my dashboard

  Background:
    Given the application is running on "http://localhost:8080"
    And a user exists with email "login.user@example.com" and password "SecurePass123!"
    And I am on the "Login" page at "/auth/login"

  Scenario: Login successfully with valid credentials
    When I enter "login.user@example.com" into field "Email"
    And I enter "SecurePass123!" into field "Password"
    And I click the "Login" button
    Then I should be redirected to "/dashboard"
    And I should see text "Dashboard"

  Scenario: Login fails with invalid password
    When I enter "login.user@example.com" into field "Email"
    And I enter "WrongPassword!" into field "Password"
    And I click the "Login" button
    Then I should remain on "/auth/login"
    And I should see text "Invalid email or password"
