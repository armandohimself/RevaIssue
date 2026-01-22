Feature: Adding a comment to an issue

    Background:
        Given the user is logged in as "tester" with password "password"
        And the user opens the issue card

    Scenario: User adds a comment to an issue
        When the user types "This is my comment" into the comment input
        And the user submits the comment
        Then the comment table shows "This is my comment"
        And the comment author is "tester"

    Scenario: New comment appears on the bottom of the comment list
        When the user types "First comment" into the comment input
        And the user submits the comment
        And the user types "Second comment" into the comment input
        And the user submits the comment
        Then the comment table shows "First comment" before "Second comment"
        And the comment author for both comments is "tester"

    Scenario: User submits a comment using the Enter key
        When the user types "Comment via Enter key" into the comment input
        And the user presses the Enter key to submit the comment
        Then the comment table shows "Comment via Enter key"
        And the comment author is "tester"

    Scenario: User tries to submit an empty comment
        When the user types "" into the comment input
        And the user presses the Enter key to submit the comment
        Then the comment table does not show an empty comment