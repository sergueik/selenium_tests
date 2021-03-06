package com.github.sergueik.selenium;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
// import static org.hamcrest.Matchers.matchesRegex;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;

// https://matchers.jcabi.com/regex-matchers.html
// https://stackoverflow.com/questions/8505153/assert-regex-matches-in-junit
// https://piotrga.wordpress.com/2009/03/27/hamcrest-regex-matcher/
// http://hamcrest.org/JavaHamcrest/javadoc/2.0.0.0/org/hamcrest/text/MatchesPattern.html
// https://www.baeldung.com/hamcrest-text-matchers

// https://matchers.jcabi.com/regex-matchers.html
// import com.jcabi.matchers.RegexMatchers;

import org.hamcrest.MatcherAssert;

import java.util.regex.Pattern;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.nio.file.Paths;

//reserved for turning on Selenide and Allure
//import com.codeborne.selenide.*;
//import static com.codeborne.selenide.Selenide.$;
//import static com.codeborne.selenide.Selenide.open;
//import com.codeborne.selenide.logevents.SelenideLogger;
//import io.qameta.allure.selenide.AllureSelenide;

// https://seleniumhq.github.io/selenium/docs/api/java/org/openqa/selenium/support/ui/FluentWait.html#pollingEvery-java.time.Duration-
// NOTE: needs java.time.Duration not the org.openqa.selenium.support.ui.Duration;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

// based on https://github.com/tw1911/test1/blob/master/src/test/java/com/tw1911/test1/GoogleSearchTests.java
public class ParallelMultiBrowserTest {

	private static String osName = getOSName();
	private DriverWrapper driverWrapper = new DriverWrapper();
	private static final boolean remote = true;
	// private static final boolean remote = Boolean
	// .parseBoolean(System.getenv("REMOTE"));
	private static final boolean headless = Boolean.parseBoolean(System.getenv("HEADLESS"));
	// public WebDriver driver;
	private static final String searchString = "Тестовое задание";
	// public WebDriverWait wait;
	// public Actions actions;
	// public Alert alert;
	// public JavascriptExecutor js;
	// public TakesScreenshot screenshot;
	@SuppressWarnings("unused")
	private static String handle = null;

	public int scriptTimeout = 5;
	public int flexibleWait = 30;
	public int implicitWait = 1;
	public int pollingInterval = 500;
	@SuppressWarnings("unused")
	private static long highlightInterval = 100;

	// NOTE: pass distinct base url and locators to parallel tests for debugging
	@DataProvider(name = "same-browser-provider", parallel = true)
	public Object[][] provideSameBrowser() throws Exception {
		return new Object[][] { { "chrome", "https://www.google.com/?hl=ru", "input[name*='q']" },
				{ "chrome", "https://www.google.com/?hl=ko", "input[name='q']" }, };
	}

	// NOTE: pass distinct base url and locators to parallel tests for debugging
	@DataProvider(name = "different-browser-provider", parallel = true)
	public Object[][] provideDifferentBrowser() throws Exception {
		return new Object[][] { { "chrome", "https://www.google.com/?hl=ru", "input[name*='q']" },
				{ "firefox", "https://www.google.com/?hl=ko", "input[name='q']" }, };
	}

	private static final Map<String, String> browserDrivers = new HashMap<>();
	static {
		browserDrivers.put("chrome", osName.equals("windows") ? "chromedriver.exe" : "chromedriver");
		browserDrivers.put("firefox", osName.equals("windows") ? "geckodriver.exe" : "geckodriver");
		browserDrivers.put("edge", "MicrosoftWebDriver.exe");
	}

	private static final Map<String, String> browserDriverSystemProperties = new HashMap<>();
	static {
		browserDriverSystemProperties.put("chrome", "webdriver.chrome.driver");
		browserDriverSystemProperties.put("firefox", "webdriver.gecko.driver");
		browserDriverSystemProperties.put("edge", "webdriver.edge.driver");
	}

