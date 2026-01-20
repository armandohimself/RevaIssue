package com.abra.revaissue.E2E.steps;

import org.openqa.selenium.WebDriver;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import com.abra.revaissue.E2E.poms.LoginPage;
import com.abra.revaissue.E2E.poms.DashboardPage;

import static org.junit.jupiter.api.Assertions.*;

public class AdminLoginSteps {

    private WebDriver driver;
    private LoginPage loginPage;
    private DashboardPage dashboardPage;

    public AdminLoginSteps() {
        this.driver = BaseSeleniumTest.driver;
        this.loginPage = new LoginPage(driver);
        this.dashboardPage = new DashboardPage(driver);
    }

    @Given("the admin is on the login page")
    public void the_admin_is_on_the_login_page() {
        loginPage.navigateTo("http://localhost:4200/login");
        assertTrue(loginPage.isOnLoginPage(), "Should be on login page");
    }

    @When("the admin enters a valid username {string} and password {string}")
    public void the_admin_enters_a_valid_username_and_password(String username, String password) {
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
    }

    @When("the admin clicks the login button")
    public void the_admin_clicks_the_login_button() {
        loginPage.clickLoginButton();
    }

    @Then("the admin should be redirected to the dashboard")
    public void the_admin_should_be_redirected_to_the_dashboard() {
        assertTrue(dashboardPage.isDashboardDisplayed(), "Should be on dashboard");
    }

    @Given("the admin in on the login page")
    public void the_admin_in_on_the_login_page() {
        loginPage.navigateTo("http://localhost:4200/login");
        assertTrue(loginPage.isOnLoginPage(), "Should be on login page");
    }

    @When("the admin enter username {string} and password {string}")
    public void the_admin_enter_username_and_password(String username, String password) {
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
    }

    @When("the admin clicks on the login button")
    public void the_admin_clicks_on_the_login_button() {
        loginPage.clickLoginButton();
    }

    @Then("the admin should see an error message {string}")
    public void the_admin_should_see_an_error_message(String expectedMessage) {
        assertTrue(loginPage.isErrorMessageDisplayed(), "Error should be displayed");
        String actual = loginPage.getErrorMessage();
        assertTrue(actual.contains(expectedMessage), 
            "Expected: " + expectedMessage + ", Got: " + actual);
    }
}
