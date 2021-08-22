package com.github.sergueik.selenium;
/**
 * Copyright 2021 Serguei Kouzmine
 */

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.InvalidSelectorException;
import java.lang.ClassCastException;
import java.util.Map;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Attr;

/**
 * Selected test scenarios for Selenium WebDriver
 * 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class DataImageTest extends BaseTest {

	private static WebElement element;
	private static Object result;
	private static Attr attr;
	private static Map<String, Object> data;

	// $x("//img[@id='data_image']/@src")[0]
	private static final String script = "var path = arguments[0]; \n"
			+ "var element; try { \n"
			+ "element = window.document.evaluate(path, window.document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue; \n"
			+ "return element; } \n" + "catch (e) { return e.toString()}";

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
	}

	@BeforeMethod
	public void loadPage() {
		// Arrange
		driver.navigate().to(getPageContent("data_image.html"));
	}

	@Test
	public void test1() {

		// Act
		element = driver.findElement(By.cssSelector("img#data_image"));
		assertThat(element, notNullValue());
		assertThat(element.getAttribute("src"), notNullValue());

		assertThat(element.getAttribute("src").length(), greaterThan(0));
		System.err.println("image source: " + element.getAttribute("src"));
	}

	@Test
	public void test2() {
		// Act
		try {
			result = driver.findElement(By.xpath("//img[@id='data_image']/@src"));
			assertThat(result, notNullValue());
			System.err.println(
					"image source: " + result.toString().substring(0, 100) + "...");
		} catch (InvalidSelectorException e) {
			// The result of the xpath expression "//img[@id='data_image']/@src"
			// is: [object Attr]. It should be an element."
			System.err.println("Exception (ignored): " + e.toString());
		}
	}

	@Test
	public void test3() {
		// Act
		result = executeScript(script, "//img[@id='data_image']/@src");
		assertThat(result, notNullValue());
		System.err.println("DOM call result: " + result.toString());

	}

	@Test
	public void test4() {
		// Act
		try {
			attr = (Attr) executeScript(script, "//img[@id='data_image']/@src");
			assertThat(attr, notNullValue());
			System.err.println("Attr result: " + attr.getValue());
		} catch (ClassCastException e) {
			System.err.println("Exception (ignored): " + e.toString());
			// com.google.common.collect.Maps$TransformedEntriesMap
			// cannot be cast to
			// org.w3c.dom.Attr;
		}

	}

	@SuppressWarnings("unchecked")
	public void test5() {
		// Act
		data = (Map<String, Object>) executeScript(script,
				"//img[@id='data_image']/@src");
		assertThat(data, notNullValue());
		assertThat(data, hasKey("nodeValue"));
		System.err.println("data[\"nodeValue\"] : "
				+ data.get("nodeValue").toString().substring(0, 100) + "...");

	}

	@Test
	@SuppressWarnings("unchecked")
	public void test6() {
		// Act
		data = (Map<String, Object>) executeScript(script,
				"//img[@id='data_image']/@src");
		assertThat(data, notNullValue());
		data.keySet().stream().map(key -> {
			Object value = data.get(key);
			return String.format("%s=%s", key,
					(value == null ? "null" : value.toString()));
		}).forEach(System.err::println);
	}

}

