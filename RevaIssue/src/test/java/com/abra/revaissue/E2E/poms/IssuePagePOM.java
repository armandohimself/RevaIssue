package com.abra.revaissue.E2E.poms;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class IssuePagePOM {
    private final WebDriver driver;

    @FindBy(className = "mat-mdc-select-value")
    private WebElement projectDropdown;
    @FindBy(css = "app-issue-view-card .card")
    private WebElement issueDetailsCard;


    public IssuePagePOM(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void openIssuePage(){
        String URL = "http://localhost:4200/issues";
        driver.get(URL);
    }

//    public String getCurrentUrl(){
//        return driver.getCurrentUrl();
//    }

    public void selectProject(String projectName){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        wait.until(ExpectedConditions.elementToBeClickable(projectDropdown)).click();
        WebElement projectText = driver.findElement(By.xpath("//span[contains(text(), '" + projectName + "')]"));
        wait.until(ExpectedConditions.elementToBeClickable(projectText)).click();
        //wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".cdk-overlay-backdrop.cdk-overlay-backdrop-showing")));
    }

    private List<WebElement> getIssueRows() {
        return driver.findElements(By.cssSelector("div.issues-list div.issue-row"));
    }

    private WebElement rowByTitle(String keyword) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        String finalKeyword = keyword.toLowerCase();

        return wait.until(driver -> {
            try{
                List<WebElement> rows = getIssueRows();
                for (WebElement row : rows) {
                    String title = row.findElement(By.cssSelector(".issue-name")).getText().trim().toLowerCase();
                    if (title.contains(finalKeyword)) {
                        return row;
                    }
                }
            } catch(StaleElementReferenceException e){
                return null;
            }
            return null;
        });
    }
    public boolean containsIssueTitle(String keyword) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        keyword = keyword.toLowerCase();

        String finalKeyword = keyword;
        return wait.until(driver -> {
            try{
                List<WebElement> rows = getIssueRows();
                for (WebElement row : rows) {
                    String title = row.findElement(By.cssSelector(".issue-name")).getText().trim().toLowerCase();
                    if (title.contains(finalKeyword)) {
                        return true;
                    }
                }
            } catch(StaleElementReferenceException e){
                return false;
            }
            return false;
        });
    }

    public void clickIssue(String title){
        WebElement row = rowByTitle(title);
        row.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        wait.until(ExpectedConditions.visibilityOf(issueDetailsCard));
    }
    public String getSeverityForIssue(String title) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        WebElement row = rowByTitle(title);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".issue-severity")));
        return row.findElement(By.cssSelector(".issue-severity")).getText().trim();
    }

