package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.lang.reflect.Method;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Selected test scenarios for Selenium WebDriver
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 * based on https://qna.habr.com/q/773525
 * def get_title(tengri_url) -> str:
 */

public class SpanActionTest extends BaseTest {

	private String baseUrl = "https://tengrinews.kz/";
	private static WebElement element = null;
	private static String articleUrl = null;
	private static final String text = "Показать комментарии";
	private static String xpath = String
			.format("//span[contains(text(), \"%s\")]", text);

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
	}

	@BeforeMethod
	public void beforeMethod(Method method) {
		super.beforeMethod(method);
		driver.get(baseUrl);
		element = wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.cssSelector(String.format(
						".tn-tape-container .tn-tape-grid div.tn-tape-item:nth-of-type(%d) > a",
						5))));
		assertThat(element, notNullValue());
		articleUrl = element.getAttribute("href");
		assertThat(articleUrl, notNullValue());
		element.click();
		wait.until(ExpectedConditions.urlContains(articleUrl));
	}

	@AfterMethod
	@Override
	public void afterMethod() {
		driver.get("about:blank");
	}

	@Test(enabled = true)
	public void test1() {
		// Arrange
		element = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
		assertThat(element, notNullValue());
		// Act
		actions.moveToElement(element).build().perform();
		// sleep(10000);
		highlight(element);
		try {
			executeScript("arguments[0].click();", element);
		} catch (ElementClickInterceptedException e) {
			System.err.println("Exception (ignored): " + e.toString());
		}
		// Assert
	}

	@Test(enabled = true, expectedExceptions = ElementClickInterceptedException.class)
	public void test2() {
		// Arrange
		element = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
		assertThat(element, notNullValue());
		// Act
		highlight(element);
		try {
			element.click();
		} catch (ElementClickInterceptedException e) {
			System.err
					.println("Exception (rethrowing and allowing): " + e.toString());
			throw e;
		}
		// Assert
	}
}
