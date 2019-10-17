package com.github.sergueik.selenium;

import static org.testng.Assert.assertTrue;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.testng.Assert.assertTrue;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.sergueik.selenium.BaseTest;

public class TabTest extends BaseTest {

	// based on discussion:
	// https://stackoverflow.com/questions/17547473/how-to-open-a-new-tab-using-selenium-webdriver

	private static String baseURL = "http://stackoverflow.com/";
	private static final StringBuffer verificationErrors = new StringBuffer();
	private final static String cssSelector = "a.-logo";
	private static WebElement logoElement;
	private static String parentHandle = null;
	private static int numberOfWindows = 0;

	@BeforeMethod
	public void BeforeMethod(Method method) {
		super.beforeMethod(method);
		driver.get(baseURL);
	}

	@AfterMethod
	public void AfterMethod(ITestResult result) {
		if (verificationErrors.length() != 0) {
			throw new RuntimeException(String.format("Error(s) in the method %s : %s",
					result.getMethod().getMethodName(), verificationErrors.toString()));
		}
		driver.get("about:blank");
	}

	//
	@Test(enabled = true)
	public void getTabOpenTest() {
		// Arrange
		parentHandle = driver.getWindowHandle(); // Save parent window
		numberOfWindows = driver.getWindowHandles().size();
		try {
			logoElement = wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.cssSelector(cssSelector)));
			if (logoElement != null) {
				actions.moveToElement(logoElement).build().perform();
				System.err.println(
						"Ctrl-clicking on: " + logoElement.getAttribute("outerHTML"));
				logoElement.sendKeys(Keys.chord(Keys.CONTROL, Keys.RETURN));
			}
			// switch to the other window now
			boolean isChildWindowOpen = wait
					.until(ExpectedConditions.numberOfWindowsToBe(2));
			if (isChildWindowOpen) {
				// switch to the other window now
				walkTabs(parentHandle);
			}
		} catch (TimeoutException e) {
			System.err.println("Exception (aborting) " + e.toString());
			return;
		}
	}

	@Test(enabled = true, expectedExceptions = {
			org.openqa.selenium.TimeoutException.class }, expectedExceptionsMessageRegExp = "Expected condition failed: waiting for number of open windows to be \\d .*$")
	public void emptyNewTabTest() {
		// Arrange
		parentHandle = driver.getWindowHandle(); // Save parent window
		numberOfWindows = driver.getWindowHandles().size();
		WebElement bodyElement = driver.findElement(By.cssSelector("body"));
		assertThat(bodyElement, notNullValue());
		// body Element is not null
		if (bodyElement != null) {
			bodyElement.sendKeys(Keys.CONTROL + "t");
		}
		// makes no difference
		if (bodyElement != null) {
			bodyElement.sendKeys(Keys.chord(Keys.CONTROL, "t"));
		}

		WebDriverWait waitShort = new WebDriverWait(driver, 3);
		waitShort.pollingEvery(Duration.ofMillis(300));

		boolean isChildWindowOpen = waitShort
				.until(ExpectedConditions.numberOfWindowsToBe(numberOfWindows + 1));
		if (isChildWindowOpen) {
			// switch to the other window now
			walkTabs(parentHandle);
		}
	}

	@Test(enabled = true)
	public void getLinkTargetTabOpenTest() {
		// Arrange
		parentHandle = driver.getWindowHandle(); // Save parent window
		numberOfWindows = driver.getWindowHandles().size();
		try {
			logoElement = wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.cssSelector(cssSelector)));
			if (logoElement != null) {
				actions.moveToElement(logoElement).build().perform();
				String script = "var element = arguments[0];"
						+ "var attribute = document.createAttribute('target');"
						+ "attribute.value = '_blank';"
						+ "element.setAttributeNode(attribute);";
				String result = (String) executeScript(script, logoElement);
				assertThat(result, nullValue());
				System.err.println("Clicking on modified element:"
						+ logoElement.getAttribute("outerHTML"));
				logoElement.click();
			}
			boolean isChildWindowOpen = wait
					.until(ExpectedConditions.numberOfWindowsToBe(numberOfWindows + 1));
			if (isChildWindowOpen) {
				// switch to the other window now
				walkTabs(parentHandle);
			}
		} catch (TimeoutException e) {
			System.err.println("Exception (aborting) " + e.toString());
			return;
		}
	}

	private void walkTabs(String parentHandle) {
		// switch to the other window now
		Set<String> handles = driver.getWindowHandles();
		// Switch to child window
		for (String handle : handles) {
			driver.switchTo().window(handle);
			if (!parentHandle.equals(handle)) {
				driver.manage().window().maximize();
				System.err.println("Close the browser tab: " + handle);
				driver.close();
				driver.switchTo().window(parentHandle);
				sleep(1000);
				break;
			}
		}
	}

}
