package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Selected test scenarios for Selenium WebDriver
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

// https://ultimateqa.com/dummy-automation-websites/
// see also: XMLHttpRequest overwrite at Object level (in Russian)
// https://habr.com/ru/post/148140/?_ga=2.134444785.1051269828.1617671354-1174242598.1608300071
// https://github.com/ilinsky/xmlhttprequest
// https://www.codota.com/code/java/methods/org.openqa.selenium.JavascriptExecutor/executeAsyncScript

public class XMLHttpRequestExecuteAsyncTest extends BaseTest {

	private static WebElement element;
	private static List<WebElement> elements;
	private static final StringBuffer verificationErrors = new StringBuffer();
	private static String baseURL = null;

	// origin:
	// https://www.guru99.com/handling-ajax-call-selenium-webdriver.html
	// it does not appear to be doing XMLHttpR equests anymore :-(
	@Test(enabled = true)
	public void test1() {
		baseURL = "http://demo.guru99.com/test/ajax.html";
		driver.get(baseURL);
		element = wait.until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".container form")));
		assertThat(element, notNullValue());
		highlight(element);
		// Get the text before performing an ajax call
		elements = element.findElements(By.cssSelector("input[type='radio']"));
		assertThat(elements, notNullValue());
		assertThat(elements.size(), is(2));
		highlight(elements.get(0));

		String textBefore = element.getText().trim();

		element = driver.findElement(By.id("yes"));
		assertThat(element, notNullValue());
		highlight(element);
		element.click();

		// Get the text after ajax call
		try {
			element = wait.until(ExpectedConditions
					.visibilityOf(driver.findElement(By.className("radiobutton"))));
			assertThat(element, notNullValue());
			highlight(element);
			String textAfter = element.getText().trim();
			// empty ?
		} catch (TimeoutException e) {
			// the result DOM element does not yet exist
			// ignore
		}

		// verify both texts before ajax call and after ajax call text
		element = driver.findElement(By.id("buttoncheck"));
		assertThat(element, notNullValue());
		highlight(element);
		System.err.println("Ajax Call Performed");
		element.click();

		sleep(100);

		// Get the text after ajax call
		element = wait.until(ExpectedConditions
				.visibilityOf(driver.findElement(By.className("radiobutton"))));
		assertThat(element, notNullValue());
		highlight(element);
		String textAfter = element.getText().trim();
		String expectedText = "Radio button is checked and it's value is Yes";
		// Verify expected text with text updated after ajax call
		assertThat(textAfter, is(expectedText));

	}

	@AfterSuite
	public void afterSuite() throws Exception {
		driver.close();
		driver.quit();
	}

	@BeforeMethod
	public void BeforeMethod() {

	}

	@AfterMethod
	public void AfterMethod(ITestResult result) {
		if (verificationErrors.length() != 0) {
			throw new RuntimeException(String.format("Error in the method %s : %s",
					result.getMethod().getMethodName(), verificationErrors.toString()));
		}

		try {
			driver.get("about:blank");
		} catch (org.openqa.selenium.UnhandledAlertException e) {
			System.err.println("Exception (aborting method): " + e.toString());
			return;
		}
	}

}
