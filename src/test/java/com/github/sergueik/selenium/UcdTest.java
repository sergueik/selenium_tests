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

		//
		// TODO: to see the DOM of the popup run with one of Chrome extensions
		// View Rendered Source
		// https://chrome.google.com/webstore/detail/view-rendered-source/ejgngohbdedoabanmclafpkoogegdpob?hl=en
		// View Generated Source
		// https://chrome.google.com/webstore/detail/view-generated-source/epmicgdiljcefknmbppapkbaakbgacjm?hl=en
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

		// TODO: clear "display:none" attribute
		// of data enclosing table
		// table class="dijit dijitMenu dijitMenuPassive dijitReset dijitMenuTable
		// my-profile-menu oneuiHeaderGlobalActionsMenu" role="menu" tabindex="0"
		// data-dojo-attach-event="onkeypress:_onKeyPress" cellspacing="0"
		// id="dijit_Menu_1" widgetid="dijit_Menu_1" style="display: none;
		/*
				String script = "var selector = arguments[0]; \n"
						+ "var nodes = window.document.querySelectorAll(selector);"
						+ "var element = nodes[0];\n" + "element.getAttribute('style', '');";
				// made table visible - not the node we are looking for
				js.executeScript(script, "table.dijitMenuPassive");
			*/
		WebElement popupElement = wait.until(ExpectedConditions.visibilityOf(
				driver.findElement(By.cssSelector("div.dijitComboBoxMenuPopup"))));
		assertThat(popupElement, notNullValue());
		System.err.println("Popup: " + popupElement.getAttribute("innerHTML"));
		highlight(popupElement);
		// wait.until(ExpectedConditions.visibilityOf(driver.findElement(
		// By.cssSelector("div.dijitComboBoxMenuPopup div.dijitMenuItem"))));
		elements = popupElement.findElements(By.cssSelector("div.dijitMenuItem"));
		assertThat(elements.size(), greaterThan(0));
		element = elements.stream()
				.filter(e -> e.getText().contains("hello App Process"))
				.collect(Collectors.toList()).get(0);
		System.err.println("Popup: " + element.getAttribute("innerHTML"));
		highlight(element);
		element.click();
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
	}
}
