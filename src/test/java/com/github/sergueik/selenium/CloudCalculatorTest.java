package com.github.sergueik.selenium;

// The import org.hamcrest.Matchers.matchesRegex cannot be resolved
// import static org.hamcrest.Matchers.matchesRegex;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
 * Find Angular-generated input and  select elements by their id using the HTML5 element-to-element
 * relationship attributes, and exercising the ng-click workaround - only one works
 * 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class CloudCalculatorTest extends BaseTest {

	private static boolean debug = true;
	private static String baseURL = "https://cloud.google.com/products/calculator";

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

	@Test(enabled = true)
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
	public void testSwitchIntoNestedPage() {

		frameElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector("devsite-iframe iframe[name^='goog_']")));
		assertThat(frameElement, notNullValue());

		iframe = driver.switchTo().frame(frameElement);
		// get first iframe element - no good attribute to use
		frameElement = iframe.findElement(By.cssSelector("iframe:nth-of-type(1)"));
		assertThat(frameElement, notNullValue());
		nestedIframe = iframe.switchTo().frame(frameElement);
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
		// https://stackoverflow.com/questions/32499174/selenium-click-event-does-not-trigger-angularjs-ng-click
		// NOTE: the basic click() or Actions do not work
		// (new Actions(nestedIframe)).moveToElement(selectValue).click().perform();
		executeScript("arguments[0].click();", selectValue);
		highlight(selectValue, 1000, "solid red");

		// Find Angular-generated select element by id using HTML5 element
		// relationship attributes
		selectElement = nestedIframe.findElement(By.cssSelector(
				String.format("md-select[id='%s']", selectLabel.getAttribute("for"))));
		if (debug) {
			System.err.println("Select source (options visible): " + selectElement
					.findElement(By.xpath("..")).getAttribute("outerHTML"));
		}

		optionText = "Windows Server 2008r2";
		String xpath2 = String.format(
				"//*[@id='%s']//md-option[@id][div[contains(text(), '%s')]]",
				ownedAreaId, optionText);
		if (debug) {
			System.err.println("Locating DOM using xpath: " + xpath2);
		}
		WebElement option2 = nestedIframe.findElement(By.xpath(xpath2));
		if (debug) {
			System.err.println(
					"Selecting different option: " + option2.getAttribute("outerHTML"));
		}
		assertThat(option2, notNullValue());
		optionId = option2.getAttribute("id");
		WebElement option3 = nestedIframe
				.findElement(By.cssSelector(String.format("#%s", optionId)));
		assertThat(option3, notNullValue());
		// https://stackoverflow.com/questions/32499174/selenium-click-event-does-not-trigger-angularjs-ng-click
		if (debug) {
			System.err.println(String.format(
					"Simulate mouse click on chosen option \"%s\"", option3.getText()));
		}
		executeScript("arguments[0].click();", option3);
		sleep(1000);

		iframe.switchTo().defaultContent();
		driver.switchTo().defaultContent();

	}

}
