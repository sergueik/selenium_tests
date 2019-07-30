package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.reflect.Method;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Selected test scenarios for Selenium WebDriver
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class UnhideElementTest extends BaseTest {

	private static String baseURL = "https://letskodeit.teachable.com/pages/practice";
	// redirects to https://learn.letskodeit.com/p/practice
	private static final StringBuffer verificationErrors = new StringBuffer();

	@BeforeMethod
	public void BeforeMethod(Method method) {
		super.beforeMethod(method);

		System.err.println("Property \"selenium.version\" = "
				+ System.getProperty("selenium.version"));
		System.err.println("Property \"selenium.version\" = "
				+ getPropertyEnv("selenium.version", "unknown"));
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

	@Test(enabled = true)
	public void javascriptUnhideTest() {
		// Arrange
		By hideButtonLocator = By.id("hide-textbox");
		WebElement hideButton = wait.until(
				ExpectedConditions.visibilityOfElementLocated(hideButtonLocator));
		By elementLocator = By.id("displayed-text");
		WebElement element = driver.findElement(elementLocator);
		System.err.println("Acting on: " + element.getAttribute("outerHTML"));
		actions.moveToElement(element).build().perform();
		assertThat(element.isDisplayed(), is(true));
		sleep(1000);
		// NOTE: highlight method has no visual effect on the
		// bootstrap-skinned button
		highlight(hideButton);
		flash(hideButton);
		hideButton.click();
		sleep(1000);
		element = driver.findElement(elementLocator);
		System.err.println("Hidden: " + element.getAttribute("outerHTML"));
		assertThat(element.isDisplayed(), is(false));

		// NOTE: fails while applying javascript
		By showButtonLocator = By.id("show-textbox");
		WebElement showButton = driver.findElement(showButtonLocator);
		showButton.click();
		element = driver.findElement(elementLocator);
		System.err.println("Shown: " + element.getAttribute("outerHTML"));
		assertThat(element.isDisplayed(), is(true));
		// run the button "onClick" handlers directly
		sleep(1000);
		executeScript("window.hideElement()");
		sleep(1000);
		assertThat(element.isDisplayed(), is(false));
		executeScript("window.showElement()");
		sleep(1000);
		assertThat(element.isDisplayed(), is(true));
		// run the button "onClick" handlers directly
		sleep(1000);
		executeScript("window.hideElement()");
		sleep(1000);
		// try brute force - commented - fails to achieve
		unhideElement(element);
		sleep(1000);
		assertThat(element.isDisplayed(), is(true));
		// run the button "onClick" handlers directly
		sleep(10000);

	}

}