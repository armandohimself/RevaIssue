Feature: Admin Login

    Scenario: Admin logs in with valid credentials
        Given  the admin is on the login page
        When   the admin enters a valid username "admin" and password "password"
        And    the admin clicks the login button
        Then   the admin should be redirected to the dashboard

    Scenario: Admin fails to login with invalid credentials
        Given   the admin in on the login page
        When    the admin enter username "invalid" and password "wrong"
        And     the admin clicks on the login button
        Then    the admin should see an error message "Invalid credentials"