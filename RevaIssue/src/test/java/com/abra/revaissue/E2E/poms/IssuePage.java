package com.abra.revaissue.E2E.poms;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class IssuePage extends ParentPOM {

    private final String URL = "http://localhost:4200/issues";

    public IssuePage(WebDriver driver) {
        super(driver);
    }

    public void openIssuePage() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("app-app-shell")));
        driver.get(URL);
    }

    public void openFirstIssueCard() {
        WebElement firstIssueCard = waitForElement(By.tagName("app-issue-card"));
        firstIssueCard.click();
    }
}
