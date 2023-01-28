package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

// based on: https://www.seleniumeasy.com/selenium-tutorials/accessing-shadow-dom-elements-with-webdriver
public class ShadowDOMTest extends BaseTest {

	private static final Logger log = LogManager.getLogger(ShadowDOMTest.class);
	private String baseURL = "chrome://downloads/";
	private String responseMessage = null;
	private JSONObject result = null;
	private static WebElement element = null;
	private static WebDriverWait wait;
	private static String expression = "document.querySelector('body > downloads-manager').shadowRoot.querySelector('#toolbar').shadowRoot.querySelector('#toolbar').shadowRoot.querySelector('#leftSpacer > h1').textContent";
	// origin:http://www.louisianaoutdoorproperties.com
	private static Document jsoupDocument;
	private static WebElement element1, element2, element3;
	private static WebElement root1, root2, root3;

	@BeforeClass
	public void beforeClass() throws IOException {
		super.setBrowser("chrome");
		super.beforeClass();
		assertThat(driver, notNullValue());
	}

	@BeforeMethod
	public void loadPage() {
		System.err.println("Open Chrome downloads");
		driver.get(baseURL);
	}

	// based on video:
	// https://youtu.be/O76h9Hf9-Os?list=PLMd2VtYMV0OSv62KjzJ4TFGLDTVtTtQVr&t=527
	// Karate UI Api Testing Framework is likely to be calling CDP under the hood
	@Test
	public void jsopathTest() {
		System.err.println("Execute: " + expression);
		String value = (String) executeScript("return " + expression);
		assertThat(value, is("Downloads"));
		System.err.println("Result value: " + value);
	}

	@Test(expectedExceptions = ClassCastException.class)
	public void shadowDOMElementsTest() {

		System.err.println("Validate downloads page header text");
		element1 = driver.findElement(By.tagName("downloads-manager"));

		System.err.println("element html: " + element1.getAttribute("outerHTML"));

		// Get shadow root element
		// NOTE: nolonger works:
		// java.lang.ClassCastException:
		// com.google.common.collect.Maps$TransformedEntriesMap cannot be cast to
		// org.openqa.selenium.WebElement
		WebElement root1 = expandRootElement(element1);
		assertThat(root1, notNullValue());
		try {
			assertThat(root1.getText(), notNullValue());
			System.err.println("Root1 text: " + root1.getText());
		} catch (JavascriptException e) {
			// Exception (ignored) org.openqa.selenium.JavascriptException: javascript
			// error: Failed to execute 'getComputedStyle' on 'Window': parameter 1 is
			// not of type 'Element'.
			System.err.println("Exception (1) (ignored) " + e.getMessage());
		}
		System.err.println(root1.toString());
		// [org.openqa.selenium.remote.RemoteWebElement@7de7299a -> unknown locator]

		try {
			jsoupDocument = Jsoup.parse(root1.getAttribute("outerHTML"));
			System.err.println(jsoupDocument.ownText());
		} catch (JavascriptException e) {
			// org.openqa.selenium.JavascriptException:
			// javascript error: a.getAttributeNode is not a function
			System.err.println("Exception (2) (ignored) " + e.getMessage());
		}
		// Element members : null
		System.err.println("Element members : " + executeScript(
				"let x = arguments[0]; let result = []; for (y in x) {result.push(y);} JSON.stringify(result)",
				root1));

		element2 = root1.findElement(By.cssSelector("downloads-toolbar"));
		root2 = expandRootElement(element2);
		assertThat(root2, notNullValue());
		try {
			jsoupDocument = Jsoup.parse(root2.getAttribute("outerHTML"));
			System.err.println(jsoupDocument.ownText());
		} catch (JavascriptException e) {
			// org.openqa.selenium.JavascriptException:
			// javascript error: a.getAttributeNode is not a function
			System.err.println("Exception (3) (ignored) " + e.getMessage());
		}

		element3 = root2.findElement(By.cssSelector("cr-toolbar"));
		root3 = expandRootElement(element3);
		assertThat(root3, notNullValue());
		try {
			jsoupDocument = Jsoup.parse(root3.getAttribute("outerHTML"));
			System.err.println(jsoupDocument.html());
		} catch (JavascriptException e) {
			// javascript error: a.getAttributeNode is not a function
			System.err.println("Exception (4) (ignored) " + e.getMessage());
		}

		try {
			String actualHeading = root3
					.findElement(By.cssSelector("div[id=leftContent]")).getText();
			/* "div[id=leftContent]>h1"*/
			// Verify header title
			assertEquals("Downloads", actualHeading);
		} catch (NoSuchElementException e) {
			// NoSuchElementException NoSuchElement no such element
			System.err.println("Exception (5) (ignored) " + e.getMessage());
		}
		System.err.println("stringify: " + executeScript(
				"return JSON.stringify(arguments[0].shadowRoot)", element3));
	}

	// Cast shadow Root to WebElement
	public WebElement expandRootElement(WebElement element) {
		Object result = executeScript("return arguments[0].shadowRoot", element);
		log.info("result: ", result);
		return (WebElement) result;
	}

}
