package com.github.sergueik.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.ScriptTimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Keyboard;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.StringContains.containsString;

public class PageWithBodyTest extends BaseTest {

	private String baseURL = "about:blank";
	private static WebElement element;
	private static Keyboard keyboard;

	@BeforeMethod
	public void beforeMethod() {
		System.err.println("Navigate to URL: " + baseURL);
		driver.get(baseURL);
		setPage("<body><a id=first' href='#first'>go to Heading 1</a></body>");
		sleep(1000);
	}

	@Test
	public void test1() {
		try {
			element = driver.findElement(By.id("first"));
			assertThat(element, notNullValue());
			System.err.println("Element: " + element.getAttribute("outerHTML"));
		} catch (NoSuchElementException e) {
			System.err.println("Exception (ignored): " + e.toString());
		}
	}

	@Test
	public void test2() {
		try {
			element = (WebElement) executeScript("return document.getElementsByTagName('a')[0]");
			assertThat(element, notNullValue());
			System.err.println("Element: " + element.getAttribute("outerHTML"));
		} catch (ScriptTimeoutException e) {
			System.err.println("Exception (ignored): " + e.toString());
		}

	}
}
