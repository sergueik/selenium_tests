package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.testng.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Selected test scenarios for Selenium WebDriver
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class FindingIdFromLabelForTest extends BaseTest {

	private static final Logger log = LogManager
			.getLogger(FindingIdFromLabelForTest.class);
	private String text = "Группа продавцов";

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
	}

	@BeforeMethod
	public void loadPage() {
		driver.navigate().to(getPageContent("extjs_ex.htm"));
	}

	@Test(enabled = true)
	public void findingIdVialabelTextXPathTest() {
		// Arrange
		System.err.println("Looking for label with text %s" + text);
		List<WebElement> labels = driver.findElements(
				By.xpath(String.format("//label[contains(text(), '%s')]", text)));
		assertTrue(labels.size() > 0);
		WebElement label = labels.get(0);
		highlight(label);
		String selector = label.getAttribute("for");
		System.err.println(String.format("Label is for id: \"%s\"", selector));
		// Act
		try {
			WebElement inputElement = driver.findElement(By.id(selector));
			// Assert
			assertThat(inputElement, notNullValue());
			assertThat(inputElement.getAttribute("name"), equalTo("saleGroupId"));
			System.err.println(String.format("%s finds %s", selector,
					inputElement.getAttribute("name")));
			flash(inputElement);
		} catch (NoSuchElementException e) {
			System.err.println(String.format("%s leading to exception: %s...",
					selector, e.toString().substring(0, 200)));
		}
		sleep(100);
	}

}
