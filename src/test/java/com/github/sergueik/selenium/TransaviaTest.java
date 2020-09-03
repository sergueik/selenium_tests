package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Selected test scenarios for Selenium WebDriver
 * based on: http://software-testing.ru/forum/index.php?/topic/37443-sajt-opredeliaet-webdriver-kak-robota/
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class TransaviaTest extends BaseTest {

	private static String baseURL = "https://www.transavia.com/en-EU/home/";
	private static String selector = "form#contact_form > fieldset div.form-group div.input-group textarea.form-control";
	private static final StringBuffer verificationErrors = new StringBuffer();
	private final String osName = super.getOSName();
	private Map<String, String> browserDrivers = new HashMap<>();
	private Map<String, String> browserDriverSystemProperties = new HashMap<>();
	private DriverWrapper driverWrapper = new DriverWrapper();

	@BeforeClass
	@Override
	public void beforeClass() throws IOException {

		browserDrivers.put("chrome",
				osName.equals("windows") ? "chromedriver.exe" : "chromedriver");
		browserDrivers.put("firefox",
				osName.equals("windows") ? "geckodriver.exe" : "geckodriver");
		browserDrivers.put("edge", "MicrosoftWebDriver.exe");
		browserDriverSystemProperties.put("chrome", "webdriver.chrome.driver");
		browserDriverSystemProperties.put("firefox", "webdriver.gecko.driver");
		browserDriverSystemProperties.put("edge", "webdriver.edge.driver");

		String browser = super.getBrowser();

		boolean remote = true;
		boolean headless = false;
		// browser = "firefox";
		DriverWrapper.setHubUrl("http://127.0.0.1:4444/wd/hub");
		// run remotely, while the BaseClass runs locally
		System.err.println("Launching " + browser);
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
		driver = driverWrapper.current();

	}

	@SuppressWarnings("deprecation")
	@BeforeMethod
	public void BeforeMethod(Method method) {
		super.beforeMethod(method);
		// override

		wait = new WebDriverWait(driver, 30);
		wait.pollingEvery(Duration.ofMillis((long) 300));
		driver.get(baseURL);
		ExpectedCondition<Boolean> urlChange = driver -> driver.getCurrentUrl()
				.matches(String.format("^%s.*", baseURL));
		wait.until(urlChange);
		System.err.println("Current  URL: " + driver.getCurrentUrl());
	}

	@AfterMethod
	public void AfterMethod(ITestResult result) {
		if (verificationErrors.length() != 0) {
			throw new RuntimeException(String.format("Error(s) in the method %s : %s",
					result.getMethod().getMethodName(), verificationErrors.toString()));
		}
		driver.get("about:blank");
	}

	@Test(enabled = true)
	// Дальше я должен выбирать направление откуда и куда и нажимать поиск.
	// Если я почищю куки и буду заходить на http://transavia.com/ , то мне
	// предлогает выбрать регион.
	public void acknowledgeCookieTest() {
		// wait for the server pocessing to take place
		sleep(1000);
		String pageTitle = driver.getTitle();
		System.err.println("page: " + pageTitle);// Sorry to interrupt.
		if (pageTitle.matches("Sorry to interrupt.*")) {
			// System.err.println("page: " + driver.getPageSource());
			WebElement bannerElement = driver
					.findElement(By.cssSelector("div.info-banner-blocker"));
			assertThat(bannerElement, notNullValue());
			try {
				System.err
						.println("outerHTML: " + bannerElement.getAttribute("outerHTML"));
				highlight(bannerElement);
			} catch (StaleElementReferenceException e) {
				/*
				org.openqa.selenium.StaleElementReferenceException:
					Element not found in the cache - perhaps the page has changed since it was looked up
					*/
				// ignore
			}
			bannerElement.click();
			WebElement parentElement = bannerElement.findElement(By.xpath(".."));
			System.err.println(
					"Parent Element: " + parentElement.getAttribute("outerHTML"));
			List<WebElement> buttons = parentElement
					.findElements(By.cssSelector("button"));
			System.err.println(String.format("See %d buttons", buttons.size()));
			assertThat(buttons.size(), greaterThan(1));
			final String text = "Accept all cookies";
			// stream with debugging first,
			buttons.stream().map(_button -> {
				System.err
						.println("Button Element: " + _button.getAttribute("outerHTML"));
				/*
				Button Element: 
					<button onclick="_stCookiePopup.send_popup_accept()" class="button info-banner-button button-call-to-actions info-banner-neg-expandible">Accept all cookies</button>
				*/
				return _button;
			}).filter(_button -> {
				return (boolean) (_button.getText().indexOf(text) > -1);
			}).forEach(_button -> {
				highlight(_button);
				_button.click();
			});
			sleep(300000);
			if (false) {
				// not reached
				try {
					WebElement buttonElement = wait.until(ExpectedConditions
							.visibilityOf(parentElement.findElement(By.cssSelector(
									String.format("//button[contains(text(), '%s')]", text)))));
					assertThat(buttonElement, notNullValue());
					flash(buttonElement);
					buttonElement.click();
				} catch (InvalidSelectorException e) {
					// Caused by: org.openqa.selenium.InvalidSelectorException:
					// InvalidSelectorError: An invalid or illegal selector was specified
					// The given selector
					// "//button[contains(text(), 'Accept all cookies')]"
					// is either invalid or does not result in a WebElement.

					// ignore - left as an exercise for later
				}

			}
		} else {
			// there is no banner
			System.err.println("Appears that there is no banner: current  URL: "
					+ driver.getCurrentUrl());
			sleep(1000);
			TakesScreenshot screenshot = ((TakesScreenshot) driver);

			File screenshotFile = screenshot.getScreenshotAs(OutputType.FILE);
			// Move image file to new destination
			try {
				FileUtils.copyFile(screenshotFile, new File("c:\\temp\\UserID.jpg"));
				System.err.println("Screen shot saved.");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println("Exception (ignored): " + e.toString());
			}
		}
	}
}
