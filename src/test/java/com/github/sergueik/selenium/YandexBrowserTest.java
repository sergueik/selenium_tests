package com.github.sergueik.selenium;

import java.io.File;
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
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.opera.OperaOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Sample test scenario for Selenium WebDriver operating Yandex Browser
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

// Based on
// https://stackoverflow.com/questions/30707783/java-selenium-webdriver-with-yandex
public class YandexBrowserTest {

	private static WebDriver driver;
	// private static WebDriver frame;
	private static final int width = 320;
	private static final int height = 240;
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
		// https://github.com/operasoftware/operachromiumdriver/releases
		System.setProperty("webdriver.opera.driver",
				Paths.get(System.getProperty("user.home")).resolve("Downloads")
						.resolve("operadriver.exe").toAbsolutePath().toString());
		// use offline installer
		// https://www.filehorse.com/download-yandex-browser/

		OperaOptions options = new OperaOptions();
		options.setBinary(Paths.get(System.getProperty("user.home"))
				.resolve("AppData\\Local\\Yandex\\YandexBrowser\\Application")
				.resolve("browser.exe").toAbsolutePath().toString());
		driver = new OperaDriver(options);
		actions = new Actions(driver);
		wait = new WebDriverWait(driver, flexibleWait);
		wait.pollingEvery(Duration.ofMillis(polling));
		driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
	}

	@Before
	public void beforeTest() {
		driver.get(baseURL);
		driver.manage().window().setSize(browserWindowDimention);
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
	@Test
	public void Test() {
		System.err.println("Driver: " + driver.getClass());
	}

}