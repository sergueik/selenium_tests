package com.github.sergueik.selenium;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.RuntimeException;
import static java.lang.Boolean.*;

import java.net.URLDecoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.text.DateFormat;

import org.apache.commons.lang3.exception.ExceptionUtils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.InvalidCookieDomainException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Platform;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

// NOTE: this test restarts the browser
public class GoogleDriveTest {

	private static WebDriver driver;
	private static WebDriver frame;
	private static WebDriverWait wait;
	private static Actions actions;
	private static WebElement element = null;
	private static Boolean debug = false;
	private static String selector = null;
	private static long implicitWait = 10;
	private static int flexibleWait = 30;
	private static long polling = 1000;
	private static long highlight = 100;
	private static long afterTest = 1000;
	private static String baseURL = "https://drive.google.com/";
	private static String loginURL = "https://accounts.google.com/";
	private static final StringBuffer verificationErrors = new StringBuffer();
	private static Map<String, String> env = System.getenv();
	private static String username = "";
	private static String password = "";
	private static Formatter formatter;
	private static StringBuilder loggingSb;
	private static String propertiesFileName = "test.properties";
	private static final String propertyFilePath = getPropertyEnv(
			"property.filepath", "src/test/resources");

	private static WebDriver setupDriver() {
		System.setProperty("webdriver.gecko.driver",
				new File("c:/java/selenium/geckodriver.exe").getAbsolutePath());
		System.setProperty("webdriver.firefox.bin",
				new File("c:/Program Files (x86)/Mozilla Firefox/firefox.exe")
						.getAbsolutePath());

		DesiredCapabilities capabilities = DesiredCapabilities.firefox();
		capabilities.setCapability("marionette", false);
		WebDriver driver = new FirefoxDriver(capabilities);
		return driver;
	}

	@BeforeClass
	public static void setUp() {

		if (env.containsKey("DEBUG") && env.get("DEBUG").equals("true")) {
			debug = true;
		}
		loggingSb = new StringBuilder();
		formatter = new Formatter(loggingSb, Locale.US);
		driver = setupDriver();
		HashMap<String, String> propertiesMap = PropertiesParser
				.getProperties(String.format("%s/%s/%s", System.getProperty("user.dir"),
						propertyFilePath, propertiesFileName));
		username = propertiesMap.get("username");
		password = propertiesMap.get("password");
		wait = new WebDriverWait(driver, flexibleWait);
		// Selenium Driver version sensitive code: 3.13.0 vs. 3.8.0 and older
		wait.pollingEvery(Duration.ofMillis(polling));
		// wait.pollingEvery(polling, TimeUnit.MILLISECONDS);
		driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
		driver.get(baseURL);
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
	@Test
	public void getCookieTest() throws Exception {

		doLogin();
		Set<Cookie> cookies = driver.manage().getCookies();
		if (debug) {
			System.err.println("Cookies:");
		}
		JSONArray cookieJSONArray = new JSONArray();
		for (Cookie cookie : cookies) {
			if (debug) {
				System.err.println(formatter
						.format(
								"Name: '%s'\n" + "Value: '%s'\n" + "Domain: '%s'\n"
										+ "Path: '%s'\n" + "Expiry: '%tc'\n" + "Secure: '%b'\n"
										+ "HttpOnly: '%b'\n" + "\n",
								cookie.getName(), cookie.getValue(), cookie.getDomain(),
								cookie.getPath(), cookie.getExpiry(), cookie.isSecure(),
								cookie.isHttpOnly())
						.toString());
			}
			JSONObject cookieJSONObject = new JSONObject(cookie);
			if (debug) {
				System.err.println(cookieJSONObject.toString());
			}
			cookieJSONArray.put(cookieJSONObject);
		}
		JSONObject cookiesJSONObject = new JSONObject();
		cookiesJSONObject.put("cookies", cookieJSONArray);
		if (debug) {
			System.err.println(cookiesJSONObject.toString());
		}
		doLogout();
	}

	// @Ignore
	@Test
	public void useCookieTest() throws Exception {
		doLogin();
		System.err.println("Getting the cookies");
		Set<Cookie> cookies = driver.manage().getCookies();
		System.err.println("Closing the browser");
		wait = null;
		System.err.println("re-open the browser, about to use the session cookies");
		driver.close();
		driver = setupDriver();
		// driver = new FirefoxDriver();
		// re-initialize wait object
		wait = new WebDriverWait(driver, flexibleWait);
		wait.pollingEvery(polling, TimeUnit.MILLISECONDS);
		System.err.println("Navigating to " + baseURL);
		driver.get(baseURL);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		System.err.println("Loading cookies");
		for (Cookie cookie : cookies) {
			if (debug) {
				System.err.println(formatter
						.format(
								"Name: '%s'\n" + "Value: '%s'\n" + "Domain: '%s'\n"
										+ "Path: '%s'\n" + "Expiry: '%tc'\n" + "Secure: '%b'\n"
										+ "HttpOnly: '%b'\n" + "\n",
								cookie.getName(), cookie.getValue(), cookie.getDomain(),
								cookie.getPath(), cookie.getExpiry(), cookie.isSecure(),
								cookie.isHttpOnly())
						.toString());
			}
			try {
				driver.manage().addCookie(cookie);
			} catch (InvalidCookieDomainException e) {
				// ignore the exception
			}
		}
		driver.get(baseURL);
		driver.navigate().refresh();

		System.err.println("Waiting for inbox");
		/*
		 * try { wait.until(ExpectedConditions.urlContains("#inbox")); } catch
		 * (TimeoutException | UnreachableBrowserException e) {
		 * verificationErrors.append(e.toString()); }
		 */
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
		}
		doLogout();
	}

