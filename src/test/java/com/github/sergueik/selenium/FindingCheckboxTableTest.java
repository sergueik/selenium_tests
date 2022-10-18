package com.github.sergueik.selenium;

/**
 * Copyright 2022 Serguei Kouzmine
 */
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
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
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class FindingCheckboxTableTest extends BaseTest {

	private static final Logger log = LogManager
			.getLogger(FindingCheckboxTableTest.class);
	private String text1 = "AP-116516";
	private int cnt = 0;
	private String selector = "";
	private String selector1 = "";
	private List<WebElement> labels = new ArrayList<>();
	private WebElement label;
	private List<WebElement> checkboxes = new ArrayList<>();
	private WebElement checkbox;
	private String name = "";

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
	}

	@BeforeMethod
	public void loadPage() {
		driver.navigate().to(getPageContent("table_search_by.html"));
	}

	// https://www.w3.org/TR/1999/REC-xpath-19991116/
	// https://www.w3.org/TR/xpath20/
	@Test(enabled = true)
	public void test1() {
		// Arrange
		System.err.println(String.format("Looking for span with text %s", text1));
		selector1 = String.format("//span[contains(text(), '%s')]", text1);
		labels = driver.findElements(By.xpath(selector1));
		assertTrue(labels.size() > 0);
		label = labels.get(0);
		highlight(label);
		System.err.println("text: " + label.getText());
		final String selector2 = String.format(
				"//span[contains(text(), '%s')]/../..//input[@type='checkbox']", text1);
		checkboxes = driver.findElements(By.xpath(selector2));
		System.err.println(String
				.format("Looking for checkbox name in the row with text %s", text1));
		assertTrue(checkboxes.size() > 0);
		checkbox = checkboxes.get(0);
		highlight(checkbox);
		name = checkbox.getAttribute("name");
		System.err.println(String.format("Check box name: \"%s\"", name));
		final String selector3 = String.format("//input[@name='%s']", name);
		// Act
		try {
			System.err
					.println(String.format("Looking for checkbox with name %s", name));
			checkbox = driver.findElement(By.xpath(selector3));
			// Assert
			assertThat(checkbox, notNullValue());
			assertThat(checkbox.getAttribute("name"), equalTo(name));
			System.err.println("Found via selector: " + selector2);
		} catch (NoSuchElementException e) {
			System.err.println(
					String.format("Exception: %s...", e.toString().substring(0, 200)));
		}
	}

	@Test(enabled = true)
	public void test2() {
		// Arrange
		selector = String.format("//span[text()[contains(., '%s')]]", text1);
		System.err.println(String.format("Looking for span with text %s", text1));
		labels = driver.findElements(By.xpath(selector));
		assertTrue(labels.size() > 0);
		label = labels.get(0);
		highlight(label);
		System.err.println("Found element with text: " + label.getText());
	}

	@Test(enabled = true)
	public void test3() {
		// Arrange
		selector = String
				.format("//span[contains(text(), '%s')]/preceding::input[1]", text1);
		checkbox = driver.findElement(By.xpath(selector));
		highlight(checkbox);
		name = checkbox.getAttribute("name");
		System.err.println(String.format(
				"Selector %s found input element with name: \"%s\" type: \"%s\"",
				selector, name, checkbox.getAttribute("type")));
	}

	@Test(enabled = true)
	public void test4() {
		// Arrange
		selector = String.format(
				"//span[contains(text(), '%s')]/preceding::input[@type='checkbox']",
				text1);
		checkboxes = driver.findElements(By.xpath(selector));
		System.err.println(String
				.format("Looking for first input next to span with text %s", text1));
		assertTrue(checkboxes.size() > 0);
		System.err.println(String.format("found %d elements", checkboxes.size()));
		cnt = 0;
		checkbox = checkboxes.get(cnt);
		highlight(checkbox);
		name = checkbox.getAttribute("name");
		System.err.println(
				String.format("Found input element #%d with name: \"%s\" type: \"%s\"",
						cnt, name, checkbox.getAttribute("type")));
		for (cnt = 0; cnt != checkboxes.size(); cnt++) {
			selector1 = String.format(
					"//span[contains(text(), '%s')]/preceding::input[@type='checkbox'][%d]",
					text1, cnt + 1);
			checkbox = driver.findElement(By.xpath(selector1));
			System.err.println(
					String.format("Found Check box element #%d with HTML: \"%s\"", cnt,
							checkbox.getAttribute("outerHTML")));
			highlight(checkbox);
		}
		// finding the wrong check box ?
		selector = String.format(
				"//span[contains(text(), '%s')]/preceding::input[@type='checkbox']",
				text1);
		checkboxes = driver.findElements(By.xpath(selector));
		System.err.println(
				String.format("Looking for checkbox next to span with text %s", text1));
		assertTrue(checkboxes.size() > 0);
		System.err.println(
				String.format("Found %d Check box elements", checkboxes.size()));
		checkboxes.stream().forEach(c -> {
			highlight(c);
			System.err
					.println(String.format("Found Check box element with HTML: \"%s\"",
							c.getAttribute("outerHTML")));
		});
		cnt = 0;
		for (WebElement c : checkboxes) {
			highlight(c);
			System.err.println(
					String.format("Found Check box element #%d with HTML: \"%s\"", cnt,
							c.getAttribute("outerHTML")));

			cnt++;

		}
	}

}