	@Test(enabled = true, dataProvider = "same-browser-provider", threadPoolSize = 2)
	public void googleBadSearchTest(String browser, String baseURL, String cssSelector) {

		System.err.println("Launching " + browser + (remote ? " remotely" : ""));
		System.setProperty(browserDriverSystemProperties.get(browser), Paths.get(System.getProperty("user.home"))
				.resolve("Downloads").resolve(browserDrivers.get(browser)).toAbsolutePath().toString());
		if (browser.equals("chrome")) {
			DesiredCapabilities capabilities = DesiredCapabilities.chrome();
			ChromeOptions chromeOptions = new ChromeOptions();
			// options for headless
			if (headless) {
				for (String optionAgrument : (new String[] { "headless", "window-size=1200x800" })) {
					chromeOptions.addArguments(optionAgrument);
				}
			}
			capabilities.setBrowserName(DesiredCapabilities.chrome().getBrowserName());
			DriverWrapper.add(remote ? "remote" : "chrome", capabilities);
			// NOTE: new exception when a chromedriver / chrome release mismatch
			// e.g. from with chromeDriver 2.44, chrome 65
			// "session not created: chrome version must be >= 69.0.3497.0"
		} else if (browser.equals("firefox")) {
			System.setProperty("webdriver.firefox.bin",
					osName.equals("windows")
							? new File("c:/Program Files (x86)/Mozilla Firefox/firefox.exe").getAbsolutePath()
							: "/usr/bin/firefox");
			DesiredCapabilities capabilities = DesiredCapabilities.firefox();
			if (!remote) {
				capabilities.setCapability("marionette", false);
				// NOTE: the Exception org.openqa.selenium.WebDriverException:
				// "timed out waiting 45 seconds for firefox to start."
				// is thrown after "marionette" property set to true
				// and remote browser node and hub run in Vagrant box
			}
			DriverWrapper.add(remote ? "remote" : "firefox", capabilities);
		}
		driverWrapper.setDebug(true);

		System.err.println("Driver inventory: " + DriverWrapper.getDriverInventoryDump().toString());

		WebDriver driver = driverWrapper.current();
		driver.get(baseURL);

		System.err.println("Thread id: " + Thread.currentThread().getId() + "\n" + "Driver hash code: "
				+ driver.hashCode() + "\n" + "Driver hash code: " + driverWrapper.current().hashCode());

		driver.get(baseURL);

		Actions actions = new Actions(driver);

		driver.manage().timeouts().setScriptTimeout(scriptTimeout, TimeUnit.SECONDS);
		// helpers
		TakesScreenshot screenshot = ((TakesScreenshot) driver);
		JavascriptExecutor js = ((JavascriptExecutor) driver);

		// Declare a wait time
		WebDriverWait wait = new WebDriverWait(driver, flexibleWait);

		wait.pollingEvery(Duration.ofMillis(pollingInterval));
		// NOTE: selenium driver version-sensitive code
		// the active version is 3.13.0 compatible
		// 3.8.0 and older have different signature
		// wait.pollingEvery(pollingInterval, TimeUnit.MILLISECONDS);

		wait.pollingEvery(Duration.ofMillis(pollingInterval));

		driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);

