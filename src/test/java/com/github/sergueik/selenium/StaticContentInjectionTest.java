package com.github.sergueik.selenium;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import org.openqa.selenium.support.ui.ExpectedCondition;
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
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class StaticContentInjectionTest extends BaseTest {

	private static final boolean debug = true;
	private static final StringBuffer verificationErrors = new StringBuffer();
	private static final Logger log = LogManager
			.getLogger(StaticContentInjectionTest.class);

	@BeforeClass
	public void beforeClass() throws IOException {
		super.setBrowser("chrome"); // firefox is lagging
		super.beforeClass();
		assertThat(driver, notNullValue());
	}

	@BeforeMethod
	public void loadPage() {
		super.setDebug(debug);
		openAboutBlankPage();
	}

	@Test(enabled = true)
	public void testOpenEmptyPlaceholderPage() {
		openEmptyPlaceholderPage();
		sleep(100);
	}

	// NOTE: somewhat unstable
	@Test(enabled = true)
	public void testWriteDocument() {
		writeDocument("extjs_ex.htm");
		sleep(100);
	}

	// NOTE: big payloads are deadly e.g. list2.html
	@Test(enabled = true)
	public void testBodyInnerHTML() {
		bodyInnerHTML("text_resource.htm");
		sleep(100);
	}

	// NOTE: unstable - disabled
	@Test(enabled = false)
	public void testBodyInnerHTMLTimedOut() {
		bodyInnerHTMLTimedOut("extjs_ex.htm", 5);
		sleep(100);
	}

}
