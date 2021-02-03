package com.github.sergueik.selenium;
/**
 * Copyright 2021 Serguei Kouzmine
 */

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import java.lang.reflect.Type;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Selected test scenarios for Selenium WebDriver the test site:
 * https://github.com/sergueik/springboot_study/tree/master/basic-jsp
 * 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class ChildToParentWindowpostMessageTest extends BaseTest {

	private static String result = null;

	private static WebDriver iframe = null;
	private static WebElement element = null;

	private static final boolean debug = Boolean
			.parseBoolean(getPropertyEnv("DEBUG", "false"));
	private static final boolean remote = Boolean
			.parseBoolean(getPropertyEnv("REMOTE", "false"));

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
	}

	@BeforeMethod
	public void loadPage() {
		driver.navigate().to("about:blank");

	}

	@Test(enabled = true)
	public void test1() {
		driver.navigate().to("http://localhost:8080/demo/iframe_example.html");
		iframe = wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.cssSelector("iframe#frame1")));
		sleep(100);
		executeScript(iframe, getScriptContent("data_sender.js"), new Object[] {});
		sleep(2000);
		alert = driver.switchTo().alert();
		result = alert.getText();
		System.err.println("Raw result: " + result.substring(0, 200) + "...");
		alert.accept();
		element = iframe.findElement(By.cssSelector("form input[type='button']"));
		actions.moveToElement(element).click().build().perform();
		sleep(2000);
		alert = driver.switchTo().alert();
		result = alert.getText();
		System.err.println("Raw result: " + result.substring(0, 200) + "...");
		alert.accept();
		driver.switchTo().defaultContent();
		element = driver.findElement(By.id("messages"));

		assertThat(element, notNullValue());
		result = element.getAttribute("innerHTML");
		System.err.println("Raw result: " + result.substring(0, 200) + "...");
	}

	@Test(enabled = true)
	public void test2() {
		driver.navigate().to("http://localhost:8080/demo/iframe_nocode_example.html");
		executeScript(getScriptContent("parent_script.js"));
		iframe = wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.cssSelector("iframe#frame1")));
		sleep(100);
		executeScript(iframe, getScriptContent("data_sender.js"), new Object[] {});
		sleep(2000);
		alert = driver.switchTo().alert();
		result = alert.getText();
		System.err.println("Raw result: " + result.substring(0, 200) + "...");
		alert.accept();
		element = iframe.findElement(By.cssSelector("form input[type='button']"));
		actions.moveToElement(element).click().build().perform();
		sleep(2000);
		alert = driver.switchTo().alert();
		result = alert.getText();
		System.err.println("Raw result: " + result.substring(0, 200) + "...");
		alert.accept();
		driver.switchTo().defaultContent();
		element = driver.findElement(By.id("messages"));

		assertThat(element, notNullValue());
		result = element.getAttribute("innerHTML");
		System.err.println("Raw result: " + result.substring(0, 200) + "...");
	}

}
