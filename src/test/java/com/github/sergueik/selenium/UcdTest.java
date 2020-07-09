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
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class UcdTest extends BaseTest {

	private final static boolean debug = true;
	private static String ucdHost = "192.168.0.64";
	private static String baseURL = String.format("https://%s:8443/", ucdHost);

	private static final String username = "admin";
	private static final String password = "admin";
	private static final String applicationName = "hello Application";
	private static final String processName = "hello App Process";
	private static final String groupName = "helloWorld Tutorial";

	private static WebElement element = null;
	private static WebElement dialogElement = null;
	private static WebElement popupElement = null;
	private static List<WebElement> elements = new ArrayList<>();
	private static String href = null;
	private static final int pauseTimeout = 5000;

	@BeforeMethod
	public void beforeMethod() {
		driver.get(baseURL);
	}

	@AfterMethod
	public void afterMethod() {
		sleep(pauseTimeout);
	}

	// this is a multi step test exercised for its side effect on UCD
	@Test(enabled = false)
	public void test1() {
		userLogin();
		navigateToLaunchDialog();
		dialogElement = wait.until(ExpectedConditions.visibilityOf(
				driver.findElement(By.cssSelector("div[role = 'dialog']"))));
		assertThat(dialogElement, notNullValue());
		highlight(dialogElement);
		elements = dialogElement.findElements(By.cssSelector(
				"input.dijitInputInner[id *= 'dijit_form_FilteringSelect']"));
		assertThat(elements.size(), greaterThan(0));
		element = elements.get(0);
		String widgetid = element.getAttribute("id");
		if (debug) {
			System.err.println("Choice input id: " + widgetid);
		}
		element.sendKeys(Keys.DOWN);

		// To find DOM of Javascript-generated popup run Chrome with extension
		// "View Rendered Source"
		// https://chrome.google.com/webstore/detail/view-rendered-source/ejgngohbdedoabanmclafpkoogegdpob?hl=en
		// "View Generated Source"
		// https://chrome.google.com/webstore/detail/view-generated-source/epmicgdiljcefknmbppapkbaakbgacjm?hl=en

		popupElement = wait.until(ExpectedConditions.visibilityOf(
				driver.findElement(By.cssSelector("div.dijitComboBoxMenuPopup"))));
		assertThat(popupElement, notNullValue());
		if (debug) {
			System.err.println("Popup: " + popupElement.getAttribute("innerHTML"));
		}
		highlight(popupElement);

		elements = popupElement.findElements(By.cssSelector("div.dijitMenuItem"));
		assertThat(elements.size(), greaterThan(0));
		element = elements.stream().filter(e -> e.getText().contains(processName))
				.collect(Collectors.toList()).get(0);
		assertThat(element, notNullValue());
		System.err.println("Popup: " + element.getAttribute("innerHTML"));
		highlight(element);
		element.click();
		// sleep(1000);
		element = wait.until(ExpectedConditions.visibilityOf(dialogElement
				.findElement(By.cssSelector("div.linkPointer.inlineBlock"))));
		assertThat(element, notNullValue());
		assertThat(element.getText(), is("Choose Versions"));
		highlight(element);

		element.click();

		wait.until(ExpectedConditions.visibilityOf(driver.findElement(
				By.cssSelector("div.version-selection-dialog[role = 'dialog']"))));
		// TODO: version selections dialog
		closeDialog("div.version-selection-dialog[role = 'dialog']");
		closeDialog("div[role = 'dialog']");
		userSignOut();
	}

	// the code is largely identical to test1 but uses keyboard navigation treick
	// without discoderng the pipup DOM
	@Test(enabled = false)
	public void test2() {
		userLogin();
		navigateToLaunchDialog();
		dialogElement = driver.findElement(By.cssSelector("div[role = 'dialog']"));
		assertThat(dialogElement, notNullValue());
		highlight(dialogElement);
		element = dialogElement.findElement(By.cssSelector(
				"input.dijitInputInner[id *= 'dijit_form_FilteringSelect']"));
		assertThat(element, notNullValue());
		String widgetid = element.getAttribute("id");
		if (debug) {
			System.err.println("Choice input id: " + widgetid);
		}
		element.sendKeys(Keys.DOWN);
		sleep(1000);

		element.clear();
		sleep(1000);
		element.sendKeys(applicationName);
		// fastSetText(element, "process two");
		// TODO: verify alert is not present
		element.sendKeys(Keys.DOWN);
		element.sendKeys(Keys.ENTER);

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

		dialogElement = wait
				.until(ExpectedConditions.visibilityOf(driver.findElement(
						By.cssSelector("div.version-selection-dialog[role = 'dialog']"))));
		element = dialogElement.findElement(By.cssSelector("span.closeDialogIcon"));
		assertThat(element, notNullValue());
		element.click();
		// closeDialog("div.version-selection-dialog[role = 'dialog']");

		dialogElement = driver.findElement(By.cssSelector("div[role = 'dialog']"));
		element = dialogElement.findElement(By.cssSelector("span.closeDialogIcon"));
		assertThat(element, notNullValue());
		element.click();

		// closeDialog("div[role = 'dialog']");
		userSignOut();
	}

	@Test(enabled = false)
	public void test3() {
		userLogin();
		userSignOut();
	}

	@Test(enabled = true)
	public void test4() {
		userLogin();
		navigateToResourceTree();
		sleep(10000);
		userSignOut();
	}

	private void userLogin() {
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
	}

	private void userSignOut() {
		element = wait.until(
				ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector(
						String.format("div.idxHeaderPrimary a[title='%s']", username)))));
		assertThat(element, notNullValue());
		assertThat(element.getText(), is(username));
		element.click();
		element = wait.until(ExpectedConditions.visibilityOf(
				driver.findElement(By.cssSelector("div.dijitPopup.dijitMenuPopup"))));
		assertThat(element, notNullValue());
		highlight(element);
		if (debug) {
			System.err.println("Popup: " + element.getAttribute("innerHTML"));
		}
		elements = element.findElements(By.xpath(
				".//td[contains(@class, 'dijitMenuItemLabel')][contains(text(),'Sign Out')]"));
		assertThat(elements.size(), greaterThan(0));
		element = elements.get(0);
		highlight(element);
		element.click();
	}

	private void navigateToResourceTree() {
		element = wait.until(
				ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector(
						"a.tab.linkPointer[href = '#main/resources'] span.tabLabel"))));
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
		// select group by name
		fastSetText(element, groupName);
		highlight(element);
		element.sendKeys(Keys.ENTER);
		sleep(1000);
		// TODO: improve the selector
		element = wait.until(
				ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector(
						"*[id *= 'uniqName_'] > div.selectableTable.webextTable.treeTable > table"))));
		assertThat(element, notNullValue());
		highlight(element);
		// if (debug) {
		// System.err.println(element.getAttribute("innerHTML"));
		// }
		elements = element.findElements(By.cssSelector(
				"tbody.treeTable-body > tr:nth-of-type(1) > td:nth-of-type(3) div.inlineBlock a[href *= '#resource']"));
		assertThat(elements.size(), is(1));
		element = elements.get(0);
		highlight(element);
		assertThat(element.getText(), is(groupName));
		// System.err.println(element.getAttribute("innerHTML"));
		element.click();
		element = wait.until(ExpectedConditions.visibilityOf(driver.findElement(
				By.cssSelector("div.masterContainer div.containerLabel"))));
		assertThat(element, notNullValue());
		highlight(element);
		assertThat(element.getText(), is("Subresources"));
	}

	private void navigateToLaunchDialog() {
		// switch to Applications
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
		// select Application by name
		fastSetText(element, applicationName);
		element.sendKeys(Keys.ENTER);
		sleep(1000);
		element = wait.until(
				ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector(
						"*[id *= 'uniqName_'] > div.selectableTable.webextTable.treeTable > table"))));
		elements = element.findElements(By.cssSelector(
				"tbody.treeTable-body > tr > td:nth-child(2) div.inlineBlock a[href *= '#application']"));
		assertThat(elements.size(), is(1));
		element = elements.get(0);
		highlight(element);

		assertThat(element.getText(), is(applicationName));

		href = element.getAttribute("href").replaceAll("^.*#application/", "");
		if (debug) {
			System.err.println("Application: " + element.getText() + " = " + href);
		}
		actions.moveToElement(element).click().build().perform();
		wait.until(ExpectedConditions.urlContains(href));
		element = wait.until(ExpectedConditions
				.visibilityOf(driver.findElement(By.xpath(String.format(
						"//a[contains(@href, '#environment')][contains(text(), '%s')]",
						applicationName)))));
		assertThat(element, notNullValue());
		if (debug) {
			System.err.println("Launcher: " + element.getAttribute("innerHTML"));
		}
		elements = element.findElements(By.xpath("../.."));
		assertThat(elements.size(), is(1));
		element = elements.get(0)
				.findElement(By.cssSelector("div.request-process"));
		assertThat(element, notNullValue());
		element.click();
	}

	private void closeDialog(String selector) {
		dialogElement = driver.findElement(By.cssSelector(selector));
		element = dialogElement.findElement(By.cssSelector("span.closeDialogIcon"));
		assertThat(element, notNullValue());
		element.click();

	}

}
