package com.github.sergueik.selenium;

/**
 * Copyright 2021 Serguei Kouzmine
 */

// The import org.hamcrest.Matchers.matchesRegex cannot be resolved
// import static org.hamcrest.Matchers.matchesRegex;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.openqa.selenium.support.ui.Select;

/**
 * Selected test scenarios for Selenium WebDriver
 * 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo
 */

public class ClassicSelectDemoTest extends BaseTest {

	private final static String baseUrl = "https://www.w3schools.com/tags/tryit.asp?filename=tryhtml_select";
	private static String baseURL = "https://www.w3schools.com";
	private final String filename = "tryhtml_select";
	private final static String selector = "select#cars";
	private WebElement element;
	private Select select;
	private final String selectOption = "audi";

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
	public void loadSubjectURL() {
		// Arrange
		driver
				.get(baseURL + String.format("/tags/tryit.asp?filename=%s", filename));
	}

	@AfterMethod
	public void loadBaseURL() {
		// Arrange
		driver.get(baseURL);
	}

	@Test(enabled = true)
	public void test1() {
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

		WebElement resultIframe = wait
				.until(ExpectedConditions.visibilityOfElementLocated(
						By.cssSelector("iframe[name='iframeResult']")));
		assertThat(resultIframe, notNullValue());

		// Act
		WebDriver iframe = driver.switchTo().frame(resultIframe);
		// Assert
		element = iframe.findElement(By.cssSelector(selector));
		select = new Select(element);
		assertThat(select, notNullValue());

		for (WebElement element : select.getOptions()) {
			System.err.println(String.format("value: \'%s\", text: \"%s\"",
					element.getAttribute("value"), element.getText()));
		}
		select.selectByValue(selectOption); // void
		element = iframe.findElement(By.cssSelector("input[type='submit']"));
		element.click();
		sleep(1000);
		System.err.println("Navigated to: " + iframe.getCurrentUrl());
		element = iframe.findElement(By.cssSelector("body div"));
		System.err.println("Page reports result: " + element.getText());
		// TODO: examine redirect
		driver.switchTo().defaultContent();
	}

}