package com.github.sergueik.selenium;

import static java.lang.System.err;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

// https://qna.habr.com/q/787765
// https://riptutorial.com/selenium-webdriver/example/28140/scrolling-using-python 
public class YandexChatTest extends BaseTest {

	private static String baseURL = "https://www.urbandictionary.com/"; // "https://tinder.com/?lang=en";

	private static WebElement element = null;
	private static List<WebElement> elements = new ArrayList<>();
	private final static boolean debug = true;

	@SuppressWarnings("deprecation")
	@BeforeMethod
	public void BeforeMethod() {
		driver.get(baseURL);
	}

	@Test(enabled = true)
	public void test1() {
		element = wait.until(
				ExpectedConditions.visibilityOf(driver.findElement(By.id("content"))));

		String script = getScriptContent("mouseWheel.js");
		try {
			executeScript(script, element, 10000);
		} catch (Exception e) {
			err.println("Ignored: " + e.toString());
		}
	}
}
