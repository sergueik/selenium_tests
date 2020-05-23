package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

// 
public class CookiesIAgreeTest extends BaseTest {

	private static String baseURL = "https://link.testproject.io/vr9";
	private static WebElement element = null;
	private static List<WebElement> elements = new ArrayList<>();
	private final static boolean debug = true;

	@SuppressWarnings("deprecation")
	@BeforeMethod
	public void BeforeMethod() {
		driver.get(baseURL);
		// legacy lambda cutom wait condition.
		// NOTE: wrone match appears to lock the browser
		Wait<WebDriver> wait = new FluentWait<>(driver)
				.withTimeout(flexibleWait, TimeUnit.SECONDS)
				.pollingEvery(pollingInterval, TimeUnit.SECONDS)
				.ignoring(NoSuchElementException.class);
		wait.until(driver -> {
			assert driver != null;
			System.err.println("current url: " + driver.getCurrentUrl());
			return driver.getCurrentUrl().matches("https://testproject.io/.*$");
		});

	}

	// https://stackoverflow.com/questions/3813294/how-to-get-element-by-innertext
	@Test(enabled = true)
	public void selectCookieMessageJavascriptCallTest() {
	// @formatter:off
		String script = "var aTags = document.getElementsByTagName(\"a\");\n"
				+ "var searchText = arguments[0];\n"
				+ "var found;\n"
				+ "for (var i = 0; i < aTags.length; i++) {\n"
				+ "  if (aTags[i].textContent.trim() == searchText) {\n"
				+ "    found = aTags[i];\n" 
				+ "    break;\n" 
				+ "  }\n"
				+ "}\n"
				+ "return found;";
	// @formatter:on
		element = (WebElement) executeScript(script, "Learn more");

		assertThat(element, notNullValue());
		if (debug) {
			System.err.println("Element: " + element.getAttribute("outerHTML"));
		}

		element = (WebElement) executeScript(script, "I AGREE");

		assertThat(element, notNullValue());
		if (debug) {
			System.err.println("Element: " + element.getAttribute("outerHTML"));
		}
		// @formatter:off
		script = "var aTags = document.getElementsByTagName(\"a\");\n"
				+ "var searchId = arguments[0];\n"
				+ "var found;\n"
				+ "for (var i = 0; i < aTags.length; i++) {\n"
				+ "  if (aTags[i].getAttribute(\"id\") == searchId) {\n"
				+ "    found = aTags[i];\n" 
				+ "    break;\n" 
				+ "  }\n"
				+ "}\n"
				// does not work
				+ "return found.textContent.replace(\" \", \"\")"
				+ ".replace(String.fromCharCode(32), \"\")"
				+ ".replace(String.fromCharCode(10), \"\");";
	// @formatter:on

		String elementText = (String) executeScript(script, "cc-button");

		assertThat(element, notNullValue());
		if (debug) {
			System.err
					.println(String.format("Element textContent: \"%s\"", elementText));
		}
		// @formatter:off
		script = "var aTags = document.getElementsByTagName(\"a\");\n"
				+ "var searchId = arguments[0];\n"
				+ "var found;\n"
				+ "for (var i = 0; i < aTags.length; i++) {\n"
				+ "  if (aTags[i].getAttribute(\"id\") == searchId) {\n"
				+ "    found = aTags[i];\n" 
				+ "    break;\n" 
				+ "  }\n"
				+ "}\n"
				
				+ "return found.textContent.trim();";
	// @formatter:on

		elementText = (String) executeScript(script, "cc-button");

		assertThat(element, notNullValue());
		if (debug) {
			System.err.println(
					String.format("Element textContent (trimmed): \"%s\"", elementText));
		}

		// @formatter:off
		script = "var aTags = document.getElementsByTagName(\"a\");\n"
				+ "var searchId = arguments[0];\n"
				+ "var found;\n"
				+ "for (var i = 0; i < aTags.length; i++) {\n"
				+ "  if (aTags[i].getAttribute(\"id\") == searchId) {\n"
				+ "    found = aTags[i];\n" 
				+ "    break;\n" 
				+ "  }\n"
				+ "}\n"
				+  "var textContent = found.textContent; var result = [];" 
				+ "for (cnt = 0;  cnt!= textContent.length ; cnt ++){"
				+ "result.push(textContent.codePointAt(cnt)); \n"
				+ "}\n"
				+ "return result;";
	// @formatter:on

		List<Integer> result = (List<Integer>) executeScript(script, "cc-button");

		if (debug) {
			System.err.println("Element textContent (codepoints):" + result);
		}
	}

