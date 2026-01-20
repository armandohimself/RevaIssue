package com.abra.revaissue.E2E.poms;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class IssuePagePOM {
    private WebDriver driver;
    private final String URL = "http://localhost:4200/issues";

    private WebElement projectDropdown;
    private WebElement issueTable;
    private List<WebElement> issueRows;
    private WebElement editButton;
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


}
