package com.github.sergueik.selenium;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.WebDriverException;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static java.lang.System.err;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
/*
 * based on https://stackoverflow.com/questions/59277001/selenium-is-not-loading-tiktok-pages
 * inspired by https://qna.habr.com/q/705563  
*/

public class TikTakTest extends BaseTest {

	private static String baseURL = "https://www.tiktok.com/@egorkreed";
	private static final StringBuffer verificationErrors = new StringBuffer();

	@BeforeClass
	public void beforeClass() throws IOException {
		// super.setBrowser("firefox");
		super.setBrowser("chrome");
		super.beforeClass();
		// TODO: load prepared profile
	}

	@BeforeMethod
	public void beforeMethod() {
		// TODO: load prepared profile
		driver.get(baseURL);
		wait.until(driver -> driver.getCurrentUrl()
				.matches(String.format("^%s.*", baseURL)));
		err.println("BeforeMethod: Current  URL: " + driver.getCurrentUrl());
		sleep(3000);
	}

	@Test(enabled = true)
	public void selectFeedsTest() {

		try {
			WebElement element = wait.until(ExpectedConditions.visibilityOf(driver
					.findElement(By.cssSelector("div[class*=\"video-feed-item\"]"))));
			assertThat(element, notNullValue());
		} catch (WebDriverException e) {
			// Permission denied to access property "handleEvent"
			err.println("Exception (ignored) " + e.toString());
			// continue
		}
		List<WebElement> elements = driver
				.findElements(By.cssSelector("div[class*=\"video-feed-item\"]"));
		System.err.println("Start with video feed item element : "
				+ elements.get(0).getAttribute("class"));

		elements = driver
				.findElements(By.cssSelector("div[class*=\"video-feed-item\"] a"));
		elements.stream().limit(10).forEach(
				e -> err.println("Video link selected: " + e.getAttribute("href")));

		int maxCnt = 10;
		int cnt = 0;

		for (WebElement e : elements) {
			cnt++;
			if (cnt >= maxCnt) {
				break;
			}
			err.println("Video link selected: " + e.getAttribute("href"));
		}

		sleep(1000);
	}

	@AfterMethod
	public void afterMethod(ITestResult result) {
		if (verificationErrors.length() != 0) {
			throw new RuntimeException(String.format("Error(s) in the method %s : %s",
					result.getMethod().getMethodName(), verificationErrors.toString()));
		}
		driver.get("about:blank");
	}

	@AfterClass
	public void afterTest() {
		// TODO: load prepared profile
	}

}