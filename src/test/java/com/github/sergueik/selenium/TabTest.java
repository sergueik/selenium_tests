package com.github.sergueik.selenium;

import static org.testng.Assert.assertTrue;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.testng.Assert.assertTrue;

import java.lang.reflect.Method;
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
		WebElement logoElement;
		String parentHandle = driver.getWindowHandle(); // Save parent window
		try {
			logoElement = wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.cssSelector(cssSelector)));
			if (logoElement != null) {
				actions.moveToElement(logoElement).build().perform();
				System.err
						.println("Ctrl-clicking on: " + logoElement.getAttribute("outerHTML"));
				logoElement.sendKeys(Keys.chord(Keys.CONTROL, Keys.RETURN));
			}
			// switch to the other window now
			boolean isChildWindowOpen = wait
					.until(ExpectedConditions.numberOfWindowsToBe(2));
			if (isChildWindowOpen) {
				Set<String> handles = driver.getWindowHandles();
				// Switch to child window
				for (String handle : handles) {
					driver.switchTo().window(handle);
					if (!parentHandle.equals(handle)) {
						driver.manage().window().maximize();
						System.err.println("Close the extra browser tab: " + handle);
						driver.close();
						driver.switchTo().window(parentHandle);
						sleep(1000);
						break;
					}
				}
			}
		} catch (TimeoutException e) {
			System.err.println("Exception (aborting) " + e.toString());
			return;
		}
	}

	@Test(enabled = true)
	public void getLinkTargetTabOpenTest() {
		// Arrange
		WebElement logoElement;
		String parentHandle = driver.getWindowHandle(); // Save parent window
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
					.until(ExpectedConditions.numberOfWindowsToBe(2));
			if (isChildWindowOpen) {
				Set<String> handles = driver.getWindowHandles();
				// Switch to child window
				for (String handle : handles) {
					driver.switchTo().window(handle);
					if (!parentHandle.equals(handle)) {
						driver.manage().window().maximize();
						System.err.println("Close the extra browser tab: " + handle);
						driver.close();
						driver.switchTo().window(parentHandle);
						sleep(1000);
						break;
					}
				}
			}
		} catch (TimeoutException e) {
			System.err.println("Exception (aborting) " + e.toString());
			return;
		}
	}
}
