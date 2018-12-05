package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

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
	private static final boolean remote = true;
	// private static final boolean remote = Boolean
	// .parseBoolean(System.getenv("REMOTE"));
	private static final boolean headless = Boolean
			.parseBoolean(System.getenv("HEADLESS"));
	public WebDriver driver;
	public WebDriverWait wait;
	public Actions actions;
	public Alert alert;
	public JavascriptExecutor js;
	public TakesScreenshot screenshot;
	@SuppressWarnings("unused")
	private static String handle = null;

	public int scriptTimeout = 5;
	public int flexibleWait = 30;
	public int implicitWait = 1;
	public int pollingInterval = 500;
	@SuppressWarnings("unused")
	private static long highlightInterval = 100;

	@DataProvider(name = "browser-provider", parallel = true)
	public Object[] provide() throws Exception {
		return new Object[] { "firefox", "chrome" };
	}

	private static final Map<String, String> browserDrivers = new HashMap<>();
	static {
		browserDrivers.put("chrome",
				osName.equals("windows") ? "chromedriver.exe" : "chromedriver");
		browserDrivers.put("firefox",
				osName.equals("windows") ? "geckodriver.exe" : "geckodriver");
		browserDrivers.put("edge", "MicrosoftWebDriver.exe");
	}

	private static final Map<String, String> browserDriverSystemProperties = new HashMap<>();
	static {
		browserDriverSystemProperties.put("chrome", "webdriver.chrome.driver");
		browserDriverSystemProperties.put("firefox", "webdriver.gecko.driver");
		browserDriverSystemProperties.put("edge", "webdriver.edge.driver");
	}
	public String baseURL = "http://google.com";

	@Test(dataProvider = "browser-provider", threadPoolSize = 2)
	public void googleSearchTest(String browser) {

		System.err.println("Launching " + browser + (remote ? " remotely" : ""));
		System.setProperty(browserDriverSystemProperties.get(browser),
				Paths.get(System.getProperty("user.home")).resolve("Downloads")
						.resolve(browserDrivers.get(browser)).toAbsolutePath().toString());
		if (browser.equals("chrome")) {
			DesiredCapabilities capabilities = DesiredCapabilities.chrome();
			ChromeOptions chromeOptions = new ChromeOptions();
			// options for headless
			if (headless) {
				for (String optionAgrument : (new String[] { "headless",
						"window-size=1200x800" })) {
					chromeOptions.addArguments(optionAgrument);
				}
			}
			capabilities
					.setBrowserName(DesiredCapabilities.chrome().getBrowserName());
			DriverWrapper.add(remote ? "remote" : "chrome", capabilities);
			// new exception from e.g. ChromeDriver 2.44:
			// session not created: Chrome version must be >= 69.0.3497.0
		} else if (browser.equals("firefox")) {
			System
					.setProperty("webdriver.firefox.bin",
							osName.equals("windows") ? new File(
									"c:/Program Files (x86)/Mozilla Firefox/firefox.exe")
											.getAbsolutePath()
									: "/usr/bin/firefox");
			DesiredCapabilities capabilities = DesiredCapabilities.firefox();
			if (!remote) {
				capabilities.setCapability("marionette", false);
				// NOTE: toggling marionette with remote Vagrant hub-proxied browser
				// leads
				// to the exception org.openqa.selenium.WebDriverException:
				// Timed out waiting 45 seconds for Firefox to start.
			}
			DriverWrapper.add(remote ? "remote" : "firefox", capabilities);
		}
		driver = DriverWrapper.current();
		driver.get(baseURL);
		actions = new Actions(driver);

		driver.manage().timeouts().setScriptTimeout(scriptTimeout,
				TimeUnit.SECONDS);
		// Declare a wait time
		wait = new WebDriverWait(driver, flexibleWait);

		wait.pollingEvery(Duration.ofMillis(pollingInterval));
		// Selenium Driver version sensitive code: 3.13.0 vs. 3.8.0 and older have
		// different signature
		// wait.pollingEvery(pollingInterval, TimeUnit.MILLISECONDS);

		screenshot = ((TakesScreenshot) driver);
		js = ((JavascriptExecutor) driver);
		// driver.manage().window().maximize();

		driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
		WebElement element = wait.until(
				ExpectedConditions.visibilityOf(driver.findElement(By.name("q"))));
		// TODO: element.setAttribute("value", "Тестовое задание");
		element.sendKeys("Тестовое задание");
		element = wait.until(
				// TODO; exercise culture // Поиск в Google | Google Search
				ExpectedConditions.visibilityOf(driver.findElement(
						By.xpath(String.format("//input[@value='%s']", "Google Search")))));
		element.click();
		element = wait.until(ExpectedConditions
				.visibilityOf(driver.findElement(By.id("resultStats"))));
		// occasionally observed in a parallel run:
		// org.openqa.selenium.NoSuchSessionException: invalid session id
		assertThat(element, notNullValue());
		/*
		    SelenideDriver driver = new SelenideDriver(new SelenideConfig()
		            .browser(browser)
		            .driverManagerEnabled(true)
		            .remote("http://selenoid:4444/wd/hub")
		            .headless(true));
		    driver.open("http://google.com");
		    driver.$(By.name("q")).setValue("Тестовое задание");
		    driver.$(By.xpath("//input[@value='Поиск в Google']")).click();
		    driver.$(By.id("resultStats")).shouldBe(Condition.visible);
		    driver.close();
		*/
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
		// driver.get("about:blank");
		if (driver != null) {
			try {
				driver.close();
				driver.quit();
			} catch (Exception e) {
			}
		}
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
