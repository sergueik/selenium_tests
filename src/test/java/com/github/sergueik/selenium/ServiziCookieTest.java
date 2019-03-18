package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
// import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Normalizer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.internal.Nullable;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/*
 * for login page with an arithmetic captcha
 */

public class ServiziCookieTest extends BaseTest {

	// private WebDriver driver;
	// private WebDriverWait wait;
	// private Actions actions;

	private static final long implicitWait = 10;
	private static final int flexibleWait = 30;
	private static final long polling = 1000;
	private static final long highlight = 100;
	private static final long afterTest = 1000;

	private static final String baseURL = "http://bandi.servizi.politicheagricole.it/taxcredit/default.aspx";
	// no page change
	// "http://bandi.servizi.politicheagricole.it/taxcredit/Menu.aspx";
	private static String mailURL = "http://bandi.servizi.politicheagricole.it/taxcredit/default.aspx";
	private static final String landURL = "http://bandi.servizi.politicheagricole.it/taxcredit/Menu.aspx";

	private static Map<String, String> env = System.getenv();
	private static final String usernome = getPropertyEnv("TEST_USER",
			"testuser");
	private static final String passe = getPropertyEnv("TEST_PASS", "00000000");

	private static final StringBuilder loggingSb = new StringBuilder();
	private static final Formatter formatter = new Formatter(loggingSb,
			Locale.US);

	private static final StringBuffer verificationErrors = new StringBuffer();

	private static final boolean debug = Boolean
			.parseBoolean(System.getenv("DEBUG"));
	private static final boolean headless = Boolean
			.parseBoolean(System.getenv("HEADLESS"));
	private static final boolean createTable = true;

	// TODO: blank value is picked
	private static String propertyFilePath = getPropertyEnv("property.filepath",
			"src/test/resources");
	private static Map<String, Object> cookieDataMap = new HashMap<>();

	private static final String sqlite_database_name = "login_cookies";

	private static Connection conn;
	private static String sql;

	// NOTE: value data first column
	private static final String extractQuery = "SELECT cookie, username, cookiename FROM login_cookies where username = ? and cookiename = ? limit 1";
	private static final String extractQueryTemplate = "SELECT cookie, username, cookiename  FROM login_cookies where username = '%s'and cookiename = '%s' limit 1";
	private static final String insertQuery = "INSERT INTO login_cookies(username, cookiename, cookie) VALUES(?,?,?)";
	private static final String defaultKey = "name";
	List<String> cookieNames = new ArrayList<>(
			Arrays.asList(new String[] { "ASP.NET_SessionId", "ARRAffinity" }));

