package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Selected test scenarios for Selenium WebDriver
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class IframeScrollTest extends BaseTest {

	private int scrollBy = 1000;
	private int betweenScrollDelay = 300;
	private int afterScrollDelay = 3000;
	private final static String selector = "iframe";
	public JavascriptExecutor js;
	private List<WebElement> elements = new ArrayList<>();

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
	}

	@BeforeMethod
	public void loadPage() {
		driver.navigate().to(getPageContent("iframe_scroll_example.html"));
		// Arrange
		elements = driver.findElements(By.cssSelector(selector));
		assertThat(elements, notNullValue());
		assertThat(elements.size(), greaterThan(0));
	}

	// does not work
	// https://stackoverflow.com/questions/44668169/python-selenium-unable-to-scroll-inside-iframe
	// https://stackoverflow.com/questions/16822608/scroll-an-iframe-from-parent-page
	@Test(enabled = false)
	public void test1() {
		// Act
		elements.stream().forEach(element -> {

			WebDriver iframe = driver.switchTo().frame(element);
			WebElement bodyElement = iframe.findElement(By.tagName("body"));
			assertThat(bodyElement, notNullValue());

			js = ((JavascriptExecutor) iframe);
			js.executeScript(
					"var element = arguments[0]; element.contentWindow.scrollTo(0, 300)",
					bodyElement);
			sleep(betweenScrollDelay);
			// TODO: assert some element position
		});
		sleep(afterScrollDelay);
	}

	// works
	@Test(enabled = true)
	public void test2() {
		// Arrange
		List<WebElement> elements = driver.findElements(By.cssSelector(selector));
		// Assert
		assertThat(elements, notNullValue());
		assertThat(elements.size(), greaterThan(0));
		elements.stream().forEach(element -> {

			WebDriver iframe = driver.switchTo().frame(element);
			WebElement bodyElement = iframe.findElement(By.tagName("body"));
			assertThat(bodyElement, notNullValue());

			js = ((JavascriptExecutor) iframe);
			scroll(js, 0, scrollBy);
			sleep(betweenScrollDelay);
			driver.switchTo().defaultContent();
		});
		sleep(afterScrollDelay);
	}

	// does not work ?
	@Test(enabled = false)
	public void test3() {
		// Arrange
		List<WebElement> elements = driver.findElements(By.cssSelector(selector));
		// Assert
		assertThat(elements, notNullValue());
		assertThat(elements.size(), greaterThan(0));
		elements.stream().forEach(element -> {

			WebDriver iframe = driver.switchTo().frame(element);
			actions = new Actions(iframe);
			actions.moveByOffset(0, scrollBy);
			// org.openqa.selenium.StaleElementReferenceException
			sleep(betweenScrollDelay);
			driver.switchTo().defaultContent();
		});
		sleep(afterScrollDelay);
	}

	// does not work ?
	@Test(enabled = false)
	public void test4() {
		// Arrange
		List<WebElement> elements = driver.findElements(By.cssSelector(selector));
		// Assert
		assertThat(elements, notNullValue());
		assertThat(elements.size(), greaterThan(0));
		elements.stream().forEach(element -> {

			WebDriver iframe = driver.switchTo().frame(element);
			WebElement bodyElement = iframe.findElement(By.tagName("body"));
			assertThat(bodyElement, notNullValue());

			js = ((JavascriptExecutor) iframe);
			js.executeScript(
					"var element = arguments[0];  var scrollBy = arguments[1];element.scroll({top: scrollBy, left: 0, behavior: 'smooth' })",
					bodyElement, scrollBy);
			sleep(betweenScrollDelay);
			driver.switchTo().defaultContent();
		});
		sleep(afterScrollDelay);
	}

	// Scroll
	public void scroll(final JavascriptExecutor js, final int x, final int y) {
		for (int i = 0; i <= x; i = i + 50) {
			js.executeScript("scroll(" + i + ",0)");
		}
		for (int j = 0; j <= y; j = j + 50) {
			js.executeScript("scroll(0," + j + ")");
		}
	}

}
