package com.github.sergueik.selenium;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.InvalidCookieDomainException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

// NOTE: this test restarts the browser
public class GoogleDriveTest {

	private static WebDriver driver;
	private static WebDriver frame;
	private static WebDriverWait wait;
	private static Actions actions;
	private static WebElement element = null;
	private static Boolean debug = false;
	private static String selector = null;
	private static long implicitWait = 10;
	private static int flexibleWait = 180;
	private static long polling = 1000;
	private static long highlight = 100;
	private static long afterTest = 1000;
	private static String baseURL = "https://drive.google.com/";
	private static String loginURL = "https://accounts.google.com/";
	private static final StringBuffer verificationErrors = new StringBuffer();
	private static Map<String, String> env = System.getenv();
	private static String username = getPropertyEnv("TEST_USER",
			"automationnewuser24@gmail.com");
	private static String password = getPropertyEnv("TEST_PASS", "00000000");
	private static final StringBuilder loggingSb = new StringBuilder();
	private static final Formatter formatter = new Formatter(loggingSb,
			Locale.US);
	private static String propertiesFileName = "test.properties";
	// TODO: blank value is picked
	// empty
	private static final String propertyFilePath = getPropertyEnv(
			"property.filepath", "src/test/resources");

	private static final boolean createTable = true;

	private static Map<String, Object> cookieDataMap = new HashMap<>();

	private static final String sqlite_database_name = "login_cookies";
	// private static final boolean debug = Boolean
	// .parseBoolean(System.getenv("DEBUG"));

	private static Connection conn;
	private static String sql;

	// NOTE: value data first column
	private static final String extractQuery = "SELECT cookie, username, cookiename FROM login_cookies where username = ? and cookiename = ? limit 1";
	private static final String extractQueryTemplate = "SELECT cookie, username, cookiename  FROM login_cookies where username = '%s'and cookiename = '%s' limit 1";
	private static final String insertQuery = "INSERT INTO login_cookies(username, cookiename, cookie) VALUES(?,?,?)";
	private static final String defaultKey = "name";
	List<String> cookieNames = new ArrayList<>(
			Arrays.asList(new String[] { "NID", "GAPS" }));

        // TODO: this is currently only working on a Windows system
	private static WebDriver setupDriver() {
		System.setProperty("webdriver.gecko.driver",
				new File("c:/java/selenium/geckodriver.exe").getAbsolutePath());
		System.setProperty("webdriver.firefox.bin",
				new File("c:/Program Files (x86)/Mozilla Firefox/firefox.exe")
						.getAbsolutePath());

		DesiredCapabilities capabilities = DesiredCapabilities.firefox();
		capabilities.setCapability("marionette", false);
		WebDriver driver = new FirefoxDriver(capabilities);
		return driver;
	}

