package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Selected test scenarios for Selenium WebDriver
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

// see also:
// https://automated-testing.info/t/poisk-teksta-v-elemente-bez-uchyota-dochernih-elementov/24285/7
// https://automated-testing.info/t/kak-poluchit-tekst-tega-bez-vlozhennyh-tegov-v-selenide/23607/11

public class FirstChildTest extends BaseTest {

	private static final Logger log = LogManager.getLogger(FirstChildTest.class);

	private static String baseURL = "https://ya.ru/";
	private final String expectedText = "Найти";
	private String text = "A2300";

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
	}

	@BeforeMethod
	public void loadPage() {
		driver.navigate().to(getPageContent("nested_div.html"));
	}

	private final String selector = "div";
	private int cnt = 0;

	@Test(enabled = true, expectedExceptions = {
			java.lang.ClassCastException.class })
	// java.lang.ClassCastException:
	// com.google.common.collect.Maps$TransformedEntriesMap
	// cannot be cast to
	// org.openqa.selenium.WebElement
	// root cause: multiple div elements ?
	// NOTE:
	// the firstChild property returns the first child node
	// (an element node, a text node or a comment node)
	// the firstElementChild property returns the first child element
	// (not text and comment nodes).
	public void test1() {
		String cssSelector = String.format("%s:nth-of-type(%d)", selector, cnt + 1);
		String script = String.format(
				"var element = document.querySelector('%s').firstChild; return element;",
				cssSelector);
		WebElement element = (WebElement) executeScript(script);
		String text = element.getText().trim();
		String textAscii = BaseTest.Translit.toAscii(text);
		System.err.println(String.format("Text = %s | %s", text, textAscii));
	}

	// https://stackoverflow.com/questions/332422/get-the-name-of-an-objects-type
	@Test(enabled = true)
	public void test2() {
		String cssSelector = String.format("%s:nth-of-type(%d)", selector, cnt + 1);
		String script = String.format(
				"var element = document.querySelector('%s').firstChild; return Object.prototype.toString.call(element);",
				cssSelector);
		Object object = executeScript(script);
		// TODO: JSON ?

		System.err.println(object);
		script = String.format(
				"var element = document.querySelector('%s').childNodes[0]; return Object.prototype.toString.call(element);",
				cssSelector);
		object = executeScript(script);
		System.err.println(object);
		script = String.format(
				"var element = document.querySelector('%s').firstElementChild; return Object.prototype.toString.call(element);",
				cssSelector);
		object = executeScript(script);
		System.err.println(object);

		script = String.format(
				"var element = document.querySelector('%s').firstElementChild; return element;",
				cssSelector);
		WebElement element = (WebElement) executeScript(script);
		String text = element.getText().trim();
		String textAscii = BaseTest.Translit.toAscii(text);
		System.err.println(String.format("Text = %s | %s", text, textAscii));
	}

	@Test(enabled = true)
	public void test3() {
		for (int i = 1; i <= 2; i++) {
			WebElement element = driver
					.findElement(By.xpath(String.format("//div[1]", i)));

			String script = getScriptContent("getTextOnlyNonDestructive.js");
			String text = (String) executeScript(script, element);
			String textAscii = BaseTest.Translit.toAscii(text);
			System.err.println(String.format("Text = %s | %s", text, textAscii));

		}
	}

	@Test(enabled = true)
	public void test4() {
		String cssSelector = String.format("%s:nth-of-type(%d)", selector, cnt + 1);
		String script = String.format(
				"return document.querySelector('%s').firstChild.data", cssSelector);
		String text = (String) executeScript(script);
		String textAscii = BaseTest.Translit.toAscii(text);
		System.err.println(String.format("Text = %s | %s", text, textAscii));
	}

	@Test(enabled = true)
	public void test5() {
		for (int i = 1; i <= 2; i++) {
			WebElement element = driver
					.findElement(By.xpath(String.format("//div[%d]", i)));

			String text = element.getText();
			String textAscii = BaseTest.Translit.toAscii(text);
			System.err.println(String.format("Text = %s | %s", text, textAscii));

		}
	}

	@Test(enabled = true)
	public void test6() {
		for (int i = 1; i <= 2; i++) {
			WebElement element = driver
					.findElement(By.xpath(String.format("//div[%d]", i)));
			System.err.println(
					String.format("Element = %s", element.getAttribute("outerHTML")));

			String script = "var element = arguments[0]; return element.data";
			String text = (String) executeScript(script, element);

			String textAscii = (text != null) ? Translit.toAscii(text) : "";
			System.err.println(String.format("Text = %s | %s", text, textAscii));

		}
	}

}
