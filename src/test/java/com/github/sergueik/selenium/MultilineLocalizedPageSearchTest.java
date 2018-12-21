package com.github.sergueik.selenium;

import static org.testng.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Selected test scenarios for Selenium WebDriver
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

// NOTE: chrome.exe cleanup allows leftover browser processes
public class MultilineLocalizedPageSearchTest extends BaseTest {

	private static final StringBuffer verificationErrors = new StringBuffer();

	private static String baseURL = "http://www.rfbr.ru/rffi/ru/";
	private static final String searchString = "Информация для заявителей и исполнителей проектов";
	private static final String complexXPathTemplate = "//*[contains(normalize-space(translate(text(), '\\t\\n\\r\\u00a0', '    ')),'%s')]";
	private static final String basicXPathTemplate = "//*[contains(text(),'%s')]";
	private static final String elementCssSelector = "div.grants > p";

	@BeforeMethod
	public void BeforeMethod(Method method) {
		super.beforeMethod(method);
		driver.get(baseURL);
		ExpectedCondition<Boolean> urlChange = driver -> driver.getCurrentUrl()
				.matches(String.format("^%s.*", baseURL));
		wait.until(urlChange);
		System.err.println("BeforeMethod: Current  URL: " + driver.getCurrentUrl());
	}

	@AfterMethod
	public void AfterMethod(ITestResult result) {
		if (verificationErrors.length() != 0) {
			throw new RuntimeException(String.format("Error(s) in the method %s : %s",
					result.getMethod().getMethodName(), verificationErrors.toString()));
		}
		try {
			driver.get("about:blank");
		} catch (NoSuchWindowException e) {
			// no such window: target window already closed
			System.err.println(
					"Execption(ignore) when trying to AfterMethod go to blank page: "
							+ e.toString());
		}
	}

	@Test(enabled = false)
	public void failedMultilineTextSearchTest() {
		List<WebElement> elements = driver
				.findElements(By.cssSelector(elementCssSelector));
		assertTrue(elements.size() > 0);
		WebElement element = elements.get(0);
		try {
			element = wait.until(ExpectedConditions.visibilityOf(driver.findElement(
					By.xpath(String.format(complexXPathTemplate, searchString)))));
			if (element != null) {
				highlight(element);
			}
		} catch (NoSuchElementException e) {
			System.err.println("Exception (ignored): " + e.toString());
		}
	}

	@Test(enabled = false)
	public void failedReciprocalMultilineTextSearchTest() {
		List<WebElement> elements = driver
				.findElements(By.cssSelector(elementCssSelector));
		String elementText = elements.get(0).getText();
		try {
			WebElement element = wait
					.until(ExpectedConditions.visibilityOf(driver.findElement(
							By.xpath(String.format(complexXPathTemplate, elementText)))));
			if (element != null) {
				highlight(element);
			}
		} catch (NoSuchElementException e) {
			System.err.println("Exception (ignored): " + e.toString());
		}
	}

	@Test(enabled = false)
	public void splitPartMultilineTextSearchTest() {
		List<WebElement> elements = driver
				.findElements(By.cssSelector(elementCssSelector));
		for (String line : elements.get(0).getText().split("\r?\n")) {
			try {
				WebElement element = wait.until(
						ExpectedConditions.visibilityOf(driver.findElement(By.xpath(String
								.format(basicXPathTemplate, line.replaceAll("\r?", ""))))));
				// fails with "и исполнителей проектов"
				if (element != null) {
					highlight(element);
				}
			} catch (NoSuchElementException e) {
				System.err.println("Exception (ignored): " + e.toString());
			}
		}
	}

	@Test(enabled = true)
	public void alternativeMultilineTextSearchTest() {
		List<WebElement> elements = driver
				.findElements(By.cssSelector(elementCssSelector));
		for (String line : elements.get(0).getText().split("\r?\n")) {
			try {
				WebElement result = findInnerMostByCssSelectorAndInnerText(null, line.replaceAll("\r?", ""));
				System.err.println("Result(text): " + result.getText());
			} catch (NoSuchElementException e) {
				System.err.println("Exception (ignored): " + e.toString());
			}
		}
	}
}
