package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Set;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;

import org.json.JSONException;
import org.json.JSONObject;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.testng.ITestContext;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import com.github.sergueik.selenium.BaseTest;

public class BrowserLogsTest {

	private static final String osName = BaseTest.getOSName();
	private RemoteWebDriver driver;
	private final boolean debug = true;
	private static final String filePath = "logger.html";
	private WebDriverWait wait;

	@BeforeSuite(alwaysRun = true)
	public void setupBeforeSuite(ITestContext context) throws InterruptedException, MalformedURLException {
		DesiredCapabilities capabilities;
		LoggingPreferences loggingPreferences = new LoggingPreferences();

		System.setProperty("webdriver.chrome.driver", Paths.get(System.getProperty("user.home")).resolve("Downloads")
				.resolve(osName.equals("windows") ? "chromedriver.exe" : "chromedriver").toAbsolutePath().toString());
		capabilities = DesiredCapabilities.chrome();
		loggingPreferences.enable(LogType.BROWSER, Level.ALL);
		capabilities.setCapability(CapabilityType.LOGGING_PREFS, loggingPreferences);
		driver = new ChromeDriver(capabilities);
		// driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 5);
		driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	}

	@Test(description = "Opens the local file", enabled = true)
	public void consoleLogTest() {
		driver.navigate().to(getPageContent(filePath));
		WebElement element = driver.findElement(By.cssSelector("input[name=\"clock\"]"));
		final String script = "console.log('Test from client: ' + arguments[0].value); return";
		sleep(10000);
		executeScript(script, element);
	}

	@AfterTest(alwaysRun = true, enabled = true)
	public void afterTest() {
		if (driver != null) {
			// hanging ?
			analyzeLog("After Test");
		}
	}

	@AfterSuite(alwaysRun = true, enabled = true)
	public void cleanupSuite() {
		if (driver != null) {
			analyzeLog("After Suite");
			driver.close();
			driver.quit();
		}
	}

	public void analyzeLog(String context) {
		LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
		// TODO: sqlite ? ELK ?
		if (debug) {
			System.err.println(String.format("Analyze log %s:", context));
			for (LogEntry entry : logEntries) {
				System.err.println("time stamp: " + new Date(entry.getTimestamp()) + "\t" + "log level: "
						+ entry.getLevel() + "\t" + "message: " + entry.getMessage());
			}

		}

	}

	@SuppressWarnings("unused")
	private static JSONObject extractObject(HttpResponse httpResponse) throws IOException, JSONException {
		InputStream contents = httpResponse.getEntity().getContent();
		StringWriter writer = new StringWriter();
		IOUtils.copy(contents, writer, "UTF8");
		return new JSONObject(writer.toString());
	}

	public static String resolveEnvVars(String input) {
		if (null == input) {
			return null;
		}
		final Pattern p = Pattern.compile("\\$(?:\\{(?:env:)?(\\w+)\\}|(\\w+))");
		final Matcher m = p.matcher(input);
		final StringBuffer sb = new StringBuffer();
		while (m.find()) {
			String envVarName = null == m.group(1) ? m.group(2) : m.group(1);
			String envVarValue = System.getenv(envVarName);
			m.appendReplacement(sb, null == envVarValue ? "" : envVarValue.replace("\\", "\\\\"));
		}
		m.appendTail(sb);
		return sb.toString();
	}

	// NOTE: cannot make this version static
	protected String getPageContent(String pagename) {
		try {
			URI uri = this.getClass().getClassLoader().getResource(pagename).toURI();
			if (debug) {
				System.err.println("Testing local file: " + uri.toString());
			}
			return uri.toString();
		} catch (URISyntaxException | NullPointerException e) {
			if (debug) {
				// mask the exception when debug
				return String.format("file:///%s/target/test-classes/%s", System.getProperty("user.dir"), pagename);
			} else {
				throw new RuntimeException(e);
			}
		}
	}

	public Object executeScript(String script, Object... arguments) {
		if (driver instanceof JavascriptExecutor) {
			JavascriptExecutor javascriptExecutor = JavascriptExecutor.class.cast(driver);
			return javascriptExecutor.executeScript(script, arguments);
		} else {
			throw new RuntimeException("Script execution failed.");
		}
	}

	public void sleep(Integer milliSeconds) {
		try {
			Thread.sleep((long) milliSeconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}