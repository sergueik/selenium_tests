package com.github.sergueik.selenium;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import java.nio.file.Paths;

import java.text.Normalizer;

import java.time.Duration;

import java.util.Enumeration;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import java.util.concurrent.TimeUnit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/*
 * Practice Selenium Cookies with session-based [mailbox](https://www.yandex.ru/) login 
 */
// NOTE: this test restarts the browser. Therefore not inheriting from the 
// BaseTest class
public class YandexTest {

	private static WebDriver driver;
	private static WebDriverWait wait;
	private static Actions actions;
	private static Boolean debug = false;
	private static final long implicitWait = 10;
	private static final int flexibleWait = 30;
	private static final long polling = 1000;
	private static final long highlight = 100;
	private static final long afterTest = 1000;
	private static final String baseURL = "https://ya.ru/";
	private static String mailURL = "https://mail.yandex.ru/";
	private static final String finalUrl = "https://www.yandex.ru/";
	private static final String loginURL = "https://passport.yandex.ru";
	private static final StringBuffer verificationErrors = new StringBuffer();
	private static Map<String, String> env = System.getenv();
	private static String username = "";
	private static String password = "";
	private static Formatter formatter;
	private static StringBuilder loggingSb;
	private static String configurationFileName = "test.configuration";
	private static String propertiesFileName = "test.properties";
	private static final Map<String, String> browserDrivers = new HashMap<>();
	private static final String propertyFilePath = getPropertyEnv(
			"property.filepath", "src/test/resources");

	private static String osName = null;

	public static String getOsName() {
		if (osName == null) {
			osName = System.getProperty("os.name").toLowerCase();
			if (osName.startsWith("windows")) {
				osName = "windows";
			}
		}
		return osName;
	}

	@BeforeClass
	public static void setUp() {

		if (env.containsKey("DEBUG") && env.get("DEBUG").equals("true")) {
			debug = true;
		}
		/*
		System.err.println(String.format("%s=%s", "System.env('property.filepath')",
				System.getenv("property.filepath")));
		System.err
				.println(String.format("%s=%s", "getPropertyEnv('property.filepath')",
						getPropertyEnv("property.filepath", "")));
		*/
		getOsName();
		browserDrivers.put("chrome",
				osName.equals("windows") ? "chromedriver.exe" : "chromedriver");
		browserDrivers.put("firefox",
				osName.equals("windows") ? "geckodriver.exe" : "geckodriver");
		browserDrivers.put("edge", "MicrosoftWebDriver.exe");
		HashMap<String, String> propertiesMap = PropertiesParser
				.getProperties(String.format("%s/%s/%s", System.getProperty("user.dir"),
						propertyFilePath, propertiesFileName));
		username = propertiesMap.get("username");
		password = propertiesMap.get("password");

		loggingSb = new StringBuilder();
		formatter = new Formatter(loggingSb, Locale.US);
		System.setProperty("webdriver.gecko.driver", osName.equals("windows")
				? new File("c:/java/selenium/geckodriver.exe").getAbsolutePath()
				: /* String.format("%s/Downloads/geckodriver", System.getenv("HOME"))*/
				Paths.get(System.getProperty("user.home")).resolve("Downloads")
						.resolve("geckodriver").toAbsolutePath().toString());
		System.setProperty("webdriver.firefox.bin",
				osName.equals("windows")
						? new File("c:/Program Files (x86)/Mozilla Firefox/firefox.exe")
								.getAbsolutePath()
						: "/usr/bin/firefox");
		// https://github.com/SeleniumHQ/selenium/wiki/DesiredCapabilities
		DesiredCapabilities capabilities = DesiredCapabilities.firefox();
		// we are using legacy FirefoxDriver
		// for Firefox v.59 no longer possible ?
		capabilities.setCapability("marionette", false);
		driver = new FirefoxDriver(capabilities);
		// With Chrome one can also try
		/*
		System.setProperty("webdriver.chrome.driver",
				new File("c:/java/selenium/chromedriver.exe").getAbsolutePath());
		ChromeOptions options = new ChromeOptions();
		options.addArguments(String.format(
				"user-data-dir=%s/AppData/Local/Google/Chrome/User Data/Default",
				System.getProperty("user.home")));
		options.addArguments("--start-maximized");
		WebDriver driver = new ChromeDriver(options);
		*/
		wait = new WebDriverWait(driver, flexibleWait);
		wait.pollingEvery(Duration.ofMillis(polling));
		// wait.pollingEvery(polling, TimeUnit.MILLISECONDS);
		driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
		driver.get(baseURL);
	}

