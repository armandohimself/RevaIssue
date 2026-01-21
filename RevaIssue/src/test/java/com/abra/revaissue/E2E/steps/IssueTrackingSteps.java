package com.abra.revaissue.E2E.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static com.abra.revaissue.E2E.steps.BaseSeleniumTest.driver;

public class IssueTrackingSteps {
    @Given("The user is on the login page")
    public void the_user_is_on_the_login_page() {
        driver.get("http://localhost:4200/login");
    }
    @Given("The user logs in with username {string} and password {string}")
    public void the_user_logs_in_with_username_and_password(String username, String password) {
        WebElement usernameInput = driver.findElement(By.id("username"));
        usernameInput.clear();
        usernameInput.sendKeys(username);
        WebElement passwordInput = driver.findElement(By.id("password"));
        passwordInput.clear();
        passwordInput.sendKeys(password);
        WebElement submit = driver.findElement(By.cssSelector("[type='submit']"));
        submit.click();
    }
    @Given("The user navigates to the Issues page")
    public void the_user_navigates_to_the_issues_page() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @When("The user selects the project {string}")
    public void the_user_selects_the_project(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("A list of issues with project field set to {string} is shown")
    public void a_list_of_issues_with_project_field_set_to_is_shown(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("The issues list contains the issue titled {string}")
    public void the_issues_list_contains_the_issue_titled(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("Each issue row shows Title, Status, Severity, Priority, AssignedTo, CreatedBy and Actions")
    public void each_issue_row_shows_title_status_severity_priority_assigned_to_created_by_and_actions() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
}
