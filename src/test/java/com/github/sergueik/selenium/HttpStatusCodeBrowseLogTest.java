package com.github.sergueik.selenium;

import java.nio.file.Paths;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;

import org.json.JSONException;
import org.json.JSONObject;
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
// based on: 
// https://github.com/smatei/SeleniumChromeHTTPResponse/blob/master/java/ChromeResponseCode.java
// for finding the HTTP Status 
// through scanning the logs with Selenium 3.x
// see the negative verdict to core Selenium API:
// https://stackoverflow.com/questions/6509628/how-to-get-http-response-code-using-selenium-webdriver

public class HttpStatusCodeBrowseLogTest {
	private WebDriver driver;
	private static int status = -1;
	private JSONObject json;
	private JSONObject message;
	private String method;
	private static LogEntries logEntries;
	private static LogEntry logEntry;
	private static final String url = "http://www.wikipedia.org";
	private static JSONObject params;
	private static JSONObject response;
	private static final boolean debug = false;

	@SuppressWarnings("deprecation")
	@BeforeMethod
	public void setUp() {
		System.setProperty("webdriver.chrome.driver",
				Paths.get(System.getProperty("user.home")).resolve("Downloads")
						.resolve("chromedriver").toAbsolutePath().toString());
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		LoggingPreferences loggingPreferences = new LoggingPreferences();
		try {
			loggingPreferences.enable(LogType.PERFORMANCE, Level.ALL);
		} catch (org.openqa.selenium.InvalidArgumentException e) {
			System.err.println("Exception (ignored): " + e.toString());
		}
	//	loggingPreferences.enable(LogType.BROWSER, Level.ALL);
		capabilities.setCapability(CapabilityType.LOGGING_PREFS,
				loggingPreferences);

		driver = new ChromeDriver(capabilities);
	}

	@AfterMethod
	public void tearDown() throws JSONException {
		analyzeLog();
		driver.quit();
	}

	public void analyzeLog() throws JSONException {
		String currentURL = driver.getCurrentUrl();
		try {
			logEntries = driver.manage().logs()
					.get(LogType.PERFORMANCE /* or simply
																		"performance" */);
			for (Iterator<LogEntry> it = logEntries.iterator(); it.hasNext();) {
				logEntry = it.next();

				json = new JSONObject(logEntry.getMessage());
				if (debug)
					System.err.println(json.toString());

				method = json.getJSONObject("message").getString("method");

				if (method != null && "Network.responseReceived".equals(method)) {
					params = message.getJSONObject("params");
					response = params.getJSONObject("response");
					if (currentURL.equals(response.getString("url"))) {
						status = response.getInt("status");

						System.out.println(String.format(
								"Response code for %s is: %s with headers %s", currentURL,
								response.getInt("status"), response.get("headers")));
					}
				}
			}
		} catch (org.openqa.selenium.InvalidArgumentException e) {
			// org.openqa.selenium.InvalidArgumentException: invalid argument:
			// log type 'performance' not found
			System.err.println("Exception (ignored): " + e.toString());
		}
		logEntries = driver.manage().logs().get(LogType.BROWSER);

		for (

		LogEntry entry : logEntries) {
			System.err.println("ENTRY: TIMESTAMP: " + new Date(entry.getTimestamp())
					+ " LEVEL: " + entry.getLevel() + " MESSAGE: " + entry.getMessage());
			// do something useful with the data
		}
	}

	@Test
	public void testMethod() {
		System.out.println("Navigate to " + url);
		driver.navigate().to(url);
		// do something on page
	}
}