	@Before
	public void beforeTest() {
		driver.get(baseURL);
		WebElement element = driver.findElement(By.cssSelector(
				"table.layout__table tr.layout__header div.personal div.b-inline"));
		highlight(element);
		element.click();
		wait.until(ExpectedConditions.urlContains(mailURL));
		element = driver
				.findElement(By.xpath("//span[contains(text(), 'Войти')]/.."));
		highlight(element);
		element.click(); // NOTE: does not work with the <span> - works with the <a>
		wait.until(ExpectedConditions.urlContains(loginURL));
	}

	@After
	public void afterTest() {
	}

	@AfterClass
	public static void tearDown() {
		try {
			Thread.sleep(afterTest);
		} catch (InterruptedException e) {
		}
		driver.close();
		driver.quit();
		if (verificationErrors.length() != 0) {
			throw new RuntimeException(verificationErrors.toString());
		}
	}

	@Test
	public void dummyPassingTest() {
	}

	@Ignore
	@Test
	public void getCookieTest() throws Exception {

		doLogin();
		Set<Cookie> cookies = driver.manage().getCookies();
		System.err.println("Cookies:");
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

	@Ignore
	@Test
	public void useCookieTest() throws Exception {
		String loginUrl = doLogin();
		System.err.println("Getting the cookies");
		Set<Cookie> cookies = driver.manage().getCookies();
		System.err.println("Closing the browser");
		wait = null;
		System.err.println("re-open the browser, about to use the session cookies");
		driver.close();
		driver = new FirefoxDriver();
		// re-initialize wait object
		wait = new WebDriverWait(driver, flexibleWait);
		// wait.pollingEvery(Duration.ofMillis(polling));
		wait.pollingEvery(polling, TimeUnit.MILLISECONDS);
		System.err.println("Navigating to " + loginUrl);
		driver.get(loginUrl);
		System.err.println("Loading cookies");
		for (Cookie cookie : cookies) {
			driver.manage().addCookie(cookie);
		}
		driver.navigate().refresh();

		System.err.println("Waiting for inbox");
		try {
			wait.until(ExpectedConditions.urlContains("#inbox"));
		} catch (TimeoutException | UnreachableBrowserException e) {
			verificationErrors.append(e.toString());
		}
		doLogout();
	}

	@Ignore
	@Test
	public void useExpiredCookieLaterTest() {
		/*
		 * public Cookie(java.lang.String name, java.lang.String value,
		 * java.lang.String domain, java.lang.String path, java.util.Date expiry,
		 * boolean isSecure, boolean isHttpOnly)
		 *
		 * Creates a cookie.
		 */
	}

	private String doLogin() {
		// I enter the username
		// NOTE: elements with class names ending with "Label" e.g.
		// "passport-Input-Label"
		// would not accept input
		WebElement element = driver
				.findElement(By.xpath("//form//input[@name='login']"));
		System.err.println(
				"Form element containing login: " + element.findElement(By.xpath(".."))
						.findElement(By.xpath("..")).getAttribute("outerHTML"));

		highlight(element);
		element.clear();
		element.sendKeys(username);

		element = driver.findElement(By.cssSelector("input[name='passwd']"));
		highlight(element);
		element.clear();
		element.sendKeys(password);
		// TODO: Assert that input gets added to the background form
		System.err.println("Password: " + element.getAttribute("value"));
		// Evaluate the landing page URL
		// element = driver
		// .findElement(By.cssSelector("form.new-auth-form input[name='passwd']"));
		element = driver.findElement(By.cssSelector("form button[type='submit']"));
		highlight(element);
		element = driver.findElement(By.cssSelector("form a.nb-button"));

		String login_href = element.getAttribute("href");
		System.err.println("Login href: " + login_href);
		String matcherExpression = String
				.format("%s/auth\\?(?:.*)&retpath=(.+)&(?:.*)", loginURL);
		System.err.println("Matcher: " + matcherExpression);
		Pattern pattern = Pattern.compile(matcherExpression);

		Matcher matcher = pattern.matcher(login_href);
		String retpath = null;
		if (matcher.find()) {
			try {
				retpath = java.net.URLDecoder.decode(matcher.group(1).toString(),
						"UTF-8");
			} catch (UnsupportedEncodingException e) {
				// ignore
			}
		}
		System.err.println("Login retpath: " + retpath);

		// And I click the login button
		element = driver.findElement(By.cssSelector("form button[type='submit']"));

		highlight(element);
		// String currentUrl = driver.getCurrentUrl();
		element.click();

		// wait until browser is away from the login page
		System.err.println("Waiting to get away from " + loginURL);
		try {
			wait.until(
					ExpectedConditions.not(ExpectedConditions.urlContains(loginURL)));
		} catch (TimeoutException tex) {
			// TODO: better check if the inputs were propagated and
			// the following invalid credentials error is not displayed:
			// <div class="error-hint">Возможно у&nbsp;вас выбрана другая раскладка
			// клавиатуры или нажата клавиша "Caps Lock".</div>

			try {
				element = driver.findElement(
						By.cssSelector("div.layout-inner div.js-messages div.error-msg"));
				String errorMessageTranslated = Translit.toAscii(element.getText());
				verificationErrors
						.append("Getting error message " + errorMessageTranslated);
				System.err.println("Getting error message " + errorMessageTranslated);
			} catch (NoSuchElementException elex) {
				// ignore
			}
			verificationErrors.append(tex.toString());
		}
		System.err.println("Waiting for " + retpath);

		// wait until browser is on the landing page
		wait.until(ExpectedConditions.urlContains(retpath));

		// System.out.println("Page url: " + driver.getCurrentUrl());
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(
					By.cssSelector("div.mail-App-Header div.mail-User")));
		} catch (TimeoutException | UnreachableBrowserException e) {
			verificationErrors.append(e.toString());
		}
		return retpath;
	}

	private void doLogout() {
		assertTrue(driver.getCurrentUrl().matches(".*#inbox"));

		// When I am logged on user
		WebElement element = driver.findElement(
				By.cssSelector("div.mail-App-Header div.mail-User div.mail-User-Name"));
		highlight(element);
		element.click();
		// And I am about to log off
		element = driver.findElement(By.cssSelector(
				"body.mail-Page-Body div.ui-dialog div._nb-popup-content div.b-user-dropdown-content-with-exit div.b-mail-dropdown__item a.ns-action"));
		highlight(element);
		element.click();
		// to update the path, increase the sleep interval below
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {

		}
		// And I confirm I am going to log off
		/*
		// auto-generated xpath: no longer correct, difficult to maintain
		element = driver.findElement(
				By.xpath("//div[5]/div[2]/table/tbody/tr/td/div[3]/div/a"));
		*/
		/*
		element = wait.until(ExpectedConditions.visibilityOf(driver
				.findElement(By.xpath("//table//a[@class='ns-action'][text()='Выйти на всех устройствах']"))));
		*/
		element = wait
				.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(
						"//table//a[@class='ns-action' and contains(string(),'Выйти на всех устройствах')]"))));
		// Evaluate logout URL
		highlight(element);
		String logout_href = element.getAttribute("href");
		System.err.println("Logout href: " + element.getAttribute("href"));
		/*
		 * Logout href: https://passport.yandex.ru/passport?mode=embeddedauth&action=change_default&uid=419561298&yu=3540052471494536037&retpath=https%3A%2F%2Fpassport.yandex.ru%2Fpassport%3Fmode%3Dlogout%26global%3D1%26yu%3D3540052471494536037
		 */

		String retpath = null;
		Pattern pattern = Pattern.compile(
				"https://passport.yandex.ru/passport?\\?mode=.+&retpath=(.+)$");

		Matcher matcher = pattern.matcher(logout_href);
		if (matcher.find()) {
			try {
				retpath = java.net.URLDecoder.decode(matcher.group(1).toString(),
						"UTF-8");
			} catch (UnsupportedEncodingException e) {
				// ignore
			}
		}
		System.err.println("Extracted Logout relpath: " + retpath);

		String currentUrl = driver.getCurrentUrl();
		element.click();
		try {
			wait.until(
					ExpectedConditions.not(ExpectedConditions.urlContains(currentUrl)));
		} catch (TimeoutException e) {
			verificationErrors.append(e.toString());
		}

		try {
			wait.until(ExpectedConditions.urlContains(finalUrl));
		} catch (TimeoutException e) {
			// TODO
		}
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

	// TODO: merge projects
	// from
	// https://github.com/sergueik/selenium_tests/tree/master/src/test/java/com/github/sergueik/selenium/TariffTest.java
	public static class Translit {

		private static final Charset UTF8 = Charset.forName("UTF-8");
		private static final char[] alphabetCyrillic = { ' ', 'а', 'б', 'в', 'г',
				'д', 'е', 'ё', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р',
				'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю',
				'я', 'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л',
				'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ',
				'Ъ', 'Ы', 'Б', 'Э', 'Ю', 'Я', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
				'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
				'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
				'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
				'Y', 'Z' };

		private static final String[] alphabetTranslit = { " ", "a", "b", "v", "g",
				"d", "e", "e", "zh", "z", "i", "y", "k", "l", "m", "n", "o", "p", "r",
				"s", "t", "u", "f", "h", "ts", "ch", "sh", "sch", "", "i", "", "e",
				"ju", "ja", "A", "B", "V", "G", "D", "E", "E", "Zh", "Z", "I", "Y", "K",
				"L", "M", "N", "O", "P", "R", "S", "T", "U", "F", "H", "Ts", "Ch", "Sh",
				"Sch", "", "I", "", "E", "Ju", "Ja", "a", "b", "c", "d", "e", "f", "g",
				"h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u",
				"v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I",
				"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W",
				"X", "Y", "Z" };

		private static String toAscii(final String input) {
			final CharsetEncoder charsetEncoder = UTF8.newEncoder();
			final char[] decomposed = Normalizer
					.normalize(input, Normalizer.Form.NFKD).toCharArray();
			final StringBuilder sb = new StringBuilder(decomposed.length);

			// NOTE: evaluating the character charcount is unnecessary with Cyrillic
			for (int i = 0; i < decomposed.length;) {
				final int codePoint = Character.codePointAt(decomposed, i);
				final int charCount = Character.charCount(codePoint);

				if (charsetEncoder
						.canEncode(CharBuffer.wrap(decomposed, i, charCount))) {
					sb.append(decomposed, i, charCount);
				}

				i += charCount;
			}

			StringBuilder builder = new StringBuilder();

			for (int i = 0; i < sb.length(); i++) {
				for (int x = 0; x < alphabetCyrillic.length; x++)
					if (sb.charAt(i) == alphabetCyrillic[x]) {
						builder.append(alphabetTranslit[x]);
					}
			}
			return builder.toString();

		}
	}

	// origin:
	// https://github.com/TsvetomirSlavov/wdci/blob/master/code/src/main/java/com/seleniumsimplified/webdriver/manager/EnvironmentPropertyReader.java
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
}
