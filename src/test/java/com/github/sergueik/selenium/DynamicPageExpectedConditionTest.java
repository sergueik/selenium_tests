package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
/**
 * Copyright 2021 Serguei Kouzmine
 */
import static org.hamcrest.Matchers.greaterThan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Selected test scenarios for Selenium WebDriver inspired on
 * https://qna.habr.com/q/953967#answer_1898463 page dynamically loads the
 * elements of interest
 * 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class DynamicPageExpectedConditionTest extends BaseTest {

	private static final String baseURL = "https://www.milanuncios.com/moda-mujer/?vendedor=part&orden=relevance&fromSearch=";

	private List<WebElement> elements = new ArrayList<>();
	private final static int min_count = 10;
	private static final boolean remote = Boolean.parseBoolean(getPropertyEnv("REMOTE", "false"));
	private boolean debug = false;
	private static final By by = By.className("ma-AdCard-titleLink");

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		super.setDebug(true);
		debug = super.getDebug();
		assertThat(driver, notNullValue());
	}

	@BeforeMethod
	public void loadPage() {
	}

	@Test(enabled = true)
	public void test1() {
		driver.navigate().to(baseURL);
		// wait for cookie dialog to be present,
		// effectively just wait for page to load
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#sui-TcfFirstLayerModal button")));
		// wait for close button
		wait.until(
				ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector("#sui-TcfFirstLayerModal button"))));
		elements = driver.findElements(By.cssSelector("#sui-TcfFirstLayerModal button"));
		System.err.println("Close cookie dialog: " + elements.get(1).getText() + " button");
		elements.get(1).click();
	}

	@Test(enabled = true)
	public void test2() {
		driver.navigate().to(baseURL);
		try {
			// NOTE: generic not returned the expected data - fix needed. For now just read
			// again
			elements = wait.until(new ExpectedCondition<List<WebElement>>() {

				@Override
				public List<WebElement> apply(WebDriver d) {
					List<WebElement> elements = d.findElements(by);
					if (debug)
						System.err.println("Awaiting for " + min_count + " , got " + elements.size() + " elements");
					int cnt = elements.size() > min_count ? min_count : elements.size() - 1;
					WebElement element = elements.get(cnt);
					if (debug)
						System.err.println(String.format("Moving to %d element: %s", cnt, element.getText()));
					highlight(element);
					actions.moveToElement(element).build().perform();
					// scroll page down a bit more -
					// eventually leads to more elements to be
					// displayed
					element.sendKeys(Keys.DOWN);
					return (elements.size() >= min_count) ? elements : null;
				}
			});
		} catch (TimeoutException e) {
		}
		System.err.println("Got from wait: " + elements.size() + " elements");
		assertThat(elements.size(), greaterThan(min_count - 1));
	}

	@Test
	public void test3() {
		try {
			// NOTE: scroll by 110% the distance between first and last visible elements
			// Without the extra 10%, does not work
			elements = wait.until(new ExpectedCondition<List<WebElement>>() {

				@Override
				public List<WebElement> apply(WebDriver d) {
					List<WebElement> elements = d.findElements(by);
					if (debug)
						System.err.println("Awaiting for " + min_count + " , got " + elements.size() + " elements");
					int cnt = elements.size() > min_count ? min_count : elements.size() - 1;
					WebElement element = elements.get(cnt);
					int y = (int) Math
							.ceil((elements.get(elements.size() - 1).getLocation().y - elements.get(0).getLocation().y)
									* 1.1);
					if (debug)
						System.err.println(String.format("Scrolling by %d", y));
					highlight(element);
					scroll(0, y);
					return (elements.size() >= min_count) ? elements : null;
				}
			});
		} catch (TimeoutException e) {
			// none of the select elements will have "selected" attribute set.
		}
		System.err.println("Got from wait: " + elements.size() + " elements");
		assertThat(elements.size(), greaterThan(min_count - 1));
	}

}
