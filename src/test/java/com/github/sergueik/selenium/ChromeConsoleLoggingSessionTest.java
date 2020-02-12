package com.github.sergueik.selenium;

import java.nio.file.Paths;
import java.util.Date;
import java.util.logging.Level;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ChromeConsoleLoggingSessionTest {
	private WebDriver driver;

	@BeforeMethod
	public void setUp() {
		System.setProperty("webdriver.chrome.driver", Paths.get(System.getProperty("user.home")).resolve("Downloads")
				.resolve("chromedriver").toAbsolutePath().toString());
		DesiredCapabilities caps = DesiredCapabilities.chrome();
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable(LogType.BROWSER, Level.ALL);
		caps.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
		driver = new ChromeDriver(caps);
	}

	@AfterMethod
	public void tearDown() {
		driver.quit();
	}

	public void analyzeLog() {
		LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
		for (LogEntry entry : logEntries) {
			System.out.println(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
			// do something useful with the data
		}
	}

	@Test
	public void testMethod() {
		driver.get("http://www.wikipedia.org");
		// do something on page
		analyzeLog();
	}
}