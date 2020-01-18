package com.github.sergueik.selenium;

/**
 * Copyright 2020 Serguei Kouzmine
 */

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Selected test scenarios for Selenium WebDriver
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

// inspired by https://qna.habr.com/q/700693

public class CoordsTest extends BaseTest {

	@DataProvider(name = "argument-provider", parallel = false)
	public Object[][] provideArgs() throws Exception {
		return new Object[][] {
				{ "https://www.wikipedia.org/", "#searchInput", "css" },
				{ "https://www.wikipedia.org/", "searchInput", "id" },
				{ "https://ya.ru/", "button.button", "css" },
				{ "https://ya.ru/", "#text", "css" },
				{ "https://ya.ru/", "text", "id" } };
	}

	private static WebElement element;

	@Test(enabled = true, dataProvider = "argument-provider", threadPoolSize = 2)
	public void getCoordsTest(String baseURL, String selector, String kind) {
		driver.get(baseURL);
		if (kind.matches("id")) {
			element = wait.until(
					ExpectedConditions.visibilityOf(driver.findElement(By.id(selector))));
		} else {
			element = wait.until(ExpectedConditions
					.visibilityOf(driver.findElement(By.cssSelector(selector))));
		}
		assertThat(element, notNullValue());
		String data = (String) super.executeScript(
				super.getScriptContent("getCoords.js"), selector, kind, false);
		System.err.println(data);
		sleep(100);
		data = (String) super.executeScript(super.getScriptContent("getCoords.js"),
				element, "", false);
		System.err.println(data);
	}

}