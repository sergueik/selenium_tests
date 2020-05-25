package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

// converted from Python test
// https://github.com/sergueik/powershell_selenium/blob/master/python/hide_blinkee.py
public class HideBlinkeeTest {

	public int scriptTimeout = 5;
	public int flexibleWait = 60; // too long
	public int implicitWait = 1;
	public int pollingInterval = 500;
	private WebDriver driver;
	private WebDriverWait wait;
	@SuppressWarnings("unused")
	private JavascriptExecutor js;
	private static final String osName = BaseTest.getOSName();
	private static final String driverBinary = osName.equals("windows")
			? "chromedriver.exe" : "chromedriver";
	private static final String baseURL = "https://blinkee.com";
	private static final String xpath = "//img[@class=\"theme_logo\"]";
	private static WebElement element = null;
	private final String chromeDriverPath = Paths
			.get(System.getProperty("user.home")).resolve("Downloads")
			.resolve(driverBinary).toAbsolutePath().toString();
	private static final boolean disableDynamic = false; // true;

	@SuppressWarnings("deprecation")
	@BeforeMethod
	public void BeforeMethod() {
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		System.setProperty("webdriver.chrome.driver", chromeDriverPath);
		ChromeOptions chromeOptions = new ChromeOptions();
		if (osName.equals("windows")) {
			if (System.getProperty("os.arch").contains("64")) {
				String[] paths = new String[] {
						"C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe",
						"C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe" };
				// probe file existence
				for (String path : paths) {
					File exe = new File(path);
					System.err.println("Inspecting browser path: " + path);
					if (exe.exists()) {
						chromeOptions.setBinary(path);
					}
				}
			} else {
				chromeOptions.setBinary(
						"c:\\Program Files\\Google\\Chrome\\Application\\chrome.exe");
			}
		}
		Map<String, Object> chromePrefs = new HashMap<>();
		chromePrefs.put("profile.default_content_settings.popups", 0);

		chromePrefs.put("profile.managed_default_content_settings.javascript", 2);
		chromePrefs.put("profile.managed_default_content_settings.images", 2);
		chromePrefs.put("profile.managed_default_content_settings.mixed_script", 2);
		chromePrefs.put("profile.managed_default_content_settings.media_stream", 2);
		chromePrefs.put("profile.managed_default_content_settings.stylesheets", 2);

		if (disableDynamic) {
			chromeOptions.setExperimentalOption("prefs", chromePrefs);
		}
		for (String optionAgrument : (new String[] {
				"--user-agent=Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20120101 Firefox/33.0",
				"--allow-running-insecure-content", "--allow-insecure-localhost",
				"--enable-local-file-accesses", "--disable-notifications",
				"--disable-save-password-bubble", "--disable-default-app",
				"disable-infobars", "--no-sandbox ", "--browser.download.folderList=2",
				"--disable-web-security", "--disable-translate",
				"--disable-popup-blocking", "--ignore-certificate-errors",
				"--no-proxy-server",
				"--browser.helperApps.neverAsk.saveToDisk=image/jpg,text/csv,text/xml,application/xml,application/vnd.ms-excel,application/x-excel,application/x-msexcel,application/excel,application/pdf", })) {
			chromeOptions.addArguments(optionAgrument);
		}
		capabilities.setCapability("chrome.binary", chromeDriverPath);

		capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);

		try {
			driver = new ChromeDriver(capabilities);
		} catch (SessionNotCreatedException e) {
			throw new RuntimeException(e.toString());
		}
		assertThat(driver, notNullValue());
		driver.get(baseURL);
		// legacy lambda cutom wait condition.
		// NOTE: wrone match appears to lock the browser
		Wait<WebDriver> wait = new FluentWait<>(driver)
				.withTimeout(flexibleWait, TimeUnit.SECONDS)
				.pollingEvery(pollingInterval, TimeUnit.SECONDS)
				.ignoring(NoSuchElementException.class);
		wait.until(ExpectedConditions.urlToBe(baseURL));
	}

	@AfterMethod
	public void AfterMethod(ITestResult result) {
		System.err.println("Closing browser after method " + result.getTestName());
		if (driver != null) {
			try {
				driver.get("about:blank");
				driver.close();
				driver.quit();
			} catch (Exception e) {
			}
		}
	}

	// https://stackoverflow.com/questions/3813294/how-to-get-element-by-innertext
	@Test(enabled = true)
	public void test() {
		element = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
		assertThat(element, notNullValue());
		System.err
				.println(String.format("Found\n%s", element.getAttribute("outerHTML")));
		assertThat(element.isDisplayed(), is(true));
	}
}
