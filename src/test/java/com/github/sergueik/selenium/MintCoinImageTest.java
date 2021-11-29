package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.greaterThan;

import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

/**
 * Selected test scenarios for Selenium WebDriver
 * inspired by: https://qna.habr.com/q/1081000  
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com) based on
 */

public class MintCoinImageTest extends BaseTest {

	private static final Logger log = LogManager
			.getLogger(MintCoinImageTest.class);
	private static List<String> brokenImages = new ArrayList<>();

	private static boolean debug = false;
	private static String selector;
	private static WebElement element;
	private static WebElement element2;
	private static List<WebElement> elements;
	private static String baseURL = "https://catalog.usmint.gov/coins/coin-programs/america-the-beautiful-quarters-program";
	private static final StringBuffer report = new StringBuffer();
	private static Document jsoupDocument;
	private static boolean found = false;
	private static String document;
	private static Elements jsoupElements;
	private static Element jsoupElement;
	private static Node jsoupNode;
	private List<String> jsoupSelectors = Arrays
			.asList(new String[] { "product-image", "product-id" });

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());

	}

	@BeforeMethod
	public void beforeMethod() {
		driver.navigate().to(baseURL);
		wait.until(ExpectedConditions
				.urlContains("america-the-beautiful-quarters-program"));
		// NOTE: being randomly redirected to
		// "Please verify you are a human
		// Access to this page has been denied because it appears you are using
		// automation tools to browse the website".
		// page
		// https://catalog.usmint.gov/verify-show?url=aHR0cHM6Ly9jYXRhbG9nLnVzbWludC5nb3Yvb24vZGVtYW5kd2FyZS5zdG9yZS9TaXRlcy1VU00tU2l0ZS9kZWZhdWx0L1NlYXJjaC1TaG93P2NnaWQ9YW1lcmljYS10aGUtYmVhdXRpZnVsLXF1YXJ0ZXJz
		// echo
		// 'aHR0cHM6Ly9jYXRhbG9nLnVzbWludC5nb3Yvb24vZGVtYW5kd2FyZS5zdG9yZS9TaXRlcy1VU00tU2l0ZS9kZWZhdWx0L1NlYXJjaC1TaG93P2NnaWQ9YW1lcmljYS10aGUtYmVhdXRpZnVsLXF1YXJ0ZXJz'
		// | base64 -d
		// https://catalog.usmint.gov/on/demandware.store/Sites-USM-Site/default/Search-Show?cgid=america-the-beautiful-quarters
		System.err.println(driver.getCurrentUrl());
	}

	// dump
	@Test(enabled = false)
	public void test1() {
		elements = driver
				.findElements(By.xpath("//div[contains(@class,'product-tile')]"));
		processElements(elements);

	}

	// dump, only different in selector
	@Test(enabled = false)
	public void test2() {
		elements = driver.findElements(By.cssSelector("div.product-tile"));
		processElements(elements);
	}

	// scroll
	@Test(enabled = false)
	public void test3() {
		elements = driver.findElements(By.cssSelector("div.product-tile"));
		System.err.println("Processing " + elements.size() + " elements");

		for (int cnt = 1; cnt < elements.size() + 1; cnt++) {

			System.err.println("Processing element " + cnt);

			selector = "div.product-tile";

			selector += (cnt > 1) ? ":nth-of-type(" + cnt + ")" : ":first-of-type";
			element = null;
			try {
				element = wait.until(ExpectedConditions
						.visibilityOf(driver.findElement(By.cssSelector(selector))));
			} catch (NoSuchElementException e) {
				System.err.println("Exception (ignored):  " + e.toString());
				System.err.println("Continue to next element");
				continue;
			}
			// NOTE: challenge past 3 element presumably due to
			// div.product-tile.topper-wrap
			assertThat(element, notNullValue());
			System.err
					.println("Element itemid: " + element.getAttribute("data-itemid"));
			try {
				element2 = wait.until(ExpectedConditions.visibilityOf(
						driver.findElement(By.cssSelector(selector + " div.product-id"))));
				System.err.println("Element product id: " + element2.getText());
				found = true;
			} catch (NoSuchElementException e) {
				System.err.println("Exception (ignored):  " + e.toString());
				found = false;
			}
			try {
				element2 = wait
						.until(ExpectedConditions.visibilityOf(driver.findElement(
								By.cssSelector(selector + " div[class *='product-image']"))));
				System.err
						.println("Element image: " + element2.getAttribute("outerHTML"));
				found = true;
			} catch (NoSuchElementException e) {
				System.err.println("Exception (ignored):  " + e.toString());
				found = false;
			}
			if (!found) {
				System.err
						.println("Element document: " + element.getAttribute("innerHTML"));
			}
			int y = element.getLocation().y;
			System.err.println("Element y position to scroll: " + y);
			actions.moveToElement(element).build().perform();
			scroll(0, y);
			// TODO: scroll
		}

	}

	// load more
	@Test(enabled = true)
	public void test4() {
		int increment = 250;
		int max_iterarion = 10;
		found = false;
		element = null;
		for (int cnt = 0; cnt != max_iterarion; cnt++) {
			System.err.println("Iteration :  " + cnt);
			elements = driver
					.findElements(By.xpath("//button[contains(text(),'Load More')]"));
			if (elements.size() > 0) {
				System.err.println("Found button");
				element = elements.get(0);
				break;
			}
			scroll(0, increment);
			actions.sendKeys(Keys.ARROW_DOWN);
			sleep(1000);
		}
		if (element != null) {
			try {
				actions.moveToElement(element).build().perform();
				sleep(3000);
				highlight(element);
				sleep(1000);
				element.click();
				sleep(10000);
			} catch (ElementNotInteractableException e) {
				System.err.println("Exception (ignored):  " + e.toString());
				// element not interactable: [object HTMLButtonElement] has no size and
				// location
			}
		}
		/*
		max_iterarion = 10;
		found = false;
		for (int cnt = 0; cnt != max_iterarion; cnt++) {
			if (found)
				break;
			System.err.println("Iteration :  " + cnt);
			try {
				element = wait.until(ExpectedConditions.visibilityOf(driver
						.findElement(By.xpath("//button[contains(text(),'Load More')]"))));
				found = true;
			} catch (TimeoutException e) {
				System.err.println("Exception (ignored):  " + e.toString());
				scroll(0, increment);
			}
		}
		*/

	}

	private void processElements(List<WebElement> elements) {
		System.err.println("Processing " + elements.size() + " elements");
		int max_cnt = elements.size();
		for (int cnt = 0; cnt < max_cnt; cnt++) {
			System.err.println("Processing element " + cnt);
			element = elements.get(cnt);
			document = element.getAttribute("outerHTML");
			// NOTE: not "innerHTML"
			if (debug)
				System.err.println("Element document: " + document);
			jsoupDocument = Jsoup.parse(document);
			assertThat(jsoupDocument.body(), notNullValue());
			System.err.println("Element tree: " + jsoupDocument.body().childNodeSize()
					+ " child nodes.");
			System.err.println("Element sub tree: "
					+ jsoupDocument.body().childNode(0).childNodeSize()
					+ " child nodes.");
			for (String jsoupSelector : jsoupSelectors) {
				System.err.println("Seaching: " + jsoupSelector);
				for (int k = 0; k != jsoupDocument.body().childNode(0)
						.childNodeSize(); k++) {
					jsoupNode = jsoupDocument.body().childNode(0).childNode(k);
					if (jsoupNode.hasAttr("class")
							&& jsoupNode.attr("class").startsWith(jsoupSelector)) {
						System.err.println("Found: " + jsoupSelector);
						System.err.println(jsoupNode.outerHtml());
					}
				}
			}
			// this is failing, commented
			/*
			for (String jsoupSelector : jsoupSelectors) {
				System.err.println("Seaching: " + jsoupSelector);
				jsoupElements = jsoupDocument.body()
						.getElementsByAttributeValueContaining("class", jsoupSelector);
				assertThat(jsoupElements, notNullValue());
				assertThat(jsoupElements.iterator().hasNext(), is(true));
				assertThat(jsoupElements.eachText().size(), greaterThan(0));
				System.err.println(String.format("Processing jsoup selector \"%s\" %s",
						jsoupSelector, jsoupElements.first().text()));
			}
			*/
		}

	}
}
