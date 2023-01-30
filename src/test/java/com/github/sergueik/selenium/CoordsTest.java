package com.github.sergueik.selenium;

/**
 * Copyright 2020, 2023 Serguei Kouzmine
 */

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Selected test scenarios for Selenium WebDriver
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;

import java.util.HashMap;
import java.util.Map;

// inspired by https://qna.habr.com/q/700693

public class CoordsTest extends BaseTest {
	private static Gson gson = new Gson();
	private static String response = null;
	private static WebElement element;
	private static Map<String, Object> data = new HashMap<>();
	private static Map<String, Object> result = new HashMap<>();

	@DataProvider(name = "argument-provider", parallel = false)
	public Object[][] provideArgs() throws Exception {
		return new Object[][] {
				{ "https://www.wikipedia.org/", "#searchInput", "css" },
				{ "https://www.wikipedia.org/", "searchInput", "id" },
				{ "https://ya.ru/", "body > main > div.headline", "css" },
				{ "https://ya.ru/", "#text", "css" },
				{ "https://ya.ru/", "text", "id" } };
	}

	@SuppressWarnings("unchecked")
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
		response = (String) super.executeScript(
				super.getScriptContent("getCoords.js"), selector, kind, false);
		System.err.println("response:" + response);
		sleep(100);
		response = (String) super.executeScript(
				super.getScriptContent("getCoords.js"), element, "", false);
		System.err.println("response:" + response);
		data = (Map<String, Object>) gson.fromJson(response, Map.class);
		assertThat(data, notNullValue());
		assertThat(data, hasKey("result"));
		Map<String, Object> result = (Map<String, Object>) gson
				.fromJson(data.get("result").toString(), Map.class);
		assertThat(result, notNullValue());
		assertThat(result, hasKey("x"));
		assertThat(result, hasKey("y"));
		log.info(String.format("coords: %.3f %.3f",
				Float.parseFloat(result.get("x").toString()),
				Float.parseFloat(result.get("y").toString())));

	}

}