	@BeforeClass
	public static void setUp() {

		if (env.containsKey("DEBUG") && env.get("DEBUG").equals("true")) {
			debug = true;
		}
		driver = setupDriver();
		System.err.println("Properties file path: " + propertyFilePath);
		HashMap<String, String> propertiesMap = PropertiesParser
				.getProperties(String.format("%s/%s/%s", System.getProperty("user.dir"),
						propertyFilePath, propertiesFileName));
		if (username.isEmpty()) {
			username = propertiesMap.get("username");
		}
		if (password.isEmpty()) {
			password = propertiesMap.get("password");
		}
		System.err.println("Username: " + username);
		System.err.println("Password: " + password);
		wait = new WebDriverWait(driver, flexibleWait);
		// Selenium Driver version sensitive code: 3.13.0 vs. 3.8.0 and older
		wait.pollingEvery(Duration.ofMillis(polling));
		// wait.pollingEvery(polling, TimeUnit.MILLISECONDS);
		driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
		driver.get(baseURL);
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

	@Before
	public void beforeTest() {
		driver.get(baseURL);
	}

	@After
	public void resetBrowser() {
		// load blank page
		driver.get("about:blank");
	}

	@AfterClass
	public static void tearDown() {
		try {
			Thread.sleep(afterTest);
		} catch (InterruptedException e) {
		}
		try {
			driver.close();
			driver.quit();
		} catch (Exception e) {
			// ignore
			// java.net.ProtocolException: unexpected end of stream
		}
		if (verificationErrors.length() != 0) {
			throw new RuntimeException(verificationErrors.toString());
		}
	}

	@Ignore
	@Test
	public void getCookieTest() throws Exception {

		doLogin();
		Set<Cookie> cookies = driver.manage().getCookies();
		if (debug) {
			System.err.println("Cookies:");
		}
		JSONArray cookieJSONArray = new JSONArray();
		for (Cookie cookie : cookies) {
			if (debug) {
				System.err.println(formatter
						.format(
								"Name: '%s'\n" + "Value: '%s'\n" + "Domain: '%s'\n"
										+ "Path: '%s'\n" + "Expiry: '%tc'\n" + "Secure: '%b'\n"
										+ "HttpOnly: '%b'\n" + "\n",
								cookie.getName(), cookie.getValue(), cookie.getDomain(),
								cookie.getPath(), cookie.getExpiry(), cookie.isSecure(),
								cookie.isHttpOnly())
						.toString());
			}
			JSONObject cookieJSONObject = new JSONObject(cookie);
			if (debug) {
				System.err.println(cookieJSONObject.toString());
			}
			cookieJSONArray.put(cookieJSONObject);

			System.err.println("Insering: " + cookieJSONObject.toString());
			insertData(username, cookie.getName(), cookieJSONObject.toString());

		}
		JSONObject cookiesJSONObject = new JSONObject();
		cookiesJSONObject.put("cookies", cookieJSONArray);
		if (debug) {
			System.err.println(cookiesJSONObject.toString());
		}

		doLogout();
	}

	// @Ignore
	@Test
	public void useCookieTest() throws Exception {
		doLogin();
		System.err.println("Getting the cookies");
		Set<Cookie> cookies = driver.manage().getCookies();
		System.err.println("Closing the browser");
		wait = null;
		System.err.println("re-open the browser, about to use the session cookies");
		driver.close();
		driver = setupDriver();
		wait = new WebDriverWait(driver, flexibleWait);
		// Selenium Driver version sensitive code: 3.13.0 vs. 3.8.0 and older
		wait.pollingEvery(Duration.ofMillis(polling));
		// wait.pollingEvery(polling, TimeUnit.MILLISECONDS);
		System.err.println("Navigating to " + baseURL);
		driver.get(baseURL);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		System.err.println(String.format("Loading %d cookies ", cookies.size()));
		for (Cookie cookie : cookies) {
			// no cookie
			if (debug) {
				System.err.println(formatter
						.format(
								"Name: '%s'\n" + "Value: '%s'\n" + "Domain: '%s'\n"
										+ "Path: '%s'\n" + "Expiry: '%tc'\n" + "Secure: '%b'\n"
										+ "HttpOnly: '%b'\n" + "\n",
								cookie.getName(), cookie.getValue(), cookie.getDomain(),
								cookie.getPath(), cookie.getExpiry(), cookie.isSecure(),
								cookie.isHttpOnly())
						.toString());
				JSONObject cookieJSONObject = new JSONObject(cookie);
				System.err.println("Insering: " + cookieJSONObject.toString());
				insertData(username, cookie.getName(), cookieJSONObject.toString());
			}
			try {
				driver.manage().addCookie(cookie);
			} catch (InvalidCookieDomainException e) {
				// ignore the exception
			}
		}
		driver.get(baseURL);
		driver.navigate().refresh();

		System.err.println("Waiting for inbox");
		/*
		 * try { wait.until(ExpectedConditions.urlContains("#inbox")); } catch
		 * (TimeoutException | UnreachableBrowserException e) {
		 * verificationErrors.append(e.toString()); }
		 */
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
		}
		doLogout();
	}