	@Test(enabled = false)
	public void selectCookieMessageXPathTest() {
		element = driver.findElement(By.id("cookie-law-info-bar"));
		assertThat(element, notNullValue());
		highlight(element, 100);
		if (debug) {
			System.err.println(element.getAttribute("innerHTML"));
		}

		// "I AGREE" button
		elements = driver.findElements(By.xpath("//a[contains(@class, 'button')]"));
		assertThat(elements.size(), greaterThan(0));
		// show every button. NOTE: time-consuming
		elements.stream().forEach(o -> {
			// NOTE: ignoring TimeoutException from highlight(o) and continue is quite
			// time consuming
			/*  often text element text appears empty */
			System.err.println(
					"text: " + o.getText() + "html: " + o.getAttribute("outerHTML"));
			try {
				o.sendKeys(Keys.CONTROL + "t"); // no effect ?
			} catch (ElementNotInteractableException e) {
				System.err.println("Exception (ignored): " + e.getMessage());
			}
		});
		element = driver.findElement(By.id("cc-button"));
		assertThat(element, notNullValue());
		highlight(element, 1000);// no exception but no visual effect
		if (debug) {
			System.err.println("text: " + element.getText() + "html: "
					+ element.getAttribute("outerHTML"));
		}
		element = driver.findElements(By.xpath("//a[contains(@class, 'button')]"))
				.stream()
				.filter(o -> o.getText().trim().compareToIgnoreCase("I AGREE") > -1)
				.collect(Collectors.toList()).get(0);
		assertThat(element, notNullValue());
		element.sendKeys(Keys.CONTROL + "t"); // no effect

		try {
			WebElement parentElement = driver
					.findElement(By.xpath("//a[@id = 'cc-button']"))
					.findElement(By.xpath(".."));
			element = parentElement.findElement(
					By.xpath("//*[contains(normalize-space(text()),'I AGREE')]"));
			assertThat(element, notNullValue());
		} catch (NoSuchElementException e) {

		}
		// https://stackoverflow.com/questions/11776910/xpath-expression-to-remove-whitespace
		// NOTE: the following xpath locator failing to find anything despite labor
		// intensive tweaks
		for (String xpath : Arrays.asList(new String[] {

				"//a[contains(@class, 'button')][contains(translate(normalize-space(text()), ' &#9;&#10;&#13', ''),'IAGREE')]",
				"//a[contains(@class, 'button')][contains(translate(normalize-space(text()), ' ', ''),'IAGREE')]",
				"//a[contains(@class, 'button')][contains(normalize-space(text()),'I AGREE')]",
				"//a[contains(@class, 'button')][contains(text(),'I AGREE')]",
				"//a[contains(@class, 'button')][contains('.','I AGREE')]",
				"//a[contains(@class, 'button')][contains('.','EE')]",
				"//a[contains(@class, 'button')][contains('.','ee')]",
				"//a[contains(@class, 'button')][text() ='I AGREE']",
				"//*[@id = 'cc-button'][contains(normalize-space(text()),'')]",
				"//*[contains(@class ,'button')][contains(normalize-space(translate(text()), ' &#9;&#10;&#13', ' '),'Free Sign')]", })) {
			try {
				element = driver.findElement(By.xpath(xpath));
				assertThat(element, notNullValue());
				//
				element.sendKeys(Keys.CONTROL + "t"); // no effect
				highlight(element, 1000);
				// if (debug) {
				System.err.println("text: " + element.getText() + "html: "
						+ element.getAttribute("outerHTML"));
				// }
			} catch (NoSuchElementException | ElementNotInteractableException e) {
				System.err.println("Exception (ignored): " + e.getMessage());
				// check the *** Element info: {Using=xpath, value=...} part
			}
		}
		// "Learn more" link
		element = driver
				.findElement(By.xpath("//a[@href][contains(text(),\"Learn more\")]"));
		assertThat(element, notNullValue());
		// org.openqa.selenium.ElementNotInteractableException:
		// NOTE; TimeoutException when highlight is tried
		// highlight(agreeButtonElement, 1000);
		if (debug) {
			System.err.println(element.getAttribute("outerHTML"));
		}
		element = driver.findElement(By.tagName("body"));
		if (debug) {
			System.err.println(element.getAttribute("tagName"));
		}
		// based on:
		// https://stackoverflow.com/questions/17547473/how-to-open-a-new-tab-using-selenium-webdriver

		element.sendKeys(Keys.CONTROL + "t"); // no effect
		// actions.keyDown(Keys.CONTROL).build().perform();
		// actions.keyUp(Keys.CONTROL).build().perform();
		// actions.keyDown().build().perform();
		sleep(10000);

	}

