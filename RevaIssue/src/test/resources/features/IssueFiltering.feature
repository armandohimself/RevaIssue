Feature: Issue search and filtering options
  Background:
    Given   The user is on the login page
    And     The user logs in with username "apidev1" and password "password"
    And     The user navigates to the Issues page
    And     The user selects the project "API Test Project"

  Scenario Outline: Users can search issues by name to view
    When    The user clicks the search bar
    And     The user enters the word "<searchword>"
    Then    The issues list contains only issues with "<searchword>" in title
    Examples:
      |searchword|
      |Open      |
      |API       |
      |Issue     |
      |Medium    |

  Scenario: Users can filter issues by status
    When The user selects the status "Open" from the status filter
    Then The issues list contains only issues with "OPEN" status

  Scenario: Users can filter issues by severity
    When The user selects the severity "Medium" from the  severity filter
    Then The issues list contains only issues with "MEDIUM" severity

  Scenario: Users can filter issues by priority
    When The user selects the priority "High" from the  priority filter
    Then The issues list contains only issues with "HIGH" priority