@regression @register
Feature: User Registration

  # Background navigates to the register page before each scenario
  Background:
    Given the user is on the register page

  # =====================================================================
  # Positive Scenario
  # =====================================================================

  @TC-REGISTER-001 @smoke @positive
  Scenario: Successful registration with a new unique username
    When the user registers with a new unique username and matching passwords
    Then the user should be navigated to the login page
    And the login page should display the successful registration message

  # =====================================================================
  # Negative Scenarios — Scenario Outline
  # Test data here is disposable, generated-for-this-scenario input, never
  # real credentials — unlike login.feature (which deliberately keeps real
  # credentials out of the feature file), literal values here are safe
  # since nothing here is a secret and no row here actually persists a user.
  # =====================================================================

  @TC-REGISTER-002 @negative
  Scenario Outline: Registration fails with invalid input
    When the user attempts to register with username "<username>" password "<password>" and confirm password "<confirmPassword>"
    Then the registration error message "<expectedMessage>" should be displayed
    And the user should stay on the register page

    Examples:
      | username      | password      | confirmPassword  | expectedMessage          |
      |               | ValidPass@123 | ValidPass@123     | All fields are required. |
      | testuser-reg  |               | ValidPass@123     | All fields are required. |
      | testuser-mism | ValidPass@123 | DifferentPass@987 | Passwords do not match.  |
