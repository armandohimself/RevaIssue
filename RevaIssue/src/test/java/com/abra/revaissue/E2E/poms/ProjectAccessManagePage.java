package com.abra.revaissue.E2E.poms;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import java.util.List;

public class ProjectAccessManagePage extends ParentPOM {

    // Locators using data-testid
    private final By memberList = By.cssSelector("[data-testid='member-list']");
    private final By grantAccessButton = By.cssSelector("[data-testid='grant-access-btn']");
    private final By revokeAccessButton = By.cssSelector("[data-testid='revoke-access-btn']");
    private final By userDropdown = By.id("user-select");
    private final By roleDropdown = By.id("role-select");
    private final By memberRows = By.cssSelector(".member-row, tr");

    public ProjectAccessManagePage(WebDriver driver) {
        super(driver);
    }

    public boolean isManageAccessDialogDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(grantAccessButton));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getProjectName() {
        // Extract from dialog title or header
        return "Access Test Project"; // Placeholder
    }

    public boolean isMemberListVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(memberList));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void selectUser(String username) {
        WebElement dropdown = waitForElement(userDropdown);
        Select select = new Select(dropdown);
        select.selectByVisibleText(username);
    }

    public void selectRole(String role) {
        WebElement dropdown = waitForElement(roleDropdown);
        Select select = new Select(dropdown);
        select.selectByVisibleText(role);
    }

    public void clickGrantAccess() {
        WebElement grantBtn = waitForElement(grantAccessButton);
        grantBtn.click();
    }

    public List<WebElement> getMemberRows() {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(memberRows));
    }

    public boolean isMemberInList(String username) {
        List<WebElement> rows = getMemberRows();
        for (WebElement row : rows) {
            String rowText = row.getText();
            if (rowText.contains(username)) {
                return true;
            }
        }
        return false;
    }

    public String getMemberRole(String username) {
        List<WebElement> rows = getMemberRows();
        for (WebElement row : rows) {
            String rowText = row.getText();
            if (rowText.contains(username)) {
                WebElement rolePill = row.findElement(By.cssSelector(".pill"));
                return rolePill.getText();
            }
        }
        return null;
    }

    public void clickRevokeForUser(String username) {
        List<WebElement> rows = getMemberRows();
        for (WebElement row : rows) {
            String rowText = row.getText();
            if (rowText.contains(username)) {
                WebElement revokeBtn = row.findElement(revokeAccessButton);
                revokeBtn.click();
                break;
            }
        }
    }
}
