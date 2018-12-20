package com.github.sergueik.selenium;

import static org.testng.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
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
public class TabTest extends BaseTest {

	// private static String baseURL = "https://www.urbandictionary.com/"; //

	private static String baseURL = "https://www.linux.org";
	private static String altURL = "https://www.linux.org.ru/";
	private static final StringBuffer verificationErrors = new StringBuffer();

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

	// Chrome 70,711 / Chromedriver 45 sporadically
	// hanging up when loading the UTL in the newly opened tab.
	// A plain sleep works as a workaround.
	@Test(enabled = true)
	public void testInjecLinkAndOpenInTheNewTab() {
		String handle = createWindow(altURL);
		WebDriver handleDriver = switchToWindow(handle);
		sleep(1000);
		ExpectedCondition<Boolean> urlChange = driver -> driver.getCurrentUrl()
				.matches(String.format("^%s.*", altURL));

		System.err.println("Waiting for URL: " + altURL);
		(new WebDriverWait(handleDriver, flexibleWait)).until(urlChange);
		System.err.println("Current  URL: " + driver.getCurrentUrl());
		for (int cnt = 0; cnt != 5; cnt++) {
			switchToParent();
			switchToWindow(handle);
		}
		close(handle);
	}

	// NOTE: does not hang but throwing numerous NPE when
	// switching to and closing
	// the parent window handle
	@Test(enabled = true)
	public void testOpenInTheNewTab() {

		List<WebElement> linkElements = driver
				.findElements(By.cssSelector("div[id^='article'] a"));
		// assertThat(linkElements.size(), greaterThan(1));
		assertTrue(linkElements.size() > 1);
		WebElement linkElement = linkElements.get(0);
		executeScript(String.format(
				"arguments[0].setAttribute('href', '%s');arguments[0].setAttribute('target', '%s');",
				altURL, "_blank"), linkElement);
		highlight(linkElement);
		String newHandle = null;
		Set<String> oldHandles = driver.getWindowHandles();
		@SuppressWarnings("unused")
		String parentHandle = driver.getWindowHandle();

		linkElement.click();
		Set<String> newHandles = driver.getWindowHandles();

		newHandles.removeAll(oldHandles);
		// the remaining item is the new window handle
		for (String handle : newHandles) {
			System.err.println("Identified new hanlde: " + handle);
			newHandle = handle;
		}

		WebDriver handleDriver = switchToWindow(newHandle);
		sleep(1000);
		ExpectedCondition<Boolean> urlChange = driver -> driver.getCurrentUrl()
				.matches(String.format("^%s.*", altURL));

		(new WebDriverWait(handleDriver, flexibleWait)).until(urlChange);
		System.err.println("Current  URL: " + driver.getCurrentUrl());
		for (int cnt = 0; cnt != 5; cnt++) {
			switchToWindow(newHandle);
			try {
				switchToParent();
			} catch (NullPointerException e) {
				System.err.println("Execption(ignore) when trying to switch to parent: "
						+ e.toString());
			}
		}
		close(newHandle);
	}

}
