package com.github.sergueik.selenium;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Method;
import static java.lang.System.err;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Paths;

import java.text.Normalizer;

import java.net.URI;
import java.net.URISyntaxException;

import java.time.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
// NOTE:
// import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnsupportedCommandException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
//https://www.seleniumeasy.com/selenium-tutorials/how-to-run-webdriver-in-ie-browser
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.internal.Coordinates;
// NOTE: deprecated. Used in scrolltoElement
import org.openqa.selenium.internal.Locatable;

import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebElement;

// https://seleniumhq.github.io/selenium/docs/api/java/org/openqa/selenium/support/ui/FluentWait.html#pollingEvery-java.time.Duration-
// NOTE: needs java.time.Duration not the org.openqa.selenium.support.ui.Duration;

import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import javax.annotation.Nullable;

import com.rationaleemotions.ExecutionBuilder;
import com.rationaleemotions.SshKnowHow;
import com.rationaleemotions.pojo.EnvVariable;
import com.rationaleemotions.pojo.ExecResults;
import com.rationaleemotions.pojo.SSHUser;
import com.rationaleemotions.pojo.SSHUser.Builder;

import org.apache.sshd.server.auth.pubkey.AcceptAllPublickeyAuthenticator;
import org.apache.sshd.server.auth.pubkey.PublickeyAuthenticator;

import com.github.sergueik.selenium.DriverWrapper;

