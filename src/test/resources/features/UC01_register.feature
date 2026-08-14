# FILE: src/test/resources/features/UC01_register.feature
Feature: UC01 - User registration
  As a new visitor
  I want to register with email and password
  So that I can access the Personal Finance Tracker

  Background:
    Given the application is running on "http://localhost:8080"
    And I am on the "Register" page at "/auth/register"

  Scenario: Register successfully with valid data
    When I enter "new.user1@example.com" into field "Email"
    And I enter "SecurePass123!" into field "Password"
    And I enter "SecurePass123!" into field "Confirm Password"
    And I click the "Register" button
    Then I should be redirected to "/auth/login"
    And I should see text "Registration successful"

  Scenario: Registration fails when email is already registered
    Given a user exists with email "existing.user@example.com" and password "SecurePass123!"
    When I enter "existing.user@example.com" into field "Email"
    And I enter "SecurePass123!" into field "Password"
    And I enter "SecurePass123!" into field "Confirm Password"
    And I click the "Register" button
    Then I should remain on "/auth/register"
    And I should see validation message "Email already registered"

  Scenario: Registration fails when passwords do not match
    When I enter "new.user2@example.com" into field "Email"
    And I enter "SecurePass123!" into field "Password"
    And I enter "DifferentPass123!" into field "Confirm Password"
    And I click the "Register" button
    Then I should remain on "/auth/register"
    And I should see validation message "Passwords do not match"
