package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Selected test scenarios for Selenium WebDriver
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class TranslitWaitTest extends BaseTest {

	private static final Logger log = LogManager
			.getLogger(TranslitWaitTest.class);

	private static String baseURL = "https://ya.ru/";
	private final String expectedText = "Найти";

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
	}

	@BeforeMethod
	public void BeforeMethod() {
		driver.get(baseURL);
	}

	@Test(enabled = true)
	public void test1() {
		final String expectedTextAscii = BaseTest.Translit.toAscii(expectedText);
		try {
			WebElement element = wait.until(new ExpectedCondition<WebElement>() {
				@Override
				public WebElement apply(WebDriver d) {

					Boolean result = false;
					WebElement buttonElement = driver
							.findElement(By.cssSelector("button[class *= 'button']"));
					if (buttonElement != null) {
						String text = buttonElement.getText().trim();
						String textAscii = BaseTest.Translit.toAscii(text);
						if (expectedTextAscii.equals(textAscii))
							result = true;
						System.err
								.println(String.format("in apply: Text = %s (%s)\nresult = ",
										text, textAscii, result.toString()));
					}
					return result ? buttonElement : null;
				}
			});
			assertThat(element, notNullValue());
		} catch (Exception e) {
			System.err.println("Exception: " + e.toString());
		}
	}
}

