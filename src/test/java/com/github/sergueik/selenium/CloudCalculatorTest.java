package com.github.sergueik.selenium;

// The import org.hamcrest.Matchers.matchesRegex cannot be resolved
// import static org.hamcrest.Matchers.matchesRegex;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Selected test scenarios for Selenium WebDriver
 * 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class CloudCalculatorTest extends BaseTest {

	private static String baseURL = "https://cloud.google.com/products/calculator";

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
		driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
	}

	@Override
	@AfterClass
	public void afterClass() {
		try {
			driver.close();
		} catch (NoSuchWindowException e) {

		}
		driver.quit();
		driver = null;
	}

	@BeforeMethod
	public void loadBaseURL() {
		driver.get(baseURL);
	}

	@Test(enabled = false)
	public void testFramePresent() {
		List<WebElement> iframes = driver
				.findElements(By.cssSelector("devsite-iframe iframe"));
		Map<String, Object> iframesMap = new HashMap<>();
		for (WebElement iframe : iframes) {
			String key = String.format("id: \'%s\", name: \"%s\"",
					iframe.getAttribute("id"), iframe.getAttribute("name"));
			System.err.println(String.format("Found iframe %s", key));
			iframesMap.put(key, iframe);
		}
	}

	@Test(enabled = false)
	public void testNestedFramePresent() {

		WebElement element = wait
				.until(ExpectedConditions.visibilityOfElementLocated(
						By.cssSelector("devsite-iframe iframe[name^='goog_']")));
		assertThat(element, notNullValue());

		WebDriver iframe = driver.switchTo().frame(element);
		// System.err.println("Frame source:" + iframe.getPageSource());
		List<WebElement> iframes = iframe.findElements(By.cssSelector("iframe"));
		Map<String, Object> iframesMap = new HashMap<>();
		for (WebElement nestedIframe : iframes) {
			String key = String.format("id: \'%s\", src: \"%s\"",
					nestedIframe.getAttribute("id"), nestedIframe.getAttribute("src"));
			System.err.println(String.format("Found iframe (depth 2) %s", key));
			iframesMap.put(key, nestedIframe);
		}
		driver.switchTo().defaultContent();

	}

	@Test(enabled = true)
	public void testSwitchIntoNestedPage() {

		WebElement element = wait
				.until(ExpectedConditions.visibilityOfElementLocated(
						By.cssSelector("devsite-iframe iframe[name^='goog_']")));
		assertThat(element, notNullValue());

		WebDriver iframe = driver.switchTo().frame(element);
		element = iframe.findElement(By.cssSelector("iframe:nth-of-type(1)"));
		WebDriver nestedIframe = iframe.switchTo().frame(element);
		// System.err.println("Frame source:" + nestedIframe.getPageSource());

		List<WebElement> inputLabels = nestedIframe
				.findElements(By.cssSelector("label[for^=\"input_\"]"));
		WebElement input1Label = inputLabels.stream()
				.filter(o -> o.getText().contains((CharSequence) "Number of instances"))
				.collect(Collectors.toList()).get(0);
		// System.err
		// .println("Input label source: " + input1Label.getAttribute("outerHTML"));
		WebElement input1 = nestedIframe.findElement(By.cssSelector(
				String.format("input[id=\"%s\"]", input1Label.getAttribute("for"))));
		input1.sendKeys("1");
		sleep(100);

		inputLabels = nestedIframe
				.findElements(By.cssSelector("label[for^=\"input_\"]"));
		WebElement input2Label = inputLabels.stream()
				.filter(o -> o.getText()
						.contains((CharSequence) "What are these instances for"))
				.collect(Collectors.toList()).get(0);
		// System.err
		// .println("Input label source: " +
		// input2Label.getAttribute("outerHTML"));
		WebElement input2 = nestedIframe.findElement(By.cssSelector(
				String.format("input[id=\"%s\"]", input2Label.getAttribute("for"))));
		input2.sendKeys("testing");
		sleep(100);

		List<WebElement> selectLabels = nestedIframe
				.findElements(By.cssSelector("label[for^=\"select_\"]"));
		WebElement select1Label = selectLabels.stream()
				.filter(o -> o.getText().contains((CharSequence) "Operating System"))
				.collect(Collectors.toList()).get(0);
		// System.err
		// .println("Select label source: " + label3.getAttribute("outerHTML"));
		WebElement select1 = nestedIframe.findElement(By.cssSelector(String
				.format("md-select[id=\"%s\"]", select1Label.getAttribute("for"))));

		// System.err.println("Select source: " +
		// select1.getAttribute("outerHTML"));

		WebElement selectValue = select1
				.findElement(By.cssSelector("md-select-value > span > div"));
		assertThat(selectValue, notNullValue());
		// https://stackoverflow.com/questions/32499174/selenium-click-event-does-not-trigger-angularjs-ng-click
		System.err.println("Simulate mouse click on element");
		executeScript("arguments[0].click();", selectValue);
		highlight(selectValue, 1000, "solid red");
		sleep(10000);
		// NOTE: even after fixing the driver argument with embedded iframe,
		// the Actions does not work
		/*
		Actions action = new Actions(nestedIframe);
		action.moveToElement(selectValue).perform();
		action.moveToElement(selectValue).click().perform();
		
		highlight(selectValue, 1000, "solid black");
		*/
		select1 = nestedIframe.findElement(By.cssSelector(String
				.format("md-select[id=\"%s\"]", select1Label.getAttribute("for"))));
		System.err.println("Select source (options visible): "
				+ select1.findElement(By.xpath("..")).getAttribute("outerHTML"));
		/*
				WebElement menu1 = select1
						.findElement(By.xpath("div[@class=\"md-select-menu-container\"]"));
				assertThat(menu1, notNullValue());
				System.err.println(
						"Select menu container source: " + menu1.getAttribute("outerHTML"));
		
				WebElement value1 = menu1.findElement(By.xpath(
						"md-select-menu/md-content/md-option[@selected=\"selected\"]/div"));
				assertThat(value1, notNullValue());
				System.err
						.println("Selected option source: " + value1.getAttribute("outerHTML"));
		
				WebElement value2 = menu1.findElement(By.xpath(
						"md-select-menu/md-content/md-option[@aria-selected=\"false\"][1]"));
				System.err.println(
						"Not selected option source: " + value2.getAttribute("outerHTML"));
				value2.click();
				sleep(10000);
				*/
		iframe.switchTo().defaultContent();
		driver.switchTo().defaultContent();

	}

}
