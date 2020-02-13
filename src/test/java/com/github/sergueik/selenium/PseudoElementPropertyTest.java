package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.containsString;
// The import org.hamcrest.Matchers.matchesRegex cannot be resolved
// import static org.hamcrest.Matchers.matchesRegex;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Selected test scenarios for Selenium WebDriver
 * 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class PseudoElementPropertyTest extends BaseTest {

	private static String baseURL = "https://www.w3schools.com/css/tryit.asp?filename=trycss_before";

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
	public void loadBaseURL() {
		driver.get(baseURL);
	}

	@Test(enabled = true)
	public void testResultFramePresent() {
		List<WebElement> iframes = driver.findElements(By.cssSelector("div#iframewrapper iframe"));
		Map<String, Object> iframesMap = new HashMap<>();
		for (WebElement iframe : iframes) {
			String key = String.format("id: \'%s\", name: \"%s\"", iframe.getAttribute("id"),
					iframe.getAttribute("name"));
			System.err.println(String.format("Found iframe %s", key));
			iframesMap.put(key, iframe);
		}
	}

	// ::before is used for styling of containing element.
	// Selenium is not working with pseudo-elements in general yet
	// see also:
	// https://stackoverflow.com/questions/45427223/click-on-pseudo-element-using-selenium
	@Test(enabled = true)
	public void testBeforeProperties() {
		final String script = "return window.getComputedStyle(arguments[0],':before')";

		// Arrange
		WebElement resultIframe = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("iframe[name='iframeResult']")));
		assertThat(resultIframe, notNullValue());

		// Act
		WebDriver iframe = driver.switchTo().frame(resultIframe);
		WebElement element = iframe.findElement(By.tagName("h1"));
		// Assert

		System.err.println("Frame page: " + iframe.getPageSource().replaceAll("\\n", " "));
		// NOTE: the result found by trial and error o be the ArrayList
		@SuppressWarnings("unchecked")
		List<String> result = (List<String>) executeScript(script, element);
		System.err.println("Pseudo-element styles (all): " + String.join(",", result));

		System.err.println("Element styles (filtered): ");
		result.stream().filter(o -> o.matches("(?:top|left|width|height|content)")).forEach(System.err::println);

		driver.switchTo().defaultContent();
	}

	@Test(enabled = true)
	public void testCertainStyleofBeforePseudoElement() {
		// Arrange
		WebElement resultIframe = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("iframe[name='iframeResult']")));
		assertThat(resultIframe, notNullValue());
		// Act
		WebDriver iframe = driver.switchTo().frame(resultIframe);
		WebElement element = iframe.findElement(By.tagName("h1"));
		// NOTE: the result found by trial and error o be the ArrayList

		final String script = "return window.getComputedStyle(arguments[0],':before').getPropertyValue(arguments[1]);";
		List<String> propertyKeys = Arrays
				.asList(new String[] { "top", "left", "height", "width", "box-sizing", "content" });
		// optionally construct the map of properties to examine closely:
		Map<String, Object> properties = propertyKeys.stream().collect(Collectors.toMap(propertyKey -> propertyKey,
				propertyKey -> (String) executeScript(script, element, propertyKey)));
		// Assert
		// assertThat(properties.get("content").toString(),
		// matchesRegex(Pattern.compile("(?:\\d+)")));
		assertThat(properties.get("content").toString(), containsString("smiley.gif"));
		// Logging
		propertyKeys.stream().map(propertyKey -> String.format("%s=%s", propertyKey,
				(String) executeScript(script, element, propertyKey))).forEach(System.err::println);
		driver.switchTo().defaultContent();
	}

}

