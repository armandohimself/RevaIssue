Feature: Admin Audit Logs

  Background:
    Given the admin is on the login page
    When  the admin enters a valid username "admin" and password "password"
    And   the admin clicks the login button
    Then  the admin should be redirected to the dashboard

  Scenario: Admin sees empty state when no logs exist
    Given the admin is on the admin dashboard
    When  the admin clicks on the "Logs" tab
    And   there are no logs in the system
    Then  the admin should see a "No logs found" message

  Scenario: Logs are automatically generated for state changes
    Given the admin is on the admin dashboard
    And   the admin clicks on the "Add User" tab
    When  the admin creates a new user with username "auditTestUser"
    And   the admin clicks on the "Logs" tab
    Then  the admin should see a log entry for user creation
    And   the log message should mention "auditTestUser"
    And   the log should show the admin as the acting user

  Scenario: Admin views all audit logs
    Given the admin is on the admin dashboard
    When  the admin clicks on the "Logs" tab
    Then  the admin should see a list of audit logs
    And   each log entry should display date, user, entity type, and message
    And   the logs should be sorted by date descending

  Scenario: Admin views log details
    Given the admin is on the admin dashboard
    And   the admin clicks on the "Logs" tab
    When  the admin views a log entry
    Then  the log should show the acting user's username
    And   the log should show the entity type
    And   the log should show a descriptive message
    And   the log should show a timestamp

  Scenario: Admin sees loading state while fetching logs
    Given the admin is on the admin dashboard
    When  the admin clicks on the "Logs" tab
    Then  the admin should see a loading spinner
    And   the loading spinner should disappear when logs are loaded
