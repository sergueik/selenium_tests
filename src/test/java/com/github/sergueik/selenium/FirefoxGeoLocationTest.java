package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;

import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.openqa.selenium.TimeoutException;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

// based on: https://techyworks.blogspot.com/2013/10/change-geolocation-using-selenium.html
public class FirefoxGeoLocationTest {
	protected static String osName = BaseTest.getOSName();
	public WebDriver driver;
	public WebDriverWait wait;
	public Actions actions;
	public Alert alert;
	public JavascriptExecutor js;
	public TakesScreenshot screenshot;
	private static String baseURL = "https://www.w3schools.com/html/tryit.asp?filename=tryhtml5_geolocation";

	@BeforeClass
	public void beforeClass() {
		System.setProperty("webdriver.gecko.driver",
				osName.equals("windows")
						? new File(String.format("%s/Downloads/geckodriver",
								System.getenv("HOME"))).getAbsolutePath()
						: Paths.get(System.getProperty("user.home")).resolve("Downloads")
								.resolve("geckodriver").toAbsolutePath().toString());
		System.setProperty("webdriver.firefox.bin",
				osName.equals("windows")
						? new File("c:/Program Files (x86)/Mozilla Firefox/firefox.exe")
								.getAbsolutePath()
						: "/usr/bin/firefox");

		FirefoxProfile profile = new FirefoxProfile();

		profile.setPreference("geo.wifi.uri", getPageContent("geoLocation.json"));
		DesiredCapabilities capabilities = DesiredCapabilities.firefox();
		capabilities.setCapability(FirefoxDriver.PROFILE, profile);
		capabilities.setCapability("locationContextEnabled", true);
		capabilities.setCapability("marionette", false);
		driver = new FirefoxDriver(capabilities);
		wait = new WebDriverWait(driver, 10);
		actions = new Actions(driver);
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		driver.get(baseURL);
	}

	@Test(enabled = true)
	public void testResultFramePresent() {
		// Arrange
		List<WebElement> iframes = driver
				.findElements(By.cssSelector("div#iframewrapper iframe"));
		Map<String, Object> iframesMap = new HashMap<>();
		for (WebElement iframe : iframes) {
			String key = String.format("id: \'%s\", name: \"%s\"",
					iframe.getAttribute("id"), iframe.getAttribute("name"));
			System.err.println(String.format("Found iframe %s", key));
			iframesMap.put(key, iframe);
		}
	}

	@Test(enabled = true)
	public void test1() {
		/*
		if (!BaseTest.getBrowser().matches("firefox")) {
			System.err.println("This test is only working with firefox");
			return;
		}
		*/
		WebElement resultIframe = wait
				.until(ExpectedConditions.visibilityOfElementLocated(
						By.cssSelector("iframe[name='iframeResult']")));
		assertThat(resultIframe, notNullValue());

		// Act
		WebDriver iframe = driver.switchTo().frame(resultIframe);
		WebElement element = iframe
				.findElement(By.cssSelector("button[onclick=\"getLocation()\"]"));
		assertThat(element, notNullValue());
		System.err.println(
				String.format("Found element %s", element.getAttribute("outerHTML")));
		element.click();
		sleep(10000);
	}

	@AfterClass
	public void afterClass() {
		if (driver != null) {
			try {
				driver.close();
				driver.quit();
			} catch (Exception e) {
			}
		}
	}

	public void sleep(Integer milliSeconds) {
		try {
			Thread.sleep((long) milliSeconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected String getPageContent(String pagename) {
		try {
			System.err.println("loading " + pagename + " with "
					+ this.getClass().getClassLoader().getResource(pagename));
			URI uri = BaseTest.class.getClassLoader().getResource(pagename).toURI();
			System.err.println("Testing local file: " + uri.toString());
			return uri.toString();
		} catch (URISyntaxException e) { // NOTE: multi-catch statement is not
			// supported in -source 1.6
			throw new RuntimeException(e);
		}
	}

}
