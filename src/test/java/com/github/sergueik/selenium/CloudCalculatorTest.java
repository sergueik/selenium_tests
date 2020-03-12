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

	private final static String xpathTemplate = "//*[@id='%s']//md-option[@id][div[contains(text(), '%s')]]";
	private static String xpath;
	private static String frameName = null;
	private static WebDriver iframe = null;
	private static WebDriver nestedIframe = null;
	private static final String clusterPurpose = "testing";
	private static List<WebElement> iframes = new ArrayList<>();
	private static Map<String, Object> iframesMap = new HashMap<>();
	private static List<WebElement> inputLabels = new ArrayList<>();
	private static List<WebElement> selectLabels = new ArrayList<>();
	private static WebElement inputLabel = null;
	private static WebElement inputElement = null;
	private static String ownedAreaId = null;
	private static String optionId = null;
	private static String containerId = null;
	private static WebElement selectLabel = null;
	private static WebElement selectElement = null;
	private static WebElement selectValue = null;
	private static String optionText = null;
	private static WebElement optionElement = null;

	private static WebElement frameElement = null;
	private static WebElement checkBoxElement = null;
	private static WebElement element = null;

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

	@Test(enabled = false)
	public void testSwitchIntoNestedFrame() {

		iframesMap = new HashMap<>();
		for (WebElement element : driver
				.findElements(By.cssSelector("devsite-iframe iframe"))) {
			iframesMap.put(String.format("{\"key\":\"name\",\"value\":\"%s\"}",
					element.getAttribute("name")), element);
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
		String result = readData(frameName, Optional.of(collector));
		if (debug) {
			System.err.println(
					"key: " + collector.get("key") + " value: " + collector.get("value"));
		}
		frameElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath(String.format("//devsite-iframe//iframe[@%s='%s']",
						collector.get("key"), collector.get("value")))));
		assertThat(frameElement, notNullValue());

		// get nested iframe elements by id
		iframesMap = new HashMap<>();
		for (WebElement element : (driver.switchTo().frame(frameElement))
				.findElements(By.cssSelector("iframe"))) {
			String key = String.format("{\"key\":\"id\",\"value\":\"%s\"}",
					element.getAttribute("id"));
			if (debug) {
				System.err.println(String.format("Found iframe %s:\n%s", key,
						element.getAttribute("outerHTML")));
			}
			iframesMap.put(key, element);
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
		collector = new HashMap<>();
		result = readData(frameName, Optional.of(collector));
		if (debug) {
			System.err.println(
					"key: " + collector.get("key") + " value: " + collector.get("value"));
		}
		/*
		element = (driver.switchTo().frame(frameElement)).findElement(
				By.xpath(String.format("//devsite-iframe//iframe[@%s='%s']",
						collector.get("key"), collector.get("value"))));
		assertThat(element, notNullValue());
		*/
	}

	@Test(enabled = true)
	public void testElements() {

		frameElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector("devsite-iframe iframe[name^='goog_']")));
		assertThat(frameElement, notNullValue());

		// get nested iframe element by id? NPE
		// keep finding first of
		// element = (driver.switchTo().frame(frameElement))
		// .findElement(By.cssSelector("iframe#myFrame"));
		iframe = driver.switchTo().frame(frameElement);
		element = iframe.findElement(By.cssSelector("iframe:nth-of-type(1)"));
		assertThat(element, notNullValue());
		nestedIframe = iframe.switchTo().frame(element);
		if (debug) {
			// System.err.println("Frame source:" + nestedIframe.getPageSource());
		}
		inputLabels = nestedIframe
				.findElements(By.cssSelector("label[for^='input_']"));
		inputLabel = inputLabels.stream()
				.filter(o -> o.getText().contains((CharSequence) "Number of instances"))
				.collect(Collectors.toList()).get(0);
		if (debug) {
			System.err.println(
					"Input label source: " + inputLabel.getAttribute("outerHTML"));
		}
		inputElement = nestedIframe.findElement(By.cssSelector(
				String.format("input[id='%s']", inputLabel.getAttribute("for"))));
		inputElement.sendKeys("1");
		sleep(100);

		inputLabels = nestedIframe
				.findElements(By.cssSelector("label[for^='input_']"));
		inputLabel = inputLabels.stream()
				.filter(o -> o.getText()
						.contains((CharSequence) "What are these instances for"))
				.collect(Collectors.toList()).get(0);
		if (debug) {
			System.err.println(
					"Input label source: " + inputLabel.getAttribute("outerHTML"));
		}
		inputElement = nestedIframe.findElement(By.cssSelector(
				String.format("input[id='%s']", inputLabel.getAttribute("for"))));
		inputElement.sendKeys(clusterPurpose);
		sleep(100);

		selectLabels = nestedIframe
				.findElements(By.cssSelector("label[for^='select_']"));
		selectLabel = selectLabels.stream()
				.filter(o -> o.getText().contains((CharSequence) "Operating System"))
				.collect(Collectors.toList()).get(0);
		containerId = selectLabel.getAttribute("for");
		if (debug) {
			System.err.println(
					"Select label source: " + selectLabel.getAttribute("outerHTML"));
		}
		selectElement = nestedIframe.findElement(
				By.cssSelector(String.format("md-select[id='%s']", containerId)));

		if (debug) {
			System.err
					.println("Select source: " + selectElement.getAttribute("outerHTML"));
		}
		ownedAreaId = selectElement.getAttribute("aria-owns");
		selectValue = selectElement
				.findElement(By.cssSelector("md-select-value > span > div"));
		assertThat(selectValue, notNullValue());
		if (debug) {
			System.err.println(String.format("Simulate mouse click on element \"%s\"",
					selectElement.getText()));
		}
		// NOTE: the basic click() or Actions do not work
		// (new Actions(nestedIframe)).moveToElement(selectValue).click().perform();
		executeScript("arguments[0].click();", selectValue);
		highlight(selectValue, 1000, "solid red");

		selectElement = nestedIframe.findElement(By.cssSelector(
				String.format("md-select[id='%s']", selectLabel.getAttribute("for"))));
		if (debug) {
			System.err.println("Select source (options visible): " + selectElement
					.findElement(By.xpath("..")).getAttribute("outerHTML"));
		}

		optionText = "Windows Server 2008r2";
		xpath = String.format(xpathTemplate, ownedAreaId, optionText);
		if (debug) {
			System.err.println("Locating DOM using xpath: " + xpath);
		}
		optionElement = nestedIframe.findElement(By.xpath(xpath));
		if (debug) {
			System.err.println("Selecting different option: "
					+ optionElement.getAttribute("outerHTML"));
		}
		assertThat(optionElement, notNullValue());
		optionId = optionElement.getAttribute("id");
		// find again by id
		optionElement = nestedIframe
				.findElement(By.cssSelector(String.format("#%s", optionId)));
		assertThat(optionElement, notNullValue());
		if (debug) {
			System.err
					.println(String.format("Simulate mouse click on chosen option \"%s\"",
							optionElement.getText()));
		}
		executeScript("arguments[0].click();", optionElement);
		sleep(1000);

		selectLabels = nestedIframe
				.findElements(By.cssSelector("label[for^='select_']"));
		if (debug) {
			selectLabels.stream().forEach(
					e -> System.err.println("Element: " + e.getAttribute("outerHTML")));
		}
		selectLabel = selectLabels.stream()
				// NOTE: case sensitive
				.filter(o -> o.getText().contains((CharSequence) "Machine type"))
				.collect(Collectors.toList()).get(0);
		containerId = selectLabel.getAttribute("for");
		if (debug) {
			System.err.println(
					"Select label source: " + selectLabel.getAttribute("outerHTML"));
		}
		selectElement = nestedIframe.findElement(
				By.cssSelector(String.format("md-select[id='%s']", containerId)));

		if (debug) {
			// System.err.println("Select source: " +
			// selectElement.getAttribute("outerHTML"));
		}
		ownedAreaId = selectElement.getAttribute("aria-owns");
		selectValue = selectElement
				.findElement(By.cssSelector("md-select-value > span > div"));
		assertThat(selectValue, notNullValue());
		if (debug) {
			System.err.println(String.format("Simulate mouse click on element \"%s\"",
					selectElement.getText()));
		}
		// want to switch to "n1-standard-1"
		executeScript("arguments[0].click();", selectValue);
		highlight(selectValue, 1000, "solid red");

		selectElement = nestedIframe.findElement(By.cssSelector(
				String.format("md-select[id='%s']", selectLabel.getAttribute("for"))));
		if (debug) {
			System.err.println("Select source (options visible): " + selectElement
					.findElement(By.xpath("..")).getAttribute("outerHTML"));
		}

		optionText = "n1-standard-1";
		xpath = String.format(xpathTemplate, ownedAreaId, optionText);
		if (debug) {
			System.err.println("Locating DOM using xpath: " + xpath);
		}
		optionElement = nestedIframe.findElement(By.xpath(xpath));
		if (debug) {
			System.err.println("Selecting different option: "
					+ optionElement.getAttribute("outerHTML"));
		}
		assertThat(optionElement, notNullValue());
		// skipped the "id" attribute exercise
		if (debug) {
			System.err
					.println(String.format("Simulate mouse click on chosen option \"%s\"",
							optionElement.getText()));
		}
		executeScript("arguments[0].click();", optionElement);

		checkBoxElement = nestedIframe.findElement(By
				.cssSelector("md-input-container md-checkbox[aria-label='Add GPUs']"));
		assertThat(checkBoxElement, notNullValue());
		checkBoxElement.sendKeys(Keys.SPACE);
		sleep(1000);
		iframe.switchTo().defaultContent();
		driver.switchTo().defaultContent();

	}

	public String readData(Optional<Map<String, String>> parameters) {
		return readData(null, parameters);
	}

	public String readData(String payload,
			Optional<Map<String, String>> parameters) {
		return readData(payload, parameters,
				"(?:key|value)" /* "(?:id|name|src)" */);
	}

	// Deserialize the tiny hashmap from the JSON
	public String readData(String payload,
			Optional<Map<String, String>> parameters, String acceptedKeys) {
		if (debug) {
			System.err.println("Accepted keys: " + acceptedKeys);
		}

		Map<String, String> collector = (parameters.isPresent()) ? parameters.get()
				: new HashMap<>();

		String data = (payload == null) ? "{\"id\":\"42\"]}" : payload;
		if (debug) {
			System.err.println("Processing payload: " + data.replaceAll(",", ",\n"));
		}
		try {
			JSONObject elementObj = new JSONObject(data);
			@SuppressWarnings("unchecked")
			Iterator<String> propIterator = elementObj.keys();
			while (propIterator.hasNext()) {

				String propertyKey = propIterator.next();
				System.err.println(
						((propertyKey.matches(acceptedKeys)) ? "Processing" : "Ignoring")
								+ " key: " + propertyKey);
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
		return (String) collector.get("id");
	}

}
