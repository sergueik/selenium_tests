package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.reflect.Method;

import org.junit.rules.Timeout;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
// import org.openqa.selenium.support.ui.Duration;
// NOTE: probably using the wrong class
import java.time.Duration;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Selected test scenarios for Selenium WebDriver
 * based on: http://software-testing.ru/forum/index.php?/topic/37443-sajt-opredeliaet-webdriver-kak-robota/
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class TransaviaTest extends BaseTest {

	private static String baseURL = "https://www.transavia.com/en-EU/home/";
	private static String selector = "form#contact_form > fieldset div.form-group div.input-group textarea.form-control";
	private static final StringBuffer verificationErrors = new StringBuffer();

	@SuppressWarnings("deprecation")
	@BeforeMethod
	public void BeforeMethod(Method method) {
		super.beforeMethod(method);
		// override

		wait = new WebDriverWait(driver, 120);
		wait.pollingEvery(Duration.ofMillis((long) 1000));
		driver.get(baseURL);
		ExpectedCondition<Boolean> urlChange = driver -> driver.getCurrentUrl()
				.matches(String.format("^%s.*", baseURL));
		wait.until(urlChange);
		System.err.println("Current  URL: " + driver.getCurrentUrl());
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
	// Дальше я должен выбирать направление откуда и куда и нажимать поиск.
	// Если я почищю куки и буду заходить на http://transavia.com/ , то мне
	// предлогает выбрать регион.
	public void acknowledgeCookieTest() {
		sleep(12000);
		String pageTitle = driver.getTitle();
		System.err.println("page: " + pageTitle);// Sorry to interrupt.
		if (pageTitle.matches("Sorry to interrupt.*")) {
			// System.err.println("page: " + driver.getPageSource());
			WebElement bannerElement = driver
					.findElement(By.cssSelector("div.info-banner-blocker"));
			assertThat(bannerElement, notNullValue());
			highlight(bannerElement);
			System.err
					.println("outerHTML: " + bannerElement.getAttribute("outerHTML"));
			WebElement buttonElement = wait.until(ExpectedConditions
					.visibilityOf(driver.findElement(By.cssSelector("button.button"))));
			assertThat(buttonElement, notNullValue());
			highlight(buttonElement);
			buttonElement.click();

		} else {

		}
	}
}
