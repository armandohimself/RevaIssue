package com.abra.revaissue.E2E.poms;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LogsPagePOM extends ParentPOM {

    private WebDriverWait wait;

    // Locators
    private By logsTable = By.cssSelector("table.logs-table");
    private By logRows = By.cssSelector("table.logs-table tbody tr");
    private By paginator = By.cssSelector("mat-paginator");
    private By nextPageButton = By.cssSelector("button.mat-mdc-paginator-navigation-next");
    private By previousPageButton = By.cssSelector("button.mat-mdc-paginator-navigation-previous");
    private By pageSizeSelect = By.cssSelector("mat-select[aria-label*='Items per page']");
    private By loadingSpinner = By.cssSelector("mat-spinner");
    private By emptyState = By.cssSelector(".no-data");

    public LogsPagePOM(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void clickTab(String tabName) {
        String id = "tab-label-" + tabName.toLowerCase().replace(" ", "-");

        WebElement tab = null;
        try {
            WebElement label = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
            tab = label.findElement(By.xpath("ancestor::div[@role='tab']"));
            wait.until(ExpectedConditions.elementToBeClickable(tab)).click();
        } catch (Exception e) {
            try {
                tab = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[@role='tab' and contains(., '" + tabName + "')]")
                ));
                tab.click();
            } catch (Exception e2) {
                tab = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[contains(text(), '" + tabName + "')]/ancestor::div[@role='tab']")
                ));
                tab.click();
            }
        }
        
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public boolean hasLogEntries() {
        try {
            List<WebElement> rows = driver.findElements(logRows);
            return rows.size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public List<WebElement> getLogRows() {
        wait.until(ExpectedConditions.presenceOfElementLocated(logsTable));
        return driver.findElements(logRows);
    }

    public boolean hasPaginator() {
        try {
            WebElement paginatorElement = driver.findElement(paginator);
            return paginatorElement.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickNextPage() {
        WebElement nextButton = wait.until(
            ExpectedConditions.elementToBeClickable(nextPageButton)
        );
        nextButton.click();
        waitForLogsToLoad();
    }

    public void clickPreviousPage() {
        WebElement prevButton = wait.until(
            ExpectedConditions.elementToBeClickable(previousPageButton)
        );
        prevButton.click();
        waitForLogsToLoad();
    }

    public void selectPageSize(int size) {
        WebElement pageSizeDropdown = wait.until(
            ExpectedConditions.elementToBeClickable(pageSizeSelect)
        );
        pageSizeDropdown.click();
        
        WebElement sizeOption = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//mat-option//span[contains(text(), '" + size + "')]")
            )
        );
        sizeOption.click();
        waitForLogsToLoad();
    }

    public boolean hasEmptyState() {
        try {
            WebElement empty = driver.findElement(emptyState);
            return empty.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isLoading() {
        try {
            WebElement spinner = driver.findElement(loadingSpinner);
            return spinner.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void waitForLogsToLoad() {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(loadingSpinner));
        } catch (Exception e) {
            // Spinner may not show up if loading was instant
        }
        
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public WebElement getLogEntry(int index) {
        List<WebElement> rows = getLogRows();
        if (index < rows.size()) {
            return rows.get(index);
        }
        throw new IndexOutOfBoundsException("Log entry index out of bounds: " + index);
    }

    public String getLogDate(int index) {
        WebElement row = getLogEntry(index);
        return row.findElement(By.xpath(".//td[1]")).getText();
    }

    public String getLogUser(int index) {
        WebElement row = getLogEntry(index);
        return row.findElement(By.xpath(".//td[2]")).getText();
    }

    public String getLogEntityType(int index) {
        WebElement row = getLogEntry(index);
        return row.findElement(By.xpath(".//td[3]")).getText();
    }

    public String getLogMessage(int index) {
        WebElement row = getLogEntry(index);
        return row.findElement(By.xpath(".//td[4]")).getText();
    }

    public boolean hasLogWithMessage(String messageText) {
        List<WebElement> messageCells = driver.findElements(
            By.xpath("//table//td[4][contains(text(), '" + messageText + "')]")
        );
        return messageCells.size() > 0;
    }

    public boolean hasLogByUser(String username) {
        List<WebElement> userCells = driver.findElements(
            By.xpath("//table//td[2][contains(text(), '" + username + "')]")
        );
        return userCells.size() > 0;
    }

    public boolean hasLogForEntityType(String entityType) {
        List<WebElement> entityTypeCells = driver.findElements(
            By.xpath("//table//td[3]//mat-chip[contains(text(), '" + entityType + "')]")
        );
        return entityTypeCells.size() > 0;
    }
    
    public int getLogCount() {
        return getLogRows().size();
    }
}
