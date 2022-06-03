package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Selected test scenarios for Selenium WebDriver
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 * based on discussion https://qna.habr.com/q/1161030
 */

public class ClassLocatorTest extends BaseTest {

	private static final Logger log = LogManager.getLogger(ClassLocatorTest.class);
	private String baseUrl = "https://obnovi-oboi.ru/goods?category=oboi";
	private final int cnt = 5; 
	// limit the number of elements to scrape - not focused on complete data collection

	WebElement element;
	WebElement element2;
	List<WebElement> elements = new ArrayList<>();

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
	}

	@BeforeMethod
	public void beforeMethod(Method method) {
		super.beforeMethod(method);
		// Arrange
		driver.get(baseUrl);
	}

	@AfterMethod
	@Override
	public void afterMethod() {
		driver.get("about:blank");
	}
	@Test(enabled = true, expectedExceptions = {TimeoutException.class, NoSuchElementException.class,  NullPointerException.class } )
	public void test1() {
		// Assert
		element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("row items")));
		assertThat(element, notNullValue());
		highlight(element);
		elements = element.findElements(By.className("card good-item"));
		assertTrue(elements.size() > 0);
		elements.stream().limit(cnt).forEach(o -> {
			try {
				highlight(o);
				log.info("found card element:" + o.getAttribute("class"));
			} catch (StaleElementReferenceException e) {
				// simply ignore
			}
		});
	}


	@Test(enabled = true )
	public void test2() {
		// Assert
		element = wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.cssSelector("div.row.items")));
		assertThat(element, notNullValue());
		highlight(element, 1000);
		log.info("Found container element: " + element.getAttribute("class"));
		element2 = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector("div.row div.card div.card-body")));
		log.info("Found inner element: " + element2.getAttribute("class"));
		assertThat(element2, notNullValue());
		highlight(element2, 1000);
		elements = element
				.findElements(By.cssSelector("div.row div.card div.card-body"));
		assertTrue(elements.size() > 0);
		elements.stream().limit(cnt).forEach(o -> {
			try {
				highlight(o);
				// this.getClass().getSuperclass() can and should be dropped
				log.info("found card element:" + o.getAttribute("class"));
				log.info("href: "
						+ o.findElement(By.className("card-title")).getAttribute("href"));
			} catch (StaleElementReferenceException e) {
				// simply ignore
			}
		});
	}

	@Test(enabled = true)
	public void test3() {
		// Assert
		element = wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.cssSelector("div.row.items")));
		assertThat(element, notNullValue());
		highlight(element, 1000);
		log.info("Found container element: " + element.getAttribute("class"));
		element = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector("div.row div.card div.card-body")));
		log.info("Found card element: " + element.getAttribute("class"));
		assertThat(element, notNullValue());
		highlight(element, 1000);
		elements = driver
				.findElements(By.cssSelector("div.row div.card div.card-body"));
		assertTrue(elements.size() > 0);
		elements.stream().limit(cnt).forEach(o -> {
			try {
				highlight(o);
				log.info("found card element:" + o.getAttribute("class"));
				log.info("href: "
						+ o.findElement(By.className("card-title")).getAttribute("href"));
			} catch (StaleElementReferenceException e) {
				// simply ignore
			}
		});
	}

}


