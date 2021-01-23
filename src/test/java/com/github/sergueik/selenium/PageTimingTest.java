package com.github.sergueik.selenium;
/**
 * Copyright 2021 Serguei Kouzmine
 */

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Selected test scenarios for Selenium WebDriver based on
 * https://antoinevastel.com/bot%20detection/2017/08/05/detect-chrome-headless.html
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class PageTimingTest extends BaseTest {

	private static final StringBuffer verificationErrors = new StringBuffer();
	private static final Logger log = LogManager.getLogger(PageTimingTest.class);

	@SuppressWarnings("unused")
	private static Pattern pattern;
	private static Matcher matcher;

	private static final boolean debug = false;
	private static final boolean remote = Boolean
			.parseBoolean(getPropertyEnv("REMOTE", "false"));

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
	}

	@BeforeMethod
	public void loadPage() {

	}

	@Test
	public void chromeHeadlessDetectionTest() {
		driver.navigate().to("https://www.uat.edu/");
		Map<String, String> statuses = new HashMap<>();
		String result = (String) executeScript(
				getScriptContent("compute-timing.js"), new Object[] {});
		// argument is ignored
		System.err.println(result);
	}
}

/**
 * unload
 *   unloadEventStart
 *   unloadEventEnd
 *
 * navigationStart
 *
 * redirect
 *   redirectStart
 *   redirectEnd
 *
 * App cache
 *   fetchStart
 *
 * DNS
 *   domainLookupStart
 *   domainLookupEnd
 *
 * TCP
 *   connectStart
 *     secureConnectionStart
 *   connectEnd
 *
 * Request
 *   requestStart
 *
 * Response
 *   responseStart
 *   responseEnd
 *
 * Processing
 *   domLoading
 *   domInteractive
 *
 *   domContentLoadedEventStart
 *   domContentLoadedEventEnd
 *
 *   domComplete
 *
 * onLoad
 *   loadEventStart
 *   loadEventEnd
 *
 */