		WebElement element = wait
				.until(ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector(cssSelector))));
		System.err.println("Thread id: " + Thread.currentThread().getId() + "\n" + "Driver hash code: "
				+ driver.hashCode() + "\n" + "WebDriveWait hash code: " + wait.hashCode() + "\n"
				+ "Web Element hash code: " + element.hashCode());
		// TODO: element.setAttribute("value", searchString);
		element.sendKeys(searchString);
		element = wait.until(
				// TODO; exercise culture // Поиск в Google | Google Search
				ExpectedConditions.visibilityOf(driver
						.findElement(By.xpath(String.format("//input[contains(@value, '%s')]", "Google Search")))));
		element.click();
		element = wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.id("resultStats"))));
		// occasionally observed in a parallel run:
		// org.openqa.selenium.NoSuchSessionException: invalid session id
		assertThat(element, notNullValue());
		/*
		 * SelenideDriver driver = new SelenideDriver(new SelenideConfig()
		 * .browser(browser) .driverManagerEnabled(true)
		 * .remote("http://selenoid:4444/wd/hub") .headless(true));
		 * driver.open("http://google.com");
		 * driver.$(By.name("q")).setValue(searchString);
		 * driver.$(By.xpath("//input[@value='Поиск в Google']")).click();
		 * driver.$(By.id("resultStats")).shouldBe(Condition.visible); driver.close();
		 */
	}

	@Test(enabled = false, dataProvider = "different-browser-provider", threadPoolSize = 2)
	public void googleAternativeSearchTest(String browser, String baseURL, String cssSelector) {

		System.err.println("Launching " + browser + (remote ? " remotely" : ""));
		System.setProperty(browserDriverSystemProperties.get(browser), Paths.get(System.getProperty("user.home"))
				.resolve("Downloads").resolve(browserDrivers.get(browser)).toAbsolutePath().toString());
		if (browser.equals("chrome")) {
			DesiredCapabilities capabilities = DesiredCapabilities.chrome();
			ChromeOptions chromeOptions = new ChromeOptions();
			// options for headless
			if (headless) {
				for (String optionAgrument : (new String[] { "headless", "window-size=1200x800" })) {
					chromeOptions.addArguments(optionAgrument);
				}
			}
			capabilities.setBrowserName(DesiredCapabilities.chrome().getBrowserName());
			DriverWrapper.add(remote ? "remote" : "chrome", capabilities);
		} else if (browser.equals("firefox")) {
			System.setProperty("webdriver.firefox.bin",
					osName.equals("windows")
							? new File("c:/Program Files (x86)/Mozilla Firefox/firefox.exe").getAbsolutePath()
							: "/usr/bin/firefox");
			DesiredCapabilities capabilities = DesiredCapabilities.firefox();
			if (!remote) {
				capabilities.setCapability("marionette", false);
				// NOTE: the Exception org.openqa.selenium.WebDriverException:
				// "timed out waiting 45 seconds for firefox to start."
				// is thrown after "marionette" property set to true
				// and remote browser node and hub run in Vagrant box
			}
			DriverWrapper.add(remote ? "remote" : "firefox", capabilities);
		}
		driverWrapper.setDebug(true);
		WebDriver driver = driverWrapper.current();
		System.err.println("Thread id: " + Thread.currentThread().getId() + "\n" + "Driver inventory: "
				+ DriverWrapper.getDriverInventoryDump().toString() + "\n" + "Driver hash code: " + driver.hashCode());

		driver.get(baseURL);
		Actions actions = new Actions(driver);

		driver.manage().timeouts().setScriptTimeout(scriptTimeout, TimeUnit.SECONDS);

		// helpers
		TakesScreenshot screenshot = ((TakesScreenshot) driver);
		JavascriptExecutor js = ((JavascriptExecutor) driver);

		WebDriverWait wait = new WebDriverWait(driver, flexibleWait);
		wait.pollingEvery(Duration.ofMillis(pollingInterval));

		driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);

		WebElement element = driver.findElement(By.cssSelector(cssSelector));
		System.err.println("Thread id: " + Thread.currentThread().getId() + "\n" + "Driver inventory: "
				+ DriverWrapper.getDriverInventoryDump().toString() + "\n" + "Driver hash code: " + driver.hashCode()
				+ "\n" + "Web Element hash code: " + element.hashCode());

		element.sendKeys(searchString);
		element = wait.until(
				// TODO; exercise culture // Поиск в Google | Google Search
				ExpectedConditions
						.visibilityOf(driver.findElement(By.xpath(String.format("//input[@name = '%s']", "btnK"))))); // [@type='submit']
																														// ?
		element.click();
		// try{
		element = wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.id("resultStats"))));
		// }
		assertThat(element, notNullValue());
		assertTrue(element.getText().matches("^.*\\b(?:\\d+)\\b.*$"));
		// assertThat(element.getText(), matchesRegex(Pattern.compile("(?:\\d+)")));
	}

	@BeforeClass
	public static void setUp() {
		if (remote) {
			DriverWrapper.setHubUrl("http://127.0.0.1:4444/wd/hub");
		}
		// SelenideLogger.addListener("allure", new AllureSelenide());
	}

	@AfterMethod
	public void afterMethod() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
		}
		// driver.get("about:blank");
		/*
		 * if (driver != null) { try { driver.close(); driver.quit(); } catch (Exception
		 * e) { } }
		 */
	}

	@AfterClass
	public static void tearDown() {
		// SelenideLogger.removeListener("allure");
	}

	// Utilities
	public static String getOSName() {
		if (osName == null) {
			osName = System.getProperty("os.name").toLowerCase();
			if (osName.startsWith("windows")) {
				osName = "windows";
			}
		}
		return osName;
	}
}