	@BeforeClass
	public void beforeClass() {

		try {
			super.beforeClass();
		} catch (IOException e) {
			System.err.println("Exception (ignored): " + e.toString());
		}
		try {
			// origin:
			// https://www.tutorialspoint.com/sqlite/sqlite_java.htm
			Class.forName("org.sqlite.JDBC");
			String dbURL = resolveEnvVars(String.format(
					"jdbc:sqlite:${USERPROFILE}\\Desktop\\%s.db", sqlite_database_name));
			// NOTE: SQLite driver on its own will not create folders to construct
			// path to the file,
			// default is current project directory
			// dbURL = "jdbc:sqlite:performance.db";
			conn = DriverManager.getConnection(dbURL);
			if (conn != null) {
				// System.err.println("Connected to the database");
				DatabaseMetaData databaseMetadata = conn.getMetaData();
				System.err.println("Driver name: " + databaseMetadata.getDriverName());
				System.err
						.println("Driver version: " + databaseMetadata.getDriverVersion());
				System.err.println(
						"Product name: " + databaseMetadata.getDatabaseProductName());
				System.err.println(
						"Product version: " + databaseMetadata.getDatabaseProductVersion());
				if (createTable) {
					createNewTable();
					// insertData("name", "dummy", "cookie");
					// conn.close();
				}
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not initialize: " + e.toString());
			// System.exit(1); ?
		} catch (Exception e) {

		} finally {
		}

	}

	@AfterTest
	public void afterTest() {
	}

	@AfterClass
	public static void tearDownAfterClass() {
		if (verificationErrors.length() != 0) {
			throw new RuntimeException(verificationErrors.toString());
		}
	}

	private int processArithCaptcha(String arithCaptcha) {

		// System.err.println("Non sono un robot: " + arithCaptcha);

		Pattern pattern = Pattern
				.compile("(\\d+)\\s+((?:per|divizo|meno|pi.))\\s+(\\d+)\\s*=\\s*");
		Matcher matcher = pattern.matcher(arithCaptcha);

		assertTrue(matcher.find());

		Map<String, String> formOps = new HashMap<>();
		formOps.put("per", "multiply");
		formOps.put("diviso", "divide");
		formOps.put("meno", "substract");
		formOps.put("pi?", "add");
		String opLoc = matcher.group(2);
		String op = formOps.containsKey(opLoc) ? formOps.get(opLoc)
				: formOps.get("pi?");
		Integer left = Integer.parseInt(matcher.group(1));
		Integer right = Integer.parseInt(matcher.group(3));
		System.err.println(
				"It is: " + left.toString() + " " + op + " " + right.toString());
		Integer result = 0;
		switch (op) {
		case "multiply":
			result = left * right;
			break;
		case "divide":
			result = left / right;
			break;
		case "substract":
			result = left - right;
			break;
		case "add":
			result = left + right;
			break;
		default:
			result = -1;
		}

		return result.intValue();
	}

	@Test(enabled = true, priority = 4)
	public void loginTest() {
		assertThat(driver, notNullValue());
		driver.get(baseURL);
		WebElement element = driver
				.findElement(By.xpath("//*[contains(text(), 'A C C E D I')]"));
		highlight(element);
		element.click();
		wait.until(ExpectedConditions.urlContains(mailURL));

		element = driver.findElement(By.id("ctl00_phContent_Login_txtEmail"));
		highlight(element);
		element.clear();
		element.sendKeys(usernome);
		element = driver.findElement(By.id("ctl00_phContent_Login_txtOTP"));
		highlight(element);
		element.clear();
		element.sendKeys(passe);

		// solve the arithmetic
		element = driver.findElement(By.id("btnRobot"));
		String arithCaptcha = element.getText();
		System.err.println("Non sono un robot: " + arithCaptcha);
		int result = processArithCaptcha(arithCaptcha);
		element = driver.findElement(By.id("ctl00_phContent_Login_txtRisultato"));
		// #ctl00_phContent_Login_txtRisultato
		highlight(element);
		System.err.println("Result = " + result);
		element.sendKeys(String.format("%d", result));
		sleep(1000);

		element = driver.findElement(
				By.xpath("//input[contains(@name,'Login')][@value='ACCEDI']"));
		highlight(element);
		element.click();

		System.err.println("Navigating to " + landURL);
		wait.until(ExpectedConditions.urlContains(landURL));
		assertTrue(driver.getCurrentUrl().matches(landURL));
		sleep(1000);
	}

	@Test(enabled = true, priority = 0)
	public void getCookieTest() {
		driver.get(baseURL);
		WebElement element = driver
				.findElement(By.xpath("//*[contains(text(), 'A C C E D I')]"));
		highlight(element);
		element.click();
		wait.until(ExpectedConditions.urlContains(baseURL));

		element = driver.findElement(By.id("ctl00_phContent_Login_txtEmail"));
		highlight(element);
		element.clear();
		element.sendKeys(usernome);
		element = driver.findElement(By.id("ctl00_phContent_Login_txtOTP"));
		highlight(element);
		element.clear();
		element.sendKeys(passe);
		// solve the arithmetic
		element = driver.findElement(By.id("btnRobot"));
		String arithCaptcha = element.getText();
		System.err.println("Non sono un robot: " + arithCaptcha);
		int result = processArithCaptcha(arithCaptcha);

		element = driver.findElement(By.id("ctl00_phContent_Login_txtRisultato"));
		// #ctl00_phContent_Login_txtRisultato
		System.err.println("Result = " + result);
		element.sendKeys(String.format("%d", result));
		sleep(1000);
		// input#ctl00_phContent_Login_btnCaptcha
		// value="Rigenera"
		/// onclick="javascript:__doPostBack('ctl00$phContent$Login$btnCaptcha','')"

		element = driver.findElement(
				By.xpath("//input[contains(@name,'Login')][@value='ACCEDI']"));
		highlight(element);

		// continue the test
		element.click();
		sleep(5000);

		Set<Cookie> cookies = driver.manage().getCookies();
		System.err.println("Cookies:");
		JSONArray cookieJSONArray = new JSONArray();
		for (Cookie cookie : cookies) {
			System.err.println(
					formatter.format("Name: '%s'\n", cookie.getName()).toString());
			System.err.println(
					formatter.format("Value: '%s'\n", cookie.getValue()).toString());
			System.err.println(
					formatter.format("Domain: '%s'\n", cookie.getDomain()).toString());
			System.err.println(
					formatter.format("Path: '%s'\n", cookie.getPath()).toString());
			System.err.println(
					formatter.format("Expiry: '%tc'\n", cookie.getExpiry()).toString());
			System.err.println(formatter.format("Secure: '%b'\n", cookie.isSecure()));
			System.err
					.println(formatter.format("HttpOnly: '%b'\n", cookie.isHttpOnly()));
			/*
						System.err.println(formatter
			.format(
			"Name: '%s'\n" + "Value: '%s'\n" + "Domain: '%s'\n"
			+ "Path: '%s'\n" + "Expiry: '%tc'\n" + "Secure: '%b'\n"
			+ "HttpOnly: '%b'\n" + "\n",
			cookie.getName(), cookie.getValue(), cookie.getDomain(),
			cookie.getPath(), cookie.getExpiry(), cookie.isSecure(),
			cookie.isHttpOnly())
			.toString());
			*/
			JSONObject cookieJSONObject = new JSONObject(cookie);
			System.err.println("Insering: " + cookieJSONObject.toString());
			insertData(usernome, cookie.getName(), cookieJSONObject.toString());

			cookieJSONArray.put(cookieJSONObject);
		}
		JSONObject cookiesJSONObject = new JSONObject();
		try {
			cookiesJSONObject.put("cookies", cookieJSONArray);
		} catch (JSONException e) {

		}
		System.err.println(cookiesJSONObject.toString());

	}

	@SuppressWarnings("deprecation")
	@Test(enabled = false, priority = 1)
	public void useCookieTest() throws Exception {
		getCookieTest();
		// NOTE: hack to trick compiler from unreachable statement to dead code
		/* if (true) {
			return;
		}
		*/
		Set<Cookie> cookies = driver.manage().getCookies();
		System.err.println("Closing the browser");
		wait = null;
		System.err.println("re-open the browser, about to use the session cookies");
		driver.close();

		System.setProperty("webdriver.chrome.driver",
				osName.equals("windows")
						? (new File("c:/java/selenium/chromedriver.exe")).getAbsolutePath()
						: Paths.get(System.getProperty("user.home")).resolve("Downloads")
								.resolve("chromedriver").toAbsolutePath().toString());

		DesiredCapabilities capabilities = DesiredCapabilities.chrome();
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
		// https://stackoverflow.com/questions/18106588/how-to-disable-cookies-using-webdriver-for-chrome-and-firefox-java
		// chromePrefs.put("profile.default_content_settings.cookies", 2);
		// no cookies are allowed

		chromeOptions.setExperimentalOption("prefs", chromePrefs);
		if (osName.equals("windows")) {
			if (System.getProperty("os.arch").contains("64")) {
				String[] paths = new String[] {
						"C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe",
						"C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe" };
				// check file existence
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
		} else {
		}
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
				String.format("--browser.download.dir=%s", downloadFilepath)
				/* "--user-data-dir=/path/to/your/custom/profile"  , */
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

		capabilities.setBrowserName(DesiredCapabilities.chrome().getBrowserName());
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
			logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
			logPrefs.enable(LogType.BROWSER, Level.ALL);
			logPrefs.enable(LogType.DRIVER, Level.ALL);
		*/
		capabilities.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);

		driver = new ChromeDriver(capabilities);

		// driver.setLogLevel(Level.ALL);
		wait = new WebDriverWait(driver, flexibleWait);
		driver.get(landURL);
		sleep(1000); // pause
		System.err.println("Loading cookies");
		for (Cookie cookie : cookies) {
			System.err.println("Loading cookie: " + cookie.getName());
			// https://seleniumhq.github.io/selenium/docs/api/java/org/openqa/selenium/Cookie.html
			Cookie cookieClone = new Cookie(cookie.getName(), cookie.getValue(),
					cookie.getDomain(), cookie.getPath(),
					(java.util.Date) cookie.getExpiry(), (boolean) cookie.isSecure(),
					(boolean) cookie.isHttpOnly());
			String cookieData = extractData(usernome, cookie.getName()); // no longer
																																		// enough
			System.err.println("Got cookie: " + cookieData);
			deserializeData(cookieData, Optional.of(cookieDataMap));
			System.err.println(cookieDataMap);

			cookieClone = new Cookie(cookieDataMap.get("name").toString(),
					cookieDataMap.get("value").toString(),
					cookieDataMap.get("domain").toString(),
					cookieDataMap.get("path").toString(), (java.util.Date) null
					/* TODO: (java.util.Date) cookieDataMap.get("expiry") */, Boolean.parseBoolean(cookieDataMap.get("secure").toString()), Boolean.parseBoolean(cookieDataMap.get("httpOnly").toString()));

			driver.manage().addCookie(cookieClone);
		}
		// org.openqa.selenium.InvalidCookieDomainException:
		driver.navigate().refresh();
		// if (true) {
		if (false) {
			return;
		}

		// TODO: handle refreshes with Caution. As the 'Click Day' time approaches,
		// reload the page continuously
		// until the transmission button is active.
		// To refresh the page press F5.
		/*
		System.err.println("Waiting for inbox");
		try {
			wait.until(ExpectedConditions.urlContains("#inbox"));
		} catch (TimeoutException | UnreachableBrowserException e) {
			verificationErrors.append(e.toString());
		}
		*/
		int cnt = 0;
		while (cnt < 10) {
			// pencil button
			WebElement element = wait
					.until(ExpectedConditions.visibilityOfElementLocated(
							By.xpath("//*[contains(@id,'btnCompleta')][@title='Modifica']")));
			System.err
					.println("Pencil button:\n" + element.getAttribute("outerHTML"));
			highlight(element);
			element.click();
			// navigates to
			// http://bandi.servizi.politicheagricole.it/taxcredit/moduloTCR.aspx?ID=331
			List<WebElement> elements = driver
					.findElements(By.xpath("//*[contains(@id,'msgClickDay')]"));

			if (elements.size() > 0) {
				System.err.println(elements.get(0).getText());
				System.err.println("Waiting for F5 warning to disappear");
				driver.navigate().refresh();
				sleep(1000);
				cnt = cnt + 1;
			}
		}
		/*
		WebElement element = wait
				.until(ExpectedConditions.visibilityOfElementLocated(
						By.xpath("//*[contains(@id,'msgClickDay')]")));
						*/
		try {
			// check if we can user stock methods or write own ?
			wait.until(ExpectedConditions.invisibilityOfElementWithText(
					By.xpath("//*[contains(@id,'msgClickDay')]"),
					"Per aggiornare la pagina premere F5"));
		} catch (TimeoutException | UnreachableBrowserException e) {
			verificationErrors.append(e.toString());
		}
		sleep(120000);
		doLogout();
	}

	@Test(enabled = false, priority = 2)
	public void pureCookieTest() throws Exception {

		driver.get(baseURL);
		System.err.println("Generating cookies: " + cookieNames);

		for (String cookieName : cookieNames) {

			String cookieData = extractData(usernome, cookieName);
			deserializeData(cookieData, Optional.of(cookieDataMap));

			Date now = new Date();
			long t = now.getTime();
			t += 7 * 24 * 60 * 60 * 1000;
			Date futureDate = new Date(t);

			cookieDataMap.put("expiry", futureDate);
			System.err
					.println("Constructing cookie: " + cookieName + "\n" + cookieDataMap);

			Cookie cookie = new Cookie(cookieDataMap.get("name").toString(),
					cookieDataMap.get("value").toString(),
					cookieDataMap.get("domain").toString(),
					cookieDataMap.get("path").toString(),
					(java.util.Date) cookieDataMap.get("expiry")
					/* TODO: (java.util.Date) cookieDataMap.get("expiry") */, Boolean.parseBoolean(cookieDataMap.get("secure").toString()), Boolean.parseBoolean(cookieDataMap.get("httpOnly").toString()));

			driver.manage().addCookie(cookie);
		}
		// org.openqa.selenium.InvalidCookieDomainException:
		driver.get(landURL);

		driver.navigate().refresh();

		if (debug) {
			Set<Cookie> cookies = driver.manage().getCookies();
			for (Cookie cookie : cookies) {
				System.err.println(formatter.format("Name: '%s'\n", cookie.getName())
						.toString()
						+ formatter.format("Value: '%s'\n", cookie.getValue()).toString()
						+ formatter.format("Domain: '%s'\n", cookie.getDomain()).toString()
						+ formatter.format("Path: '%s'\n", cookie.getPath()).toString()
						+ formatter.format("Expiry: '%tc'\n", cookie.getExpiry()).toString()
						+ formatter.format("Secure: '%b'\n", cookie.isSecure())
						+ formatter.format("HttpOnly: '%b'\n", cookie.isHttpOnly()));
			}
		}
		sleep(120000);
		// TODO: handle refreshes with Caution. As the 'Click Day' time approaches,
		// reload the page continuously
		// until the transmission button is active.
		// To refresh the page press F5.
		/*
		System.err.println("Waiting for inbox");
		try {
			wait.until(ExpectedConditions.urlContains("#inbox"));
		} catch (TimeoutException | UnreachableBrowserException e) {
			verificationErrors.append(e.toString());
		}
		*/
		int cnt = 0;
		while (cnt < 10) {
			// pencil button
			WebElement element = wait
					.until(ExpectedConditions.visibilityOfElementLocated(
							By.xpath("//*[contains(@id,'btnCompleta')][@title='Modifica']")));
			System.err
					.println("Pencil button:\n" + element.getAttribute("outerHTML"));
			highlight(element);
			element.click();
			// navigates to
			// http://bandi.servizi.politicheagricole.it/taxcredit/moduloTCR.aspx?ID=331
			List<WebElement> elements = driver
					.findElements(By.xpath("//*[contains(@id,'msgClickDay')]"));

			if (elements.size() > 0) {
				System.err.println(elements.get(0).getText());
				System.err.println("Waiting for F5 warning to disappear");
				driver.navigate().refresh();
				sleep(1000);
				cnt = cnt + 1;
			}
		}
		/*
		WebElement element = wait
				.until(ExpectedConditions.visibilityOfElementLocated(
						By.xpath("//*[contains(@id,'msgClickDay')]")));
						*/
		try {
			// check if we can user stock methods or write own ?
			wait.until(ExpectedConditions.invisibilityOfElementWithText(
					By.xpath("//*[contains(@id,'msgClickDay')]"),
					"Per aggiornare la pagina premere F5"));
		} catch (TimeoutException | UnreachableBrowserException e) {
			verificationErrors.append(e.toString());
		}
		sleep(120000);
		doLogout();
	}

	private static void doLogout() {

	}

	// http://www.sqlitetutorial.net/sqlite-java/create-table/
	public static void createNewTable() {
		sql = "DROP TABLE IF EXISTS login_cookies";
		try (java.sql.Statement statement = conn.createStatement()) {
			statement.execute(sql);
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		sql = "CREATE TABLE IF NOT EXISTS login_cookies (\n"
				+ "	id integer PRIMARY KEY,\n" + "	username text NOT NULL,\n"
				+ "	cookiename text NOT NULL,\n" + "	cookie text\n" + ");";
		try (java.sql.Statement statement = conn.createStatement()) {
			statement.execute(sql);
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
	}

	// http://www.sqlitetutorial.net/sqlite-java/insert/
	public static void insertData(String userName, String cookieName,
			String jsonDataString) {
		try (PreparedStatement _statement = conn.prepareStatement(insertQuery)) {
			System.err.println("Prepare statement: " + insertQuery);
			// NOTE: Values not bound to statement is not thrown as exception
			_statement.setString(1, userName);
			_statement.setString(2, cookieName);
			// TODO: time stamp
			_statement.setString(3, jsonDataString);
			_statement.executeUpdate();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
	}

	public static String extractData(String key) {
		String value = null;
		ResultSet result = null;
		try {
			System.err.println(
					String.format("Prepare statement: %s\nwith %s", extractQuery, key));
			PreparedStatement statement = conn.prepareStatement(extractQuery);
			statement.setString(1, key);
			result = statement.executeQuery();
		} catch (Exception e1) {
			System.err.println("Exception(ignored): " + e1.toString());
			// NOTE: there must be NO quotes around ? parameter in where or the
			// following
			// java.lang.ArrayIndexOutOfBoundsException:
			// at org.sqlite.jdbc3.JDBC3PreparedStatement.setString
			// TODO: pre-validate
			// see also extractQueryTemplate below
			try {
				System.err.println("Format statement query: " + extractQueryTemplate);
				Statement statement = conn.createStatement();
				result = statement
						.executeQuery(String.format(extractQueryTemplate, key));
			} catch (Exception e2) {
				e2.printStackTrace();
			}

		}

		if (result != null) {
			System.err.println("Got results:");
			try {
				while (result.next()) {
					String cookie = result.getString(1);
					String username = result.getString(2);
					String cookiename = result.getString(3);
					System.err.println("username: " + username);
					System.err.println("cookie:\n" + cookie);
					value = cookie;
				}
			} catch (SQLException e) {
				System.err.println("Exception(ignored): " + e.toString());
			}
		}
		return value;
	}

	public static String extractData(String key1, String key2) {
		String value = null;
		ResultSet result = null;
		try {
			System.err.println(String.format("Prepare statement: %s\nwith %s,%s",
					extractQuery, key1, key2));
			PreparedStatement statement = conn.prepareStatement(extractQuery);
			statement.setString(1, key1);
			statement.setString(2, key2);
			result = statement.executeQuery();
		} catch (Exception e) {
			System.err.println("Exception(ignored): " + e.toString());
			// NOTE: there must be NO quotes around ? parameter in where or the
			// following
			// java.lang.ArrayIndexOutOfBoundsException:
			// at org.sqlite.jdbc3.JDBC3PreparedStatement.setString
			// TODO: pre-validate
			// see also extractQueryTemplate below
			try {
				System.err.println("Format statement query: " + extractQueryTemplate);
				Statement statement = conn.createStatement();
				result = statement
						.executeQuery(String.format(extractQueryTemplate, key1, key2));
			} catch (Exception e2) {
				e2.printStackTrace();
			}

		}

		if (result != null) {
			System.err.println("Got results:");
			try {
				while (result.next()) {
					String cookie = result.getString(1);
					String username = result.getString(2);
					String cookiename = result.getString(3);
					System.err.println("username: " + username);
					System.err.println("cookiename: " + cookiename);
					System.err.println("cookie:\n" + cookie);
					value = cookie;
				}
			} catch (SQLException e) {
				System.err.println("Exception(ignored): " + e.toString());
			}
		}
		return value;
	}

	public String deserializeData(Optional<Map<String, Object>> parameters) {
		return deserializeData(null, parameters);
	}

	// Deserialize the hashmap from the JSON
	// see also
	// https://stackoverflow.com/questions/3763937/gson-and-deserializing-an-array-of-objects-with-arrays-in-it
	// https://futurestud.io/tutorials/gson-mapping-of-arrays-and-lists-of-objects
	public String deserializeData(String payload,
			Optional<Map<String, Object>> parameters) {

		Map<String, Object> collector = (parameters.isPresent()) ? parameters.get()
				: new HashMap<>();

		String data = (payload == null) ? "{}" : payload;
		try {
			JSONObject elementObj = new JSONObject(data);
			@SuppressWarnings("unchecked")
			Iterator<String> propIterator = elementObj.keys();
			while (propIterator.hasNext()) {
				String propertyKey = propIterator.next();
				String propertyVal = elementObj.getString(propertyKey);
				// logger.info(propertyKey + ": " + propertyVal);
				if (debug) {
					System.err
							.println("Deserialize Data: " + propertyKey + ": " + propertyVal);
				}
				collector.put(propertyKey, propertyVal);
			}
		} catch (JSONException e) {
			System.err.println("Exception (ignored): " + e.toString());
			return null;
		}
		return collector.get(defaultKey).toString();
	}
}
