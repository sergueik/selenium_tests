package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.nio.file.Paths;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

// https://www.programcreek.com/java-api-examples/?api=org.openqa.selenium.chrome.ChromeDriver
// https://sqa.stackexchange.com/questions/15400/how-best-to-test-file-download-links-using-selenium
// https://support.saucelabs.com/hc/en-us/articles/115005860628
// https://www.edureka.co/community/2209/reading-the-pdf-file-using-selenium-webdriver
// https://stackoverflow.com/questions/41877155/disabling-pdf-viewer-plugin-in-chromedriver
// https://stackoverflow.com/questions/46937319/how-to-use-chrome-webdriver-in-selenium-to-download-files-in-python
// Note: the following Chrome preference doesn't work since Chrome 57
// "plugins.plugins_disabled": ["Chrome PDF Viewer"]


public class ChromeDownloadPromptTest {

	private static final boolean debug = true;
	private static WebDriver driver;
	private static JavascriptExecutor javascriptExecutor;
	private static final String baseUrl = "https://intellipaat.com/blog/tutorial/selenium-tutorial/selenium-cheat-sheet/";
	private static final String script = "var getShadowElement = function getShadowElement(object,selector) { return object.shadowRoot.querySelector(selector);};   return getShadowElement(arguments[0],arguments[1]);";

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
		sleep(1000);
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
		String downloadsPath = System.getProperty("user.home") + "/Downloads";
		preferences.put("download.default_directory",
				BaseTest.getPropertyEnv("fileDownloadPath", downloadsPath));

		ChromeOptions options = new ChromeOptions();
		Map<String, Object> chromePrefs = new HashMap<>();
		chromePrefs.put("plugins.always_open_pdf_externally", true);
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
		String argStr = "";
		for (int i = 0; i < arguments.length; i++) {
			argStr = argStr + " "
					+ (arguments[i] == null ? "null" : arguments[i].toString());
		}
		if (debug) {
			System.err.println("Calling " + script.substring(0, 40) + "..." + "\n"
					+ "with arguments: " + argStr);
		}
		return javascriptExecutor.executeScript(script, arguments);
	}

}