package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

/**
* Sample test scenario for building  strongly typed link extracted from web page via joup
* @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
*/

// Based on
// https://toster.ru/q/645771
public class GeoPlacesDropdownTest extends BaseTest {

	private static final Logger log = LogManager
			.getLogger(GeoPlacesDropdownTest.class);
	private String baseURL = "https://developers.google.com/maps/documentation/javascript/examples/places-placeid-geocoder";
	private static String elementText;
	private static WebElement element;
	private static List<WebElement> elements;
	private static WebDriver iframeDriver;

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
	}

	@BeforeMethod
	public void BeforeMethod() {
		driver.get(baseURL);
	}

	@Test(enabled = true)
	public void testDropdownSelect() {
		// Locating map container page element
		element = driver.findElement(
				By.cssSelector("#gc-wrapper article > div.devsite-article-body"));

		assertThat(element, notNullValue());
		highlight(element, 1000);
		elements = element.findElements(By.tagName("iframe"));
		assertThat(elements.size(), greaterThan(0));
		for (WebElement iframeElement : elements) {
			try {
				String key = String.format("id: \'%s\", name: \"%s\"",
						Base64.getEncoder()
								.encode(MessageDigest.getInstance("SHA-256")
										.digest(iframeElement.getAttribute("src").getBytes())),
						iframeElement.getAttribute("src") /* may be empty */);
				System.err.println(String.format("Found iframe with src: \"%s\"",
						iframeElement.getAttribute("src")));
			} catch (NoSuchAlgorithmException e) {
				// noop
			}
		}

		iframeDriver = driver.switchTo().frame(elements.get(0));
		sleep(500);
		// type the beginning the address
		element = iframeDriver.findElement(By.id("pac-input"));
		assertThat(element, notNullValue());

		element.sendKeys("MIA");

		sleep(1000);
		// overwrite super actions
		actions = new Actions(iframeDriver);
		// Scroll few items down the pac menu
		for (int cnt = 0; cnt != 10; cnt++) {
			actions.sendKeys(Keys.ARROW_DOWN).perform();
			sleep(500);
		}
		elements = iframeDriver.findElements(By.className("pac-item"));
		assertThat(elements, notNullValue());
		for (WebElement addressElement : elements) {
			// System.err.println(addressElement.getAttribute("outerHTML"));
			elementText = null;
			try {
				elementText = getElementText(addressElement);
				assertThat(elementText, notNullValue());
				System.err.println("item (1): " + elementText);
			} catch (NoSuchElementException e) {
				System.err.println("Exception (ignored): " + e.getMessage());
			}
			elementText = null;
			try {
				elementText = getElementText(addressElement);
				assertThat(elementText, notNullValue());
				System.err.println("item (2): " + elementText);
			} catch (NoSuchElementException e) {
				System.err.println("Exception (ignored): " + e.getMessage());
				elementText = element.getText();
			}
		}

	}

	// get text of dynamic menu item
	public String getElementText(WebElement element) {
		return (String) executeScript("return arguments[0].textContent", element);
	}
}
