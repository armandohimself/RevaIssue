package com.abra.revaissue.E2E.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import static com.abra.revaissue.E2E.steps.BaseSeleniumTest.issuePage;


public class IssueWorkFlowSteps {
    @When("The user clicks the create issue button")
    public void the_user_clicks_the_create_issue_button() {
        issuePage.clickCreateButton();
    }
    @When("The user enters issue title {string}")
    public void the_user_enters_issue_title(String string) {
        issuePage.enterCreateIssueTitle(string);
    }
    @When("The user enters issue description {string}")
    public void the_user_enters_issue_description(String string) {
        issuePage.enterCreateIssueDescription(string);
    }
    @When("The user selects severity {string} from the create issue dropdown")
    public void the_user_selects_severity_from_the_create_issue_dropdown(String string) {
        issuePage.selectCreateSeverity(string);
    }
    @When("The user selects priority {string} from the create issue dropdown")
    public void the_user_selects_priority_from_the_create_issue_dropdown(String string) {
        issuePage.selectCreatePriority(string);
    }
    @When("The user clicks the create button")
    public void the_user_clicks_the_create_button() {
        issuePage.clickCreateActionButton();
    }
    @When("The user changes status of issue {string} from {string} to {string}")
    public void the_user_changes_status_of_issue_from_to(String issue, String initialStatus, String newStatus) {
        issuePage.selectStatusForIssue(issue, newStatus);
    }
    @Then("The issue {string} displays status {string}")
    public void the_issue_displays_status(String issue, String status) {
        Assertions.assertEquals(status, issuePage.getSelectedStatusForIssue(issue));
    }
}
