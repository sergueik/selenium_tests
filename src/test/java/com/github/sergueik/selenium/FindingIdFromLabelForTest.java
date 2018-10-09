package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Selected test scenarios for Selenium WebDriver
 * https://www.w3.org/WAI/tutorials/forms/labels/
 * http://software-testing.ru/forum/index.php?/topic/31004-dinamicheskie-id/page-2
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class FindingIdFromLabelForTest extends BaseTest {

	private static final Logger log = LogManager
			.getLogger(FindingIdFromLabelForTest.class);
	private String text1 = "Группа продавцов";
	private String text2 = "Офис";

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
	public void pageElementTest() {
		// Arrange
		PageElement pageElement = PageElement.getInstance();
		pageElement.setDebug(true);
		pageElement.setDriver(driver);
		pageElement.setWait(wait);
		// Act
		assertThat(pageElement.getValue(text1), nullValue());
		pageElement.setValue(text1, "foo");
		// Assert
		String value1 = pageElement.getValue(text1);
		assertThat(value1, equalTo("foo"));
		System.err.println("New value: " + value1);

		try {
			assertThat(pageElement.getValue(text2), equalTo(""));
		} catch (java.lang.AssertionError e) {
			assertThat(pageElement.getValue(text2), nullValue());
		}
		// Act
		pageElement.setValue(text2, "bar");
		// Assert
		String value2 = pageElement.getValue(text2);
		assertThat(value2, equalTo("bar"));
		System.err.println("New value: " + value2);
	}

	@Test(enabled = false)
	public void findingIdVialabelTextXPathTest() {
		// Arrange
		System.err.println(String.format("Looking for label with text %s", text1));
		List<WebElement> labels = driver.findElements(
				By.xpath(String.format("//label[contains(text(), '%s')]", text1)));
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

	static public class PageElement {

		private static PageElement instance = new PageElement();
		private boolean debug = false;
		private WebDriver driver;
		private WebDriverWait wait;
		private long timeout = 100;

		private Map<String, String> cachedValues = new HashMap<>();
		private List<WebElement> labelElements = new ArrayList<>();
		private WebElement labelElement;
		private WebElement inputElement;
		private String selector = null;

		public void setDebug(boolean value) {
			this.debug = value;
		}

		private PageElement() {
		}

		public static PageElement getInstance() {
			return instance;
		}

		public void setDriver(WebDriver value) {
			this.driver = value;
		}

		public void setWait(WebDriverWait value) {
			this.wait = value;
		}

		public String getValue(final String labelText) {
			if (!cachedValues.containsKey(labelText)) {
				labelElements = driver.findElements(By.xpath(
						String.format("//label[contains(text(), '%s')]", labelText)));
				if (debug)
					assertTrue(labelElements.size() > 0);
				labelElement = labelElements.get(0);
				selector = labelElement.getAttribute("for");
				try {
					inputElement = driver.findElement(By.id(selector));
					// Assert
					if (debug)
						assertThat(inputElement, notNullValue());
					String valueText = inputElement.getAttribute("value") != null
							? inputElement.getAttribute("value") : inputElement.getText();
					if (debug)
						System.err.println(String.format(
								"Label with text \"%s\" is for input named \"%s\" with id \"%s\" value \"%s\"",
								labelText, inputElement.getAttribute("name"), selector,
								valueText));
					// only cache non-empty values
					if (valueText != null && valueText.trim().length() != 0) {
						if (!(cachedValues.containsKey(labelText))) {
							cachedValues.put(labelText, valueText);
						} else {
							cachedValues.replace(labelText, valueText);
						}
					}
				} catch (NoSuchElementException e) {
					System.err.println(String.format("Exception (caught): %s...",
							selector, e.toString().substring(0, 200)));
				}
			}
			return cachedValues.containsKey(labelText) ? cachedValues.get(labelText)
					: null;
		}

		public void setValue(final String labelText, final String value) {

			labelElements = driver.findElements(By
					.xpath(String.format("//label[contains(text(), '%s')]", labelText)));
			if (debug)
				assertTrue(labelElements.size() > 0);
			labelElement = labelElements.get(0);
			selector = labelElement.getAttribute("for");
			try {
				inputElement = driver.findElement(By.id(selector));
				if (debug)
					assertThat(inputElement, notNullValue());
				inputElement.sendKeys(value);
				try {
					Thread.sleep(timeout);
				} catch (InterruptedException e) {
					System.err.println("Exception (ignored): " + e.toString());
				}
				String valueText = inputElement.getAttribute("value") != null
						? inputElement.getAttribute("value") : inputElement.getText();
				if (debug)
					assertThat(valueText, equalTo(value));
				if (debug)
					System.err.println(String.format(
							"Label with text \"%s\" is for input with id: \"%s\" value set to \"%s\"",
							labelText, selector, inputElement.getAttribute("value")));
			} catch (NoSuchElementException e) {
				System.err.println(String.format("Exception (caught): %s...", selector,
						e.toString().substring(0, 200)));
			}

		}
	}
}
