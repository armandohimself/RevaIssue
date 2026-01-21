package com.abra.revaissue.E2E.poms;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class IssuePagePOM {
    private WebDriver driver;
    private final String URL = "http://localhost:4200/issues";

    @FindBy(className = "mat-mdc-select-value")
    private WebElement projectDropdown;
    @FindBy(css = "div.issues-list")
    private WebElement issuesList;
    @FindBy(css = "div.issues-list div.issue-row")
    private List<WebElement> issueRows;
    @FindBy(css = "div.issue-row .issue-name")
    private List<WebElement> issueTitles;
    @FindBy(css = "div.issue-row .issue-severity")
    private List<WebElement> issueSeverities;
    @FindBy(css = "div.issue-row .issue-priority")
    private List<WebElement> issuePriorities;
    @FindBy(css = "div.issue-row select.status-select")
    private List<WebElement> issueStatusSelects;
    @FindBy(css = "div.issue-row button.edit-btn")
    private List<WebElement> issueEditButtons;
    @FindBy(css = "app-issue-view-card .card")
    private WebElement issueDetailsCard;
    @FindBy(css = "app-issue-view-card .close-btn")
    private WebElement closeIssueDetailsButton;

    private WebElement updateButton;
    private WebElement severityDropdown;
    private WebElement historyLog;

    public IssuePagePOM(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void openIssuePage(){
        driver.get(URL);
    }

    public String getCurrentUrl(){
        return driver.getCurrentUrl();
    }

    public void selectProject(String projectName){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.elementToBeClickable(projectDropdown)).click();
        WebElement projectText = driver.findElement(By.xpath("//span[contains(text(), '" + projectName + "')]"));
        wait.until(ExpectedConditions.elementToBeClickable(projectText)).click();
        By backdrop = By.cssSelector(".cdk-overlay-backdrop.cdk-overlay-backdrop-showing");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(backdrop));
    }

    private WebElement rowByTitle(String title) {
        return issueRows.stream()
                .filter(r -> r.findElement(By.cssSelector(".issue-name")).getText().trim().equals(title))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No issue row found with title: " + title));
    }
    public boolean containsIssueTitle(String title) {
        return issueRows.stream().anyMatch(r ->
                r.findElement(By.cssSelector(".issue-name")).getText().trim().equals(title)
        );
    }

    public void clickIssue(String title){
        WebElement row = rowByTitle(title);
        row.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOf(issueDetailsCard));
    }
    public String getSeverityForIssue(String title) {
        WebElement row = rowByTitle(title);
        return row.findElement(By.cssSelector(".issue-severity")).getText().trim();
    }

    public String getPriorityForIssue(String title) {
        WebElement row = rowByTitle(title);
        return row.findElement(By.cssSelector(".issue-priority")).getText().trim();
    }

    public String getAssignedToForIssue(String title) {
        WebElement row = rowByTitle(title);
        return row.findElements(By.cssSelector(".issue-user")).get(0).getText().trim();
    }

    public String getCreatedByForIssue(String title) {
        WebElement row = rowByTitle(title);
        return row.findElements(By.cssSelector(".issue-user")).get(1).getText().trim();
    }

    public String getSelectedStatusForIssue(String title) {
        WebElement row = rowByTitle(title);
        WebElement select = row.findElement(By.cssSelector("select.status-select"));
        return new org.openqa.selenium.support.ui.Select(select).getFirstSelectedOption().getText().trim();
    }

    public String getIssueDetailStatus() {
        return driver.findElement(By.cssSelector("app-issue-view-card .status-badge")).getText().trim();
    }

    public String getIssueDetailSeverity() {
        return driver.findElement(By.xpath("//app-issue-view-card//label[text()='Severity']/following-sibling::span")).getText().trim();
    }

    public String getIssueDetailPriority() {
        return driver.findElement(By.xpath("//app-issue-view-card//label[text()='Priority']/following-sibling::span")).getText().trim();
    }

    public void clickEditButtonForIssue(String title) {
        WebElement row = rowByTitle(title);
        row.findElement(By.cssSelector("button.edit-btn")).click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("app-issue-edit-card .overlay")));
    }

    public void selectEditSeverity(String severityValue) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement severitySelect = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("app-issue-edit-card select#severity"))
        );

        new Select(severitySelect).selectByValue(severityValue);
    }

    public void clickEditUpdateButton() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        By updateBtn = By.xpath("//app-issue-edit-card//button[.//span[@class='mdc-button__label' and text()='Update']]");
        wait.until(ExpectedConditions.elementToBeClickable(updateBtn)).click();

        By overlay = By.cssSelector("app-issue-edit-card .overlay");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(overlay));
    }

    public void waitForIssueDetailsToOpen() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("app-issue-view-card .overlay")));
    }

    public boolean isHistorySectionVisible() {
        return !driver.findElements(By.cssSelector("app-issue-view-card .logs-section")).isEmpty();
    }

    public boolean historyContains(String match) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement logsList = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("app-issue-view-card .logs-list"))
        );

        String text = logsList.getText().toLowerCase();
        return match != null && text.contains(match.toLowerCase());
    }


}
