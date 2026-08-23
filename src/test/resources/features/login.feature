@regression @login
Feature: Login Functionality

  # Background navigates to login page before each scenario
  Background:
    Given the user is on the login page

  # =====================================================================
  # Positive Scenario
  # =====================================================================

  @TC-LOGIN-001 @smoke @positive
  Scenario: Successful login with valid credentials
    When the user enters valid credentials
    And the user submits the login form
    Then the user should be navigated to secure page
    And the secure page should display successful login message
    And the logout button should be displayed on secure page

  # =====================================================================
  # Negative Scenarios — Scenario Outline with readable labels only
  # No actual credentials in Examples table — resolved from config in step defs
  # expectedMessage kept in table — it is UI text, not sensitive data
  # =====================================================================

  @TC-LOGIN-002 @negative
  Scenario Outline: Login fails with invalid or empty credentials
    When the user attempts login with "<credentialScenario>"
    And the user submits the login form
    Then the error message "<expectedMessage>" should be displayed
    And the user should stay on the login page

    Examples:
      | credentialScenario | expectedMessage           |
      | invalid username   | Your username is invalid! |
      | invalid password   | Your password is invalid! |
      | empty username     | Your username is invalid! |
      | empty password     | Your password is invalid! |

  # =====================================================================
  # Data Driven Positive Scenarios
  # Scenario Outline used correctly — same flow, multiple valid credential sets
  # Actual values in Examples table acceptable here — these are test credentials
  # stored in properties file and not production secrets
  # =====================================================================

  @TC-LOGIN-003 @positive @datadriven
  Scenario Outline: Successful login with multiple valid credential sets
    When the user attempts login with "<credentialScenario>"
    And the user submits the login form
    Then the user should be navigated to secure page
    And the secure page should display successful login message

    Examples:
      | credentialScenario |
      | valid credentials  |