package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.Keys;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class UcdTest extends BaseTest {

	private static String ucdHost = "192.168.0.64";
	private static String baseURL = String.format("https://%s:8443/", ucdHost);
	private static final String username = "admin";
	private static final String password = "admin";
	private static final String application = "hello Application";

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
		// this is a multi step "test"
		element = wait.until(ExpectedConditions
				.visibilityOf(driver.findElement(By.id("usernameField"))));
		element.sendKeys(username);
		element = driver
				.findElement(By.cssSelector("form input[name = 'password']"));
		fastSetText(element, password);
		element = driver
				.findElement(By.cssSelector("form span[widgetid = 'submitButton']"));
		highlight(element);
		element.click();
		wait.until(ExpectedConditions.urlContains("dashboard"));
		// switch to Applictions
		element = wait.until(
				ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector(
						"a.tab.linkPointer[href = '#main/applications'] span.tabLabel"))));
		assertThat(element, notNullValue());
		highlight(element);
		element.click();
		element = wait.until(
				ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector(
						"*[id *= 'uniqName_'] > div.selectableTable.webextTable.treeTable > table"))));
		assertThat(element, notNullValue());
		System.err.println(
				"table: " + element.getAttribute("innerHTML").substring(0, 100));
		elements = element.findElements(By.cssSelector(
				"tr.noPrint.tableFilterRow input[class *= 'dijitInputInner']"));
		assertThat(elements, notNullValue());
		assertThat(elements.size(), greaterThan(1));
		element = elements.get(0);
		fastSetText(element, application);
		element.sendKeys(Keys.ENTER);
		element = wait.until(
				ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector(
						"*[id *= 'uniqName_'] > div.selectableTable.webextTable.treeTable > table"))));
		elements = element.findElements(By.cssSelector(
				"tbody.treeTable-body > tr > td:nth-child(2) div.inlineBlock a[href *= '#application']"));
		assertThat(elements.size(), is(1));
		element = elements.get(0);
		highlight(element);

		assertThat(element.getText(), is(application));

		String href = element.getAttribute("href").replaceAll("^.*#application/",
				"");
		System.err.println("Application: " + element.getText() + " = " + href);
		actions.moveToElement(element).click().build().perform();
		wait.until(ExpectedConditions.urlContains(href));
		element = wait.until(ExpectedConditions
				.visibilityOf(driver.findElement(By.xpath(String.format(
						"//a[contains(@href, '#environment')][contains(text(), '%s')]",
						application)))));
		assertThat(element, notNullValue());
		System.err.println("Launcher: " + element.getAttribute("innerHTML"));
		elements = element.findElements(By.xpath("../.."));
		assertThat(elements.size(), is(1));
		element = elements.get(0)
				.findElement(By.cssSelector("div.request-process"));
		assertThat(element, notNullValue());
		element.click();

		wait.until(ExpectedConditions.visibilityOf(
				driver.findElement(By.cssSelector("div[role = 'dialog']"))));
		WebElement dialogElement = driver
				.findElement(By.cssSelector("div[role = 'dialog']"));
		assertThat(dialogElement, notNullValue());
		highlight(dialogElement);
		elements = dialogElement.findElements(By.cssSelector(
				"input.dijitInputInner[id *= 'dijit_form_FilteringSelect']"));
		assertThat(elements.size(), greaterThan(0));
		element = elements.get(0);
		String widgetid = element.getAttribute("id");
		System.err.println("Choice input id: " + widgetid);
		element.sendKeys(Keys.DOWN);
		sleep(1000);
		element.clear();
		sleep(1000);
		element.sendKeys(application);
		// fastSetText(element, "process two");
		// TODO: verify alert is not present
		element.sendKeys(Keys.DOWN);
		element.sendKeys(Keys.ENTER);
		sleep(1000);
		elements = dialogElement
				.findElements(By.cssSelector("div.linkPointer.inlineBlock"));
		assertThat(elements.size(), greaterThan(0));
		elements.stream().forEach(
				o -> System.err.println("inputs: " + o.getAttribute("outerHTML")));
		element = elements.stream()
				.filter(o -> o.getText().matches("Choose Versions"))
				.collect(Collectors.toList()).get(0);
		highlight(element);

		element.click();

		wait.until(ExpectedConditions.visibilityOf(driver.findElement(
				By.cssSelector("div.version-selection-dialog[role = 'dialog']"))));

		sleep(1000);
		/*
				elements = dialogElement.findElements(
						By.cssSelector(String.format("div[widgetid= '%s']", widgetid)));
				element = elements.get(0);
				String widget_owns = element.getAttribute("aria-owns");
				System.err.println("Choice popup widgetid: "
						+ element.getAttribute("widgetid") + " owns:" + widget_owns
						+ " expanded:" + element.getAttribute("aria-expanded"));
				System.err
						.println("Choice popup HTML: " + element.getAttribute("outerHTML"));
				try {
					// read the popup
					// dijit_form_FilteringSelect_3_popup
					elements = dialogElement.findElements(
							By.cssSelector(String.format("*[id= '%s']", widget_owns)));
					assertThat(elements.size(), greaterThan(0));
					element = elements.get(0);
					System.err.println("Choice popup: " + element.getAttribute("outerHTML"));
					element.click();
				} catch (ElementNotInteractableException e) {
					System.err.println("Exception (ignored): " + e.toString());
				}
				sleep(1000);
				*/
	}
}
