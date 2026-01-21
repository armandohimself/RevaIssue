package com.abra.revaissue.E2E.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import static com.abra.revaissue.E2E.steps.BaseSeleniumTest.issuePage;

public class IssueFilteringSteps {
    @When("The user clicks the search bar")
    public void the_user_clicks_the_search_bar() {
        issuePage.clickSearchBar();
    }
    @When("The user enters the word {string}")
    public void the_user_enters_the_word(String string) {
        issuePage.enterSearchWord(string);
    }
    @Then("The issues list contains only issues with {string} in title")
    public void the_issues_list_contains_only_issues_with_in_title(String string) {
        Assertions.assertTrue(issuePage.issueListContainsOnlyKeyword(string));
    }
    @When("The user selects the status {string} from the status filter")
    public void the_user_selects_the_status_from_the_status_filter(String status) {
        issuePage.filterByStatus(status);
    }
    @Then("The issues list contains only issues with {string} status")
    public void the_issues_list_contains_only_issues_with_status(String status) {
        Assertions.assertTrue(issuePage.issueListContainsOnlyStatus(status));
    }
    @When("The user selects the severity {string} from the  severity filter")
    public void the_user_selects_the_severity_from_the_severity_filter(String severity) {
        issuePage.filterBySeverity(severity);
    }
    @Then("The issues list contains only issues with {string} severity")
    public void the_issues_list_contains_only_issues_with_severity(String severity) {
        Assertions.assertTrue(issuePage.issueListContainsOnlySeverity(severity));
    }
    @When("The user selects the priority {string} from the  priority filter")
    public void the_user_selects_the_priority_from_the_priority_filter(String priority) {
        issuePage.filterByPriority(priority);
    }
    @Then("The issues list contains only issues with {string} priority")
    public void the_issues_list_contains_only_issues_with_priority(String priority) {
        Assertions.assertTrue(issuePage.issueListContainsOnlyPriority(priority));
    }
}
