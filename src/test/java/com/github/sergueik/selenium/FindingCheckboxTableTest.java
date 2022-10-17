package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Selected test scenarios for Selenium WebDriver
 * https://www.w3.org/WAI/tutorials/forms/labels/
 * http://software-testing.ru/forum/index.php?/topic/31004-dinamicheskie-id/page-2
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class FindingCheckboxTableTest extends BaseTest {

	private static final Logger log = LogManager
			.getLogger(FindingCheckboxTableTest.class);
	private String text1 = "AP-116516";

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
	}

	@BeforeMethod
	public void loadPage() {
		driver.navigate().to(getPageContent("table_search_by.html"));
	}

	@Test(enabled = true)
	public void test() {
		// Arrange
		System.err.println(String.format("Looking for span with text %s", text1));
		final String selector1 = String.format("//span[contains(text(), '%s')]",
				text1);
		List<WebElement> labels = driver.findElements(By.xpath(selector1));
		assertTrue(labels.size() > 0);
		WebElement label = labels.get(0);
		highlight(label);
		System.err.println("text: " + label.getText());
		final String selector2 = String.format(
				"//span[contains(text(), '%s')]/../..//input[@type='checkbox']", text1);
		List<WebElement> checkboxes = driver.findElements(By.xpath(selector2));
		System.err.println(String
				.format("Looking for checkbox name in the row with text %s", text1));
		assertTrue(checkboxes.size() > 0);
		WebElement checkbox = checkboxes.get(0);
		highlight(checkbox, 2000);
		// NOTE: highlight does not work ?
		flash(checkbox);
		String name = checkbox.getAttribute("name");
		System.err.println(String.format("Check box name: \"%s\"", name));
		final String selector3 = String.format("//input[@name='%s']", name);
		// Act
		try {
			System.err
					.println(String.format("Looking for checkbox with name %s", name));
			WebElement inputElement = driver.findElement(By.xpath(selector3));
			// Assert
			assertThat(inputElement, notNullValue());
			assertThat(inputElement.getAttribute("name"), equalTo(name));
			flash(inputElement);
			System.err.println("Found via selector: " + selector2);
		} catch (NoSuchElementException e) {
			System.err.println(
					String.format("Exception: %s...", e.toString().substring(0, 200)));
		}
		sleep(100);
	}

}

