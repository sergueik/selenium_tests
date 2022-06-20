package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.hasItems;

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
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
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

public class Selenium4GridUITest extends BaseTest {

	private static final Logger log = LogManager
			.getLogger(Selenium4GridUITest.class);
	private static List<WebElement> elements;
	private static final StringBuffer report = new StringBuffer();
	private static List<WebElement> elements2;
	private static List<WebElement> elements3;
	private WebElement element;

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
	}

	@BeforeMethod
	public void loadPage() {
		driver.navigate().to("http://192.168.0.125:4444/ui/index.html#/");
	}

	@Test(enabled = true)
	public void test1() {
		elements = driver.findElements(By.cssSelector(
				"div#root div.MuiGrid-item h6.MuiTypography-root.MuiTypography-h6 div.MuiBox-root"));
		assertThat(elements, notNullValue());
		elements.stream().filter(o -> o.getText().contains("URI:"))
				.forEach(o -> log.info(o.getAttribute("innerHTML")));
		// assertThat(elements.size(), equalTo(4));
	}

	@Test(enabled = true)
	public void test2() {
		elements = driver.findElements(By.cssSelector(
				"div.MuiContainer-root div.MuiCardContent-root > div.MuiGrid-container> div.MuiGrid-item > h6"));
		assertThat(elements, notNullValue());
		elements.stream().filter(o -> {
			List<WebElement> elements2 = o
					.findElements(By.cssSelector("div.MuiBox-root"));
			if (elements2 != null && elements2.size() > 0) {
				String text = elements2.get(0).getText();
				if (text.contains("URI:"))
					return true;
				else
					return false;
			} else
				return false;
		}).forEach(o -> log.info(o.getAttribute("innerHTML")));
		// assertThat(elements.size(), equalTo(4));
	}

	@Test(enabled = true)
	public void test3() {
		element = driver.findElement(By.id("root"));
		elements = element.findElements(By.tagName("h6"));
		assertThat(elements, notNullValue());
		elements2 = elements.stream().filter(o -> {
			elements3 = o.findElements(By.cssSelector("div.MuiBox-root"));
			if (elements3 != null && elements3.size() > 0) {
				String text = elements3.get(0).getText();
				if (text.contains("URI:"))
					return true;
				else
					return false;
			} else
				return false;
		}).collect(Collectors.toList());

		elements2.stream().forEach(o -> log.info(o.getAttribute("innerHTML")));
		assertThat(elements2.size(), equalTo(4));
	}

	@Test(enabled = true)
	public void test4() {
		element = driver.findElement(By.id("root"));
		elements = element.findElements(By.tagName("h6"));
		assertThat(elements, notNullValue());
		List<String> results = new ArrayList<>();
		for (WebElement element1 : elements) {
			for (WebElement element2 : element1.findElements(By.tagName("div"))) {
				if (null != element2.getAttribute("className")
						&& element2.getAttribute("className").contains("MuiBox-root")
						&& element2.getText().contains("URI:"))
					results.add(element1.getText());
			}
		}
		assertThat(results, notNullValue());
		assertThat(results.size(), equalTo(4));
		log.info("URI: " + results);
	}

}