//    public String getPriorityForIssue(String title) {
//        WebElement row = rowByTitle(title);
//        return row.findElement(By.cssSelector(".issue-priority")).getText().trim();
//    }
//
//    public String getAssignedToForIssue(String title) {
//        WebElement row = rowByTitle(title);
//        return row.findElements(By.cssSelector(".issue-user")).getFirst().getText().trim();
//    }
//
//    public String getCreatedByForIssue(String title) {
//        WebElement row = rowByTitle(title);
//        return row.findElements(By.cssSelector(".issue-user")).get(1).getText().trim();
//    }

    public String getSelectedStatusForIssue(String title) {
        WebElement row = rowByTitle(title);
        WebElement select = row.findElement(By.cssSelector("select.status-select"));
        return new org.openqa.selenium.support.ui.Select(select).getFirstSelectedOption().getText();
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
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("app-issue-edit-card .overlay")));
    }

    public void selectEditSeverity(String severityValue) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));

        WebElement severitySelect = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("app-issue-edit-card select#severity")));

        new Select(severitySelect).selectByValue(severityValue);
    }

    public void clickEditUpdateButton() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));

        By updateBtn = By.xpath("//app-issue-edit-card//button[.//span[@class='mdc-button__label' and text()='Update']]");
        wait.until(ExpectedConditions.elementToBeClickable(updateBtn)).click();
        //wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("app-issue-edit-card .overlay")));
    }

    public boolean isHistorySectionVisible() {
        return !driver.findElements(By.cssSelector("app-issue-view-card .logs-section")).isEmpty();
    }

    public boolean historyContains(String match) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));

        WebElement logsList = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("app-issue-view-card .logs-list")));

        String text = logsList.getText().toLowerCase();
        return match != null && text.contains(match.toLowerCase());
    }

    public void clickCreateButton() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));

        By createBtn = By.xpath("//button[.//span[@class='mdc-button__label' and text()=' Create Issue ']]");
        wait.until(ExpectedConditions.elementToBeClickable(createBtn)).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("app-issue-create-card .overlay")));

    }

    public void enterCreateIssueTitle(String title) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        WebElement titleInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("app-issue-create-card input#name")));
        titleInput.clear();
        titleInput.sendKeys(title);
    }

    public void enterCreateIssueDescription(String description) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        WebElement descInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("app-issue-create-card textarea#description")));
        descInput.clear();
        descInput.sendKeys(description);
    }

    public void selectCreateSeverity(String severityValue) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        WebElement severitySelect = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("app-issue-create-card select#severity")));
        new Select(severitySelect).selectByValue(severityValue);
    }

    public void selectCreatePriority(String priorityValue) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        WebElement prioritySelect = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("app-issue-create-card select#priority")));
        new Select(prioritySelect).selectByValue(priorityValue);
    }

    public void clickCreateActionButton() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));

        By createBtn = By.xpath("//app-issue-create-card//button[.//span[@class='mdc-button__label' and text()='Create']]");
        wait.until(ExpectedConditions.elementToBeClickable(createBtn)).click();
        //wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("app-issue-create-card .overlay")));
    }

    public void selectStatusForIssue(String title, String status) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        WebElement row = rowByTitle(title);
        WebElement statusSelect = row.findElement(By.cssSelector("select.status-select"));
        wait.until(ExpectedConditions.elementToBeClickable(statusSelect));
        new Select(statusSelect).selectByValue(status);
    }

    public void enterSearchWord(String keyword){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        WebElement searchbar = driver.findElement(By.cssSelector("input[matinput]"));
        wait.until(ExpectedConditions.elementToBeClickable(searchbar)).click();
        searchbar.clear();
        searchbar.sendKeys(keyword);
    }

    public void clickSearchBar(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        WebElement searchbar = driver.findElement(By.cssSelector("input[matinput]"));
        wait.until(ExpectedConditions.elementToBeClickable(searchbar)).click();
    }

    public boolean issueListContainsOnlyKeyword(String keyword) {
        List<WebElement> rows = driver.findElements(By.cssSelector("div.issues-list div.issue-row"));
        keyword = keyword.toLowerCase();

        for (WebElement row : rows) {
            String title = row.findElement(By.cssSelector(".issue-name")).getText().trim().toLowerCase();
            if (!title.contains(keyword)) {
                return false;
            }
        }
        return true;
    }
    public boolean issueListContainsOnlyStatus(String status) {
        List<WebElement> rows = driver.findElements(By.cssSelector("div.issues-list div.issue-row"));

        for (WebElement row : rows) {
            WebElement element = row.findElement(By.cssSelector("select.status-select"));
            String actualStatus = new Select(element).getFirstSelectedOption().getText().trim();
            if (!actualStatus.equals(status)) {
                return false;
            }
        }
        return true;
    }
    public boolean issueListContainsOnlyPriority(String priority) {
        List<WebElement> rows = driver.findElements(By.cssSelector("div.issues-list div.issue-row"));

        for (WebElement row : rows) {
            String actualPriority = row.findElement(By.cssSelector(".issue-priority")).getText().trim();
            if (!actualPriority.equals(priority)) {
                return false;
            }
        }
        return true;
    }
    public boolean issueListContainsOnlySeverity(String severity) {
        List<WebElement> rows = driver.findElements(By.cssSelector("div.issues-list div.issue-row"));

        for (WebElement row : rows) {
            String actualSeverity = row.findElement(By.cssSelector(".issue-severity")).getText().trim();
            if (!actualSeverity.equals(severity)) {
                return false;
            }
        }
        return true;
    }

    public void clickSelectByText(String selectType){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(@class, 'mat-mdc-select-min-line') and text()='" + selectType + "']"))).click();
    }

    public void selectOption(String option){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(@class, 'mdc-list-item__primary-text') and text()='" + option + "']"))).click();
        //wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".cdk-overlay-popover cdk-overlay-connected-position-bounding-box")));
    }

    public void filterByStatus(String status){
        clickSelectByText("All Statuses");
        selectOption(status);
    }
    public void filterBySeverity(String severity){
        clickSelectByText("All Severities");
        selectOption(severity);
    }
    public void filterByPriority(String priority){
        clickSelectByText("All Priorities");
        selectOption(priority);
    }
}
