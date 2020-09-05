package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.IsNot.not;

import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Selected test scenarios for Selenium WebDriver
 * 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

// based on: https://qna.habr.com/q/841129

public class FindingSiblingTextNodeTest extends BaseTest {

	private static final Logger log = LogManager.getLogger(FindingSiblingTextNodeTest.class);
	private String text = null;
	private String selector = null;

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
	}

	@BeforeMethod
	public void loadPage() {
		driver.navigate().to(getPageContent("xpath_sibling_text_resource.htm"));
	}

	@Test(enabled = true, expectedExceptions = { org.openqa.selenium.InvalidSelectorException.class })
	public void test1() {
		// Arrange
		selector = "//article[@class='eText']/p/b[. = 'label1:']/following-sibling::text()[1]";
		Object textElement = driver.findElement(By.xpath(selector));
		// Assert
		assertThat(textElement, notNullValue());
		System.err.println(String.format("%s finds %s", selector, textElement));

	}

	@Test(enabled = true)
	public void test2() {
		// Arrange
		selector = "//article[@class='eText']/p/b[. = 'label1:']/..";
		WebElement element = driver.findElement(By.xpath(selector));

		String script = "var element = arguments[0]; var result = []; element.childNodes.forEach( function(childNode) { if (childNode.nodeName == '#text') { result.push( childNode); } }); return(result[0]);";

		Object result = js.executeScript(script, element);

		// Assert
		assertThat(result, notNullValue());
		System.err.println(String.format("test2 finds %s", result));

	}

	@Test(enabled = true)
	public void test3() {
		// Arrange
		selector = "//article[@class='eText']/p/b[. = 'label1:']/..";
		WebElement element = driver.findElement(By.xpath(selector));
		String result = getTextOnly(element, true);

		// Assert
		assertThat(result, notNullValue());
		System.err.println(String.format("test3 finds %s", result));

	}

	@Test(enabled = true)
	public void test4() {
		// Arrange
		selector = "//article[@class='eText']/p/b[. = 'label1:']";
		WebElement element = driver.findElement(By.xpath(selector));
		String script = "var element = arguments[0]; var valid = false; var result = []; element.parentElement.childNodes.forEach(function(childNode) { if (valid) { if (childNode.nodeName == '#text') { result.push(childNode); } else {  valid = false; } } if (childNode == element) { valid = true; } }); return (result[0]);";

		Object result = js.executeScript(script, element);

		// Assert
		assertThat(result, notNullValue());
		System.err.println(String.format("test4 finds %s", result));

	}

	@Test(enabled = true, expectedExceptions = { org.openqa.selenium.InvalidSelectorException.class })
	public void test5() {
		// Arrange
		selector = "//article[@class='eText']/p/b[text() = 'label2:'][generate-id(following-sibling::text()[1]/preceding-sibling::node()[1]) = generate-id(.)]/normalize-space(following-sibling::text()[1])";
		Object textElement = driver.findElement(By.xpath(selector));
		// Assert
		assertThat(textElement, notNullValue());
		System.err.println(String.format("%s finds %s", selector, textElement));

	}
}
