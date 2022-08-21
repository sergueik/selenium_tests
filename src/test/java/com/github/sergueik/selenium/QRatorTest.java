package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/* Selected test scenarios for Selenium WebDriver
* @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
* based on discussion https://qna.habr.com/q/1191432
* see also
* https://qrator.net/en/
* https://habr.com/ru/company/qrator/profile/
*/

public class QRatorTest extends BaseTest {

	private static final Logger log = LogManager.getLogger(QRatorTest.class);
	private String baseUrl = "https://business.kazanexpress.ru/";
	private static final StringBuilder loggingSb = new StringBuilder();
	private static final Formatter formatter = new Formatter(loggingSb,
			Locale.US);

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
	}

	@SuppressWarnings("deprecation")
	@BeforeMethod
	public void beforeMethod(Method method) {
		super.beforeMethod(method);
		driver.get(baseUrl);

		Set<Cookie> cookies = driver.manage().getCookies();
		System.err.println("Cookies:");
		String value = "";
		JSONArray cookieJSONArray = new JSONArray();
		for (Cookie cookie : cookies) {
			System.err.println(formatter
					.format(
							"Name: '%s'\n" + "Value: '%s'\n" + "Domain: '%s'\n"
									+ "Path: '%s'\n" + "Expiry: '%tc'\n" + "Secure: '%b'\n"
									+ "HttpOnly: '%b'\n" + "\n",
							cookie.getName(), cookie.getValue(), cookie.getDomain(),
							cookie.getPath(), cookie.getExpiry(), cookie.isSecure(),
							cookie.isHttpOnly())
					.toString());
			if (cookie.getName() == "qrator_jsr")
				value = cookie.getValue();
			JSONObject cookieJSONObject = new JSONObject(cookie);
			// System.err.println("Inserting: " + cookieJSONObject.toString());
			// insertData(usernome, cookie.getName(), cookieJSONObject.toString());

			cookieJSONArray.put(cookieJSONObject);
		}
		sleep(10000);
		JSONObject cookiesJSONObject = new JSONObject();
		try {
			cookiesJSONObject.put("cookies", cookieJSONArray);
		} catch (JSONException e) {

		}
		System.err.println(cookiesJSONObject.toString());

		// https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/Cookie.html
		java.util.Date expiry = new Date();
		expiry.setMinutes(expiry.getMinutes() + 30);
		Cookie cookie = new Cookie("qrator_jsr", value, ".kazanexpress.ru", "/",
				expiry);

		driver.manage().addCookie(cookie);
		// driver.navigate().refresh();
		driver.get(baseUrl);
		// TODO:
		// index page returns
		// https://business.kazanexpress.ru/__qrator/qauth_utm_v2.js
		// and browser performs a
		// POST "https://business.kazanexpress.ru/__qrator/validate?" +
		// "pow=88" + "& " + "nonce=" +
		// <value derived from qrator_jsr cookie>
		// the other option which could be labor intensive to maintain
		// would be to save a real user session cookie
	}

	@AfterMethod
	@Override
	public void afterMethod() {
		driver.get("about:blank");
	}

	@Test(expectedExceptions = { org.openqa.selenium.TimeoutException.class })
	public void test() {
		// Arrange
		WebElement element = wait
				.until(ExpectedConditions.visibilityOfElementLocated(
						By.cssSelector("body > main > section.hero > div > h1")));
		highlight(element);
		sleep(100);
	}

}
