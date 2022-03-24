package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.io.IOException;
import java.lang.reflect.Method;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Selected test scenarios for Selenium WebDriver
 * 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class CapchaWaitTest extends BaseTest {
	private final static String baseURL = "https://www.wikipedia.org";
	private final int wait_seconds = 120;
	private final long wait_poll_milliseconds = 10000;
	private final static String cssSelector = "#searchInput";
	private static final int length = 5;
	private static final StringBuffer verificationErrors = new StringBuffer();

	@BeforeClass
	public void beforeClass() throws IOException {
		super.setFlexibleWait(wait_seconds);
		super.setPollingInterval(wait_poll_milliseconds);
		super.beforeClass();
		assertThat(driver, notNullValue());
	}

	@BeforeMethod
	public void BeforeMethod(Method method) {
		super.beforeMethod(method);
		// Arrange
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
	public void test1() {
		// Assert
		try {
			wait.until(new ExpectedCondition<Boolean>() {
				@Override
				public Boolean apply(WebDriver d) {
					Boolean result = false;
					WebElement element = d.findElement(By.cssSelector(cssSelector));
					String value = element.getAttribute("value");
					if (value.length() < length) {
						System.err.println(String.format(
								"waiting for input of specific length of %d characters",
								length));
						result = false;
					} else {
						System.err.println("Found value: " + value);
						result = true;
					}
					if (result) {
						System.err.println("Done wait");
					}
					return result;
				}
			});
		} catch (Exception e) {
			System.err.println("Exception: " + e.toString());
			verificationErrors.append("Exception: " + e.toString());
		}
		assertThat(driver.findElement(By.cssSelector(cssSelector))
				.getAttribute("value").length(), greaterThan(length - 1));

	}

	@Test(enabled = true)
	public void test2() {
		ExpectedCondition<Boolean> inputSizeChange = driver -> driver
				.findElement(By.cssSelector(cssSelector)).getAttribute("value")
				.length() >= length;
		wait.until(inputSizeChange);
		assertThat(driver.findElement(By.cssSelector(cssSelector))
				.getAttribute("value").length(), greaterThan(length - 1));

	}

}
