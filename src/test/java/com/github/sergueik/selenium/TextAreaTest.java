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

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Selected test scenarios for Selenium WebDriver
 * 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo
 */
public class TextAreaTest extends BaseTest {

	private static String baseURL = "https://www.w3schools.com";
	private final String filename = "tryhtml_textarea";

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
	public void loadSubjectURL() {
		// Arrange
		driver
				.get(baseURL + String.format("/tags/tryit.asp?filename=%s", filename));
	}

	@AfterMethod
	public void loadBaseURL() {
		// Arrange
		driver.get(baseURL);
	}

	@Test(enabled = true)
	public void test1() {
		List<WebElement> iframes = driver
				.findElements(By.cssSelector("div#iframewrapper iframe"));
		Map<String, Object> iframesMap = new HashMap<>();
		for (WebElement iframe : iframes) {
			String key = String.format("id: \'%s\", name: \"%s\"",
					iframe.getAttribute("id"), iframe.getAttribute("name"));
			System.err.println(String.format("Found iframe %s", key));
			iframesMap.put(key, iframe);
		}
	}

	@Test(enabled = true)
	public void test2() {

		// simplified 'getText' for retrieveing the values shown in textarea nodes
		// that are not displayed through Selenium

		final String script = "const element = arguments[0]; const debug = arguments[1]; return element.value;";

		WebElement resultIframe = wait
				.until(ExpectedConditions.visibilityOfElementLocated(
						By.cssSelector("iframe[name='iframeResult']")));
		assertThat(resultIframe, notNullValue());

		// Act
		WebDriver iframe = driver.switchTo().frame(resultIframe);
		WebElement element = iframe
				.findElement(By.xpath("//textarea[ @id='w3review']"));
		// Assert

		System.err.println(
				"Frame page: " + iframe.getPageSource().replaceAll("\\n", " "));
		@SuppressWarnings("unchecked")
		String result1 = (String) executeScript(script, element);
		String result2 = (String) executeScript(getScriptContent("getValue.js"),
				element);
		String result3 = (String) executeScript(getScriptContent("getText.js"),
				element);
		System.err.println("Value (1): " + result1);
		System.err.println("Value (1): " + result2);
		System.err.println("Value (3): " + result3);
		System.err.println("Text: " + element.getText());
		System.err.println("innerHTML: " + element.getAttribute("innerHTML"));
		driver.switchTo().defaultContent();
	}

}
