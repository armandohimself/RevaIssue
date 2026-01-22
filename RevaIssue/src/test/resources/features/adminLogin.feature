Feature: Admin Authentication (API)

    # Scenario 1: Login gives a token
    Scenario: Admin can login and receives a token
        Given  the API base url is configured
        When   the admin logs in with username "admin" and password "password"
        Then   the response status should be 200
        And    the response should contain a token

    # Scenario 2: Token works for /me
    Scenario: Admin token allows access to /me
        Given   the API base url is configured
        When    the admin logs in with username "admin" and password "password"
        Then    the response should contain a token
        When    the client calls "/users/me" with that token
        Then    the response status should be 200
        And     the current user name should be "admin"

    # Scenario: Admin fails to login with invalid credentials
    #     Given   the admin is on the login page
    #     When    the admin enter username "invalid" and password "wrong"
    #     And     the admin clicks on the login button
    #     Then    the admin should see an error message "Invalid credentials"
