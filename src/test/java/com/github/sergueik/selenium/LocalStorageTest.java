package com.github.sergueik.selenium;
/**
 * Copyright 2023 Serguei Kouzmine
 */

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.github.sergueik.selenium.BaseTest;

import net.sourceforge.htmlunit.corejs.javascript.JavaScriptException;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Interacting with Chrome local Storage using Selenium WebDriver 
 * based on: 
 * https://github.com/mrafee113/pyselenium_localstorage 
 * see also (and for SessionStorage): 
 * https://github.com/nihit007/LocalStorageandSessionStorage/blob/master/src/test/java/com/session/local/RetrievingStorageData.java 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */
public class LocalStorageTest extends BaseTest {

	private static String baseURL = "https://www.google.com";
	private static final StringBuffer verificationErrors = new StringBuffer();
	private static String defaultScript = null;
	private static Map<String, String> scripts = new HashMap<>();
	static {
		scripts.put("length", "return window.localStorage.length;");
		scripts.put("dictionary",
				"var ls = window.localStorage, items = {}; "
						+ "for (var i = 0, k; i < ls.length; ++i) "
						+ "  items[k = ls.key(i)] = ls.getItem(k); " + "return items; ");
		scripts.put("keys",
				"var ls = window.localStorage, keys = []; "
						+ "for (var i = 0; i < ls.length; ++i) " + "  keys[i] = ls.key(i); "
						+ "return keys; ");
		scripts.put("getItem", "return window.localStorage.getItem(arguments[0]);");
		scripts.put("setItem",
				"window.localStorage.setItem(arguments[0], arguments[1]);");
		scripts.put("removeItem", "window.localStorage.removeItem(arguments[0]);");
		scripts.put("clear", "window.localStorage.clear();");
	}

	@BeforeMethod
	public void BeforeMethod(Method method) {
		// Chrome only, run with profile
		// mvn -Pchrome test
		super.beforeMethod(method);
		driver.get(baseURL);
	}

	@AfterMethod
	public void AfterMethod(ITestResult result) {
		if (verificationErrors.length() != 0) {
			throw new RuntimeException(String.format("Error(s) in the method %s : %s",
					result.getMethod().getMethodName(), verificationErrors.toString()));
		}
		driver.get("about:blank");
	}

	@Test(enabled = true, priority = 10)
	public void test1() {
		// Syntax error on token "(", assert expected before this token ??
		// (void) executeScript(scripts.get("setItem"),"test", 123L);
		executeScript(scripts.get("setItem"), "test", 123L);
		Long result = Long
				.parseLong((String) executeScript(scripts.get("getItem"), "test"));
		assertThat(result, is(123L));
		// System.err.println("keys: " + keys);
	}

	@Test(enabled = true, priority = 20)
	public void test2() {
		Long length = (Long) executeScript(scripts.get("length"));
		System.err.println("length: " + length);
	}

	@Test(enabled = true, priority = 30)
	public void test3() {
		@SuppressWarnings("unchecked")
		List<String> keys = (List<String>) executeScript(scripts.get("keys"));
		System.err.println("keys: " + keys);
	}

	@Test(enabled = true, priority = 40)
	public void test4() {
		@SuppressWarnings("unchecked")
		Map<String, String> dictionary = (Map<String, String>) executeScript(
				scripts.get("dictionary"));
		System.err.println("keys: " + Arrays.asList(dictionary.keySet()));
	}

	@Test(enabled = true, priority = 50)
	public void test5() {
		executeScript(scripts.get("clear"));
	}

	@Test(enabled = true, priority = 60)
	public void test6() {
		@SuppressWarnings("unchecked")
		List<String> keys = (List<String>) executeScript(scripts.get("keys"));
		assertThat(keys.size(), is(0));

	}
}
