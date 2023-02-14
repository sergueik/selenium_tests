package com.github.sergueik.selenium;

/**
 * Copyright 2023 Serguei Kouzmine
 */

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Selected test scenarios for Selenium WebDriver
 * 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo
 */

public class ComputedStyleTest extends BaseTest {
	// NOTE: logger is useless in this class
	private static final Logger log = LogManager
			.getLogger(ComputedStyleTest.class);
	private static String baseURL = "https://www.w3schools.com";
	private final String filename = "tryjsref_getcomputedstyle";
	private static String selector = null;
	private WebElement element;

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
		driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
	}

	@Override
	@AfterClass
	public void afterClass() {
		try {
			driver.close();
		} catch (NoSuchWindowException e) {

		}
		driver.quit();
		driver = null;
	}

	@BeforeMethod
	public void beforeMethod() {
		driver
				.get(baseURL + String.format("/jsref/tryit.asp?filename=%s", filename));
	}

	@AfterMethod
	public void afterMethod() {
		driver.get("about:blank");
	}

	@Test(enabled = true)
	public void test1() {
		// Arrange
		List<WebElement> iframes = driver
				.findElements(By.cssSelector("div#iframewrapper iframe"));
		Map<String, Object> iframesMap = new HashMap<>();
		for (WebElement iframe : iframes) {
			String key = String.format("id: \'%s\", name: \"%s\"",
					iframe.getAttribute("id"), iframe.getAttribute("name"));
			System.err.println(String.format("Found iframe %s", key));
			iframesMap.put(key, iframe);
		}
	}

	@Test(enabled = true)
	public void test2() {
		// Arrange
		WebElement resultIframe = wait
				.until(ExpectedConditions.visibilityOfElementLocated(
						By.cssSelector("iframe[name='iframeResult']")));
		assertThat(resultIframe, notNullValue());

		// Act
		WebDriver iframe = driver.switchTo().frame(resultIframe);
		// Assert
		selector = "#test";
		element = iframe.findElement(By.cssSelector(selector));
		assertThat(element, notNullValue());
		String value = styleOfElement(element, "background-color");

		System.err.println("computed style: background-color: " + value);
		selector = "#demo";
		element = iframe.findElement(By.cssSelector(selector));
		System.err.println(element.getText());
		assertThat(element.getText(), containsString(value));
		driver.switchTo().defaultContent();
	}

}