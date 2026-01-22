package com.abra.revaissue.E2E.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import com.abra.revaissue.E2E.poms.ProjectsPage;
import com.abra.revaissue.E2E.poms.ProjectAccessManagePage;

import static com.abra.revaissue.E2E.steps.BaseSeleniumTest.driver;
import static org.junit.jupiter.api.Assertions.*;

public class ProjectAccessManagementSteps {

    private ProjectsPage projectsPage;
    private ProjectAccessManagePage accessPage;

    @Given("a test project {string} exists")
    public void a_test_project_exists(String projectName) {
        projectsPage = new ProjectsPage(driver);
        projectsPage.navigateToProjects();
        // Check if any project exists for testing
        int projectCount = projectsPage.getProjectCardCount();
        assertTrue(projectCount > 0, "At least one project should exist for access management testing");
    }

    @When("the admin clicks the manage access button on project {string}")
    public void the_admin_clicks_the_manage_access_button_on_project(String projectName) {
        projectsPage.clickManageAccessButton();
        accessPage = new ProjectAccessManagePage(driver);
    }

    @Then("the manage access dialog is displayed")
    public void the_manage_access_dialog_is_displayed() {
        assertTrue(accessPage.isManageAccessDialogDisplayed(), 
            "Manage access dialog should be displayed");
    }

    @Then("the member list is visible")
    public void the_member_list_is_visible() {
        assertTrue(accessPage.isMemberListVisible(), 
            "Member list should be visible");
    }
}
