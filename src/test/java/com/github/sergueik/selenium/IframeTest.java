package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
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
public class IframeTest extends BaseTest {

	private static String baseURL = "https://www.w3schools.com";
	private final static String text = "The iframe element";
	private final static String filename = "tryhtml_iframe";
	private final static String title = "W3Schools Free Online Web Tutorials";
	private final static String href = "/html/default.asp";

	private static WebElement element;
	private static WebDriver iframe2;
	private static WebDriver iframe1;

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

	@Test(enabled = false)
	public void test1() {
		List<WebElement> elements = driver
				.findElements(By.cssSelector("div#iframewrapper iframe"));
		Map<String, Object> iframesMap = new HashMap<>();
		for (WebElement iframe : elements) {
			String key = String.format("id: \'%s\", name: \"%s\"",
					iframe.getAttribute("id"), iframe.getAttribute("name"));
			System.err.println(String.format("Found iframe %s", key));
			iframesMap.put(key, iframe);
		}
	}

	@Test(enabled = true)
	public void test2() {

		element = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector("iframe[name='iframeResult']")));
		assertThat(element, notNullValue());
		System.err.println("iframe1 element: " + element.getAttribute("outerHTML"));

		// Act
		// NOTE: print source attribute of the iframe element before switching into
		System.err.println("iframe1 src: " + element.getAttribute("src"));
		iframe1 = driver.switchTo().frame(element);

		System.err.println("iframe1 url: " + iframe1.getCurrentUrl());
		System.err.println("iframe1 body: " + iframe1.getPageSource());

		// https://habr.com/ru/post/588773
		element = iframe1.findElement(By
				.xpath(String.format("//h1[contains(normalize-space(.),'%s')]", text)));
		element = iframe1
				.findElement(By.xpath(String.format("//iframe[@title = '%s']", title)));

		// Assert
		assertThat(element, notNullValue());
		System.err
				.println("iframe2 element: " + element.getAttribute("outerHTML	"));
		System.err.println("iframe2 src: " + element.getAttribute("src"));
		iframe2 = iframe1.switchTo().frame(element);
		System.err.println("iframe2 url: " + iframe2.getCurrentUrl());
		System.err.println(
				"iframe2 body (fragment): " + iframe2.findElement(By.tagName("body"))
						.getAttribute("innerHTML").substring(0, 1000));
		element = iframe2.findElement(
				By.cssSelector(String.format(".w3-button[href = '%s']", href)));
		assertThat(element, notNullValue());
		System.err.println(
				"button element in nested frame: " + element.getAttribute("outerHTML"));
		try {
			actions = new Actions(iframe2);
			iframe2.manage().timeouts().setScriptTimeout(scriptTimeout,
					TimeUnit.SECONDS);
			actions.moveToElement(element).build().perform();
			sleep(1000);
		} catch (ElementNotInteractableException e) {
			System.err.println("Exception (ignored): " + e.toString());
			// org.openqa.selenium.ElementNotInteractableException:
			// element not interactable: https://www.w3schools.com/html/default.asp
			// has no size and location
		}

		// Declare a wait time
		wait = new WebDriverWait(iframe2, flexibleWait);
		// NOTE: commented: need to override a lot of utility functions to use
		// iframe instead of driver
		// highlight(element);
		try {
			element.click();
			sleep(1000);
		} catch (ElementNotInteractableException e) {
			System.err.println("Exception (ignored): " + e.toString());
			// org.openqa.selenium.ElementNotInteractableException:
			// element not interactable: https://www.w3schools.com/html/default.asp
			// has no size and location
		}

		// NOTE: the type check does not work. commented
		// if (iframe2 instanceof JavascriptExecutor) {
		try {
			JavascriptExecutor.class.cast(iframe2)
					.executeScript("arguments[0].click()", element);
			System.err.println("clicked");
			sleep(10000);
			System.err.println("iframe2 url: " + iframe2.getCurrentUrl());

		} catch (StaleElementReferenceException e) {
			System.err.println("Exception (ignored): " + e.toString());
			// org.openqa.selenium.StaleElementReferenceException:
			// stale element reference: element is not attached to the page document
		}

		// }
		try {
			element.click();
		} catch (NoSuchWindowException | StaleElementReferenceException e) {
			System.err.println("Exception (ignored): " + e.toString());
			// Exception (ignored): org.openqa.selenium.NoSuchWindowException:
			// no such window

		}

		iframe1.switchTo().defaultContent();
		driver.switchTo().defaultContent();
	}

	// see also:
	// https://www.toolsqa.com/selenium-webdriver/handle-iframes-in-selenium/
	@Test(enabled = true)
	public void test3() {

		element = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector("iframe[name='iframeResult']")));
		assertThat(element, notNullValue());
		iframe1 = driver.switchTo().frame(0);

		element = iframe1
				.findElement(By.xpath(String.format("//iframe[@title = '%s']", title)));

		// Assert
		assertThat(element, notNullValue());
		iframe2 = iframe1.switchTo().frame(0);
		element = iframe2.findElement(
				By.cssSelector(String.format(".w3-button[href = '%s']", href)));
		assertThat(element, notNullValue());
		System.err.println(
				"button element in nested frame: " + element.getAttribute("outerHTML"));
		iframe1.switchTo().defaultContent();
		driver.switchTo().defaultContent();
	}
}
