package com.abra.revaissue.E2E.poms;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.List;

public class ProjectsPage extends ParentPOM {

    private final String URL = "http://localhost:4200/projects";

    // Locators using data-testid and text
    private final By projectCards = By.cssSelector("[data-testid='project-card']");
    private final By manageAccessButton = By.cssSelector("[data-testid='manage-access-btn']");

    public ProjectsPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToProjects() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("app-app-shell")));
        driver.get(URL);
        wait.until(ExpectedConditions.urlContains("/projects"));
    }

    public boolean isProjectsPageDisplayed() {
        try {
            wait.until(ExpectedConditions.urlContains("/projects"));
            return true;
        } catch (Exception e) {
            System.out.println("Projects page not found. URL: " + driver.getCurrentUrl());
            return false;
        }
    }

    public List<WebElement> getProjectCards() {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(projectCards));
    }

    public int getProjectCardCount() {
        return getProjectCards().size();
    }

    public WebElement getProjectCardByName(String projectName) {
        List<WebElement> cards = getProjectCards();
        for (WebElement card : cards) {
            String cardText = card.getText();
            if (cardText.contains(projectName)) {
                return card;
            }
        }
        return null;
    }

    public void clickCreateProject() {
        // Implementation pending - depends on create project button/dialog
    }

    public void clickManageAccessButton() {
        WebElement manageBtn = waitForElement(manageAccessButton);
        manageBtn.click();
    }

    public void clickEditOnProject(String projectName) {
        // Implementation pending - find edit button in specific card
    }

    public void clickArchiveOnProject(String projectName) {
        // Implementation pending - find archive button in specific card
    }

    public String getProjectStatus(String projectName) {
        WebElement card = getProjectCardByName(projectName);
        if (card != null) {
            WebElement statusBadge = card.findElement(By.cssSelector(".badge"));
            return statusBadge.getText();
        }
        return null;
    }

    public boolean isArchiveButtonDisabled(String projectName) {
        WebElement card = getProjectCardByName(projectName);
        if (card != null) {
            // Find archive button within card - implementation pending
            return false;
        }
        return false;
    }
}
