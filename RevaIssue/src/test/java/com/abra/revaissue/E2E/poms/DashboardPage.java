package com.abra.revaissue.E2E.poms;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DashboardPage extends ParentPOM {
    
    private WebDriverWait wait;
    
    private By dashboardTitle = By.xpath("//*[contains(text(), 'Admin Dashboard')]");
    
    public DashboardPage(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    
    public boolean isDashboardDisplayed() {
        try {
            wait.until(ExpectedConditions.urlContains("/admin/dashboard"));
            wait.until(ExpectedConditions.presenceOfElementLocated(dashboardTitle));
            return true;
        } catch (Exception e) {
            System.out.println("Dashboard not found. URL: " + driver.getCurrentUrl());
            return false;
        }
    }
}