/**
 * Selected test scenarios for Selenium WebDriver
 * 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class BaseTest {

	public WebDriver driver;
	public WebDriverWait wait;
	public Actions actions;
	public Alert alert;
	public JavascriptExecutor js;
	public TakesScreenshot screenshot;
	private static String handle = null;

	private boolean debug = false;

	public void setDebug(boolean value) {
		this.debug = value;
	}

	public String getHandle() {
		return handle;
	}

	private String parentHandle;

	public String getParentHandle() {
		return parentHandle;
	}

	protected static final Logger log = LogManager.getLogger(BaseTest.class);

	public int scriptTimeout = 5;
	public int flexibleWait = 60; // too long
	public int implicitWait = 1;
	public int pollingInterval = 500;
	private static long highlightInterval = 100;

	public String baseURL = "about:blank";

	private List<String> chromeExtensions = new ArrayList<>();
	protected static String osName = getOSName();

	private String extensionDir = String.format(
			"%s\\AppData\\Local\\Google\\Chrome\\User Data\\Default\\Extensions",
			getPropertyEnv("USERPROFILE", "C:\\Users\\Serguei"));

	private static String browser = getPropertyEnv("webdriver.driver", "chrome");
	// use -P profile to override
	private static final boolean headless = Boolean
			.parseBoolean(getPropertyEnv("HEADLESS", "false"));

	public static String getBrowser() {
		return browser;
	}

	public static void setBrowser(String browser) {
		BaseTest.browser = browser;
	}

	private static final Map<String, String> browserDrivers = new HashMap<>();
	static {
		browserDrivers.put("chrome",
				osName.equals("windows") ? "chromedriver.exe" : "chromedriver");
		browserDrivers.put("firefox",
				osName.equals("windows") ? "geckodriver.exe" : "driver");
		browserDrivers.put("edge", "MicrosoftWebDriver.exe");
	}

	public void setExtensionDir(String value) {
		this.extensionDir = value;
	}

	public void setScriptTimeout(int value) {
		this.scriptTimeout = value;
	}

	public int getFlexibleWait() {
		return this.flexibleWait;
	}

	public void setFlexibleWait(int value) {
		this.flexibleWait = value;
	}

	public void setImplicitWait(int value) {
		this.implicitWait = value;
	}

	public void setPollingInterval(long value) {
		this.pollingInterval = (int) value;
	}

	public void setPollingInterval(int value) {
		this.pollingInterval = value;
	}

	// WARNING: do not use @Before... or @AfterSuite otherwise the descendant test
	// class may fail
	@AfterSuite
	public void afterSuite() throws Exception {
	}

	// WARMING: do not define or the descendant test class will fail
	@BeforeSuite
	public void beforeSuite() {
	}

	// https://intoli.com/blog/firefox-extensions-with-selenium/
	// without .crx extension
	public void addChromeExtension(String value) {
		this.chromeExtensions.add(value);
	}

	// https://intoli.com/blog/chrome-extensions-with-selenium/
	// https://stackoverflow.com/questions/35858679/adding-extension-to-selenium2webdriver-chrome-driver
	// https://productforums.google.com/forum/#!topic/chrome/g02KlhK12fU
	// NOTE: simpler solution for local driver exist
	// https://sites.google.com/a/chromium.org/chromedriver/capabilities#TOC-List-of-recognized-capabilities
	// alternative:
	// options = webdriver.ChromeOptions()
	// options.add_argument("--app-id = mbopgmdnpcbohhpnfglgohlbhfongabi")
	private void loadChromeExtensionsBase64Encoded(ChromeOptions chromeOptions) {
		List<String> chromeExtensionsBase64Encoded = new ArrayList<>();
		for (String extensionName : this.chromeExtensions) {
			String extensionFilePath = this.extensionDir + "\\" + extensionName
					+ ".crx";
			// err.println("About to load extension " + extensionFilePath);
			File extensionFile = new File(extensionFilePath);

			// origin:
			// http://www.oodlestechnologies.com/blogs/Encode-%26-Decode-Image-Using-Base64-encoding-and-Decoding
			// http://www.java2s.com/Code/Java/File-Input-Output/Base64encodedecodedatausingtheBase64encodingscheme.htm
			if (extensionFile.exists() && !extensionFile.isDirectory()) {
				try {
					FileInputStream extensionFileInputStream = new FileInputStream(
							extensionFile);
					byte extensionData[] = new byte[(int) extensionFile.length()];
					extensionFileInputStream.read(extensionData);

					byte[] base64EncodedByteArray = Base64.encodeBase64(extensionData);

					extensionFileInputStream.close();
					chromeExtensionsBase64Encoded.add(new String(base64EncodedByteArray));
					err.println(String.format(
							"Chrome extension successfully encoded and added: %s...",
							new String(base64EncodedByteArray).substring(0, 64)));
				} catch (FileNotFoundException e1) {
					err.println(
							"Chrome extension not found: " + extensionFilePath + " " + e1);
				} catch (IOException e2) {
					err.println("Problem with reading Chrome extension: " + e2);
				}
			}
			chromeOptions.addEncodedExtensions(chromeExtensionsBase64Encoded);
		}
	}

	// For IE Internet zones see https://github.com/allquixotic/iepmm (NOTE:
	// cryptic)
	@BeforeClass
	public void beforeClass() throws IOException {

		/*
		 * TODO: TripadvisorTest: observed user agent problem with firefox - mobile
		 * version of page is rendered, and the toast message displayed with the
		 * warning: "We noticed that you're using an unsupported browser. The
		 * TripAdvisor website may not display properly.We support the following
		 * browsers: Windows: Internet Explorer, Mozilla Firefox, Google Chrome. Mac:
		 * Safari".
		 */
		/*
		 * DesiredCapabilities caps = DesiredCapabilities.internetExplorer();
		 * caps.setCapability(InternetExplorerDriver.INITIAL_BROWSER_URL, ""); WebDriver
		 * driver = new InternetExplorerDriver(caps);
		 */

		/*
		 * err.println(String.format("%s=%s", "System.env('webdriver.driver')",
		 * System.getenv("webdriver.driver"))); System.err
		 * .println(String.format("%s=%s", "getPropertyEnv('webdriver.driver')",
		 * getPropertyEnv("webdriver.driver", "")));
		 */
		err.println("Launching " + browser);
		if (browser.equals("chrome")) {
			System.setProperty("webdriver.chrome.driver",
					osName.equals("windows")
							? (new File("c:/java/selenium/chromedriver.exe"))
									.getAbsolutePath()
							: Paths.get(System.getProperty("user.home")).resolve("Downloads")
									.resolve("chromedriver").toAbsolutePath().toString());

			DesiredCapabilities capabilities = DesiredCapabilities.chrome();
			// https://peter.sh/experiments/chromium-command-line-switches/
			ChromeOptions chromeOptions = new ChromeOptions();

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
			// https://stackoverflow.com/questions/18106588/how-to-disable-cookies-using-webdriver-for-chrome-and-firefox-java
			// chromePrefs.put("profile.default_content_settings.cookies", 2);
			// no cookies are allowed

			chromeOptions.setExperimentalOption("prefs", chromePrefs);
			// "disable-infobars" option replacement
			// to suppress "Chrome is being controlled by automated test software"
			chromeOptions.setExperimentalOption("excludeSwitches",
					Collections.singletonList("enable-automation"));
			chromeOptions.setExperimentalOption("useAutomationExtension", false);
			if (osName.equals("windows")) {
				// TODO: jni
				if (System.getProperty("os.arch").contains("64")) {
					String[] paths = new String[] {
							"C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe",
							"C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe" };
					// check file existence
					for (String path : paths) {
						File exe = new File(path);
						err.println("Inspecting browser path: " + path);
						if (exe.exists()) {
							chromeOptions.setBinary(path);
						}
					}
				} else {
					chromeOptions.setBinary(
							"c:\\Program Files\\Google\\Chrome\\Application\\chrome.exe");
				}
			}
			// see also: https://developers.google.com/recaptcha/docs/faq
			// https://peter.sh/experiments/chromium-command-line-switches/
			for (String optionAgrument : (new String[] {
					"--user-agent=Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20120101 Firefox/33.0",
					"--allow-running-insecure-content", "--allow-insecure-localhost",
					"--enable-local-file-accesses", "--disable-notifications",
					"--disable-save-password-bubble",
					/* "start-maximized" , */
					"--disable-default-app", "disable-infobars", "--no-sandbox ",
					"--browser.download.folderList=2", "--disable-web-security",
					"--disable-translate", "--disable-popup-blocking",
					"--ignore-certificate-errors", "--no-proxy-server",
					"--browser.helperApps.neverAsk.saveToDisk=image/jpg,text/csv,text/xml,application/xml,application/vnd.ms-excel,application/x-excel,application/x-msexcel,application/excel,application/pdf",
					// "--start-fullscreen",
					String.format("--browser.download.dir=%s", downloadFilepath)
					/*
					 * "--user-data-dir=/path/to/your/custom/profile",
					 * "--profile-directory=name_of_custom_profile_directory",
					 */
			})) {
				chromeOptions.addArguments(optionAgrument);
			}

			// options for headless
			if (headless) {
				for (String optionAgrument : (new String[] { "headless",
						"window-size=1200x800" })) {
					chromeOptions.addArguments(optionAgrument);
				}
			}

			// http://learn-automation.com/handle-untrusted-certificate-selenium/
			capabilities
					.setBrowserName(DesiredCapabilities.chrome().getBrowserName());
			capabilities.setCapability(
					org.openqa.selenium.chrome.ChromeOptions.CAPABILITY, chromeOptions);
			capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			// https://stackoverflow.com/questions/48851036/how-to-configure-log-level-for-selenium
			// https://stackoverflow.com/questions/28572783/no-log4j2-configuration-file-found-using-default-configuration-logging-only-er
			LoggingPreferences logPrefs = new LoggingPreferences();
			logPrefs.enable(LogType.PERFORMANCE, Level.INFO);
			logPrefs.enable(LogType.BROWSER, Level.INFO);
			logPrefs.enable(LogType.DRIVER, Level.INFO);
			/*
			 * logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
			 * logPrefs.enable(LogType.BROWSER, Level.ALL); logPrefs.enable(LogType.DRIVER,
			 * Level.ALL);
			 */
			capabilities.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);

			loadChromeExtensionsBase64Encoded(chromeOptions);
			// see also:
			// https://github.com/pulkitsinghal/selenium/blob/master/java/client/src/org/openqa/selenium/chrome/ChromeOptions.java
			// For use with RemoteWebDriver
			/*
			 * RemoteWebDriver driver = new RemoteWebDriver( new
			 * URL("http://localhost:4444/wd/hub"), capabilities);
			 */

			DriverWrapper.add("chrome", capabilities);
			driver = DriverWrapper.current();

			// driver.setLogLevel(Level.ALL);
		} else if (browser.equals("firefox")) {

			// https://developer.mozilla.org/en-US/Firefox/Headless_mode
			// 3.5.3 and later
			System.setProperty("webdriver.gecko.driver", osName.equals("windows")
					? new File("c:/java/selenium/geckodriver.exe").getAbsolutePath()
					: /* String.format("%s/Downloads/geckodriver", System.getenv("HOME")) */
					Paths.get(System.getProperty("user.home")).resolve("Downloads")
							.resolve("geckodriver").toAbsolutePath().toString());
			System
					.setProperty("webdriver.firefox.bin",
							osName.equals("windows") ? new File(
									"c:/Program Files (x86)/Mozilla Firefox/firefox.exe")
											.getAbsolutePath()
									: "/usr/bin/firefox");
			// https://github.com/SeleniumHQ/selenium/wiki/DesiredCapabilities
			DesiredCapabilities capabilities = DesiredCapabilities.firefox();
			// use legacy FirefoxDriver
			// for Firefox v.59 no longer possible ?
			capabilities.setCapability("marionette", false);
			// http://www.programcreek.com/java-api-examples/index.php?api=org.openqa.selenium.firefox.FirefoxProfile
			capabilities.setCapability("locationContextEnabled", false);
			capabilities.setCapability("acceptSslCerts", true);
			capabilities.setCapability("elementScrollBehavior", 1);
			FirefoxProfile profile = new FirefoxProfile();
			// NOTE: the setting below may be too restrictive
			// http://kb.mozillazine.org/Network.cookie.cookieBehavior
			// profile.setPreference("network.cookie.cookieBehavior", 2);
			// no cookies are allowed
			profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
					"application/octet-stream,text/csv");
			profile.setPreference("browser.helperApps.neverAsk.openFile",
					"text/csv,application/x-msexcel,application/excel,application/x-excel,application/vnd.ms-excel,image/png,image/jpeg,text/html,text/plain,application/msword,application/xml");
			// TODO: cannot find symbol: method
			// addPreference(java.lang.String,java.lang.String)location: variable
			// profile of type org.openqa.selenium.firefox.FirefoxProfile
			profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
					"text/csv,application/x-msexcel,application/excel,application/x-excel,application/vnd.ms-excel,image/png,image/jpeg,text/html,text/plain,application/msword,application/xml");
			profile.setPreference("browser.helperApps.alwaysAsk.force", false);
			profile.setPreference("browser.download.manager.alertOnEXEOpen", false);
			// http://learn-automation.com/handle-untrusted-certificate-selenium/
			profile.setAcceptUntrustedCertificates(true);
			profile.setAssumeUntrustedCertificateIssuer(true);

			// NOTE: ERROR StatusLogger No log4j2 configuration file found. Using
			// default configuration: logging only errors to the console.
			LoggingPreferences logPrefs = new LoggingPreferences();
			logPrefs.enable(LogType.PERFORMANCE, Level.INFO);
			logPrefs.enable(LogType.PROFILER, Level.INFO);
			logPrefs.enable(LogType.BROWSER, Level.INFO);
			logPrefs.enable(LogType.CLIENT, Level.INFO);
			logPrefs.enable(LogType.DRIVER, Level.INFO);
			logPrefs.enable(LogType.SERVER, Level.INFO);
			capabilities.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);

			profile.setPreference("webdriver.firefox.logfile", "/dev/null");
			// NOTE: the next setting appears to have no effect.
			// does one really need os-specific definition?
			// like /dev/null for Linux vs. nul for Windows
			System.setProperty("webdriver.firefox.logfile",
					osName.equals("windows") ? "nul" : "/dev/null");

			// no longer supported as of Selenium 3.8.x
			// profile.setEnableNativeEvents(false);
			profile.setPreference("dom.webnotifications.enabled", false);
			// optional
			/*
			 * profile.setPreference("general.useragent.override",
			 * "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20120101 Firefox/33.0");
			 */
			// err.println(System.getProperty("user.dir"));
			capabilities.setCapability(FirefoxDriver.PROFILE, profile);
			try {
				DriverWrapper.add("firefox", capabilities);
				driver = DriverWrapper.current();
				// driver.setLogLevel(FirefoxDriverLogLevel.ERROR);
			} catch (WebDriverException e) {
				e.printStackTrace();
				throw new RuntimeException(
						"Cannot initialize Firefox driver: " + e.toString());
			}
		}
		actions = new Actions(driver);

		driver.manage().timeouts().setScriptTimeout(scriptTimeout,
				TimeUnit.SECONDS);
		// Declare a wait time
		wait = new WebDriverWait(driver, flexibleWait);

		// Selenium Driver version sensitive code: 3.13.0 vs. 3.8.0 and older
		wait.pollingEvery(Duration.ofMillis(pollingInterval));
		// wait.pollingEvery(pollingInterval, TimeUnit.MILLISECONDS);

		screenshot = ((TakesScreenshot) driver);

		js = ((JavascriptExecutor) driver);
		// driver.manage().window().maximize();

		driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
		// Go to URL
		driver.get(baseURL);
	}

	@AfterClass
	public void afterClass() {

		try {
			driver.get("about:blank");
		} catch (Exception e) {
			/*
			 * org.openqa.selenium.NoSuchWindowException: no such window: target window
			 * already closed from unknown error: web view not found
			 */
		}
		if (driver != null) {
			try {
				driver.close();
				driver.quit();
			} catch (Exception e) {
				/*
				 * org.apache.commons.exec.ExecuteException: The stop timeout of 2000 ms was
				 * exceeded (Exit value: -559038737) ... at
				 * org.openqa.selenium.os.OsProcess.destroy(OsProcess.java:135) at
				 * org.openqa.selenium.os.CommandLine.destroy(CommandLine.java:153) ...
				 */
			}
		}
	}

	@BeforeMethod
	public void beforeMethod(Method method) {
		String methodName = method.getName();
		err.println("Test Name: " + methodName + "\n");
	}

	// INFO: Unable to drain process streams. Ignoring but the exception being
	// swallowed follows.
	// org.apache.commons.exec.ExecuteException:
	// The stop timeout of 2000 ms was exceeded (Exit value: -559038737)
	// aftert the test completion one discovers lefrover browser processes
	// quickly starting to consume the 100 % CPU
	// observed with a number of chrome driver, browser version combinations
	// e.g.
	// Chrome browser version: 69, driver version: 42
	@AfterMethod
	public void afterMethod() {
		// driver.get("about:blank");
	}

	@AfterTest(alwaysRun = true)
	public void afterTest() {
		if (osName.equals("windows")) {
			killProcess(browserDrivers.get(browser));
		}
		if (osName.equals("windows")) {
			purgeScopedDirs();
		}
	}

	public void highlight(WebElement element) {
		highlight(element, 100, "solid yellow");
	}

	public void highlight(WebElement element, long highlightInterval) {
		highlight(element, highlightInterval, "solid yellow");
	}

	public void highlight(WebElement element, long highlightInterval,
			String color) {
		err.println("Color: " + color);
		if (wait == null) {
			wait = new WebDriverWait(driver, flexibleWait);
		}
		// Selenium Driver version sensitive code: 3.13.0 vs. 3.8.0 and older
		// https://stackoverflow.com/questions/49687699/how-to-remove-deprecation-warning-on-timeout-and-polling-in-selenium-java-client
		wait.pollingEvery(Duration.ofMillis((int) pollingInterval));

		// wait.pollingEvery(pollingInterval, TimeUnit.MILLISECONDS);

		try {
			wait.until(ExpectedConditions.visibilityOf(element));
			executeScript(String.format("arguments[0].style.border='3px %s'", color),
					element);
			Thread.sleep(highlightInterval);
			executeScript("arguments[0].style.border=''", element);
		} catch (InterruptedException e) {
			// err.println("Exception (ignored): " + e.toString());
		}
	}

	// based on:
	// https://github.com/fudax/selenium_recorder/blob/master/src/main/java/com/star/bot/apis/JScriptCollection.java
	public void unhideElement(WebElement element) {
		/*
		 * // https://letskodeit.teachable.com/pages/practice // hide-textbox var unhide
		 * = function(element) { element.style.visibility = 'visible';
		 * element.style.height = '1px'; element.style.width = '1px';
		 * element.style.opacity = 1; } var e = document.querySelector("#hide-textbox");
		 * unhide(e);
		 */
		/*
		 * function hideElement() { var x = document.getElementById("displayed-text");
		 * //style.visibility = "hidden"; x.style.display = "none"; } function
		 * showElement() { var x = document.getElementById("displayed-text");
		 * //style.visibility = "visible"; x.style.display = "block"; }
		 */
		System.err.println("Acting on: " + element.getAttribute("outerHTML"));

		int size = 20;
		// @formatter:off
		/*
		 * executeScript( String.format( "var element = arguments[0];" +
		 * "element.style.display = 'block';" + "element.style.visibility = 'visible';"
		 * + "element.style.height = '%dpx';" + "element.style.width = '%dpx';" +
		 * "element.style.opacity = 0;", size, size), element);
		 */
		// @formatter:on
		// @formatter:off
		executeScript("var element = arguments[0];" + "element.style.display = 'block';", element);
		// @formatter:on

		System.err.println("Modified the subject element contents: "
				+ element.getAttribute("outerHTML"));
		try {
			wait.until(ExpectedConditions.visibilityOf(element));
		} catch (TimeoutException e) {
			err.println("Exception (ignored): " + e.toString());
		}
	}

	// based on: discussion of using webdriver versus container element in waits
	// (in Russian)
	// http://software-testing.ru/forum/index.php?/topic/37820-tcelesoobraznost-webdriverwait/
	public void waitVisibilityInside(final WebElement container, final By by,
			final int timeout) {
		@SuppressWarnings("unused")
		boolean status = false;
		try {
			status = (new WebDriverWait(driver, timeout))
					.until(new ExpectedCondition<Boolean>() {
						@Override
						public Boolean apply(WebDriver d) {
							List<WebElement> t = container.findElements(by);
							return (t == null || t.size() == 0);
						}
					});
		} catch (Exception e) {
			err.println("Exception: " + e.toString());
			status = true;
		}
	}

	public void highlight(By locator) throws InterruptedException {
		highlight(locator, "solid yellow");
	}

	public void highlight(By locator, String color) throws InterruptedException {
		log.info("Highlighting element {}", locator);
		WebElement element = driver.findElement(locator);
		executeScript(String.format("arguments[0].style.border='3px %s'", color),
				element);
		Thread.sleep(highlightInterval);
		executeScript("arguments[0].style.border=''", element);
	}

	// based on https://groups.google.com/forum/#!topic/selenium-users/3J27G1GxCa8
	public void setAttribute(WebElement element, String attributeName,
			String attributeValue) {
		executeScript(
				"var element = arguments[0]; var attributeName = arguments[1]; var attributeValue = arguments[2]; element.setAttribute(attributeName, attributeValue)",
				element, attributeName, attributeValue);
	}

	protected void fastSetText(String selectorOfElement, String text) {
		fastSetText(selectorOfElement, text, 0);
	}

	protected void setTargetAttribute(WebElement element) {
		String script = getScriptContent("setTargetAttribute.js");
		try {
			executeScript(script, element);
		} catch (Exception e) {
			err.println("Ignored: " + e.toString());
		}
	}

	protected void fastSetText(String selector, String text, long interval) {
		String script = getScriptContent("setValueWithSelector.js");
		try {
			executeScript(script, selector, text);
		} catch (Exception e) {
			err.println("Ignored: " + e.toString());
		}
	}

	protected void fastSetText(WebElement element, String text) {
		fastSetText(element, text, 0);
	}

	protected void fastSetText(WebElement element, String text, long interval) {
		String script = getScriptContent("setValue.js");
		try {
			wait.until(ExpectedConditions.visibilityOf(element));
			executeScript(script, element, text);
		} catch (Exception e) {
			err.println("Ignored: " + e.toString());
		}
	}

	//
	protected WebElement getParentBlockElement(WebElement element) {
		String highlightScript = getScriptContent("closestParentBlockNode.js");
		// return (WebElement) executeScript(String.format(
		// "%s\nreturn parent_block(arguments[0]);", highlightScript), element);
		return (WebElement) executeScript(highlightScript, element);
	}

	protected void highlightNew(WebElement element) {
		highlightNew(element, 100);
	}

	protected void highlightNew(WebElement element, long highlightInterval) {
		Rectangle elementRect = element.getRect();
		String highlightScript = getScriptContent("highlight.js");
		// append calling

		try {
			wait.until(ExpectedConditions.visibilityOf(element));
			executeScript(
					String.format(
							"%s\nhighlight_create(arguments[0],arguments[1],arguments[2],arguments[3]);",
							highlightScript),
					elementRect.y, elementRect.x, elementRect.width, elementRect.height);
			Thread.sleep(highlightInterval);
			executeScript(String.format("%s\nhighlight_remove();", highlightScript));
		} catch (InterruptedException e) {
			// err.println("Ignored: " + e.toString());
		}

	}

	// hover
	// https://stackoverflow.com/questions/11038638/simulate-hover-in-jquery
	public void jqueryHover(String cssSelector) {
		executeScript(
				"var selector = arguments[0]; $(selector).mouseenter().mouseleave();",
				cssSelector);
	}

	protected static String getScriptContent(String scriptName) {
		try {
			final InputStream stream = BaseTest.class.getClassLoader()
					.getResourceAsStream(scriptName);
			final byte[] bytes = new byte[stream.available()];
			stream.read(bytes);
			return new String(bytes, "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException(scriptName);
		}
	}

	public void flash(WebElement element) {
		String bgcolor = element.getCssValue("backgroundColor");
		for (int i = 0; i < 3; i++) {
			changeColor("rgb(000,0)", element);
			changeColor(bgcolor, element);
		}
	}

	public void changeColor(String color, WebElement element) {
		executeScript("arguments[0].style.backgroundColor = '" + color + "'",
				element);
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
		}
	}

	// http://www.javawithus.com/tutorial/using-ellipsis-to-accept-variable-number-of-arguments
	public Object executeScript(String script, Object... arguments) {
		if (driver instanceof JavascriptExecutor) {
			JavascriptExecutor javascriptExecutor = JavascriptExecutor.class
					.cast(driver);
			/*
			 *
			 * // currently unsafe err.println(arguments.length + " arguments received.");
			 * String argStr = "";
			 * 
			 * for (int i = 0; i < arguments.length; i++) { argStr = argStr + " " +
			 * (arguments[i] == null ? "null" : arguments[i].toString()); }
			 * 
			 * err.println("Calling " + script.substring(0, 40) + "..." + \n" + "with
			 * arguments: " + argStr);
			 */
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

	// origin:
	// https://github.com/TsvetomirSlavov/wdci/blob/master/code/src/main/java/com/seleniumsimplified/webdriver/manager/EnvironmentPropertyReader.java
	public static String getPropertyEnv(String name, String defaultValue) {
		String value = System.getProperty(name);
		if (value == null) {
			value = System.getenv(name);
			if (value == null) {
				value = defaultValue;
			}
		}
		return value;
	}

	// private static final String dirNamePattern = "scoped_dir.*";

	private static class CustomDirectoryFileFilter extends AbstractFileFilter
			implements Serializable {
		private String dirNamePattern = null;

		public void setDirNamePattern(String data) {
			this.dirNamePattern = data;
		}

		public CustomDirectoryFileFilter() {
		}

		public CustomDirectoryFileFilter(String dirNamePattern) {
			this.dirNamePattern = dirNamePattern;
		}

		@Override
		public boolean accept(final File file) {
			boolean status = file.isDirectory()
					&& file.getName().matches(dirNamePattern);
			if (status) {
				System.err
						.println(String.format("Matching item: \"%s\"", file.getName()));
			}
			return status;
		}

	}

	// delete all "scoped_dir" folders under temp
	// https://stackoverflow.com/questions/43289035/chromedriver-not-deleting-scoped-dir-in-temp-folder-after-test-is-complete
	// https://www.programcreek.com/java-api-examples/?class=org.apache.commons.io.FileUtils&method=listFilesAndDirs
	public static void purgeScopedDirs() {

		// cannot set additional properties if declared as instance of the
		// superclass type
		// AbstractFileFilter filter = new CustomDirectoryFileFilter();
		CustomDirectoryFileFilter filter = new CustomDirectoryFileFilter();
		filter.setDirNamePattern("scoped_dir.*");
		FileUtils.listFilesAndDirs(new File(System.getProperty("java.io.tmpdir")),
				(IOFileFilter) new NotFileFilter(TrueFileFilter.INSTANCE),
				/* filter  */ new BaseTest.CustomDirectoryFileFilter("scoped_dir.*"))
				.stream().forEach(f -> {
					try {
						System.err.println("About to remove: " + f.getCanonicalPath());
						// FileUtils.deleteDirectory(f);
					} catch (IOException e) {
						System.err.println("Exception (ignored): " + e.toString());
					}
				});

		FileUtils
				.listFilesAndDirs(new File(System.getProperty("java.io.tmpdir")),
						(IOFileFilter) new NotFileFilter(TrueFileFilter.INSTANCE),
						(IOFileFilter) DirectoryFileFilter.DIRECTORY)
				.stream().filter(f -> f.getName().matches("scoped_dir.*"))
				.forEach(f -> {
					try {
						System.err.println("Removing: " + f.getCanonicalPath());
						FileUtils.deleteDirectory(f);
					} catch (IOException e) {
						System.err.println("Exception (ignored): " + e.toString());
					}
				});

		// c# example
		/*
			foreach (string tempfile in Directory.GetDirectories(System.IO.Path.GetTempPath(), "scoped_dir*", SearchOption.AllDirectories)) {
				try {
					System.IO.DirectoryInfo directory = new System.IO.DirectoryInfo(tempfolder);
					foreach (System.IO.DirectoryInfo subDirectory in directory.GetDirectories())
						subDirectory.Delete(true);
				} catch (Exception e) {
					writeEx("Exception: " + e.Message);
				}
			}
		 */
	}

	// origin: https://github.com/rationaleemotions/simplessh
	// NOTE: dispatches the actual work to
	// https://github.com/torquebox/jruby-maven-plugins/blob/master/ruby-tools/src/main/java/de/saumya/mojo/ruby/script/Script.java
	// that may not be the fastest way of doing it
	public static void killRemoteProcess(String processName) {
		// TODO: actually read the "vagrant.properties" properties file
		String identityFile = getPropertyEnv("IdentityFile",
				"C:/Vagrant/.vagrant/machines/default/virtualbox/private_key");
		String hostName = getPropertyEnv("HostName", "127.0.0.1");
		String sshFolder = identityFile.replaceAll("/[^/]+$", "");
		String user = getPropertyEnv("User", "vagrant");
		int port = Integer.parseInt(getPropertyEnv("Port", "2222"));
		String command = String.format("killall %s", processName.trim());
		SSHUser sshUser = new SSHUser.Builder().forUser(user)
				.withSshFolder(new File(sshFolder))
				.usingPrivateKey(new File(identityFile)).build();
		SshKnowHow ssh = new ExecutionBuilder().connectTo(hostName).onPort(port)
				.includeHostKeyChecks(false).usingUserInfo(sshUser).build();

		ExecResults execResults = ssh.executeCommand(command);
		err.println(execResults.getOutput().toString());
	}

	// https://www.javaworld.com/article/2071275/core-java/when-runtime-exec---won-t.html?page=2
	public static void killProcess(String processName) {
		err.println("Killing the process: " + processName);

		if (processName.isEmpty()) {
			return;
		}
		// on Windows OS the only way to get the parentprocessid of a processid is
		// through wmi
		// https://stackoverflow.com/questions/33911332/powershell-how-to-get-the-parentprocessid-by-the-processid
		// that implies switch from wrapping System.Diagnostics.Proces to
		// https://docs.microsoft.com/en-us/windows/desktop/cimwin32prov/win32-process
		// the latter does not appear to be easily wrappable through jni
		// NOTE: for chrome-specific quirks for finding related processes see:
		// https://automated-testing.info/t/selenium-webdriver-ubit-proczessy-chrome-i-chromedriver-pri-ostanovki-abort-dzhoby-na-jenkins-cherez-postbildstep/22341/11
		/*
		 * 
		 * <# $process_id=5308
		 * 
		 * (get-cimInstance -class Win32_Process -filter "parentprocessid = $process_id"
		 * ).processid 4736 9496 9536 9120 1996 8876 10188 (get-process -id 4736 )
		 * .processname 'chrome' #>
		 */
		String command = String.format((osName.toLowerCase().startsWith("windows"))
				? "taskkill.exe /T /F /IM %s" : "killall %s", processName.trim());
		// /T Terminates the specified process and any child processes which were
		// started by it
		try {
			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec(command);
			// process.redirectErrorStream( true);

			BufferedReader stdoutBufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));

			BufferedReader stderrBufferedReader = new BufferedReader(
					new InputStreamReader(process.getErrorStream()));
			String line = null;

			StringBuffer processOutput = new StringBuffer();
			while ((line = stdoutBufferedReader.readLine()) != null) {
				processOutput.append(line);
			}
			StringBuffer processError = new StringBuffer();
			while ((line = stderrBufferedReader.readLine()) != null) {
				processError.append(line);
			}
			int exitCode = process.waitFor();
			// ignore exit code 128: the process "<browser driver>" not found.
			if (exitCode != 0 && (exitCode ^ 128) != 0) {
				err.println("Process exit code: " + exitCode);
				if (processOutput.length() > 0) {
					err.println("<OUTPUT>" + processOutput + "</OUTPUT>");
				}
				if (processError.length() > 0) {
					// e.g.
					// The process "chromedriver.exe"
					// with PID 5540 could not be terminated.
					// Reason: Access is denied.
					err.println("<ERROR>" + processError + "</ERROR>");
				}
			}
		} catch (Exception e) {
			err.println("Exception (ignored): " + e.getMessage());
		}
	}

	public String getResourcePath(String resourceFileName) {
		return String.format("%s/src/main/resources/%s",
				System.getProperty("user.dir"), resourceFileName);
	}

	// based on:
	// https://github.com/lopukhDA/Angular-tests-on-c-sharp-and-protractor/blob/master/NUnit.Tests1/WebDriver.cs
	public Boolean checkElementAttribute(WebElement element, String value,
			Optional<String> attributeOpt) {
		String attribute = attributeOpt.isPresent() ? attributeOpt.get() : "class";
		return (element.getAttribute(attribute).contains(value)) ? true : false;
	}

	public Boolean checkElementAttribute(WebElement element, String value,
			String... attributes) {
		String attribute = attributes.length > 0 ? attributes[0] : "class";
		return (element.getAttribute(attribute).contains(value)) ? true : false;
	}

	protected boolean areElementsPresent(WebElement parentWebElement,
			By byLocator) {
		return parentWebElement.findElements(byLocator).size() == 1;
		// usage:
		// assertTrue(areElementsPresent(driver.findElements(By.cssSelector("li[class*=
		// product]")).get(0), By.cssSelector("[class*=sticker]")));
	}

	// Scroll
	public void scroll(final int x, final int y) {
		final JavascriptExecutor js = (JavascriptExecutor) driver;
		for (int i = 0; i <= x; i = i + 50) {
			js.executeScript("scroll(" + i + ",0)");
		}
		for (int j = 0; j <= y; j = j + 50) {
			js.executeScript("scroll(0," + j + ")");
		}
	}

	// origin:
	// https://github.com/TsvetomirSlavov/JavaScriptForSeleniumMyCollection/blob/master/src/utils/UtilsQAAutoman.java
	// https://seleniumhq.github.io/selenium/docs/api/java/org/openqa/selenium/interactions/internal/Coordinates.html
	// https://www.programcreek.com/java-api-examples/index.php?api=org.openqa.selenium.interactions.internal.Coordinates
	@SuppressWarnings("deprecation")
	public void scrolltoElement(WebElement element) {
		// TODO: read property and act accordingly ?
		String seleniumVersion = System.getProperty("selenium.version", "");

		Coordinates coordinate;
		try {
			coordinate = ((Locatable) element).getCoordinates();
			// coordinate.onScreen()
			coordinate.onPage();
			coordinate.inViewPort();
		} catch (ClassCastException e) {
			err.println("Exception (ignored)" + e.toString());
			actions.moveToElement(element).build().perform();
		}

	}

	protected String cssSelectorOfElement(WebElement element) {
		return (String) executeScript(getScriptContent("cssSelectorOfElement.js"),
				element);
	}

	protected boolean detectFullScreen() {
		return (boolean) executeScript(getScriptContent("detectFullScreen.js"));
	}

	protected String styleOfElement(WebElement element, Object... arguments) {
		return (String) executeScript(getScriptContent("getStyle.js"), element,
				arguments);
	}

	protected String cssSelectorOfElementAlternative(WebElement element) {
		return (String) executeScript(
				getScriptContent("cssSelectorOfElementAlternative.js"), element);
	}

	protected String xpathOfElement(WebElement element) {
		return (String) executeScript(getScriptContent("xpathOfElement.js"),
				new Object[] { element });
	}

	protected RemoteWebElement findByCssSelectorAndInnerText(
			String elementLocator, String elementText) {
		return findByCssSelectorAndInnerText(elementLocator, elementText, false);
	}

	protected RemoteWebElement findByCssSelectorAndInnerText(String elementText) {
		return findByCssSelectorAndInnerText(null, elementText, false);
	}

	// Alternative to an XPath selector
	// "//*[contains(text(),'${text_to_find}'))"
	// or its longer and more fragile alternative:
	// "//*/text()[contains(normalize-space(translate(string(.), '\t\n\r\u00a0', '
	// ')), '${text_to_find}')]/parent::*"
	// uses core DOM API
	// https://developer.mozilla.org/en-US/docs/Web/API/Document/querySelectorAll
	// basically to scan the DOM, unless a nonempty elementLocator is provided
	// then returns the last element in the array of matching elements -
	// presumably the innermost matching element
	protected RemoteWebElement findByCssSelectorAndInnerText(
			String elementLocator, String elementText, boolean debug) {
		return (RemoteWebElement) executeScript(
				getScriptContent("findByCssSelectorAndInnerText.js"), elementLocator,
				elementText, debug);
	}

	protected String getSelectionText(boolean debug) {
		return (String) executeScript(getScriptContent("getSelectionText.js"),
				debug);
	}

	protected String getSelectionText() {
		return getSelectionText(false);
	}

	protected boolean isElementNotVisible(By locator) {
		try {
			// disable implicit wait
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
			log.info("Element {} is visible", locator);
			return false;
		} catch (Exception e) {
			log.info("Element {} is not visible", locator);
			return true;
		} finally {
			driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
		}
	}

	// based on:
	// https://testerslittlehelper.wordpress.com/2016/03/25/quick-find-element/
	protected boolean isElementPresent(By locator) {
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		List<WebElement> list = driver.findElements(locator);
		driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
		return list.size() > 0;
	}

	protected String getBodyText() {
		log.info("Getting boby text");
		return driver.findElement(By.tagName("body")).getText();
	}

	private static int instanceCount = 0;

	// based on
	// http://automated-testing.info/t/kak-ya-mogu-otkrit-v-firefox-dve-vkladki-i-perehodit-s-odnoj-na-vtoruyu-pri-neobhodimosti/1741/4
	// Creates a new window / browser tab using script injection
	// Loads a specified url there
	protected String createWindow(String url) {

		Set<String> oldHandles = driver.getWindowHandles();
		parentHandle = driver.getWindowHandle();

		// Inject an anchor element
		String name = "Window_" + instanceCount++;
		executeScript("var anchorTag = document.createElement('a'); "
				+ "anchorTag.appendChild(document.createTextNode('nwh'));"
				+ "anchorTag.setAttribute('id', arguments[0]);"
				+ "anchorTag.setAttribute('href', arguments[1]);"
				+ "anchorTag.setAttribute('target', '_blank');"
				+ "anchorTag.setAttribute('style', 'display:block;');"
				+ "var firstElement = document.getElementsByTagName('body')[0].getElementsByTagName('*')[0];"
				+ "firstElement.parentElement.appendChild(anchorTag);", name, url);
		// common error with this approach: Element is not clickable at point
		// HTML, HEAD, BODY, some element

		WebElement element = driver.findElement(By.id(name));
		sleep(1000);
		try {
			// element.getLocation()
			Point location = element.getLocation();
			err.println("Scrolling to " + location.y);
			scroll(location.x, location.y);
		} catch (UnsupportedCommandException e) {

		}
		scrolltoElement(element);
		highlight(element);
		sleep(1000);
		// Click on the anchor element

		element.click();
		Set<String> newHandles = driver.getWindowHandles();

		newHandles.removeAll(oldHandles);
		// the remaining item is the new window handle
		for (String handle : newHandles) {
			err.println("Returning hanlde: " + handle);
			return handle;
		}
		return null;
	}

	public void scrollIntoView(WebElement element) {
		scrollIntoView(element, true);
	}

	// DOM method:
	// https://developer.mozilla.org/en-US/docs/Web/API/Element/scrollIntoView
	public void scrollIntoView(WebElement element, boolean force) {
		try {
			// plain
			// executeScript("arguments[0].scrollIntoView({ behavior: \"smooth\" });",
			// element);
			// based on
			// http://www.performantdesign.com/2009/08/26/scrollintoview-but-only-if-out-of-view/
			// referenced in
			// https://stackoverflow.com/questions/6215779/scroll-if-element-is-not-visible
			//
			String result = (String) executeScript(
					getScriptContent("scrollIntoViewIfOutOfView.js"), element, debug,
					force);

			if (debug) {
				err.println("Result: " + result);
			}
			highlight(element.findElement(By.xpath("..")));
			if (debug) {
				err.println(xpathOfElement(element));
			}
		} catch (Exception e) {
			// temporarily catch all exceptions.
			err.println("Exception: " + e.toString());
		}

	}

	private void confirmHanldeNotClosed(String windowHandle) {
		if (windowHandle == null || windowHandle.equals("")) {
			throw new WebDriverException("Window/Tab was closed");
		}
	}

	protected void close(String windowHandle) {
		switchToWindow(windowHandle).close();
		handle = null;
		if (parentHandle != null) {
			driver.switchTo().window(parentHandle);
		}
	}

	protected WebDriver switchToWindow(String windowHandle) {
		confirmHanldeNotClosed(windowHandle);
		handle = windowHandle;
		return driver.switchTo().window(windowHandle);
	}

	protected WebDriver switchToParent() {
		confirmHanldeNotClosed(handle);
		return driver.switchTo().window(parentHandle);
	}

	protected String xPathToCSS(String xpath) {
		String result = null;
		try {
			result = (String) executeScript(getScriptContent("cssify.js"),
					new Object[] { xpath });
		} catch (WebDriverException e) {
		}
		return result;
	}

	// dummy: added just to smoke test the testng 6.11 to 6.14 migration
	protected String xPathToCSS(String xpath, @Nullable WebElement element) {
		return xPathToCSS(xpath);
	}

	// origin: https://github.com/RomanIovlev/Css-to-XPath-Java
	// see also: https://github.com/featurist/css-to-xpath
	// for Convert XPath to CSS selector
	// hguiney / cssify.js
	// https://gist.github.com/hguiney/3320053
	//
	public static class CssToXPath {
		public static String cssToXPath(String css) {
			if (css == null || css.isEmpty())
				return "";
			int i = 0;
			int start;
			int length = css.length();
			String result = "//";
			while (i < length) {
				char symbol = css.charAt(i);
				if (isTagLetter(symbol)) {
					start = i;
					while (i < length && isTagLetter(css.charAt(i)))
						i++;
					if (i == length)
						return result + css.substring(start);
					result += css.substring(start, i);
					continue;
				}
				if (symbol == ' ') {
					result += "//";
					i++;
					continue;
				}
				if (Arrays.asList('.', '#', '[').contains(symbol)) {
					List<String> attributes = new ArrayList<>();
					while (i < length && css.charAt(i) != ' ') {
						switch (css.charAt(i)) {
						case '.':
							i++;
							start = i;
							while (i < length && isAttrLetter(css.charAt(i)))
								i++;
							attributes.add(convertToClass(i == length ? css.substring(start)
									: css.substring(start, i)));
							break;
						case '#':
							i++;
							start = i;
							while (i < length && isAttrLetter(css.charAt(i)))
								i++;
							attributes.add(convertToId(i == length ? css.substring(start)
									: css.substring(start, i)));
							break;
						case '[':
							i++;
							String attribute = "@";
							while (i < length
									&& (!Arrays.asList('=', ']').contains(css.charAt(i)))) {
								attribute += css.charAt(i);
								i++;
							}
							if (css.charAt(i) == '=') {
								attribute += "=";
								i++;
								if (css.charAt(i) != '\'')
									attribute += "'";
								while (i < length && css.charAt(i) != ']') {
									attribute += css.charAt(i);
									i++;
								}
								if (i == length)
									throw new RuntimeException("Incorrect Css. No ']' symbol");
								if (attribute.charAt(attribute.length() - 1) != '\'')
									attribute += "'";
							}
							attributes.add(attribute);
							i++;
							break;
						default:
							throw new RuntimeException(String.format(
									"Can't process Css. Unexpected symbol %s in attributes",
									css.charAt(i)));
						}
					}
					if (result.charAt(result.length() - 1) == '/')
						result += "*";
					result += "[" + String.join(" and ", attributes) + "]";
					continue;
				}
				throw new RuntimeException(
						String.format("Can't process Css. Unexpected symbol '%s'", symbol));
			}
			return result;
		}

		private static String convertToClass(String value) {
			return "contains(@class,'" + value + "')";
		}

		private static String convertToId(String value) {
			return convertToAtribute("id", value);
		}

		private static String convertToAtribute(String attr, String value) {
			return "@" + attr + "='" + value + "'";
		}

		private static boolean isAttrLetter(char symbol) {
			return symbol >= 'a' && symbol <= 'z' || symbol >= 'A' && symbol <= 'Z'
					|| symbol >= '0' && symbol <= '9' || symbol == '-' || symbol == '_'
					|| symbol == '.' || symbol == ':';
		}

		private static boolean isTagLetter(char symbol) {
			return symbol >= 'a' && symbol <= 'z';
		}

	}

	protected String getPageContent(String pagename) {
		try {
			URI uri = BaseTest.class.getClassLoader().getResource(pagename).toURI();
			err.println("Testing local file: " + uri.toString());
			return uri.toString();
		} catch (URISyntaxException e) { // NOTE: multi-catch statement is not
			// supported in -source 1.6
			throw new RuntimeException(e);
		}
	}

	// for the pre-2.20.0 versions of Selenium Webdriver before
	// ExpectedConditions alertIsPresent becomes available
	public Alert getAlert(final long time) {
		return new WebDriverWait(driver, time, 200)
				.until(new ExpectedCondition<Alert>() {
					@Override
					public Alert apply(WebDriver d) {
						Alert alert = null;
						try {
							err.println("getAlert evaluating alert");
							alert = d.switchTo().alert();
							if (alert != null) {
								err.println("getAlert detected alert");
								return alert;
							} else {
								err.println("getAlert see no alert");
								return null;
							}
						} catch (NoAlertPresentException e) {
							err.println("getAlert see no alert");
							return null;
						}
					}
				});
	}

	// https://github.com/sergueik/selenium_tests/tree/master/src/test/java/com/github/sergueik/selenium/TariffTest.java
	public static class Translit {

		private static final Charset UTF8 = Charset.forName("UTF-8");
		private static final char[] alphabetCyrillic = { ' ', 'а', 'б', 'в', 'г',
				'д', 'е', 'ё', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р',
				'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю',
				'я', 'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л',
				'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ',
				'Ъ', 'Ы', 'Б', 'Э', 'Ю', 'Я', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
				'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
				'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
				'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
				'Y', 'Z', '<', '>', '"', ':', '(', ')', '=', '-', '.', '0', '1', '2',
				'3', '4', '5', '6', '7', '8', '9' };

		private static final String[] alphabetTranslit = { " ", "a", "b", "v", "g",
				"d", "e", "e", "zh", "z", "i", "y", "k", "l", "m", "n", "o", "p", "r",
				"s", "t", "u", "f", "h", "ts", "ch", "sh", "sch", "", "i", "", "e",
				"ju", "ja", "A", "B", "V", "G", "D", "E", "E", "Zh", "Z", "I", "Y", "K",
				"L", "M", "N", "O", "P", "R", "S", "T", "U", "F", "H", "Ts", "Ch", "Sh",
				"Sch", "", "I", "", "E", "Ju", "Ja", "a", "b", "c", "d", "e", "f", "g",
				"h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u",
				"v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I",
				"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W",
				"X", "Y", "Z", "<", ">", "\"", ":", "(", ")", "=", "-", ".", "0", "1",
				"2", "3", "4", "5", "6", "7", "8", "9" };

		public static String toAscii(final String input) {
			final CharsetEncoder charsetEncoder = UTF8.newEncoder();
			final char[] decomposed = Normalizer
					.normalize(input, Normalizer.Form.NFKD).toCharArray();
			final StringBuilder sb = new StringBuilder(decomposed.length);

			// NOTE: evaluating the character charcount is unnecessary with Cyrillic
			for (int i = 0; i < decomposed.length;) {
				final int codePoint = Character.codePointAt(decomposed, i);
				final int charCount = Character.charCount(codePoint);

				if (charsetEncoder
						.canEncode(CharBuffer.wrap(decomposed, i, charCount))) {
					sb.append(decomposed, i, charCount);
				}

				i += charCount;
			}

			StringBuilder builder = new StringBuilder();

			for (int i = 0; i < sb.length(); i++) {
				for (int x = 0; x < alphabetCyrillic.length; x++)
					if (sb.charAt(i) == alphabetCyrillic[x]) {
						builder.append(alphabetTranslit[x]);
					}
			}
			return builder.toString();

		}
	}

	public static String resolveEnvVars(String input) {
		if (null == input) {
			return null;
		}
		Pattern p = Pattern.compile("\\$(?:\\{(?:env:)?(\\w+)\\}|(\\w+))");
		Matcher m = p.matcher(input);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			String envVarName = null == m.group(1) ? m.group(2) : m.group(1);
			String envVarValue = System.getenv(envVarName);
			m.appendReplacement(sb,
					null == envVarValue ? "" : envVarValue.replace("\\", "\\\\"));
		}
		m.appendTail(sb);
		return sb.toString();
	}

	// based on:
	// https://github.com/Ardesco/Selenium-Maven-Template/blob/master/src/test/java/com/lazerycode/selenium/tests/GoogleExampleIT.java#L13
	// usage:
	// wait.until(pageTitleEndsWith("#inbox"));
	// err.println("Page title: " + driver.getTitle());
	// e.g. Page title: https://mail.google.com/mail/u/0/#inbox
	private ExpectedCondition<Boolean> pageTitleEndsWith(final String search) {
		// return java 8 lambda
		return d -> d.getTitle().toLowerCase()
				.matches("(?:" + search.toLowerCase() + ")$");
	}

	// see also:
	// https://github.com/Nordstrom/Selenium-Foundation/blob/master/src/main/java/com/nordstrom/automation/selenium/support/SearchContextWait.java
	// for extending the FluentWait allowing caller specified SearchContext

	// based on: https://github.com/yashaka/NSelene
	// see also:
	// http://software-testing.ru/forum/index.php?/topic/37987-kak-proverit-pravilnost-generiruemogo-stra/
	// says wotks with Angular protractor pure
	protected void writeDocument(String pageName) {
		openAboutBlankPage();
		String pageBody = getScriptContent(pageName);
		executeScript("document.write(arguments[0]);", pageBody); // TODO: special
		if (debug) {
			err.println("Wrote document: " + pageBody);
		}
	}

	// TODO:
	protected void openEmptyPlaceholderPage() {
		writeDocument("empty.html");
	}

	// TODO:
	protected void openAboutBlankPage() {
		driver.navigate().to("about:blank");
	}

	private static String prepareBodyHTML(String pageBody) {
		// convert body quotes and chomp the line endings
		return pageBody.replaceAll("\"", "\\\"").replaceAll("\r?\n", " ");
	}

	public void bodyInnerHTML(String pageName) {
		// TODO: cache
		openEmptyPlaceholderPage();
		String pageBody = getScriptContent(pageName);
		if (debug) {
			System.err
					.println("Writing into body element: " + prepareBodyHTML(pageBody));
		}
		executeScript(
				"document.getElementsByTagName('body')[0].innerHTML = arguments[0];",
				prepareBodyHTML(pageBody));

	}

	public void bodyInnerHTMLTimedOut(String pageName, int timeout) {
		// TODO: cache
		openEmptyPlaceholderPage();
		String pageBody = getScriptContent(pageName);
		if (debug) {
			err.println("Writing into body element: " + prepareBodyHTML(pageBody)
					+ " with a timeout " + timeout);
		}
		executeScript(
				"setTimeout(function(){ document.getElementsByTagName('body')[0].innerHTML = arguments[0];  }, arguments[1]);",
				prepareBodyHTML(pageBody), timeout);
	}

	// based on:
	// https://github.com/fudax/selenium_recorder/blob/master/src/main/java/com/star/bot/apis/WebDriverBotApis.java
	public boolean clickByJavaScript(WebElement element) {
		wait.until(ExpectedConditions.visibilityOf(element));
		String result = (String) executeScript("return arguments[0].click();",
				element);
		if (debug) {
			// e.g. clickByJavaScript result: Press a button!
			err.println("clickByJavaScript result: " + result);
		}
		return (result != null);
	}

	// home-brewed method for clearing dynamic react input
	// which retain the text value after being cleared by a regular method
	// https://github.com/SeleniumHQ/selenium/issues/6741
	protected void customClear(By locator) {
		// disable implicit wait
		int delay = 200;

		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
		WebElement element = driver.findElement(locator);

		element.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));

		// alternative
		while (element.getAttribute("value") != "") {
			element.sendKeys(Keys.BACK_SPACE);
		}
	}

	protected void customSendKeys(By locator, String value) {
		// disable implicit wait
		int delay = 200;

		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
		WebElement element = driver.findElement(locator);
		// https://github.com/SeleniumHQ/selenium/issues/6741
		actions.click(element).pause(delay).keyDown(Keys.CONTROL).sendKeys("a")
				.keyUp(Keys.CONTROL).pause(delay).sendKeys(Keys.BACK_SPACE).pause(delay)
				.sendKeys(value).perform();
	}

	// based on: https://xpinjection.com/articles/waits-and-timeouts-in-webdriver/
	// also explanation of misc. WebDriverWait- related details in good Russian
	// translation
	private static By locator;
	private static String attributeName;
	private static String attributeValue;
	static {
		//
		@SuppressWarnings("unused")
		Function<? super WebDriver, String> hasAttribute = new ExpectedCondition<String>() {
			@Override
			public String apply(WebDriver webDriver) {
				return webDriver.findElement(locator).getAttribute(attributeName);
			}
		};
		@SuppressWarnings("unused")
		Predicate<WebDriver> hasExpectedValueOfAtribute = new Predicate<WebDriver>() {
			@Override
			public boolean test(WebDriver webDriver) {
				return webDriver.findElement(locator).getAttribute(attributeName)
						.contains(attributeValue);
			}
		};
	}
}
