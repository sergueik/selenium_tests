package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ValueChangeWaitTest extends BaseTest {

	private static final boolean debug = true;
	final static List<String> lastValue = new ArrayList<>();
	final static List<String> historicValues = new ArrayList<>();
	private String filePath = "clock.html";

	@BeforeMethod
	public void loadPage() {
		driver.navigate().to(getPageContent(filePath));
	}

	@Test(enabled = true)
	public void lastValueChangeDetectionTest() {
		WebElement element = driver
				.findElement(By.cssSelector("input[name=\"clock\"]"));
		final String script = "return arguments[0].value";
		String value = (String) executeScript(script, element);
		System.err.println("initial value: " + value);
		lastValue.add(0, value);

		// Assert
		try {
			wait.until(new ExpectedCondition<Boolean>() {
				@Override
				public Boolean apply(WebDriver d) {
					Boolean result = false;
					WebElement element = d
							.findElement(By.cssSelector("input[name=\"clock\"]"));
					String value = (String) executeScript(script, element);
					System.err.println("current value: " + value);
					if (lastValue.get(0).contains(value)) {
						System.err.println("Found text: " + value);
						result = false;
					} else {
						lastValue.remove(0);
						lastValue.add(0, value);
						result = true;
					}
					if (result) {
						System.err.println("Found new value. Updated");
					}
					return result;
				}
			});
		} catch (Exception e) {
			System.err.println("Exception: " + e.toString());
		}
	}

	@Test
	public void historyValueSizeTest() {
		WebElement element = driver
				.findElement(By.cssSelector("input[name=\"clock\"]"));
		String value = element.getAttribute("value");
		System.err.println("initial value: " + value);
		lastValue.add(0, value);
		final int size = 5;
		// Assert
		try {
			wait.until(new ExpectedCondition<Boolean>() {
				@Override
				public Boolean apply(WebDriver d) {
					Boolean result = false;

					WebElement element = d
							.findElement(By.cssSelector("input[name=\"clock\"]"));
					String value = element.getAttribute("value");
					System.err.println("current value: " + value);
					if (historicValues.contains(value)) {
						System.err.println("Ignored seen before: " + value);
						result = false;
					} else {
						System.err.println("Caching value: " + value);
						historicValues.add(value);
						if (historicValues.size() > size) {
							result = true;
						}
					}
					if (result) {
						System.err.println("Filled history of " + size + ": "
								+ historicValues.toString() + ". Done");
					}
					return result;
				}
			});
		} catch (Exception e) {
			System.err.println("Exception: " + e.toString());
		}
	}

}