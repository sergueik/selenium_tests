package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Key;
import org.sikuli.script.Match;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.assertTrue;

/**
 * Selected test scenarios for Selenium WebDriver
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

// based on

public class CoordsTest extends BaseTest {

	private String baseURL;
	private String selector;
	private static String id;

	@Test(priority = 1, enabled = true)
	public void getCoordsTest1() {
		baseURL = "https://www.wikipedia.org/";
		selector = "#searchInput";
		driver.get(baseURL);
		WebElement element = wait.until(ExpectedConditions
				.visibilityOf(driver.findElement(By.cssSelector(selector))));
		assertThat(element, notNullValue());
		String data = (String) super.executeScript(
				super.getScriptContent("getCoords.js"), selector, false);
		System.err.println(data);
		sleep(100);
	}

	@Test(priority = 1, enabled = false)
	public void getCoordsTest2() {
		baseURL = "https://www.wikipedia.org/";
		id = "searchInput";
		driver.get(baseURL);
		WebElement element = wait
				.until(ExpectedConditions.visibilityOf(driver.findElement(By.id(id))));
		assertThat(element, notNullValue());
		String data = (String) super.executeScript(
				super.getScriptContent("getCoords2.js"), id, false);
		System.err.println(data);
		sleep(100);
	}

	@Test(priority = 3, enabled = true)
	public void getCoordsTest3() {
		baseURL = "https://ya.ru/";
		selector = "button.button";
		driver.get(baseURL);
		WebElement element = wait.until(ExpectedConditions
				.visibilityOf(driver.findElement(By.cssSelector(selector))));
		// no such element: Unable to locate element: {"method":"css
		// selector","selector":" #text"}
		assertThat(element, notNullValue());
		String data = (String) super.executeScript(
				super.getScriptContent("getCoords.js"), selector, false);
		System.err.println(data);
		sleep(100);
	}

	@Test(priority = 4, enabled = true)
	public void getCoordsTest4() {
		baseURL = "https://ya.ru/";
		selector = "#text";
		driver.get(baseURL);
		WebElement element = wait.until(ExpectedConditions
				.visibilityOf(driver.findElement(By.cssSelector(selector))));
		// no such element: Unable to locate element: {"method":"css
		// selector","selector":" #text"}
		assertThat(element, notNullValue());
		String data = (String) super.executeScript(
				super.getScriptContent("getCoords.js"), selector, false);
		System.err.println(data);
		sleep(100);
	}

	@Test(priority = 5, enabled = false)
	public void getCoordsTest5() {
		baseURL = "https://ya.ru/";
		id = "text";
		driver.get(baseURL);
		WebElement element = wait
				.until(ExpectedConditions.visibilityOf(driver.findElement(By.id(id))));
		assertThat(element, notNullValue());
		String data = (String) super.executeScript(
				super.getScriptContent("getCoords2.js"), id, false);
		System.err.println(data);
		sleep(100);
	}
}