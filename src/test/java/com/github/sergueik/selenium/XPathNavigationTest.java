package com.github.sergueik.selenium;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.internal.Nullable;

/**
 * Selected test scenarios for Selenium WebDriver
 * Use XPath ancestor axis and CSSSelector 'closest' method for navigation and manipulating heavily styled page.
 * based on: https://testerslittlehelper.wordpress.com/
 * 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class XPathNavigationTest extends BaseTest {

	private static final StringBuffer verificationErrors = new StringBuffer();
	private static final Logger log = LogManager
			.getLogger(XPathNavigationTest.class);

	private static String baseURL = "about:blank";

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
	}

	@BeforeMethod
	public void BeforeMethod() {
		driver.get(baseURL);
	}

	// https://habr.com/company/ruvds/blog/416539/
	// https://developer.mozilla.org/en-US/docs/Web/API/Element/closest
	@Test(enabled = true)
	public void demoqaTest1() {
		String baseURL = "http://store.demoqa.com/products-page/";
		driver.get(baseURL);

		// Arrange
		List<WebElement> elements = new ArrayList<>();
		elements = driver
				.findElements(By.cssSelector("span.currentprice:nth-of-type(1)"));
		elements.stream().forEach(element -> {
			executeScript("arguments[0].scrollIntoView({ behavior: \"smooth\" });",
					element);
			highlight(element, 1000);
			boolean debug = false;
			if (debug) {
				List<String> scripts = new ArrayList<>(Arrays.asList(new String[] {
						// immediate ancestor, not the one test is looking for, but
						// helped finding the following one
						"var element = arguments[0];\n"
								+ "var locator = 'div.wpsc_product_price';"
								+ "var targetElement = element.closest(locator);\n"
								+ "targetElement.scrollIntoView({ behavior: 'smooth' });\n"
								+ "return targetElement.outerHTML;",
						// next in the ancestor chain, located and printed the outerHTML of
						// element for debugging purposes
						"var element = arguments[0];\n" + "var locator = 'form';\n"
								+ "var targetElement = element.closest(locator);\n"
								+ "targetElement.scrollIntoView({ behavior: 'smooth' });\n"
								+ "return targetElement.outerHTML;",
						// relevant ancestor chain, chained with a quesySelector call
						// but with full classes making it hard to read and fragile
						"var element = arguments[0];\n"
								+ "var ancestorLocator = 'div.productcol';"
								+ "var targetElementLocator = 'div[class=\"input-button-buy\"]';"
								+ "var targetElement = element.closest(ancestorLocator).querySelector(targetElementLocator);\n"
								+ "targetElement.scrollIntoView({ behavior: 'smooth' });\n"
								+ "return targetElement.innerHTML;" }));
				for (String script : scripts) {
					System.err.println("Running the script:\n" + script);
					try {
						String result = (String) js.executeScript(script, element);
						System.err.println("Found:\n" + result);
						// assertThat(result, equalTo("text to find"));
					} catch (Exception e) {
						// temporarily catch all exceptions.
						System.err.println("Exception: " + e.toString());
					}
				}
			} else {
				String script = "var element = arguments[0];\n"
						+ "var ancestorLocator = arguments[1];"
						+ "var targetElementLocator = arguments[2];"
						+ "/* alert('ancestorLocator = ' + ancestorLocator); */"
						+ "var targetElement = element.closest(ancestorLocator).querySelector(targetElementLocator);\n"
						+ "targetElement.scrollIntoView({ behavior: 'smooth' });\n"
						+ "return targetElement.text || targetElement.getAttribute('value');";
				try {
					System.err.println("Running the script:\n" + script);
					String result = (String) js.executeScript(script, element, "form",
							"input[type='submit']");
					System.err.println("Found:\n" + result);
					assertTrue(result.equalsIgnoreCase("add to cart"), result);
				} catch (Exception e) {
					// temporarily catch all exceptions.
					System.err.println("Exception: " + e.toString());
				}
			}
		});
	}

	// refactored "scrollIntoView" in the method demoqaTest1
	@Test(enabled = true)
	public void demoqaTest2() {
		String baseURL = "http://store.demoqa.com/products-page/product-category/?view_type=default";
		driver.get(baseURL);

		// Arrange
		List<WebElement> elements = new ArrayList<>();
		elements = driver
				.findElements(By.cssSelector("span.currentprice:nth-of-type(1)"));
		elements.stream().forEach(element -> {
			super.setDebug(true);
			highlight(element, 500, "solid green");
			super.scrollIntoView(element);
			highlight(element, 500, "solid blue");
			boolean debug = false;
			if (debug) {
				List<String> scripts = new ArrayList<>(Arrays.asList(new String[] {
						// immediate ancestor, not the one test is looking for, but
						// helped finding the following one
						"var element = arguments[0];\n"
								+ "var locator = 'div.wpsc_product_price';"
								+ "var targetElement = element.closest(locator);\n"
								+ "return targetElement.outerHTML;",
						// next in the ancestor chain, located and printed the outerHTML of
						// element for debugging purposes
						"var element = arguments[0];\n" + "var locator = 'form';\n"
								+ "var targetElement = element.closest(locator);\n"
								+ "return targetElement.outerHTML;",
						// relevant ancestor chain, chained with a quesySelector call
						// but with full classes making it hard to read and fragile
						"var element = arguments[0];\n"
								+ "var ancestorLocator = 'div.productcol';"
								+ "var targetElementLocator = 'div[class=\"input-button-buy\"]';"
								+ "var targetElement = element.closest(ancestorLocator).querySelector(targetElementLocator);\n"
								+ "return targetElement.innerHTML;" }));
				for (String script : scripts) {
					System.err.println("Running the script:\n" + script);
					try {
						String result = (String) js.executeScript(script, element);
						System.err.println("Found:\n" + result);
						// assertThat(result, equalTo("text to find"));
					} catch (Exception e) {
						// temporarily catch all exceptions.
						System.err.println("Exception (ignored) : " + e.toString());
					}
				}
			} else {
				String script = "var element = arguments[0];\n"
						+ "var ancestorLocator = arguments[1];"
						+ "var targetElementLocator = arguments[2];"
						+ "/* alert('ancestorLocator = ' + ancestorLocator); */"
						+ "var targetElement = element.closest(ancestorLocator).querySelector(targetElementLocator);\n"
						+ "return targetElement.text || targetElement.getAttribute('value');";
				try {
					System.err.println("Running the script:\n" + script);
					String result = (String) js.executeScript(script, element, "form",
							"input[type='submit']");
					System.err.println("Found:\n" + result);
					assertTrue(result.equalsIgnoreCase("add to cart"), result);
				} catch (Exception e) {
					// temporarily catch all exceptions.
					System.err.println("Exception (ignored) : " + e.toString());
				}
			}
		});
	}
}
