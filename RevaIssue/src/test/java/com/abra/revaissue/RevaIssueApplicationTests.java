package com.abra.revaissue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

/**
 * This is your base test file.
 */
@SpringBootTest
class RevaIssueApplicationTests {

	@Test
	void contextLoads() {
		assertThat(2 + 2).isEqualTo(4);
	}

	@Test
	void testSeleniumInputElement() {
		WebDriver driver = null;
		try {
			driver = new ChromeDriver();
			driver.get("https://testautomationpractice.blogspot.com/");

			//! GUI HEADER
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(4));
			WebElement GUIElementsHeader = driver.findElement(By.linkText("GUI Elements"));
			System.out.println(GUIElementsHeader.getText());

			//! Name Input
			WebElement inputName = driver.findElement(By.id("name"));

			// Output context information about the input element
			System.out.println("=== Input Name Element Context ===");
			System.out.println("Tag: " + inputName.getTagName()); //! input
			System.out.println("ID: " + inputName.getAttribute("id")); //! name
			System.out.println("Placeholder: " + inputName.getAttribute("placeholder")); //Enter Name
			System.out.println("Type: " + inputName.getAttribute("type")); //! text

			inputName.click();
			inputName.sendKeys("Armando Arteaga");

			// Get the value that was entered
			System.out.println("Entered value: " + inputName.getAttribute("value")); // 🤠

		} finally {
			if (driver != null) {
				driver.quit();
			}
		}
	}
}
