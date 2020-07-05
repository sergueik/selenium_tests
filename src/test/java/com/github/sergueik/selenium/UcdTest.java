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

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
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
		// https://192.168.0.64:8443/#application

		String href = element.getAttribute("href").replaceAll("^.*#application/",
				"");
		System.err.println("Application: " + element.getText() + " = " + href);
		sleep(1000);
		// element.click();
		actions.moveToElement(element).click().build().perform();
		wait.until(ExpectedConditions.urlContains(href));
		sleep(1000);
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
		sleep(5000);
	}
}
