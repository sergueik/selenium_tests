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
import org.openqa.selenium.JavascriptException;

/**
 * Selected test scenarios for Selenium WebDriver
 * 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class CustomWaitTest extends BaseTest {

	private static WebElement element;

	private static final String baseURL = "https://ya.ru";

	private static final String selector = "button.button_noT_there";
	private static final String kind = "css";

	@Test(enabled = true)
	public void getCoordsTestWithAlertEnabled() {

		driver.get(baseURL);
		try {
			System.err.println("starting wait");
			super.executeScript(super.getScriptContent("customWait.js"), selector, kind, 10, 300, true);
			// NOTE: currently not blocking
			System.err.println("ending wait");
		} catch (JavascriptException e) {
			System.err.println("Exception (ignored): " + e.getMessage() + " Element must not be found");
		}
		sleep(1000);
	}

	@Test(enabled = false)
	public void getCoordsTest() {

		driver.get(baseURL);
		try {
			System.err.println("starting wait");
			super.executeScript(super.getScriptContent("customWait.js"), selector, kind, 120, 300, false);
			// NOTE: currently not blocking
			System.err.println("ending wait");
		} catch (JavascriptException e) {
			System.err.println("Exception (ignored): " + e.getMessage() + " Element must not be found");
		}
		sleep(120000);
		/*
		 * element =
		 * wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector(
		 * selector)))); assertThat(element, notNullValue());
		 */
	}

}
