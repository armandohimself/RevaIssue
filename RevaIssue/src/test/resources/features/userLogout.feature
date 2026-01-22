Feature: User Logout

  Background:
    Given the user is on the login page
    And   the user logs in with username "admin" and password "password"

  Scenario: User logs out successfully
    Given the user is logged in and on the dashboard
    When  the user clicks the logout button
    And   the user confirms logout in the dialog
    Then  the user should be redirected to the login page
    And   the JWT token should be cleared

  Scenario: User cancels logout
    Given the user is logged in and on the dashboard
    When  the user clicks the logout button
    And   the user cancels logout in the dialog
    Then  the user should remain on the dashboard
    And   the JWT token should still be present

  Scenario: Logout clears session and prevents access
    Given the user is logged in and on the dashboard
    When  the user logs out successfully
    Then  the user should be redirected to the login page
    When  the user tries to navigate to "/admin/dashboard"
    Then  the user should be redirected to the login page

  Scenario: User can login again after logout
    Given the user is logged in and on the dashboard
    When  the user logs out successfully
    Then  the user should be redirected to the login page
    When  the user logs in with username "admin" and password "password"
    Then  the user should be redirected to the dashboard
