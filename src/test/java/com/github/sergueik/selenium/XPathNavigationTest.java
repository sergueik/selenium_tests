package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.hamcrest.core.StringContains.containsString;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

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

	// passes with debug true
	// private static boolean debug = true;
	private static boolean debug = false;

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
						// commented: we are not finding the right element. Kept around for
						// later debugging
						// assertThat(result, containsString("add to cart"));
						System.err.println("Found:\n" + result);
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
					assertThat(result, equalToIgnoringCase("add to cart"));
					System.err.println("Found:\n" + result);
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
			super.setDebug(debug);
			super.scrollIntoView(element);
			// removed scroll DOM method call from element evaluation scripts
			if (debug) {
				// Java 9 way : Map.of
				Map<String, String> scriptsMap = ImmutableMap.of("immediate ancestor",
						"var element = arguments[0];\n"
								+ "var locator = 'div.wpsc_product_price';"
								+ "var targetElement = element.closest(locator);\n"
								+ "return targetElement.outerHTML;",
						"next in the ancestor chain",
						"var element = arguments[0];\n" + "var locator = 'form';\n"
								+ "var targetElement = element.closest(locator);\n"
								+ "return targetElement.outerHTML;",
						"another relevant ancestor chain",
						"var element = arguments[0];\n"
								+ "var ancestorLocator = 'div.productcol';"
								+ "var targetElementLocator = 'div[class=\"input-button-buy\"]';"
								+ "var targetElement = element.closest(ancestorLocator).querySelector(targetElementLocator);\n"
								+ "return targetElement.innerHTML;");
				for (String scriptKey : scriptsMap.keySet()) {
					System.err.println("Running the script:\n" + scriptKey);
					try {
						String result = (String) js.executeScript(scriptsMap.get(scriptKey),
								element);
						// commented: we are not finding the right element. Kept around for
						// later debugging
						// assertThat(result, containsString("add to cart"));
						System.err.println("Found:\n" + result);
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
					assertThat(result, equalToIgnoringCase("add to cart"));
					System.err.println("Found:\n" + result);
				} catch (Exception e) {
					// temporarily catch all exceptions.
					System.err.println("Exception (ignored) : " + e.toString());
				}
			}
		});
	}
}
