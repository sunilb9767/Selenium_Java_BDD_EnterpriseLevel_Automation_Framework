@regression @inputs
Feature: Web Inputs Page

  # Background navigates to the inputs page before each scenario
  Background:
    Given the user is on the inputs page

  # =====================================================================
  # Positive Scenario — echo behavior
  # Test data is non-confidential and is what the assertions verify, so it stays visible here
  # Date must be ISO format (yyyy-MM-dd) — it is set via JS, not typed
  # =====================================================================

  @TC-INPUTS-001 @smoke @positive
  Scenario: Entered values are correctly displayed after clicking Display Inputs
    When the user enters "6576587" as the number, "QA Automation Test" as the text, "Test@12345" as the password, and "2026-09-15" as the date
    And the user clicks the Display Inputs button
    Then the displayed number should match the entered number
    And the displayed text should match the entered text
    And the displayed password should match the entered password
    And the displayed date should match the entered date

  # =====================================================================
  # Positive Scenario — Clear Inputs
  # =====================================================================

  @TC-INPUTS-002 @positive
  Scenario: Clear Inputs empties all previously entered fields
    When the user enters "6576587" as the number, "QA Automation Test" as the text, "Test@12345" as the password, and "2026-09-15" as the date
    And the user clicks the Clear Inputs button
    Then the number field should be empty
    And the text field should be empty
    And the password field should be empty
    And the date field should be empty

  # =====================================================================
  # Negative Scenario — native browser behavior, not app-specific
  # type="number" inputs silently reject non-numeric keystrokes in all major browsers
  # =====================================================================

  @TC-INPUTS-003 @negative
  Scenario: Number field rejects non-numeric characters
    When the user attempts to enter "abcXYZ" into the number field
    Then the number field should be empty
