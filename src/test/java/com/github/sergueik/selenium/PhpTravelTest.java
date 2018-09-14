package com.github.sergueik.selenium;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.internal.Nullable;

/**
 * Selected test scenarios for Selenium WebDriver
 * based on:  https://phptravels.com/demo/ demo site
 * tab enumeration
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class PhpTravelTest extends BaseTest {

	private static final StringBuffer verificationErrors = new StringBuffer();
	private static final Logger log = LogManager.getLogger(PhpTravelTest.class);
	private static String userName = "user@phptravels";
	private static String password = "demouser";
	private static final boolean DEBUG = false;
	private static String baseURL = "about:blank";

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
	}

	@BeforeMethod
	public void BeforeMethod() {
		driver.get(baseURL);
	}

	// INFO: Unable to drain process streams. Ignoring but the exception being
	// swallowed follows.
	// org.apache.commons.exec.ExecuteException:
	// The stop timeout of 2000 ms was exceeded (Exit value: -559038737)
	@Test(enabled = true)
	public void adminLoginFailingTest() {
		// Arrange
		password = "demouser";
		String baseURL = "https://phptravels.com/demo/";
		driver.get(baseURL);
		String parentWindowHandler = driver.getWindowHandle();
		if (DEBUG)
			System.err.println("Parent window: " + parentWindowHandler);

		WebElement adminLoginButton = driver
				.findElements(By.className("btn-primary")).stream().map(e -> {
					if (DEBUG)
						System.err.println(e.getText());
					return e;
				}).filter(e -> e.getText().matches(".*/ADMIN"))
				.collect(Collectors.toList()).get(0);
		assertThat(adminLoginButton, notNullValue());
		highlight(adminLoginButton);
		adminLoginButton.click();
		wait.until(ExpectedConditions.numberOfWindowsToBe(2));
		Set<String> windowHandles = new HashSet<>();
		windowHandles = driver.getWindowHandles();
		for (String windowHandle : windowHandles) {

			if (!windowHandle.equals(parentWindowHandler)) {
				if (DEBUG)
					System.err.println("child window: " + windowHandle);
				driver.switchTo().window(windowHandle);
				String childTitle = driver.getTitle();
				if (DEBUG)
					System.err.println("Title of page/tab: " + childTitle);

				/*
				// from original user login scenario
				WebElement myAccount = wait
						.until(ExpectedConditions.visibilityOf(driver.findElement(
								By.xpath("//a[@class='dropdown-toggle go-text-right']"))));
				myAccount.click();
				*/
				if (DEBUG) {
					System.err.println("Page source: " + driver.getPageSource());
					WebElement body = wait.until(ExpectedConditions
							.visibilityOf(driver.findElement(By.xpath("//body"))));
					assertThat(body, notNullValue());
					System.err.println("Body html:" + body.getAttribute("innerHTML"));
				}
				WebElement form = wait.until(ExpectedConditions
						.visibilityOf(driver.findElement(By.tagName("form"))));
				assertThat(form, notNullValue());
				if (DEBUG)
					System.err.println("Form html:" + form.getAttribute("outerHTML"));
				/*
				// #loginfrm > div.panel.panel-default
				WebElement loginForm = wait.until(ExpectedConditions
						.visibilityOf(driver.findElement(By.id("loginfrm"))));
				assertThat(loginForm, notNullValue());
				highlight(loginForm);
				*/
				WebElement emailInput = wait.until(ExpectedConditions.visibilityOf(
						form.findElement(By.xpath("//input[@name='email']"))));
				assertThat(emailInput, notNullValue());
				highlight(emailInput);
				emailInput.sendKeys(userName);
				WebElement passwordInput = form.findElement(By.name("password"));
				assertThat(passwordInput, notNullValue());
				highlight(passwordInput);
				passwordInput.sendKeys(password);
				sleep(1000);
				// org.openqa.selenium.ElementNotVisibleException:
				// element not visible:
				// <div style="margin-top:10px" class="resultlogin"></div>
				// form.findElement(By.className("resultlogin")).click();
				WebElement loginButton = form
						.findElement(By.cssSelector("button[type='submit']"));
				flash(loginButton); // NOTE: no visual cue
				loginButton.click();
				sleep(3000);

			}
		}
	}

}