	private void doLogout() {

		element = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector("a[aria-label^='Google Account']")));

		highlight(element);
		element.click();
		element = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector("a[href^='https://accounts.google.com/Logout']")));
		highlight(element);
		element.click();
	}

	private void doLogin() {
		// test flow very similar to yandex login
		// ...
		// &continue=https://drive.google.com/%23&followup=https://drive.google.com/&ltmpl=drive&emr=1#identifier
		// access Google Drive
		try {
			element = driver.findElement(By
					.cssSelector("a[href^='https://accounts.google.com/ServiceLogin?']"));
		} catch (Exception e) {

		}

		element = driver.findElement(By.linkText("Go to Google Drive"));
		highlight(element);
		element.click();

		System.err.println("current URL :" + driver.getCurrentUrl());
		Pattern pattern = Pattern
				.compile(String.format("^%s.+&continue=([^&]+)&", loginURL));
		Matcher matcher = pattern.matcher(driver.getCurrentUrl());
		String retpath = null;
		if (matcher.find()) {
			try {
				retpath = java.net.URLDecoder.decode(matcher.group(1).toString(),
						"UTF-8");
			} catch (UnsupportedEncodingException e) {
				// ignore
			}
		}
		System.err.println("retpath: " + retpath);
		try {
			wait.until(ExpectedConditions.urlContains(loginURL));
		} catch (TimeoutException e) {
			System.err.println("Ignored exception " + e.toString());
		}

		// enter the username
		element = driver.findElement(By.cssSelector("*[type='email']"));
		// alternarively, select by visible placeholder text
		element = driver
				.findElement(By.cssSelector("*[aria-label='Email or phone']"));
		highlight(element);
		element.clear();

		element.sendKeys(username);
		element = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//*[@id='identifierNext']/content/span")));
		highlight(element);
		element.click();

		// enter the password
		element = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector("*[aria-label='Enter your password']")));
		highlight(element);
		element.clear();
		element.sendKeys(password);

		// sign in
		element = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//*[@id='passwordNext']/content/span")));
		highlight(element);
		element.click();
	}

	private void highlight(WebElement element) {
		highlight(element, highlight);
	}

	private void highlight(WebElement element, long highlight) {
		try {
			wait.until(ExpectedConditions.visibilityOf(element));
			executeScript("arguments[0].style.border='3px solid yellow'", element);
			Thread.sleep(highlight);
			executeScript("arguments[0].style.border=''", element);
		} catch (InterruptedException e) {
			// ignore
		}
	}

	private Object executeScript(String script, Object... arguments) {
		if (driver instanceof JavascriptExecutor) {
			JavascriptExecutor javascriptExecutor = JavascriptExecutor.class
					.cast(driver);
			return javascriptExecutor.executeScript(script, arguments);
		} else {
			throw new RuntimeException("Script execution failed.");
		}
	}

	public static String getPropertyEnv(String name, String defaultValue) {
		String value = System.getProperty(name);
		if (value == null || value.isEmpty()) {
			value = System.getenv(name);
			if (value == null || value.isEmpty()) {
				value = defaultValue;
			}
		}
		return value;
	}

	private static class PropertiesParser {
		@SuppressWarnings("unchecked")
		public static HashMap<String, String> getProperties(final String fileName) {
			Properties p = new Properties();
			HashMap<String, String> propertiesMap = new HashMap<>();
			System.err
					.println(String.format("Reading properties file: '%s'", fileName));
			try {
				p.load(new FileInputStream(fileName));
				Enumeration<String> e = (Enumeration<String>) p.propertyNames();
				for (; e.hasMoreElements();) {
					String key = e.nextElement();
					String val = p.get(key).toString();
					System.out.println(String.format("Reading: '%s' = '%s'", key, val));
					propertiesMap.put(key, val);
				}

			} catch (FileNotFoundException e) {
				System.err.println(
						String.format("Properties file was not found: '%s'", fileName));
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println(
						String.format("Properties file is not readable: '%s'", fileName));
				e.printStackTrace();
			}
			return (propertiesMap);
		}
	}

	/// TODO: refactor into a separate class Utils.java
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

}
