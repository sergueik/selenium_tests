package com.github.sergueik.selenium;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonObject.Member;
import com.eclipsesource.json.JsonValue;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.CoreMatchers.containsString;
import static org.testng.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.sergueik.selenium.BaseTest;

// https://www.programcreek.com/java-api-examples/?api=org.openqa.selenium.chrome.ChromeDriver
// https://sqa.stackexchange.com/questions/15400/how-best-to-test-file-download-links-using-selenium
// chrome://downloads
// const docs = document
// .querySelector('downloads-manager')
// .shadowRoot.querySelector('#downloads-list')
// .getElementsByTagName('downloads-item');

// https://support.saucelabs.com/hc/en-us/articles/115005860628
// https://www.edureka.co/community/2209/reading-the-pdf-file-using-selenium-webdriver
public class ChromeDownloadPromptTest {
	private static final boolean debug = true;
	private static WebDriver driver;
	private static final String baseUrl = "https://intellipaat.com/blog/tutorial/selenium-tutorial/selenium-cheat-sheet/";
	// private static final String script = "const docs =
	// document.querySelector('downloads-manager').shadowRoot.querySelector('#downloadsList').shadowRoot.getElementsByTagName('downloads-item');";
	private static final String script = "var getShadowElement = function getShadowElement(object,selector) { return object.shadowRoot.querySelector(selector);};   return getShadowElement(arguments[0],arguments[1]);";
	private static JavascriptExecutor javascriptExecutor;

	@BeforeClass
	public void loadPage() throws IOException {
		driver = createDriver();
		javascriptExecutor = JavascriptExecutor.class.cast(driver);

	}

