Feature: Project Management

  Background:
    Given the user is logged in as "admin" with password "password"
    And the user navigates to the Projects page

  Scenario: Admin views projects list
    Then the projects page displays a list of project cards
    And each project card shows the project name, description, status, and action buttons
