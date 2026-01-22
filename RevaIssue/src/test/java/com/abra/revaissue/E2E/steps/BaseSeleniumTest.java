package com.abra.revaissue.E2E.steps;

import java.time.Duration;

import com.abra.revaissue.E2E.poms.IssuePagePOM;
import com.abra.revaissue.E2E.poms.LoginPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.spring.CucumberContextConfiguration;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class BaseSeleniumTest {

    public static WebDriver driver;
    public static IssuePagePOM issuePage;
    public static LoginPage loginPage;

    @Before
    public static void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--incognito");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        loginPage = new LoginPage(driver);
        issuePage = new IssuePagePOM(driver);
    }

    @After
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}