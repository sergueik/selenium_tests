package com.github.sergueik.selenium;

/**
 * Copyright 2021 Serguei Kouzmine
 */
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

/**
 * Selected test scenarios for Selenium WebDriver
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class SelectDivScrollTest extends BaseTest {

	private final static String baseUrl = "https://www.speechtexter.com";
	private final static String selector1 = "div#top-navbar div#lang-flag-display";
	private final static String selector2 = "div#overlay-lang div#popup-lang";
	private final static String selector3 = "div#lang-menu-content div.lang-option";
	private final int maxCount = Integer.parseInt(getEnv("MAXCOUNT", "20"));
	public JavascriptExecutor js;
	private WebElement element;
	private WebElement element1;
	private WebElement element2;
	private List<WebElement> elements = new ArrayList<>();
	private final int delay1 = 300;
	private final int delay2 = 3000;
	private int scrollTop = 200;

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
	}

	@BeforeMethod
	public void loadPage() {

		driver.navigate().to(baseUrl);
		// Arrange
		element = driver.findElement(By.cssSelector(selector1));
		assertThat(element, notNullValue());
		actions.moveToElement(element);
		highlight(element);
		element.click();
		wait.until(ExpectedConditions
				.visibilityOf(driver.findElement(By.cssSelector(selector2))));
		elements = driver.findElements(By.cssSelector(selector3));
		assertThat(elements, notNullValue());
		assertThat(elements.size(), greaterThan(0));
		System.err.println(String.format("iterating over first %d of %d elements",
				maxCount, elements.size()));
	}

	@Test(enabled = true)
	public void test1() {
		// Act
		elements.stream().limit(maxCount).forEach(element1 -> {

			assertThat(element1, notNullValue());
			comment(element1);
			element2 = element1.findElement(By.cssSelector(".flag"));
			scrollIntoView(element2);
			// TODO: assert some element position
			// nth-of(%d)
			highlight(element2);
			executeScript(
					"var element = arguments[0];  var scrollTop = arguments[1];element.scroll({top: scrollTop, left: 0, behavior: 'smooth' })",
					element2, scrollTop);
			showPosition(element2);
			sleep(delay1);
		});
		sleep(delay2);
	}

	@Test(enabled = true)
	public void test2() {
		// Act
		elements.stream().limit(maxCount).forEach(element1 -> {

			assertThat(element1, notNullValue());
			comment(element1);
			element2 = element1.findElement(By.cssSelector(".flag"));
			try {
				actions.moveToElement(element2).build().perform(); // does not scroll ?
				highlight(element2);
				showPosition(element2);
				sleep(delay1);
			} catch (StaleElementReferenceException e) {
				// stale element reference: element is not attached to the page document
				System.err.println("Exception (ignored): " + e.toString());
			}
		});
		sleep(delay2);
	}

	// NOTE: not a legacy select
	// https://www.w3schools.com/tags/tryit.asp?filename=tryhtml_select
	// therefore the org.openqa.selenium.support.ui.Select is of no use
	@Test(enabled = true)
	public void test3() {
		// Act
		for (int cnt = 0; cnt != ((elements.size() > maxCount) ? maxCount
				: elements.size()); cnt++) {
			String selector = String.format("%s:nth-of-type(%d)", selector3, cnt + 1);
			element1 = driver.findElement(By.cssSelector(selector));
			assertThat(element1, notNullValue());
			comment(element1);
			element2 = element1.findElement(By.cssSelector(".flag"));
			try {
				actions.moveToElement(element2).build().perform(); // does not scroll ?
				highlight(element2);
				showPosition(element2);
				sleep(delay1);
			} catch (StaleElementReferenceException e) {
				// stale element reference: element is not attached to the page document
				System.err.println("Exception (ignored): " + e.toString());
			}
		}
	}

	private void comment(WebElement element) {

		System.err.println(String.format("text: %s[ %s ]",
				element.findElement(By.cssSelector(".option-line-1")).getText(),
				element.getAttribute("data-language")));
	}

	private void showPosition(WebElement element) {
		System.err.println(String.format(" Element position: x=%d,y=%d",
				element.getRect().getPoint().getX(),
				element.getRect().getPoint().getY()));

	}

}
