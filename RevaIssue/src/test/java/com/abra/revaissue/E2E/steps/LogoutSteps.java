package com.abra.revaissue.E2E.steps;

import static com.abra.revaissue.E2E.steps.BaseSeleniumTest.driver;
import static org.junit.jupiter.api.Assertions.*;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.abra.revaissue.E2E.poms.DashboardPage;
import com.abra.revaissue.E2E.poms.LoginPage;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;

public class LogoutSteps {

    private LoginPage loginPage;
    private DashboardPage dashboardPage;
    private WebDriverWait wait;
    private String tokenBeforeLogout;

    public LogoutSteps() {
        this.loginPage = new LoginPage(driver);
        this.dashboardPage = new DashboardPage(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Given("the user is on the login page")
    public void the_user_is_on_the_login_page() {
        loginPage.navigateTo("http://localhost:4200/login");
        assertTrue(loginPage.isOnLoginPage());
    }

    @Given("the user logs in with username {string} and password {string}")
    public void the_user_logs_in_with_username_and_password(String username, String password) {
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
        loginPage.clickLoginButton();
        
        // Wait for redirect to dashboard
        wait.until(ExpectedConditions.urlContains("dashboard"));
    }

    @Given("the user is logged in and on the dashboard")
    public void the_user_is_logged_in_and_on_the_dashboard() {
        // Verify we're on the dashboard
        assertTrue(dashboardPage.isDashboardDisplayed() || driver.getCurrentUrl().contains("dashboard"));
        
        // Store token before logout
        JavascriptExecutor js = (JavascriptExecutor) driver;
        tokenBeforeLogout = (String) js.executeScript("return localStorage.getItem('REVAISSUE_TOKEN');");
        assertNotNull(tokenBeforeLogout, "Token should exist before logout");
    }

    @When("the user clicks the logout button")
    public void the_user_clicks_the_logout_button() {
        // Find and click logout button in the nav list
        WebElement logoutButton = wait.until(
            ExpectedConditions.elementToBeClickable(By.xpath("//a[@mat-list-item and contains(., 'Logout')]"))
        );
        logoutButton.click();
        
        // Wait for logout dialog to appear
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("mat-dialog-container")));
    }

    @When("the user confirms logout in the dialog")
    public void the_user_confirms_logout_in_the_dialog() {
        // Prefer stable ID first
        try {
            WebElement confirmById = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("confirm-logout"))
            );
            confirmById.click();
        } catch (Exception e) {
            // Fallbacks
            WebElement confirmButton = null;
            try {
                confirmButton = wait.until(
                    ExpectedConditions.elementToBeClickable(
                        By.xpath("//mat-dialog-actions//button[contains(., 'Logout')]")
                    )
                );
                confirmButton.click();
            } catch (Exception e2) {
                confirmButton = wait.until(
                    ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[@mat-dialog-close='true']")
                    )
                );
                confirmButton.click();
            }
        }
        
        // Wait a moment for the dialog to close and navigation to occur
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @When("the user cancels logout in the dialog")
    public void the_user_cancels_logout_in_the_dialog() {
        // Click the "Cancel" button (prefer ID)
        try {
            WebElement cancelById = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("cancel-logout"))
            );
            cancelById.click();
        } catch (Exception e) {
            WebElement cancelButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[@mat-button and contains(., 'Cancel')]")
                )
            );
            cancelButton.click();
        }
        
        // Wait for dialog to close
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("mat-dialog-container")));
    }

    @When("the user logs out successfully")
    public void the_user_logs_out_successfully() {
        the_user_clicks_the_logout_button();
        the_user_confirms_logout_in_the_dialog();
    }

    @When("the user tries to navigate to {string}")
    public void the_user_tries_to_navigate_to(String url) {
        driver.get("http://localhost:4200" + url);
        
        // Wait for page to load
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Then("the user should be redirected to the login page")
    public void the_user_should_be_redirected_to_the_login_page() {
        wait.until(ExpectedConditions.urlContains("/login"));
        assertTrue(driver.getCurrentUrl().contains("/login"), 
            "User should be on login page but was on: " + driver.getCurrentUrl());
    }

    @Then("the JWT token should be cleared")
    public void the_jwt_token_should_be_cleared() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String currentToken = (String) js.executeScript("return localStorage.getItem('REVAISSUE_TOKEN');");
        assertNull(currentToken, "Token should be null after logout");
    }

    @Then("the user should remain on the dashboard")
    public void the_user_should_remain_on_the_dashboard() {
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("dashboard"), 
            "User should remain on dashboard but was on: " + currentUrl);
    }

    @Then("the JWT token should still be present")
    public void the_jwt_token_should_still_be_present() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String currentToken = (String) js.executeScript("return localStorage.getItem('REVAISSUE_TOKEN');");
        assertNotNull(currentToken, "Token should still exist after canceling logout");
        assertEquals(tokenBeforeLogout, currentToken, "Token should be unchanged");
    }

    @Then("the user should be redirected to the dashboard")
    public void the_user_should_be_redirected_to_the_dashboard() {
        wait.until(ExpectedConditions.urlContains("dashboard"));
        assertTrue(driver.getCurrentUrl().contains("dashboard"), 
            "User should be on dashboard but was on: " + driver.getCurrentUrl());
    }
}
