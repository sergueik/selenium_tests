package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Selected test scenarios for Selenium WebDriver
 * based on:  https://phptravels.com/demo/ demo site
 * question on https://groups.google.com/forum/#!topic/selenium-users/VJ_Uy327LXc
 * tab enumeration
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class PhpTravelTest extends BaseTest {

	private static final StringBuffer verificationErrors = new StringBuffer();
	private static final Logger log = LogManager.getLogger(PhpTravelTest.class);
	private static String userName = null;
	private static String password = null;
	private static final boolean DEBUG = false;
	// flag to turn on/ off verbose logging
	private static final boolean DEBUG_FAILING = false;
	// flag to decorate and conditionally enable failing or debugging code which
	// would also slow down the test
	private static String baseURL = "https://phptravels.com/demo/";
	private static Set<String> windowHandles = new HashSet<>();
	private static String parentWindowHandle = null;

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
	}

	@BeforeMethod
	public void BeforeMethod() {
		driver.get(baseURL);
		parentWindowHandle = driver.getWindowHandle();
	}

	@AfterMethod
	public void afterMethod() {
		if (parentWindowHandle != null) {
			driver.close();
			driver.switchTo().window(parentWindowHandle);
			parentWindowHandle = null;
		}
		driver.get("about:blank");
		super.afterMethod();
	}

	@Test(enabled = true)
	public void adminLoginTest() {

		// Arrange
		userName = "admin@phptravels.com";
		password = "demoadmin";

		if (DEBUG)
			System.err.println("Parent window: " + parentWindowHandle);

		WebElement adminLoginButton = driver
				.findElements(By.className("btn-primary")).stream()
				/* .map(e -> {
				if (DEBUG)
				System.err.println(e.getText());
				return e;
				})*/ .filter(e -> e.getText().matches("(?i).*/ADMIN"))
				.collect(Collectors.toList()).get(0);
		assertThat(adminLoginButton, notNullValue());
		highlight(adminLoginButton);
		windowHandles = driver.getWindowHandles();
		adminLoginButton.click();
		wait.until(
				ExpectedConditions.numberOfWindowsToBe(windowHandles.size() + 1));
		windowHandles = driver.getWindowHandles();
		for (String windowHandle : windowHandles) {

			if (!windowHandle.equals(parentWindowHandle)) {
				if (DEBUG)
					System.err.println("child window: " + windowHandle);
				driver.switchTo().window(windowHandle);
				String childTitle = driver.getTitle();
				if (DEBUG)
					System.err.println("Title of page/tab: " + childTitle);
				if (childTitle.contains("Administator Login")) {

					if (DEBUG) {
						System.err.println("Page source: " + driver.getPageSource());
						WebElement body = wait.until(ExpectedConditions
								.visibilityOf(driver.findElement(By.xpath("//body"))));
						assertThat(body, notNullValue());
						System.err.println("Body html:" + body.getAttribute("innerHTML"));
					}
					// Act
					WebElement form = wait.until(ExpectedConditions
							.visibilityOf(driver.findElement(By.tagName("form"))));
					assertThat(form, notNullValue());
					if (DEBUG)
						System.err.println("Form html:" + form.getAttribute("outerHTML"));
					WebElement emailInput = wait.until(ExpectedConditions.visibilityOf(
							form.findElement(By.xpath("//input[@name='email']"))));
					assertThat(emailInput, notNullValue());
					highlight(emailInput);
					emailInput.sendKeys(userName);
					WebElement passwordInput = form.findElement(By.name("password"));
					assertThat(passwordInput, notNullValue());
					highlight(passwordInput);
					passwordInput.sendKeys(password);
					WebElement loginButton = form
							.findElement(By.cssSelector("button[type='submit']"));
					flash(loginButton);
					loginButton.click();
					// Assert
					wait.until(ExpectedConditions
							.visibilityOf(driver.findElement(By.className("dash"))));
					// log out
					driver
							.findElement(By.xpath(
									"//a[contains(@class,'btn-danger')][contains(text(), 'Log Out')]"))
							.click();
				}
			}
		}
	}

	@Test(enabled = false)
	public void userLoginDemoPageTest() {
		// NOTE: there is also "LOGIN" link on the demo page
		// "//a[@class = 'login'][@href = 'http://phptravels.org']"
	}

	// TODO: clear cookie or log out to allow several login tests within the class
	@Test(enabled = true)
	public void userLoginTest() {
		// Arrange
		userName = "user@phptravels.com";
		password = "demouser";

		WebElement userLoginButton = driver.findElement(
				By.xpath("//a[@class='btn btn-primary btn-lg btn-block']"));
		assertThat(userLoginButton, notNullValue());
		highlight(userLoginButton);
		windowHandles = driver.getWindowHandles();
		userLoginButton.click();
		wait.until(
				ExpectedConditions.numberOfWindowsToBe(windowHandles.size() + 1));
		windowHandles = driver.getWindowHandles();
		for (String windowHandle : windowHandles) {

			if (!windowHandle.equals(parentWindowHandle)) {
				driver.switchTo().window(windowHandle);
				String childTitle = driver.getTitle();
				if (childTitle.contains("PHPTRAVELS")) {
					sleep(5000); // hard wait to prevent exception in waiting for visibility of MY ACCOUNT
					if (DEBUG)
						System.err.println("Title of page/tab: " + childTitle);
					// TODO: wait for
					// <div class="progress">
					// to disappear ?
					if (DEBUG) {
						// debugging
						// NOTE: avoid wait here
						WebElement navbar = driver.findElement(By.xpath("//body/*"));
						assertThat(navbar, notNullValue());
						System.err.println("Body html:" + navbar.getAttribute("innerHTML"));
					}

					if (DEBUG)
						System.err.println("Page source: " + driver.getPageSource());

					WebElement myAccount = wait.until(ExpectedConditions
							.visibilityOfElementLocated(By.linkText("MY ACCOUNT")));

					assertThat(myAccount, notNullValue());
					if (DEBUG)
						System.err.println(
								"My account html: " + myAccount.getAttribute("innerHTML")
										+ "\n xpath: " + xpathOfElement(myAccount));
					highlight(myAccount);
					myAccount.click();

					WebElement myLogin = wait.until(ExpectedConditions
							.visibilityOfElementLocated(By.linkText("Login")));
					assertThat(myLogin, notNullValue());
					// Act
					highlight(myLogin);
					if (DEBUG)
						System.err.println(
								"My account login html: " + myLogin.getAttribute("innerHTML")
										+ " \ncssSelector: " + cssSelectorOfElement(myLogin));
					myLogin.click();
					wait.until(
							ExpectedConditions.urlToBe("https://www.phptravels.net/login"));
					sleep(100);
					WebElement form = wait.until(ExpectedConditions
							.presenceOfElementLocated(By.cssSelector("#loginfrm")));
					assertThat(form, notNullValue());
					if (DEBUG)
						System.err
								.println("Login form html: " + form.getAttribute("innerHTML"));

					WebElement emailInput = form.findElement(By.name("username"));
					assertThat(emailInput, notNullValue());
					highlight(emailInput);
					emailInput.sendKeys(userName);
					WebElement passwordInput = form.findElement(By.name("password"));
					assertThat(passwordInput, notNullValue());
					highlight(passwordInput);
					passwordInput.sendKeys(password);
					WebElement loginButton = form
							.findElement(By.cssSelector("button[type='submit']"));
					flash(loginButton); // NOTE: no visual cue
					loginButton.click();
					// Assert
					wait.until(ExpectedConditions.urlMatches(".*/account/.*$"));
					sleep(1000);
				}
			}
		}
	}

	// replica of userLoginTest with debug code and custom methods removed
	@Test(enabled = false)
	public void userLoginShortenedTest() {
		// Arrange
		userName = "user@phptravels.com";
		password = "demouser";

		WebElement userLoginButton = driver.findElement(
				By.xpath("//a[@class='btn btn-primary btn-lg btn-block']"));
		windowHandles = driver.getWindowHandles();
		userLoginButton.click();
		wait.until(
				ExpectedConditions.numberOfWindowsToBe(windowHandles.size() + 1));
		windowHandles = driver.getWindowHandles();
		for (String windowHandle : windowHandles) {

			if (!windowHandle.equals(parentWindowHandle)) {
				driver.switchTo().window(windowHandle);
				String childTitle = driver.getTitle();
				if (childTitle.contains("PHPTRAVELS")) {
					WebElement myAccount = wait
							.until(ExpectedConditions.visibilityOf(driver
									.findElement(By.cssSelector("nav.navbar li#li_myaccount"))));
					myAccount.click();
					WebElement myLogin = wait
							.until(ExpectedConditions.visibilityOf(myAccount
									.findElement(By.xpath("ul[@class='dropdown-menu']/li/a"))));
					myLogin.click();
					wait.until(
							ExpectedConditions.urlToBe("https://www.phptravels.net/login"));
					WebElement form = wait.until(ExpectedConditions
							.presenceOfElementLocated(By.cssSelector("#loginfrm")));
					WebElement emailInput = wait.until(ExpectedConditions.visibilityOf(
							form.findElement(By.xpath("//input[@type='email']"))));
					emailInput.sendKeys(userName);
					WebElement passwordInput = form.findElement(By.name("password"));
					passwordInput.sendKeys(password);
					WebElement loginButton = form
							.findElement(By.cssSelector("button[type='submit']"));
					loginButton.click();
					// Assert
					wait.until(ExpectedConditions.urlMatches(".*/account/.*$"));
					sleep(1000);
					if (parentWindowHandle != null) {
						driver.close();
						driver.switchTo().window(parentWindowHandle);
						parentWindowHandle = null;
					}
				}
			}
		}
	}
}
