Feature: Projects (API)
    
  Background:
    Given the API base url is configured
    When the admin logs in with username "admin" and password "password"
    Then the response should contain a token

  Scenario: Admin creates a project
    When the admin creates a project named "BDD Project 1" with description "Created via Cucumber"
    Then the response status should be 200
    And the response should contain a project id

  Scenario: Admin can list projects (smoke)
    When the client calls "/projects" with that token
    Then the response status should be 200

  Scenario: Admin can list projects
    When the admin creates a project named "Listable Project" with description "Used for list test"
    Then the response status should be 200
    And the response should contain a project id

    When the admin lists projects
    Then the response status should be 200
    And the response should contain a project named "Listable Project"

  Scenario: Admin can view admin project details
    When the admin creates a project named "Admin View Project" with description "created for admin view"
    Then the response status should be 200
    And the response should contain a project id

    When the admin requests the admin view for that project
    Then the response status should be 200
    And the admin project response should match the created project
    And the admin project archived fields should be empty

  Scenario: Admin can PATCH update name/description/status
    When the admin creates a project named "Patch Me" with description "before"
    Then the response status should be 200
    And the response should contain a project id

    When the admin updates that project to name "Patched" description "after" status "ARCHIVED"
    Then the response status should be 200
    And the project name should be "Patched"
    And the project description should be "after"
    And the project status should be "ARCHIVED"

    When the admin requests the admin view for that project
    Then the response status should be 200
    And the admin project archived fields should be set

  Scenario: Admin can archive a project and it remains visible for UI filtering
    When the admin creates a project named "Archive Me" with description "to be archived"
    Then the response status should be 200
    And the response should contain a project id

    When the admin archives that project
    Then the response status should be 204

    When the admin fetches that project by id
    Then the response status should be 200
    And the project status should be "ARCHIVED"

    When the admin lists projects
    Then the response status should be 200
    And the response should contain a project named "Archive Me"

