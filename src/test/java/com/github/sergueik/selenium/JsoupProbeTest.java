package com.github.sergueik.selenium;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
//import org.openqa.selenium.opera.OperaDriver;
//import org.openqa.selenium.opera.OperaOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeMethod;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;

import static org.junit.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static org.hamcrest.Matchers.greaterThan;

/**
* Sample test scenario for Selenium WebDriver operating Yandex Browser
* @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
*/

// Based on
// https://stackoverflow.com/questions/30707783/java-selenium-webdriver-with-yandex
// TODO: super.driver
public class JsoupProbeTest extends BaseTest {

	private static final Logger log = LogManager.getLogger(JsoupProbeTest.class);
	private String filePath = "links.htm";
	// private String selector = "#acListWrap > div:nth-child(3) > div >
	// div.productListingMiddle > div.lower.tabsection";
	private String selector = "#acListWrap div.productListing h2.listSubtitle > a[href*='/item/'][title='Click to View']";
	private String pageSource = null;

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
	}

	@BeforeMethod
	public void loadPage() {
		pageSource = getScriptContent(filePath);
	}

	@Test(enabled = false)
	public void testPageVisual() {
		driver.navigate().to(getPageContent(filePath));
		pageSource = driver.getPageSource();
		List<WebElement> elements = driver.findElements(By.cssSelector(selector));
		assertThat(elements, notNullValue());
		assertThat(elements.size(), greaterThan(1));
		System.err.println(String.format("Processing %s: %s", selector,
				elements.get(0).getText()));
	}

	@Test(enabled = true)
	public void testPageSource() {
		Document jsoupDocument = Jsoup.parse(pageSource);
		final String attributeName = "class";
		final String attributeValue = "productListing";
		Elements divjsoupElements = jsoupDocument
				.getElementsByAttributeValue(attributeName, attributeValue);
		// Assert
		// will fail
		assertThat(divjsoupElements, notNullValue());
		assertThat(divjsoupElements.iterator().hasNext(), is(true));
		assertThat(divjsoupElements.eachText().size(), greaterThan(1));
		System.err.println(String.format("Processing attribute(\"%s\") = \"%s\" %s",
				attributeName, attributeValue, divjsoupElements.first().text()));
	}

	@Test(enabled = true)
	public void testBroken2() {
		Document jsoupDocument = Jsoup.parse(pageSource);

		final List<Map<String, String>> attrMap = new ArrayList<>();
		Map<String, String> attrStep = new HashMap<>();
		attrStep.put("id", "acListWrap");
		attrMap.add(attrStep);
		// attrStep.remove("id");

		attrStep.put("class", "productListing");
		attrMap.add(attrStep);
		// attrStep.remove("class");
		attrStep.put("class", "listSubtitle");
		attrMap.add(attrStep);
		// attrStep.remove("class");
		attrStep.put("title", "Click to View");
		attrMap.add(attrStep);
		// attrStep.remove("title");
		System.err.println("Processing " + attrMap.toString());

		Iterator<Map<String, String>> attrMapIterator = attrMap.iterator();
		Document parentDocument = jsoupDocument;
		while (attrMapIterator.hasNext()) {
			attrStep = attrMapIterator.next();
			System.err.println("Processing " + attrStep.toString());
			/*
			Set<String> attributeKeys = attrStep.keySet();
			assertThat(attributeKeys.size(), greaterThan(0));
			String attributeName = attributeKeys.toArray()[0].toString();
			String attributeValue = attrStep.get(attributeName);
			Elements jsoupElements = parentDocument
					.getElementsByAttributeValue(attributeName, attributeValue);
			
			// Assert
			assertThat(jsoupElements, notNullValue());
			assertThat(jsoupElements.iterator().hasNext(), is(true));
			assertThat(jsoupElements.eachText().size(), greaterThan(1));
			System.err
					.println(String.format("Processing attribute(\"%s\") = \"%s\" %s",
							attributeName, attributeValue, jsoupElements.first().text()));
			parentDocument = (Document) jsoupElements.first();
			*/
		}
	}

	@Test(enabled = true)
	public void testDeepPageSource() {
		Document jsoupDocument = Jsoup.parse(pageSource);

		final List<String> attrKeys = Arrays
				.asList(new String[] { "id", "class", "class", "title" });
		final List<String> attrValues = Arrays.asList(new String[] { "acListWrap",
				"productListing", "listSubtitle", "Click to View" });
		Document parentDocument = jsoupDocument;

		for (int pos = 0; pos != attrKeys.size(); pos++) {
			System.err.println(
					"Processing " + attrKeys.get(pos) + ", " + attrValues.get(pos));
			String attributeName = attrKeys.get(pos);
			String attributeValue = attrValues.get(pos);
			Elements jsoupElements = parentDocument
					.getElementsByAttributeValue(attributeName, attributeValue);

			// Assert
			assertThat(jsoupElements, notNullValue());
			assertThat(jsoupElements.iterator().hasNext(), is(true));
			assertThat(jsoupElements.eachText().size(), greaterThan(0));
			System.err
					.println(String.format("Processing attribute(\"%s\") = \"%s\" %s",
							attributeName, attributeValue, jsoupElements.first().text()));
			parentDocument = Jsoup.parse(jsoupElements.first().text());
		}
	}

	// LinkedHashMap does not work
	@Test(enabled = true)
	public void testBroken() {
		Document jsoupDocument = Jsoup.parse(pageSource);

		final LinkedHashMap<String, String> attrMap = new LinkedHashMap<>();
		attrMap.put("id", "acListWrap");
		attrMap.put("class", "productListing");
		attrMap.put("class", "listSubtitle");
		attrMap.put("title", "Click to View");

		final List<String> attrKeys = new ArrayList<>();
		for (Entry<String, String> o : attrMap.entrySet()) {
			attrKeys.add(o.getKey());
		}
		System.err.println(attrKeys.toString());
	}

}