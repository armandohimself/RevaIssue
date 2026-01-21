Feature: Projects (API)

  Background:
    Given the API base url is configured

  Scenario: Admin creates a project
    When the admin logs in with username "admin" and password "password"
    Then the response status should be 200
    And the response should contain a token
    When the admin creates a project named "BDD Project 1" with description "Created via Cucumber"
    Then the response status should be 200
    And the response should contain a project id

  Scenario: Admin can list projects
    When the admin logs in with username "admin" and password "password"
    Then the response should contain a token
    When the client calls "/projects" with that token
    Then the response status should be 200
    And the response should contain a project named "Default Project"
