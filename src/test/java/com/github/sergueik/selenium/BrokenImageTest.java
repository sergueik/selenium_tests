package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.hasItems;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com) based on
 * https://groups.google.com/forum/#!topic/selenium-users/OdXiZ4D4m6o
 * broken image discovery discussion
 */

public class BrokenImageTest extends BaseTest {

	private static final Logger log = LogManager.getLogger(BrokenImageTest.class);
	private static List<String> srcList = new ArrayList<>();
	static {
		srcList.add("missing1.jpg");
		srcList.add("missing2.jpg");
	}
	private static final StringBuffer report = new StringBuffer();

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
	}

	@BeforeMethod
	public void loadPage() {
		driver.navigate().to(getPageContent("broken_images.html"));
	}

	@SuppressWarnings("deprecation")
	@Test(enabled = true)
	// inspired by:
	public void plainBrokenImageTest() {
		// Arrange
		final Set<Object> results = new HashSet<>();
		List<WebElement> elements = driver.findElements(By.cssSelector("img"));
		// Act
		for (WebElement element : elements) {
			if (element.getAttribute("naturalWidth").equals("0")) {
				String src = element.getAttribute("src").replaceFirst("^.*/", "");
				// NOTE: src would have a full path like:
				// "file:///C:/workspace/selenium_tests/target/test-classes/missing2.jpg"
				results.add(src);
				report.append(src);
				System.err.println(element.getAttribute("outerHTML") + " is broken.");
			}
		}
		// Assert
		srcList.forEach(o -> assertThat(results, hasItems(o)));
		// Assert
		assertThat(results, containsInAnyOrder(
				Arrays.asList(equalTo("missing2.jpg"), equalTo("missing1.jpg"))));
		assertThat("Checking ", results.toArray(),
				arrayContainingInAnyOrder(srcList.toArray()));

		// Assert
		final Set<String> src2 = new HashSet<>();
		src2.add("background.png");
		src2.add("other.jpg");
		Pattern pattern = Pattern
				.compile("^(?!" + StringUtils.join(src2, "|") + ").*$");
		System.err.println("Pattern: " + pattern.toString());
		// Set<String> results2 = new HashSet<>();
		// results.forEach(o -> results2.add(o.toString()));
		String input = StringUtils.join(results.toArray(), "|");
		System.err.println("Input: " + input);
		Matcher matcher = pattern.matcher(input);
		assertTrue(matcher.find());

	}
}
