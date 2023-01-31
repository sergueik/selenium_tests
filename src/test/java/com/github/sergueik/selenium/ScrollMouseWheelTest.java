package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.gson.Gson;

public class ScrollMouseWheelTest extends BaseTest {

	private static final Logger log = LogManager
			.getLogger(ScrollMouseWheelTest.class);
	private String baseURL = "https://en.wikipedia.org/wiki/Main_Page";
	private String script;
	private static WebElement element = null;
	private static Gson gson = new Gson();
	private static String response = null;
	private static Map<String, Object> data = new HashMap<>();
	private static Map<String, Object> result = new HashMap<>();
	private static String selector = "#Welcome_to_Wikipedia > a";
	private static Point location = null;
	private static Dimension size;

	private static float lasty = 0f;

	@BeforeClass
	public void beforeClass() throws IOException {
		super.setBrowser("chrome");
		super.beforeClass();
		assertThat(driver, notNullValue());
	}

	@BeforeMethod
	public void loadPage() {
		driver.get(baseURL);
	}

	@Test(enabled = false)
	public void test1() {
		// NOTE: several exceptions in "scrollGMapExample.js"
		script = getScriptContent("mouseWheel.js");
		selector = "body > div.mw-page-container > div";
		element = wait.until(ExpectedConditions
				.visibilityOf(driver.findElement(By.cssSelector(selector))));
		int cnt;
		// makes no effect
		for (cnt = 0; cnt != 10; cnt++) {
			executeScript(script, element, 100);
			sleep(1000);
		}
	}

	@Test(enabled = false)
	public void test2() {
		int cnt;
		// NOTE: appears to scroll and return back, because
		// it does not scroll far enough
		for (cnt = 0; cnt != 3; cnt++) {
			scroll(0, 1000);
			selector = "body > div.mw-page-container > div";
			element = wait.until(ExpectedConditions
					.visibilityOf(driver.findElement(By.cssSelector(selector))));
			log.info(String.format("element: %d %d", element.getRect().getX(),
					element.getRect().getY()));

			sleep(1000);
		}
	}

	@SuppressWarnings("unchecked")
	@Test(enabled = false)
	public void test3() {

		final JavascriptExecutor js = (JavascriptExecutor) driver;
		int y = 10000;
		for (int j = 0; j <= y; j = j + 100) {
			js.executeScript("window.scroll(0," + j + ")");
			sleep(1000);
			selector = "#Welcome_to_Wikipedia > a";
			element = wait.until(ExpectedConditions
					.visibilityOf(driver.findElement(By.cssSelector(selector))));
			location = element.getLocation();

			response = (String) super.executeScript(
					super.getScriptContent("getCoords.js"), selector, "css", false);

			data = (Map<String, Object>) gson.fromJson(response, Map.class);
			result = (Map<String, Object>) gson
					.fromJson(data.get("result").toString(), Map.class);
			if (lasty == Float.parseFloat(result.get("y").toString()))
				break;
			log.info(String.format("coords: %.3f %.3f",
					Float.parseFloat(result.get("y").toString()),
					Float.parseFloat(result.get("bottom").toString())));

			// The element location will not change
			log.info(
					String.format("element: %d %d", location.getX(), location.getY()));
			lasty = Float.parseFloat(result.get("y").toString());
		}
	}

	@Test(enabled = true)
	public void test4() {
		selector = "body > div.mw-page-container > div";
		element = wait.until(ExpectedConditions
				.visibilityOf(driver.findElement(By.cssSelector(selector))));
		size = element.getSize();

		log.info(String.format("body element size: %d %d", size.getHeight(),
				size.getWidth()));
		executeScript("window.scrollTo(0, arguments[0])", size.getHeight());
		sleep(10000);
	}
}
