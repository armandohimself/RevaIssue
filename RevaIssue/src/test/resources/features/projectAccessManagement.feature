Feature: Project Access Management

  Background:
    Given the user is logged in as "admin" with password "password"
    And the user navigates to the Projects page
    And a test project "Access Test Project" exists

  Scenario: Admin views access management dialog
    When the admin clicks the manage access button on project "Access Test Project"
    Then the manage access dialog is displayed
    And the member list is visible
