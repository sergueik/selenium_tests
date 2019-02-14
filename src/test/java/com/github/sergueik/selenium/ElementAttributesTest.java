package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Selected test scenarios for Selenium WebDriver
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 * based on https://stackoverflow.com/questions/27307131/selenium-webdriver-how-do-i-find-all-of-an-elements-attributes
 */

public class ElementAttributesTest extends BaseTest {

	private static final Logger log = LogManager
			.getLogger(ElementAttributesTest.class);
	private String baseUrl = "https://stackoverflow.com";

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
	}

	@BeforeMethod
	public void beforeMethod(Method method) {
		super.beforeMethod(method);
		driver.get(baseUrl);
	}

	@AfterMethod
	@Override
	public void afterMethod() {
		driver.get("about:blank");
	}

	private static final String id = "hireme";
	private static String xpath = String.format("//*[@id='%s']", id);

	@Test(enabled = true)
	public void attributesJSONTest() {
		// Arrange
		WebElement element = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
		highlight(element);
		// Act
		String script = getScriptContent("getAttributes.js");
		String jsonString = (String) js.executeScript(script, element, true);
		// Assert
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(jsonString);
			assertThat(jsonObject.getString("id"), equalTo(id));
		} catch (JSONException e) {
			System.err.println("Exception (ignored): " + e.getMessage());
		}
		System.err.println(String.format("%s finds %s", script, jsonString));
	}

	@Test(enabled = true)
	public void attributesCollectionTest() {
		// Arrange
		WebElement element = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
		highlight(element);
		// Act
		String script = getScriptContent("getAttributes.js");
		Map<String, Object> result = (Map<String, Object>) js.executeScript(script,
				element, false);
		// Assert
		assertThat(result.get("id"), equalTo(id));
		System.err.println(String.format("%s finds %s", script, result.get("id")));
	}

	// https://aboullaite.me/jsoup-html-parser-tutorial-examples/
	@Test(enabled = true)
	public void attributesHTMLParseTest() {
		// Arrange
		WebElement element = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
		highlight(element);
		// Act
		String elementHTML = element.getAttribute("outerHTML");
		Document jsoupDocument = Jsoup.parse(elementHTML);
		Element divJsoupElement = jsoupDocument.getElementById(id);
		// Assert
		// will fail
		assertThat(divJsoupElement, notNullValue());
		assertThat(divJsoupElement.id(), equalTo(id));
		System.err
				.println(String.format("Processing %s", xpath, divJsoupElement.text()));
	}
}
