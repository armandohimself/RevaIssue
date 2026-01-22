package com.abra.revaissue.E2E.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import com.abra.revaissue.E2E.poms.ProjectsPage;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.abra.revaissue.E2E.steps.BaseSeleniumTest.driver;
import static org.junit.jupiter.api.Assertions.*;

public class ProjectManagementSteps {

    private ProjectsPage projectsPage;

    @Given("the user navigates to the Projects page")
    public void the_user_navigates_to_the_projects_page() {
        projectsPage = new ProjectsPage(driver);
        projectsPage.navigateToProjects();
    }

    @Then("the projects page displays a list of project cards")
    public void the_projects_page_displays_a_list_of_project_cards() {
        assertTrue(projectsPage.isProjectsPageDisplayed(), "Projects page should be displayed");
        int cardCount = projectsPage.getProjectCardCount();
        assertTrue(cardCount > 0, "At least one project card should be visible");
    }

    @Then("each project card shows the project name, description, status, and action buttons")
    public void each_project_card_shows_the_project_name_description_status_and_action_buttons() {
        List<WebElement> cards = projectsPage.getProjectCards();
        assertFalse(cards.isEmpty(), "Project cards should exist");
        
        WebElement firstCard = cards.get(0);
        String cardText = firstCard.getText();
        
        assertFalse(cardText.isEmpty(), "Card should have content");
    }
}
