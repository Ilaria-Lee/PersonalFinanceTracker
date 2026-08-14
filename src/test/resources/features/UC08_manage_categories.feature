# FILE: src/test/resources/features/UC08_manage_categories.feature
Feature: UC08 - Manage categories
  As an authenticated user
  I want to create and remove categories
  So that I can classify expenses effectively

  Background:
    Given the application is running on "http://localhost:8080"
    And a user exists with email "category.user@example.com" and password "SecurePass123!"
    And I am logged in as "category.user@example.com" with password "SecurePass123!"
    And I am on the "Categories" page at "/categories"

  Scenario: Add category successfully
    When I enter "Gym" into field "New Category"
    And I click the "Add Category" button
    Then I should be redirected to "/categories"
    And I should see text "Category created successfully"
    And I should see category "Gym" in the category table

  Scenario: Cannot add duplicate category
    Given category "Groceries" exists for the logged-in user
    When I enter "Groceries" into field "New Category"
    And I click the "Add Category" button
    Then I should be redirected to "/categories"
    And I should see text "Category already exists"

  Scenario: Cannot delete category in use
    Given category "Groceries" exists for the logged-in user
    And an expense exists for the logged-in user with category "Groceries" amount "45.90" date "2024-01-15" note "Market"
    When I click the "Delete" action for category "Groceries"
    And I accept the browser confirmation dialog
    Then I should be redirected to "/categories"
    And I should see text "Cannot delete: category is in use"
