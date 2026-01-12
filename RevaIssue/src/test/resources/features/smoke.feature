Feature: Cucumber Smoke Test

  Scenario: Cucumber and JUnit Platform are wired correctly
    Given cucumber is configured
    When the smoke scenario runs
    Then the scenario should pass
    Then I should see the undefined snippet