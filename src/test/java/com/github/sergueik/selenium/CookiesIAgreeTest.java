package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

// 
public class CookiesIAgreeTest extends BaseTest {

	private static String baseURL = "https://link.testproject.io/vr9";
	private static WebElement element = null;
	private static List<WebElement> elements = new ArrayList<>();
	private final static boolean debug = true;

	@BeforeMethod
	public void BeforeMethod() {
		driver.get(baseURL);
	}

	@Test(enabled = true)
	public void selectCookieMessageXPathTest() {
		element = driver.findElement(By.id("cookie-law-info-bar"));
		assertThat(element, notNullValue());
		highlight(element, 100);
		if (debug) {
			System.err.println(element.getAttribute("innerHTML"));
		}

		// "I AGREE" button
		elements = driver.findElements(By.xpath("//a[contains(@class, 'button')]"));
		assertThat(elements.size(), greaterThan(0));
		// show every button. NOTE: time-consuming
		elements.stream().forEach(o -> {
			// NOTE: ignoring TimeoutException from highlight(o) and continue is quite
			// time consuming
			/*  often text element text appears empty */
			System.err.println(
					"text: " + o.getText() + "html: " + o.getAttribute("outerHTML"));
			try {
				o.sendKeys(Keys.CONTROL + "t"); // no effect ?
			} catch (ElementNotInteractableException e) {
				System.err.println("Exception (ignored): " + e.getMessage());

			}
		});
		element = driver.findElement(By.id("cc-button"));
		assertThat(element, notNullValue());
		highlight(element, 1000);// no exception but no visual effect
		if (debug) {
			System.err.println("text: " + element.getText() + "html: "
					+ element.getAttribute("outerHTML"));
		}
		element = driver.findElements(By.xpath("//a[contains(@class, 'button')]"))
				.stream()
				.filter(o -> o.getText().trim().compareToIgnoreCase("I AGREE") > -1)
				.collect(Collectors.toList()).get(0);
		assertThat(element, notNullValue());
		element.sendKeys(Keys.CONTROL + "t"); // no effect
		// https://stackoverflow.com/questions/11776910/xpath-expression-to-remove-whitespace
		// NOTE: the following xpath locator failing to find anything despite labor
		// intensive tweaks
		for (String xpath : Arrays.asList(new String[] {

				"//a[contains(@class, 'button')][contains(translate(normalize-space(text()), ' &#9;&#10;&#13', ''),'IAGREE')]",
				"//a[contains(@class, 'button')][contains(translate(normalize-space(text()), ' ', ''),'IAGREE')]",
				"//a[contains(@class, 'button')][contains(normalize-space(text()),'I AGREE')]",
				"//a[contains(@class, 'button')][contains(text(),'I AGREE')]",
				"//a[contains(@class, 'button')][contains('.','I AGREE')]",
				"//a[contains(@class, 'button')][contains('.','EE')]",
				"//a[contains(@class, 'button')][contains('.','ee')]",
				"//a[contains(@class, 'button')][text() ='I AGREE']",
				"//*[@id = 'cc-button'][contains(normalize-space(text()),'')]",
				"//*[contains(@class ,'button')][contains(normalize-space(translate(text()), ' &#9;&#10;&#13', ' '),'Free Sign')]",
		})) {
			try {
				element = driver.findElement(By.xpath(xpath));
				assertThat(element, notNullValue());
				//
				element.sendKeys(Keys.CONTROL + "t"); // no effect
				highlight(element, 1000);
				// if (debug) {
				System.err.println("text: " + element.getText() + "html: "
						+ element.getAttribute("outerHTML"));
				// }
			} catch (NoSuchElementException | ElementNotInteractableException e) {
				System.err.println("Exception (ignored): " + e.getMessage());
				// check the *** Element info: {Using=xpath, value=...} part
			}
		}
		// "Learn more" link
		element = driver
				.findElement(By.xpath("//a[@href][contains(text(),\"Learn more\")]"));
		assertThat(element, notNullValue());
		// org.openqa.selenium.ElementNotInteractableException:
		// NOTE; TimeoutException when highlight is tried
		// highlight(agreeButtonElement, 1000);
		if (debug) {
			System.err.println(element.getAttribute("outerHTML"));
		}
		element = driver.findElement(By.tagName("body"));
		if (debug) {
			System.err.println(element.getAttribute("tagName"));
		}
		// based on:
		// https://stackoverflow.com/questions/17547473/how-to-open-a-new-tab-using-selenium-webdriver

		element.sendKeys(Keys.CONTROL + "t"); // no effect
		// actions.keyDown(Keys.CONTROL).build().perform();
		// actions.keyUp(Keys.CONTROL).build().perform();
		// actions.keyDown().build().perform();
		sleep(10000);

	}
}
