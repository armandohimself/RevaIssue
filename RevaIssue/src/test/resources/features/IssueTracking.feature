@wip
Feature: Issue Tracking - view, update, history
  Background:
    Given   The user is on the login page

  Scenario: User can view a list of issues for a given project
    Given   The user logs in with username "apitester" and password "password"
    And     The user navigates to the Issues page
    And     The user selects the project "API Test Project"
    Then    The issues list contains the issue titled "API Issue Open Low"
    And     The issues list contains the issue titled "API Issue Open High"
    And     The issues list contains the issue titled "API Issue Closed Medium"
    And     The issues list contains the issue titled "API Issue Resolved High"

  Scenario Outline: User can click on a issue row and see its details
    Given   The user logs in with username "apitester" and password "password"
    And     The user navigates to the Issues page
    And     The user selects the project "API Test Project"
    When    The user clicks the issue titled "<Title>"
    Then    The issue displays status "<Status>" and severity "<Severity>" and priority "<Priority>"
    Examples:
    |Title|Status|Severity|Priority|
    |API Issue Open High|OPEN|HIGH|HIGH|
    |API Issue Closed Medium|CLOSED|MEDIUM|MEDIUM|
    |API Issue Resolved High|RESOLVED|HIGH|MEDIUM|

  Scenario: Testers can update issue details and changes are reflected in display and history
    Given   The user logs in with username "apitester" and password "password"
    And     The user navigates to the Issues page
    And     The user selects the project "API Test Project"
    When    The user clicks the action button for issue "API Issue Open Low"
    And     The user selects severity "HIGH" from the dropdown
    And     The user clicks the update button
    Then    The issue "API Issue Open Low" displays severity "HIGH"
    And     The issue "API Issue Open Low" displays a list of history logs
    And     The history logs contain an entry mentioning "updated" and "API Issue Open Low"

  Scenario: Developers are not able to update issue details
    Given   The user logs in with username "apidev1" and password "password"
    And     The user navigates to the Issues page
    When    The user selects the project "API Test Project"
    Then    The action button for issue "API Issue Open Low" is not visible

  Scenario: Admins are not able to update issue details
    Given   The user logs in with username "admin" and password "password"
    And     The user navigates to the Issues page
    When    The user selects the project "API Test Project"
    Then    The action button for issue "API Issue Open Low" is not visible