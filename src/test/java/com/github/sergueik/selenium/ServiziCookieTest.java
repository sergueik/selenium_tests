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

import java.text.Normalizer;

import java.time.Duration;

import java.util.Enumeration;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
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
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
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

	// TODO: blank value is picked
	private static String propertyFilePath = getPropertyEnv("property.filepath",
			"src/test/resources");

	@AfterTest
	public void afterTest() {
	}

	@AfterClass
	public static void tearDownAfterClass() {
		if (verificationErrors.length() != 0) {
			throw new RuntimeException(verificationErrors.toString());
		}
	}

	@Test(enabled = true)
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
		// 17 pi∙ 69 =
		// 8 per 6 =
		// 49 meno 48 =
		// 30 divizo 5 =

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

		element = driver.findElement(By.id("ctl00_phContent_Login_txtRisultato"));
		// #ctl00_phContent_Login_txtRisultato
		highlight(element);
		element.sendKeys(result.toString());
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
}
