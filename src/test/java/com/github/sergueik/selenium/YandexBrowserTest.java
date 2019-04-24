package com.github.sergueik.selenium;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Formatter;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.opera.OperaOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;

import static org.junit.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

/**
* Sample test scenario for Selenium WebDriver operating Yandex Browser
* тестирование яндекс браузера с помощью селениум
* @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
* see also: https://events.yandex.ru/lib/talks/4261/
*/

// Based on
// https://stackoverflow.com/questions/30707783/java-selenium-webdriver-with-yandex
// TODO: super.driver
public class YandexBrowserTest /* extends BaseTest */ {

	private static WebDriver driver;
	// small size
	private static final int width = 1024;
	private static final int height = 768;
	// Yandex browser shows no page footer with small screen sizes
	// private static final int width = 768;
	// private static final int height = 640;
	// scrolling does not help
	private static final Dimension browserWindowDimention = new Dimension(width,
			height);
	private static WebDriverWait wait;
	private static Actions actions;
	private static WebElement element = null;
	private static Boolean debug = false;
	private static String selector = null;
	private static long implicitWait = 10;
	private static int flexibleWait = 180;
	private static long polling = 1000;
	private static long highlight = 100;
	private static long afterTest = 1000;
	private static String baseURL = "https://browser.yandex.com";
	private static final StringBuffer verificationErrors = new StringBuffer();
	private static Map<String, String> env = System.getenv();
	private static final StringBuilder loggingSb = new StringBuilder();
	private static final Formatter formatter = new Formatter(loggingSb,
			Locale.US);

	@BeforeClass
	public static void setUp() {

		if (env.containsKey("DEBUG") && env.get("DEBUG").equals("true")) {
			debug = true;
		}
		// download operadriver from
		// https://github.com/operasoftware/operachromiumdriver/releases
		System.setProperty("webdriver.opera.driver",
				Paths.get(System.getProperty("user.home")).resolve("Downloads")
						.resolve("operadriver.exe").toAbsolutePath().toString());
		// use offline installer yandex.exe for Windows
		// e.g. from https://www.filehorse.com/download-yandex-browser/
		// and yandex-browser package for major Linux distributions
		// from
		// https://www.linuxbabe.com/browser/yandex-browser-debian-ubuntu-fedora-opensuse-arch

		OperaOptions options = new OperaOptions();
		options.setBinary(Paths.get(System.getProperty("user.home"))
				.resolve("AppData\\Local\\Yandex\\YandexBrowser\\Application")
				.resolve("browser.exe").toAbsolutePath().toString());
		// org.openqa.selenium.WebDriverException: unknown error: no Opera binary at
		// C:\Users\Serguei\AppData\Local\Yandex\YandexBrowser\Application\browser.exe
		driver = new OperaDriver(options);
		actions = new Actions(driver);
		wait = new WebDriverWait(driver, flexibleWait);
		wait.pollingEvery(Duration.ofMillis(polling));
		driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
	}

	@Before
	public void beforeTest() {
		driver.get(baseURL);
	}

	@After
	public void resetBrowser() {
		// load blank page
		driver.get("about:blank");
	}

	@AfterClass
	public static void tearDown() {
		try {
			Thread.sleep(afterTest);
		} catch (InterruptedException e) {
		}
		try {
			driver.close();
			driver.quit();
		} catch (Exception e) {
			// ignore
			// java.net.ProtocolException: unexpected end of stream
		}
		if (verificationErrors.length() != 0) {
			throw new RuntimeException(verificationErrors.toString());
		}
	}

	// @Ignore
	// power saving mode ad dialog is stealing the focus.
	private static final String helpURL = "https://browser.yandex.com/help/";
	private static final String cssSelector1 = String
			.format("#mount > main > footer a[href='%s']", helpURL);

	private static final String cssSelector2 = "#mount > main > footer a > img.footer__icon[alt='Help']";

	@Test
	public void trickyFooterTest() {
		driver.manage().window().setSize(browserWindowDimention);

		System.err.println("Driver: " + driver.getClass());
		try {
			element = driver.findElement(By.cssSelector(cssSelector1));
		} catch (Exception e) {
			System.err.println("Exception (ignored): " + e.getMessage());

		}
		element = driver.findElement(By.cssSelector(cssSelector2));
		// src="//avatars.mds.yandex.net/";
		// Act
		assertThat(element, notNullValue());
		assertThat(element.getAttribute("src"),
				containsString("https://avatars.mds.yandex.net/"));
		scrollIntoView(element);
		actions.moveToElement(element).build().perform();
		element = wait.until(ExpectedConditions
				.visibilityOf(driver.findElement(By.cssSelector(cssSelector2))));
		element.click();
		wait.until(ExpectedConditions.urlContains(helpURL));
		assertTrue(driver.getCurrentUrl().matches(helpURL));
	}

	public void scrollIntoView(WebElement element) {
		scrollIntoView(element, true);
	}

	// DOM method:
	// https://developer.mozilla.org/en-US/docs/Web/API/Element/scrollIntoView
	public void scrollIntoView(WebElement element, boolean force) {
		try {
			// plain
			// executeScript("arguments[0].scrollIntoView({ behavior: \"smooth\" });",
			// element);
			// based on
			// http://www.performantdesign.com/2009/08/26/scrollintoview-but-only-if-out-of-view/
			// referenced in
			// https://stackoverflow.com/questions/6215779/scroll-if-element-is-not-visible
			//
			String result = (String) executeScript(
					getScriptContent("scrollIntoViewIfOutOfView.js"), element, debug,
					force);

			if (debug) {
				System.err.println("Result: " + result);
			}
			// commented during borrowing from BaseTest
			// highlight(element.findElement(By.xpath("..")));
			//
			// if (debug) {
			// System.err.println(xpathOfElement(element));
			// }
		} catch (Exception e) {
			// temporarily catch all exceptions.
			System.err.println("Exception: " + e.toString());
		}

	}

	protected static String getScriptContent(String scriptName) {
		try {
			final InputStream stream = BaseTest.class.getClassLoader()
					.getResourceAsStream(scriptName);
			final byte[] bytes = new byte[stream.available()];
			stream.read(bytes);
			return new String(bytes, "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException(scriptName);
		}
	}

	// http://www.javawithus.com/tutorial/using-ellipsis-to-accept-variable-number-of-arguments
	public Object executeScript(String script, Object... arguments) {
		if (driver instanceof JavascriptExecutor) {
			JavascriptExecutor javascriptExecutor = JavascriptExecutor.class
					.cast(driver);
			/*
			 *
			 // currently unsafe
			System.err.println(arguments.length + " arguments received.");
			String argStr = "";
			
			for (int i = 0; i < arguments.length; i++) {
				argStr = argStr + " "
						+ (arguments[i] == null ? "null" : arguments[i].toString());
			}
			
			System.err.println("Calling " + script.substring(0, 40)
					+ "..." + \n" + "with arguments: " + argStr);
					*/
			return javascriptExecutor.executeScript(script, arguments);
		} else {
			throw new RuntimeException("Script execution failed.");
		}
	}

	public void sleep(Integer milliSeconds) {
		try {
			Thread.sleep((long) milliSeconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}