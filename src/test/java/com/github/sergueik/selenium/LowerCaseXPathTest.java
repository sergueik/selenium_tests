package com.github.sergueik.selenium;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
// import org.testng.internal.Nullable;
import javax.annotation.Nullable;

/**
 * Selected test scenarios for Selenium WebDriver
 * 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */
// stackoverflow.com/questions/24183701/xpath-lowercase-is-there-xpath-functn-to-do-this
public class LowerCaseXPathTest extends BaseTest {

	private static final StringBuffer verificationErrors = new StringBuffer();
	private static final Logger log = LogManager.getLogger(LowerCaseXPathTest.class);

	private final static String url_fragment = "https://crossBrowsertesting.com";
	List<WebElement> elements = new ArrayList<>();
	private static WebElement element;

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
	}


	@BeforeMethod
	public void loadPage() {
		driver.navigate().to("https://www.seleniumeasy.com/test");
		element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#site-name")));
		System.err.println("Element: " + element.getAttribute("outerHTML"));
	}

	@Test(enabled = true)
	public void test1() {
		String cssSelector = String.format("a[href ^= '%s']", url_fragment.toLowerCase());
		System.err.println(String.format("Locating with %s", cssSelector));
		elements.clear();
		elements = driver.findElements(By.cssSelector(cssSelector));
		assertTrue(elements.size() > 0);
		highlight(elements.get(0));

	}

	@Test(enabled = true)
	public void test2() {
		for (String xpath : Arrays.asList(new String[] {
				String.format("//*[contains(@href,'%s')]", url_fragment.toLowerCase()),
				String.format("//*[contains(lower-case(@href),'%s')]", url_fragment.toLowerCase()),
				String.format("//a[contains(translate(@href, \"b\", \"B\"), \"%s\")]",
						url_fragment.toLowerCase().replaceAll("b", "B")),
				String.format(
						"//a[contains(translate(@href, \"abcdefghijklmnopqrstuvwxyz\", \"ABCDEFGHIJKLMNOPQRSTUVWXYZ\"), \"%s\")]",
						url_fragment.toUpperCase()) })) {
			System.err.println(String.format("Locating with %s", xpath));
			elements.clear();
			try {
				elements = driver.findElements(By.xpath(xpath));
				assertTrue(elements.size() > 0);
				element = elements.get(0);
				actions.moveToElement(element).build().perform();
				highlight(element);
			} catch (InvalidSelectorException e) {

				System.err.println("Exception: " + e.toString());
				// The string
				// '//*[contains(lower-case(@href),'https://crossbrowsertesting.com')]'
				// is not a valid XPath expression. because it is XPath 2.0
			}
		}
	}

}
