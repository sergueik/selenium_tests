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

public class ChromeHeadlessDetectionTest extends BaseTest {

	private static final StringBuffer verificationErrors = new StringBuffer();
	private static final Logger log = LogManager
			.getLogger(ChromeHeadlessDetectionTest.class);

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
		driver.navigate()
				.to(remote
						? "https://intoli.com/blog/not-possible-to-block-chrome-headless/chrome-headless-test.html"
						: getPageContent("chrome-headless-test.html"));

		Map<String, String> statuses = new HashMap<>();
		try {
			List<WebElement> elements = driver
					.findElements(By.xpath("//*[contains(@class, 'result')]"));
			assertTrue(elements.size() > 0);

			for (int cnt = 0; cnt != elements.size(); cnt++) {
				WebElement element = elements.get(cnt);
				String value = element.getAttribute("class").replaceAll("result", "")
						.replaceAll("\\s", "");
				highlight(element);

				WebElement element2 = element
						.findElement(By.xpath("preceding-sibling::td"));
				highlight(element);
				// NOTE: not descriptive
				String key = element.getAttribute("id");
				System.err
						.println("Collecting " + element2.getText().replaceAll("\\n", " "));
				statuses.put(key, value);
				sleep(500);
			}
			for (Entry<String, String> entry : statuses.entrySet()) {
				System.err.println(entry.getKey() + " = " + entry.getValue());
			}

		} catch (InvalidSelectorException e) {
			System.err.println("Test1 Ignored: " + e.toString());
		}
	}
	/*
	 *  headless mode, Selenium:
	 *  permissions-result = failed
	 * 	chrome-result = failed
	 *  languages-result = passed
	 * 	webdriver-result = passed
	 * 	plugins-length-result = failed
	 * 	user-agent-result = passed
	 *  headless mode, CDP:
	 *  permissions-result = failed
	 *  chrome-result = failed
	 *  languages-result = passed
	 *  webdriver-result = passed
	 *  plugins-length-result = failed
	 *  user-agent-result = passed
	 *  on-screen node, CDP:
	 *  permissions-result = failed
	 *  chrome-result = passed
	 *  languages-result = passed
	 *  webdriver-result = passed
	 *  plugins-length-result = passed
	 *  user-agent-result = passed
	 */
}