	private void doLogout() {

		// Given I am accessing Google Drive
		// And i sign out

		element = driver
				.findElement(By.cssSelector("a[aria-label^='Google Account']"
		/*
		"a[href^='https://accounts.google.com/SignOutOptions?']"*/));
		highlight(element);
		element.click();
		element = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector("a[href^='https://accounts.google.com/Logout']")));
		highlight(element);
		element.click();
	}

	private void doLogin() {
		// very similar to yandex login
		// ...
		// &continue=https://drive.google.com/%23&followup=https://drive.google.com/&ltmpl=drive&emr=1#identifier
		// Given I access Google Drive
		try {
			element = driver.findElement(By
					.cssSelector("a[href^='https://accounts.google.com/ServiceLogin?']"));
		} catch (Exception e) {

		}

		element = driver.findElement(By.linkText("Go to Google Drive"));
		highlight(element);
		element.click();

		System.err.println("current URL :" + driver.getCurrentUrl());
		Pattern pattern = Pattern
				.compile(String.format("^%s.+&continue=([^&]+)&", loginURL));
		Matcher matcher = pattern.matcher(driver.getCurrentUrl());
		String retpath = null;
		if (matcher.find()) {
			try {
				retpath = java.net.URLDecoder.decode(matcher.group(1).toString(),
						"UTF-8");
			} catch (UnsupportedEncodingException e) {
				// ignore
			}
		}
		System.err.println("retpath: " + retpath);
		try {
			wait.until(ExpectedConditions.urlContains(loginURL));
		} catch (TimeoutException e) {
			System.err.println("Ignored exception " + e.toString());
		}
		// And I enter the username

		element = driver.findElement(By.cssSelector("*[type='email']"));
		element = driver
				.findElement(By.cssSelector("*[aria-label='Email or phone']"));
		// aria-label="Email or phone"
		highlight(element);
		element.clear();

		element.sendKeys(username);
		// element = wait.until(
		// ExpectedConditions.visibilityOfElementLocated(By.linkText("Next")));
		element = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//*[@id='identifierNext']/content/span")));
		//
		highlight(element);
		element.click();
		// And I enter the password
		element = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector("*[aria-label='Enter your password']")));
		highlight(element);
		element.clear();
		element.sendKeys(password);

		// And I sign in
		// driver.findElement(By.id("signIn")).click();
		element = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//*[@id='passwordNext']/content/span")));
		//
		highlight(element);
		element.click();
	}

	private void highlight(WebElement element) {
		highlight(element, highlight);
	}

	private void highlight(WebElement element, long highlight) {
		try {
			wait.until(ExpectedConditions.visibilityOf(element));
			executeScript("arguments[0].style.border='3px solid yellow'", element);
			Thread.sleep(highlight);
			executeScript("arguments[0].style.border=''", element);
		} catch (InterruptedException e) {
			// ignore
		}
	}

	private Object executeScript(String script, Object... arguments) {
		if (driver instanceof JavascriptExecutor) {
			JavascriptExecutor javascriptExecutor = JavascriptExecutor.class
					.cast(driver);
			return javascriptExecutor.executeScript(script, arguments);
		} else {
			throw new RuntimeException("Script execution failed.");
		}
	}

	public static String getPropertyEnv(String name, String defaultValue) {
		String value = System.getProperty(name);
		if (value == null) {
			value = System.getenv(name);
			if (value == null) {
				value = defaultValue;
			}
		}
		return value;
	}

	private static class PropertiesParser {
		@SuppressWarnings("unchecked")
		public static HashMap<String, String> getProperties(final String fileName) {
			Properties p = new Properties();
			HashMap<String, String> propertiesMap = new HashMap<>();
			System.err
					.println(String.format("Reading properties file: '%s'", fileName));
			try {
				p.load(new FileInputStream(fileName));
				Enumeration<String> e = (Enumeration<String>) p.propertyNames();
				for (; e.hasMoreElements();) {
					String key = e.nextElement();
					String val = p.get(key).toString();
					System.out.println(String.format("Reading: '%s' = '%s'", key, val));
					propertiesMap.put(key, val);
				}

			} catch (FileNotFoundException e) {
				System.err.println(
						String.format("Properties file was not found: '%s'", fileName));
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println(
						String.format("Properties file is not readable: '%s'", fileName));
				e.printStackTrace();
			}
			return (propertiesMap);
		}
	}
}
