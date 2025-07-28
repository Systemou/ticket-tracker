@ticket-submission
Feature: Submit a New Support Ticket
  As a User
  I want to be able to submit a new support ticket with relevant details
  So that I can report an issue and receive assistance from the support team

  Background:
    Given I am logged in as a user
    And there are available ticket categories and priorities

  @happy-path
  Scenario: Successfully submit a new support ticket
    When I submit a ticket with title "System Login Issue" and description "I am unable to log into the application. The login page shows an error message after entering credentials."
    And I select category "TECHNICAL" and priority "HIGH"
    Then the ticket should be created successfully
    And the ticket should have status "OPEN"
    And the ticket should have a creation date
    And the ticket should be visible in my list of tickets

  @validation-title-too-short
  Scenario: Submit ticket with title too short
    When I submit a ticket with title "Bug" and description "This is a detailed description of the bug that needs to be fixed immediately."
    And I select category "BUG" and priority "MEDIUM"
    Then I should see an error message about title length requirement

  @validation-description-too-short
  Scenario: Submit ticket with description too short
    When I submit a ticket with title "Feature Request" and description "Add new feature"
    And I select category "FEATURE" and priority "LOW"
    Then I should see an error message about description length requirement

  @validation-missing-title
  Scenario: Submit ticket with missing title
    When I submit a ticket with empty title and description "This is a detailed description of the issue that needs to be addressed."
    And I select category "BUG" and priority "HIGH"
    Then I should see an error message about required title

  @validation-missing-description
  Scenario: Submit ticket with missing description
    When I submit a ticket with title "Critical Issue" and empty description
    And I select category "CRITICAL" and priority "HIGH"
    Then I should see an error message about required description

  @validation-missing-category
  Scenario: Submit ticket with missing category
    When I submit a ticket with title "General Issue" and description "This is a detailed description of the general issue that needs attention."
    And I do not select a category
    And I select priority "MEDIUM"
    Then I should see an error message about required category

  @validation-missing-priority
  Scenario: Submit ticket with missing priority
    When I submit a ticket with title "Minor Issue" and description "This is a detailed description of the minor issue that can be addressed later."
    And I select category "MINOR"
    And I do not select a priority
    Then I should see an error message about required priority

  @business-rules
  Scenario: Verify business rules are enforced
    When I submit a ticket with title "Test Ticket" and description "This is a test ticket to verify business rules are working correctly."
    And I select category "TEST" and priority "LOW"
    Then the ticket should be created with minimum length requirements enforced
    And the ticket should have automatic creation date
    And the ticket should have default status "OPEN"

  @concurrent-submissions
  Scenario: Handle multiple concurrent ticket submissions
    When I submit multiple tickets concurrently
    Then all tickets should be created successfully
    And each ticket should have unique IDs
    And each ticket should have correct creation dates
