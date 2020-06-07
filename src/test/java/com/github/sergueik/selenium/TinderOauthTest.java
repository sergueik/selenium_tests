package com.github.sergueik.selenium;

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
public class TinderOauthTest extends BaseTest {

	private static String baseURL = "https://tinder.com/?lang=en";
	private static WebElement element = null;
	private static List<WebElement> elements = new ArrayList<>();
	private final static boolean debug = true;

	@SuppressWarnings("deprecation")
	@BeforeMethod
	public void BeforeMethod() {
		driver.get(baseURL);
		wait.until(o -> o.getCurrentUrl().matches("https://tinder.com.*$"));
		// TODO: Regex
	}

	@Test(enabled = true)
	public void test1() {
		element = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//*[@id=\"content\"]//*[contains(text(),\"I Accept\")]")));
		assertThat(element, notNullValue());
		if (debug) {
			System.err.println("1: " + element.getText());
		}
		element.click();

		element = wait.until(ExpectedConditions.visibilityOfElementLocated(By
				.xpath("//*[@id=\"modal-manager\"]//*[contains(text(),\"Log in\")]")));
		assertThat(element, notNullValue());
		if (debug) {
			System.err.println("2: " + element.getAttribute("innerHTML"));
		}
		element.click();
		element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
				"//*[@id=\"modal-manager\"]//button[@aria-label=\"Log in with Facebook\"]")));
		assertThat(element, notNullValue());
		if (debug) {
			System.err.println("3: " + element.getAttribute("aria-label"));
		}
		element.click();
		wait.until(ExpectedConditions.numberOfWindowsToBe(2));
		if (debug) {
			System.err.println("5: oauth");
		}
		String parentWindow = driver.getWindowHandle();
		Set<String> allWindows = driver.getWindowHandles();
		for (String curWindow : allWindows) {
			if (!parentWindow.equals(curWindow)) {
				driver.switchTo().window(curWindow);
				System.err.println("6: switched to window: " + driver.getTitle());
				// Enter login and password
				driver.findElement(By.xpath("//*[@id=\"email\"]"))
						.sendKeys("email@gmail.com");
				driver.findElement(By.xpath("//*[@id=\"pass\"]")).sendKeys("password");
				// и т.д.
			}
		}
	}
}
