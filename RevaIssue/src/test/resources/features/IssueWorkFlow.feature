Feature: Workflow Management - create issues and change status
  Background:
    Given   The user is on the login page

  Scenario: Tester can open a new issue
    Given   The user logs in with username "apitester" and password "password"
    And     The user navigates to the Issues page
    And     The user selects the project "API Test Project"
    When    The user clicks the create issue button
    And     The user enters issue title "Newly create issue from testing"
    And     The user enters issue description "Created by the issue work flow feature file"
    And     The user selects severity "MEDIUM" from the create issue dropdown
    And     The user selects priority "HIGH" from the create issue dropdown
    And     The user clicks the create button
    Then    The issues list contains the issue titled "Newly create issue from testing"

  Scenario Outline: Developers and testers can move an issue from one status to another
    And     The user logs in with username "<username>" and password "<password>"
    And     The user navigates to the Issues page
    And     The user selects the project "API Test Project"
    When    The user changes status of issue "API Issue Open Low" from "<initialStatus>" to "<newStatus>"
    Then    The issue "API Issue Open Low" displays status "<status>"
    Examples:
      |username|password|initialStatus|newStatus|status|
      |apitester|password|RESOLVED|CLOSED|CLOSED|
      |apitester|password|CLOSED|OPEN|OPEN|
      |apidev1|password|OPEN|IN_PROGRESS|IN PROGRESS|
      |apidev1|password|IN_PROGRESS|RESOLVED|RESOLVED|

