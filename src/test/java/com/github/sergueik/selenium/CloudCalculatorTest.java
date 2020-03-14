package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Selected test scenarios for Selenium WebDriver
 * Find Angular-generated input and  select elements by their id using the HTML5 element-to-element
 * relationship attributes, and exercising the ng-click workaround - only one works
 * 
 * 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */
// see also
// https://stackoverflow.com/questions/32499174/selenium-click-event-does-not-trigger-angularjs-ng-click

public class CloudCalculatorTest extends BaseTest {

	private static boolean debug = true;
	private static String baseURL = "https://cloud.google.com/products/calculator";

	private static String frameName = null;
	private static WebDriver iframe = null;
	private static List<WebElement> iframes = new ArrayList<>();
	private static Map<String, Object> iframesMap = new HashMap<>();

	private static WebElement frameElement = null;

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

	@Test(enabled = false)
	public void testFramePresent() {
		iframes.clear();
		iframes = driver.findElements(By.cssSelector("devsite-iframe iframe"));
		iframesMap = new HashMap<>();
		for (WebElement element : iframes) {
			String key = String.format("{\"key\":\"name\",\"value\":\"%s\"}",
					element.getAttribute("name"));
			if (debug) {
				System.err.println(String.format("Found iframe %s:\n%s", key,
						element.getAttribute("outerHTML")));
			}
			iframesMap.put(key, element);
		}
	}

	@Test(enabled = true)
	public void testNestedFramePresent() {

		frameElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector("devsite-iframe iframe[name^='goog_']")));
		assertThat(frameElement, notNullValue());

		iframe = driver.switchTo().frame(frameElement);
		if (debug) {
			// System.err.println("Frame source:" + iframe.getPageSource());
		}
		iframes.clear();
		iframes = iframe.findElements(By.cssSelector("iframe"));
		iframesMap = new HashMap<>();
		for (WebElement element : iframes) {
			String key = String.format("{\"key\":\"id\",\"value\":\"%s\"}",
					element.getAttribute("id"));
			if (debug) {
				System.err.println(String.format("Found iframe %s:\n%s", key,
						element.getAttribute("outerHTML")));
			}
			iframesMap.put(key, element);
		}
		driver.switchTo().defaultContent();

	}

	@Test(enabled = true)
	public void testSwitchIntoNestedFrame() {
		String attribute = null;
		iframesMap = new HashMap<>();
		for (WebElement element : driver
				.findElements(By.cssSelector("devsite-iframe iframe"))) {
			attribute = "name";
			while (element.getAttribute(attribute) == "") {
				System.err.println(
						String.format("Waiting for attibute %s to have value", attribute));
				sleep(100);
			}
			iframesMap.put(String.format("{\"key\":\"%s\",\"value\":\"%s\"}",
					attribute, element.getAttribute(attribute)), element);
		}
		assertThat(iframesMap.keySet().isEmpty(), is(false));
		for (String key : iframesMap.keySet()) {
			if (debug) {
				System.err.println(String.format("Found iframe %s:\n%s", key,
						((WebElement) iframesMap.get(key)).getAttribute("outerHTML")));
			}
			if (frameName == null) {
				frameName = key;
			}
		}
		Map<String, String> collector = new HashMap<>();
		readData(frameName, Optional.of(collector));
		if (debug) {
			System.err.println(
					"key: " + collector.get("key") + " value: " + collector.get("value"));
		}
		frameElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath(String.format("//devsite-iframe//iframe[@%s='%s']",
						collector.get("key"), collector.get("value")))));
		assertThat(frameElement, notNullValue());

		// switch into iframe
		iframe = driver.switchTo().frame(frameElement);
		// get nested iframe elements by id
		iframesMap = new HashMap<>();
		for (WebElement element : iframe.findElements(By.cssSelector("iframe"))) {
			attribute = "id";
			String key = String.format("{\"key\":\"%s\",\"value\":\"%s\"}", attribute,
					element.getAttribute(attribute));
			if (debug) {
				System.err.println(String.format("Found iframe %s:\n%s", key,
						element.getAttribute("outerHTML")));
			}
			iframesMap.put(key, element);
		}
		frameName = null;
		assertThat(iframesMap.keySet().isEmpty(), is(false));
		for (String key : iframesMap.keySet()) {
			if (debug) {
				System.err.println(String.format("Found iframe %s:\n%s", key,
						((WebElement) iframesMap.get(key)).getAttribute("outerHTML")));
			}
			if (frameName == null) {
				frameName = key;
			}
		}
		collector = new HashMap<>();
		readData(frameName, Optional.of(collector));
		if (debug) {
			System.err.println(
					"key: " + collector.get("key") + " value: " + collector.get("value"));
		}
		wait = new WebDriverWait(iframe, flexibleWait);
		frameElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector(String.format("iframe[%s='%s']", collector.get("key"),
						collector.get("value")))));
		assertThat(frameElement, notNullValue());
		/*
		WebElement element = iframe.findElement(
				By.xpath(String.format("//devsite-iframe//iframe[@%s='%s']",
						collector.get("key"), collector.get("value"))));
		assertThat(element, notNullValue());
						*/

	}

	public String readData(Optional<Map<String, String>> parameters) {
		return readData(null, parameters);
	}

	public String readData(String payload,
			Optional<Map<String, String>> parameters) {
		return readData(payload, parameters, "(?:key|value)");
	}

	// Deserialize tiny hashmap from the JSON
	public String readData(String payload,
			Optional<Map<String, String>> parameters, String acceptedKeys) {
		if (debug) {
			System.err.println("Accepted keys: " + acceptedKeys);
		}

		Map<String, String> collector = (parameters.isPresent()) ? parameters.get()
				: new HashMap<>();

		String data = (payload == null) ? "{\"key\":\"1\", \"value\": \"\"]}"
				: payload;
		if (debug) {
			System.err.println("Processing payload: " + data.replaceAll(",", ",\n"));
		}
		try {
			JSONObject elementObj = new JSONObject(data);
			@SuppressWarnings("unchecked")
			Iterator<String> propIterator = elementObj.keys();
			while (propIterator.hasNext()) {

				String propertyKey = propIterator.next();
				if (debug) {
					System.err.println(
							((propertyKey.matches(acceptedKeys)) ? "Processing" : "Ignoring")
									+ " key: " + propertyKey);
				}
				if (!propertyKey.matches(acceptedKeys)) {
					continue;
				}
				String propertyVal = (String) elementObj.getString(propertyKey);
				if (debug) {
					System.err
							.println("Loaded string: " + propertyKey + ": " + propertyVal);
				}
				collector.put(propertyKey, propertyVal);
			}
		} catch (JSONException e) {
			System.err.println("Exception (ignored, aborting): " + e.toString());
			return null;
		}
		return (String) collector.get("key");
	}

}
