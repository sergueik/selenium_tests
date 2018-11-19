package com.github.sergueik.selenium;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.internal.Nullable;

/**
 * Selected test scenarios for Selenium WebDriver
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class PopupWindowTest extends BaseTest {

	private static final StringBuffer verificationErrors = new StringBuffer();
	private static final Logger log = LogManager.getLogger(PopupWindowTest.class);

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
	}

	@BeforeMethod
	public void loadPage() {
		driver.navigate().to(getPageContent("popup_window.htm"));
	}

	@Test(enabled = true)
	public void test1() {
		String parentHandle = driver.getWindowHandle(); // Save parent window
		boolean isChildWindowOpen = wait
				.until(ExpectedConditions.numberOfWindowsToBe(2));
		if (isChildWindowOpen) {
			Set<String> handles = driver.getWindowHandles();
			// Switch to child window
			for (String handle : handles) {
				driver.switchTo().window(handle);
				System.err.println("Window title: " + driver.getTitle());
				if (!parentHandle.equals(handle)) {
					System.err.println("Found popup opened");
					break;
				}
			}
			driver.manage().window().maximize();
		}
	}

	@Test(enabled = true)
	public void test2() {
		// Assert
		final String text = "Wait a Second";
		final String currentHandle = driver.getWindowHandle(); // Save parent window
		try {
			wait.until(new ExpectedCondition<Boolean>() {
				@Override
				public Boolean apply(WebDriver _driver) {
					Boolean result = false;
					System.err.println("Inspecting driver Window handles");
					Set<String> windowHandles = _driver.getWindowHandles();
					if (windowHandles.size() > 1) {
						System.err.println("Found " + (windowHandles.size() - 1)
								+ " additional tabs opened");
					} else {
						System.out.println("No other tabs found");
						return false;
					}

					driver.switchTo().window(currentHandle);
					driver.switchTo().defaultContent();

					// Iterator<String> windowHandleIterator = windowHandles.iterator();
					// while (windowHandleIterator.hasNext()) {
					// String handle = windowHandleIterator.next();
					for (String handle : windowHandles) {
						System.err.println("Switch to: " + handle);
						driver.switchTo().window(handle);
						String title = _driver.getTitle();
						System.err.println("Window title: " + title);
						if (title.matches("Popup Window")) {
							String pageSource = _driver.getPageSource();
							System.err.println(String.format("Page source: %s",
									pageSource.substring(org.apache.commons.lang3.StringUtils
											.indexOf(pageSource, "<body>"),
											pageSource.length() - 1)));
							if (pageSource.contains(text)) {
								System.err.println("Found text: " + text);
								result = true;
							}
						}
						if (result) {
							System.err.println("Close the browser tab: " + handle);
							_driver.close();
							return true;
						}
						System.err.println("Switch to the main window.");
						driver.switchTo().window(currentHandle);
						driver.switchTo().defaultContent();
					}
					return result;
				}
			});
		} catch (Exception e) {
			System.err.println("Exception: " + e.toString());
			verificationErrors.append(e.toString());
			// throw new RuntimeException(e.toString());
		}
	}
}