	@Test(enabled = true)
	public void listPluginsTest() {
		driver.navigate().to("chrome://downloads/");
		WebElement element = driver.findElement(By.tagName("downloads-manager"));
		Object result1 = executeScript(script, element, "#downloadsList");
		assertThat(result1, notNullValue());
		if (debug) {
			System.err.println("Result is: " + result1);
		}
		WebElement element2 = (WebElement) result1;
		System.err.println("Result element: " + element2.getAttribute("outerHTML"));

		Object result2 = executeScript(script,
				element2.findElement(By.tagName("downloads-item")), "div#details");
		assertThat(result2, notNullValue());
		if (debug) {
			System.err.println("Result is: " + result2);
		}
		WebElement element3 = (WebElement) result2;
		System.err.println("Result element: " + element3.getAttribute("outerHTML"));
		WebElement element4 = element3.findElement(By.cssSelector("span#name"));
		assertThat(element4, notNullValue());
		System.err.println("Result element: " + element4.getAttribute("outerHTML"));
		final String element4HTML = element4.getAttribute("innerHTML");
		System.err.println("Inspecting element: " + element4HTML);
		assertThat(element4HTML, containsString("Selenium-Cheat-Sheet"));
		// NOTE: the getText() is failing
		try {
			assertThat(element4.getText(), containsString("Selenium-Cheat-Sheet"));
		} catch (AssertionError e) {
			System.err.println("Exception (ignored) " + e.toString());
		}
		// can be OS-specific: "Selenium-Cheat-Sheet (10).pdf"

		Pattern pattern = Pattern.compile(
				String.format(".*Selenium-Cheat-Sheet(?:%s)*.pdf", " \\((\\d+)\\)"),
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(element4HTML);
		assertThat(matcher.find(), is(true));
		assertThat(pattern.matcher(element4HTML).find(), is(true));
		WebElement element5 = element3.findElement(By.cssSelector("a#url"));
		assertThat(element5, notNullValue());
		System.err
				.println("Inspecting element: " + element5.getAttribute("outerHTML"));

		sleep(10000);
	}

	@Test(enabled = true)
	public void downloadPDFTest() {
		driver.navigate().to(baseUrl);
		WebElement element = driver.findElement(By.xpath(
				"//*[@id=\"global\"]//a[contains(@href, \"Selenium-Cheat-Sheet.pdf\")]"));
		element.click();
		sleep(5000);
	}

	@AfterMethod
	public void afterMethod() {
		driver.get("about:blank");
	}

	@SuppressWarnings("deprecation")
	public WebDriver createDriver() {
		final String chromeDriverPath = Paths.get(System.getProperty("user.home"))
				.resolve("Downloads")
				.resolve(
						System.getProperty("os.name").toLowerCase().startsWith("windows")
								? "chromedriver.exe" : "chromedriver")
				.toAbsolutePath().toString();
		System.setProperty("webdriver.chrome.driver", chromeDriverPath);

		Map<String, Object> preferences = new Hashtable<>();
		preferences.put("profile.default_content_settings.popups", 0);
		preferences.put("download.prompt_for_download", "false");
		// https://stackoverflow.com/questions/46937319/how-to-use-chrome-webdriver-in-selenium-to-download-files-in-python
		String downloadsPath = System.getProperty("user.home") + "/Downloads";
		preferences.put("download.default_directory",
				BaseTest.getPropertyEnv("fileDownloadPath", downloadsPath));
		/* preferences.put("plugins.plugins_disabled",
				new String[] { "Adobe Flash Player", "Chrome PDF Viewer" });
				*/
		// NOTE: preference "plugins.plugins_disabled" does not work
		// expect to see that the Chrome PDF Viewer has been disabled, is not
		// https://stackoverflow.com/questions/37617061/how-to-disable-chrome-plugins-in-selenium-webdriver-using-java
		// https://bugs.chromium.org/p/chromium/issues/detail?id=528436
		preferences.put("plugins.plugins_disabled", "Chrome PDF Viewer");
		/*
		ChromeOptions options = new ChromeOptions();
		options.setExperimentalOption("prefs", preferences);
		
		options.addArguments(new String[] { "test-type" });
		options.addArguments(new String[] { "disable-extensions" });
		Map<String, Object> chromePrefs = new HashMap<>();
		chromePrefs.put("profile.default_content_settings.popups", 0);
		String downloadFilepath = System.getProperty("user.dir")
				+ System.getProperty("file.separator") + "target"
				+ System.getProperty("file.separator");
		chromePrefs.put("download.prompt_for_download", "false");
		chromePrefs.put("download.directory_upgrade", "true");
		chromePrefs.put("plugins.always_open_pdf_externally", "true");
		chromePrefs.put("download.default_directory", downloadFilepath);
		chromePrefs.put("enableNetwork", "true");
		
		boolean disableImageLoading = false;
		if (disableImageLoading) {
			// https://stackoverflow.com/questions/18657976/disable-images-in-selenium-google-chromedriver
			// https://stackoverflow.com/questions/35128850/java-selenium-chrome-driver-disable-image-loading
			// it appears the flat and structured prefs has same effect:
			chromePrefs.put("profile.managed_default_content_settings.images", 2);
			Map<String, Object> images = new HashMap<>();
			images.put("images", 2);
			chromePrefs.put("profile.default_content_settings", images);
		}
		*/
		// https://stackoverflow.com/questions/18106588/how-to-disable-cookies-using-webdriver-for-chrome-and-firefox-java
		// chromePrefs.put("profile.default_content_settings.cookies", 2);
		// no cookies are allowed

		ChromeOptions options = new ChromeOptions();
		Map<String, Object> chromePrefs = new HashMap<>();
		// https://stackoverflow.com/questions/41877155/disabling-pdf-viewer-plugin-in-chromedriver

		// 'plugins.plugins_disabled': ["Chrome PDF Viewer"], doesn't work since
		// Chrome 57
		chromePrefs.put("plugins.always_open_pdf_externally", true);
		/*
		chromePrefs.put("plugins.plugins_disabled",
				new String[] { "Chrome PDF Viewer" });
				*/
		Map<String, Object> plugin = new HashMap<>();
		plugin.put("enabled", false);
		plugin.put("name", "Chrome PDF Viewer");

		chromePrefs.put("plugins.plugins_list", Arrays.asList(plugin));

		options.setExperimentalOption("prefs", chromePrefs);

		DesiredCapabilities capabilities = DesiredCapabilities.chrome();

		capabilities.setCapability("chrome.binary", chromeDriverPath);

		capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		capabilities.setCapability(ChromeOptions.CAPABILITY, options);
		return new ChromeDriver(capabilities);
	}

	private void sleep(Integer milliSeconds) {
		try {
			Thread.sleep((long) milliSeconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private Object executeScript(String script, Object... arguments) {
		// currently unsafe err.println(arguments.length + " arguments received.");
		String argStr = "";

		for (int i = 0; i < arguments.length; i++) {
			argStr = argStr + " "
					+ (arguments[i] == null ? "null" : arguments[i].toString());
		}

		System.err.println("Calling " + script.substring(0, 40) + "..." + "\n"
				+ "with arguments: " + argStr);
		return javascriptExecutor.executeScript(script, arguments);
	}

}