	@Test(enabled = true)
	public void selectCookieMessageCssSelectorTest() {
		element = driver.findElement(By.id("cookie-law-info-bar"));
		assertThat(element, notNullValue());
		highlight(element, 100);
		if (debug) {
			System.err.println(element.getAttribute("innerHTML"));
		}

		// "I AGREE" button
		elements = driver.findElements(By.cssSelector("a.button"));
		assertThat(elements.size(), greaterThan(0));
		elements.stream().forEach(o -> {
			System.err.println(
					"text: " + o.getText() + "html: " + o.getAttribute("outerHTML"));
			try {
				o.sendKeys(Keys.CONTROL + "t"); // no effect ?
			} catch (ElementNotInteractableException e) {
				System.err.println("Exception (ignored): " + e.getMessage());
			}
		});
		element = driver.findElement(By.id("cc-button"));
		assertThat(element, notNullValue());
		highlight(element, 1000);// no exception but no visual effect
		if (debug) {
			System.err.println("text: " + element.getText() + "html: "
					+ element.getAttribute("outerHTML"));
		}
		element = driver.findElements(By.cssSelector("a.button")).stream()
				.filter(o -> o.getText().trim().compareToIgnoreCase("I AGREE") > -1)
				.collect(Collectors.toList()).get(0);
		assertThat(element, notNullValue());
		element.sendKeys(Keys.CONTROL + "t"); // no effect

		try {
			element = driver
					.findElement(By.cssSelector("a.button[innerText = \"I AGREE\"]"));
			assertThat(element, notNullValue());
			element.sendKeys(Keys.CONTROL + "t"); // no effect
		} catch (NoSuchElementException e) {
			System.err.println("Exception(ignored): " + e.toString());
		}
		try {
			WebElement parentElement = driver
					.findElement(By.xpath("//a[@id = 'cc-button']"))
					.findElement(By.xpath(".."));
			element = parentElement
					.findElement(By.cssSelector("*:contains(\"I AGREE\")"));
			assertThat(element, notNullValue());
		} catch (NoSuchElementException e) {
			System.err.println("Exception(ignored): " + e.toString());

		}
		// "Learn more" link
		try {
			element = driver
					.findElement(By.cssSelector("//a[href][innerText =  \"Learn more\"]"));
			assertThat(element, notNullValue());
		} catch (NoSuchElementException e) {
			System.err.println("Exception(ignored): " + e.toString());

		}

		sleep(1000);

	}
}
