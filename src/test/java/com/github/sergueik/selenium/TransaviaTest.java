package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.reflect.Method;

import org.junit.rules.Timeout;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
// import org.openqa.selenium.support.ui.Duration;
// NOTE: probably using the wrong class
import java.time.Duration;
import java.util.List;

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

		wait = new WebDriverWait(driver, 30);
		wait.pollingEvery(Duration.ofMillis((long) 300));
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
		sleep(1000);
		String pageTitle = driver.getTitle();
		System.err.println("page: " + pageTitle);// Sorry to interrupt.
		if (pageTitle.matches("Sorry to interrupt.*")) {
			// System.err.println("page: " + driver.getPageSource());
			WebElement bannerElement = driver
					.findElement(By.cssSelector("div.info-banner-blocker"));
			assertThat(bannerElement, notNullValue());
			try {
				System.err
						.println("outerHTML: " + bannerElement.getAttribute("outerHTML"));
				highlight(bannerElement);
			} catch (StaleElementReferenceException e) {
				/*
				org.openqa.selenium.StaleElementReferenceException:
					Element not found in the cache - perhaps the page has changed since it was looked up
					*/
				// ignore
			}
			bannerElement.click();
			WebElement parentElement = bannerElement.findElement(By.xpath(".."));
			System.err.println(
					"Parent Element: " + parentElement.getAttribute("outerHTML"));
			List<WebElement> buttons = parentElement
					.findElements(By.cssSelector("button"));
			System.err.println(String.format("See %d buttons", buttons.size()));
			assertThat(buttons.size(), greaterThan(1));
			final String text = "Accept all cookies";
			// stream with debugging first,
			buttons.stream().map(_button -> {
				System.err
						.println("Button Element: " + _button.getAttribute("outerHTML"));
				/*
				Button Element: 
					<button onclick="_stCookiePopup.send_popup_accept()" class="button info-banner-button button-call-to-actions info-banner-neg-expandible">Accept all cookies</button>
				*/
				return _button;
			}).filter(_button -> {
				return (boolean) (_button.getText().indexOf(text) > -1);
			}).forEach(_button -> {
				highlight(_button);
				_button.click();
			});
			sleep(300000);
			if (false) {
				// not reached
				try {
					WebElement buttonElement = wait.until(ExpectedConditions
							.visibilityOf(parentElement.findElement(By.cssSelector(
									String.format("//button[contains(text(), '%s')]", text)))));
					assertThat(buttonElement, notNullValue());
					flash(buttonElement);
					buttonElement.click();
				} catch (InvalidSelectorException e) {
					// Caused by: org.openqa.selenium.InvalidSelectorException:
					// InvalidSelectorError: An invalid or illegal selector was specified
					// The given selector
					// "//button[contains(text(), 'Accept all cookies')]"
					// is either invalid or does not result in a WebElement.

					// ignore - left as an exercise for later
				}

			}
		} else {
			// there is no banner
		}
	}
}
