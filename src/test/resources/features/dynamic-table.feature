@regression @dynamic-table
Feature: Dynamic Table Page

  # Columns and rows change position and cell values are randomized on every page load —
  # per the page's own stated behavior, so no scenario here assumes a fixed layout
  Background:
    Given the user is on the dynamic table page

  @TC-DYNTABLE-001 @smoke @positive
  Scenario: Chrome's CPU value in the table matches the yellow label
    Then the Chrome CPU value in the table should match the Chrome CPU label

  @TC-DYNTABLE-002 @positive
  Scenario: The table displays all expected column headers
    Then the table should contain the following columns:
      | Name    |
      | Disk    |
      | CPU     |
      | Memory  |
      | Network |

  @TC-DYNTABLE-003 @positive
  Scenario: The table lists all expected processes
    Then the table should contain the following processes:
      | Chrome            |
      | Firefox           |
      | System            |
      | Internet Explorer |

  @TC-DYNTABLE-004 @positive
  Scenario: Chrome's CPU value still matches the label after a page reload
    When the user reloads the page
    Then the Chrome CPU value in the table should match the Chrome CPU label
