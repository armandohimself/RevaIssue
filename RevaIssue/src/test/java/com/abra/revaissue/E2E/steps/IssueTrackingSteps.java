package com.abra.revaissue.E2E.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static com.abra.revaissue.E2E.steps.BaseSeleniumTest.*;

public class IssueTrackingSteps {
    @Given("The user is on the login page")
    public void the_user_is_on_the_login_page() {
        loginPage.navigateTo("http://localhost:4200/login");
    }
    @Given("The user logs in with username {string} and password {string}")
    public void the_user_logs_in_with_username_and_password(String username, String password) {
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
        loginPage.clickLoginButton();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlContains("/user/dashboard"));
    }
    @Given("The user navigates to the Issues page")
    public void the_user_navigates_to_the_issues_page() {
        issuePage.openIssuePage();
    }
    @When("The user selects the project {string}")
    public void the_user_selects_the_project(String string) {
        issuePage.selectProject(string);
    }
    @Then("The issues list contains the issue titled {string}")
    public void the_issues_list_contains_the_issue_titled(String string) {
        Assertions.assertTrue(issuePage.containsIssueTitle(string));
    }
    @When("The user clicks the issue titled {string}")
    public void the_user_clicks_the_issue_titled(String string) {
        issuePage.clickIssue(string);
    }
    @Then("The issue displays status {string} and severity {string} and priority {string}")
    public void the_issue_displays_status_and_severity_and_priority(String status, String severity, String priority) {
        Assertions.assertEquals(status, issuePage.getIssueDetailStatus());
        Assertions.assertEquals(severity, issuePage.getIssueDetailSeverity());
        Assertions.assertEquals(priority, issuePage.getIssueDetailPriority());
    }
    @When("The user clicks the action button for issue {string}")
    public void the_user_clicks_the_action_button_for_issue(String issue) {
        issuePage.clickEditButtonForIssue(issue);
    }
    @When("The user selects severity {string} from the dropdown")
    public void the_user_selects_severity_from_the_dropdown(String severity) {
        issuePage.selectEditSeverity(severity);
    }
    @When("The user clicks the update button")
    public void the_user_clicks_the_update_button() {
        issuePage.clickEditUpdateButton();
    }
    @Then("The issue {string} displays severity {string}")
    public void issue_displays_severity(String issue, String severity) {
        Assertions.assertEquals(severity, issuePage.getSeverityForIssue(issue));
    }
    @Then("The issue {string} displays a list of history logs")
    public void the_issue_displays_a_list_of_history_logs(String issue) {
        issuePage.clickIssue(issue);
        Assertions.assertTrue(issuePage.isHistorySectionVisible());

    }
    @Then("The history logs contain an entry mentioning {string} and {string}")
    public void the_history_logs_contain_an_entry_mentioning_and(String string, String string2) {
        Assertions.assertTrue(issuePage.historyContains(string));
        Assertions.assertTrue(issuePage.historyContains(string2));
    }
}
