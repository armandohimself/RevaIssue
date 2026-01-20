package com.abra.revaissue.E2E.poms;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage extends ParentPOM {
    
    private WebDriverWait wait;
    
    private By usernameInput = By.id("username");
    private By passwordInput = By.id("password");
    private By loginButton = By.cssSelector("button[type='submit']");
    private By errorMessage = By.className("error-message");
    
    public LoginPage(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    
    public void navigateTo(String url) {
        driver.get(url);
        wait.until(ExpectedConditions.presenceOfElementLocated(usernameInput));
    }
    
    public void enterUsername(String username) {
        wait.until(ExpectedConditions.presenceOfElementLocated(usernameInput));
        driver.findElement(usernameInput).clear();
        driver.findElement(usernameInput).sendKeys(username);
    }
    
    public void enterPassword(String password) {
        wait.until(ExpectedConditions.presenceOfElementLocated(passwordInput));
        driver.findElement(passwordInput).clear();
        driver.findElement(passwordInput).sendKeys(password);
    }
    
    public void clickLoginButton() {
        wait.until(ExpectedConditions.elementToBeClickable(loginButton));
        driver.findElement(loginButton).click();
    }
    
    public boolean isOnLoginPage() {
        return driver.getCurrentUrl().contains("/login");
    }
    
    public boolean isErrorMessageDisplayed() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(errorMessage));
            wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage));
            return true;
        } catch (Exception e) {
            System.out.println("Error message not found after waiting");
            return false;
        }
    }
    
    public String getErrorMessage() {
        return driver.findElement(errorMessage).getText();
    }
}
