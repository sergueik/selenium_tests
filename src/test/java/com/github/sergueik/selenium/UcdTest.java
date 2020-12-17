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
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Keys;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/* 
 automation of UCD server web interface
 see also: https://www.ibm.com/support/knowledgecenter/SS4GSP_7.0.5/com.ibm.udeploy.tutorial.doc/topics/quickstart_abstract.html
 */
public class UcdTest extends BaseTest {

	private final boolean debug = (System.getenv("DEBUG") != null && System.getenv("DEBUG") != "");
	private static String ucdServerIp = getEnv("UCD_SERVER_IP", "192.168.0.64"); // 172.17.0.2
	private static String baseURL = String.format("https://%s:8443/", ucdServerIp);

	private static final String username = "admin";
	private static final String password = "admin";
	private static final String applicationName = "Test Application";
	private static final String environmentName = "TEST";
	private static final String snapshotName = "Test Snapshot";

	private static final String processName = "hello App Process";
	private static final String groupName = "resource_group";
	private static final String componentName = "hello Component";
	private static final String versionName = "1.0";

	private static WebElement element = null;
	private static WebElement element2 = null; // UCD ui is heavily styled
	private static WebElement dialogElement = null;
	private static WebElement popupElement = null;
	private static List<WebElement> elements = new ArrayList<>();
	private static String href = null;
	private static final int pauseTimeout = 3000;
	private static String urlFragment;

	@BeforeMethod
	public void beforeMethod() {
		driver.get(baseURL);
	}

	@AfterMethod
	public void afterMethod() {
		sleep(pauseTimeout);
		driver.get("about:blank");
	}

	@Test(enabled = false)
	public void test1() {
		userLogin();
		userSignOut();
	}

	@Test(enabled = false)
	public void test2() {
		userLogin();
		navigateToResourceTree();
		userSignOut();
	}

	// launch process dialog
	// this is a multi step test exercised for its side effect on UCD
	@Test(enabled = false)
	public void test3() {
		userLogin();
		navigateToLaunchDialog();
		launchProcess2();
		userSignOut();
	}

	// launch process dialog
	// the code is largely identical to test1 but uses keyboard navigation trick
	// without discovering the popup DOM
	@Test(enabled = false)
	public void test4() {
		userLogin();
		navigateToLaunchDialog();
		launchProcess();
		userSignOut();
	}

	@Test(enabled = false)
	public void test5() {
		userLogin();
		navigateToComponent();
		userSignOut();
	}

	@Test(enabled = false)
	public void test6() {
		userLogin();
		navigateToComponent();
		launchVersionImport();
		userSignOut();
	}

	//
	@Test(enabled = false)
	public void test7() {
		userLogin();
		navigateToComponent();
		navigateToComponentVersionIportHistory();
		userSignOut();
	}

	// snapshot
	@Test(enabled = true)
	public void test8() {
		userLogin();
		// navigateToApplication();
		navigateToSnapshot();
		// NOTE: need environment(s) to be created to succeed
		userSignOut();
	}

	private void launchProcess() {
		dialogElement = wait
				.until(ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector("div[role = 'dialog']"))));
		assertThat(dialogElement, notNullValue());
		highlight(dialogElement);
		elements = dialogElement
				.findElements(By.cssSelector("input.dijitInputInner[id *= 'dijit_form_FilteringSelect']"));
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

		popupElement = wait.until(
				ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector("div.dijitComboBoxMenuPopup"))));
		assertThat(popupElement, notNullValue());
		if (debug)
			System.err.println("Popup: " + popupElement.getAttribute("innerHTML"));

		highlight(popupElement);

		elements = popupElement.findElements(By.cssSelector("div.dijitMenuItem"));
		assertThat(elements.size(), greaterThan(0));
		element = elements.stream().filter(e -> e.getText().contains(processName)).collect(Collectors.toList()).get(0);
		assertThat(element, notNullValue());
		System.err.println("Popup: " + element.getAttribute("innerHTML"));
		highlight(element);
		element.click();
		// sleep(1000);
		element = wait.until(ExpectedConditions
				.visibilityOf(dialogElement.findElement(By.cssSelector("div.linkPointer.inlineBlock"))));
		assertThat(element, notNullValue());
		assertThat(element.getText(), is("Choose Versions"));
		highlight(element);

		element.click();

		wait.until(ExpectedConditions
				.visibilityOf(driver.findElement(By.cssSelector("div.version-selection-dialog[role = 'dialog']"))));
		// TODO: version selections dialog
		closeDialog("div.version-selection-dialog[role = 'dialog']");
		closeDialog("div[role = 'dialog']");
	}

	private void launchProcess2() {

		dialogElement = driver.findElement(By.cssSelector("div[role = 'dialog']"));
		assertThat(dialogElement, notNullValue());
		highlight(dialogElement);
		element = dialogElement
				.findElement(By.cssSelector("input.dijitInputInner[id *= 'dijit_form_FilteringSelect']"));
		assertThat(element, notNullValue());
		String widgetid = element.getAttribute("id");
		if (debug)
			System.err.println("Choice input id: " + widgetid);

		element.sendKeys(Keys.DOWN);
		sleep(1000);

		element.clear();
		sleep(1000);
		element.sendKeys(applicationName);
		// fastSetText(element, "process two");
		// TODO: verify alert is not present
		element.sendKeys(Keys.DOWN);
		element.sendKeys(Keys.ENTER);

		elements = dialogElement.findElements(By.cssSelector("div.linkPointer.inlineBlock"));
		assertThat(elements.size(), greaterThan(0));
		if (debug) {
			elements.stream().forEach(o -> System.err.println("Link: " + o.getAttribute("outerHTML")));
		}
		element = elements.stream().filter(o -> o.getText().matches("Choose Versions")).collect(Collectors.toList())
				.get(0);
		if (debug) {
			System.err.println("Choose Versions link: " + element.getAttribute("innerHTML"));
		}
		element.click();

		dialogElement = wait.until(ExpectedConditions
				.visibilityOf(driver.findElement(By.cssSelector("div.version-selection-dialog[role = 'dialog']"))));

		element = dialogElement.findElement(By.cssSelector("input.dijitReset.dijitInputInner"));
		assertThat(element, notNullValue());
		element.sendKeys(componentName);
		element.sendKeys(Keys.ENTER);
		element = dialogElement
				.findElement(By.xpath(".//a[contains(@class, 'linkPointer')][contains(text(),'Add...')]"));
		assertThat(element, notNullValue());
		// System.err
		// .println("Add Version link: " + element.getAttribute("innerHTML"));
		element.click();

		dialogElement = wait
				.until(ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector("div.versionSelect"))));
		assertThat(dialogElement, notNullValue());
		if (debug) {
			System.err.println("Version select: " + dialogElement.getAttribute("innerHTML"));
		}
		highlight(dialogElement);

		element = dialogElement.findElement(By.cssSelector("input.versionSelectTextBox"));
		assertThat(element, notNullValue());
		element.sendKeys(versionName);
		element.sendKeys(Keys.DOWN);

		element = wait.until(ExpectedConditions
				.visibilityOf(driver.findElement(By.xpath("//*[contains(@id, 'dijit_form_FilteringSelect_')]"))));
		assertThat(element, notNullValue());
		// non-optional delay
		sleep(5000);

		elements = driver.findElements(By.xpath("//*[contains(@id, 'dijit_form_FilteringSelect_')]"));
		elements.stream().map(o -> o.getText()).forEach(System.err::println);
		element = elements.stream().filter(o -> o.getText().matches(versionName)).collect(Collectors.toList()).get(0);
		assertThat(element, notNullValue());
		element.click();

		dialogElement = wait
				.until(ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector("div.versionSelect"))));
		assertThat(dialogElement, notNullValue());

		element = dialogElement.findElement(By.cssSelector("input.versionSelectTextBox"));
		assertThat(element, notNullValue());
		element.clear();
		// element.sendKeys(versionName);
		sleep(1000);
		element.sendKeys(Keys.ENTER);
		dialogElement.click();
		sleep(1000);

		closeDialog("div.version-selection-dialog[role = 'dialog']");

		closeDialog("div[role = 'dialog']");
	}

	private void userLogin() {
		try {
			element = wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.id("usernameField"))));
		} catch (NoSuchElementException e) {
			// no such element: Unable to locate element: {"method":"css
			// selector","selector":"#usernameField"}) {
			element = driver
					.findElement(By.cssSelector("form[action = '/tasks/LoginTasks/login' ] input[name = 'username']"));
		}
		element.sendKeys(username);
		element = driver.findElement(By.cssSelector("form input[name = 'password']"));
		fastSetText(element, password);
		element = driver.findElement(By.cssSelector("form span[widgetid = 'submitButton']"));
		highlight(element);
		element.click();
		urlFragment = "dashboard";
		urlFragment = "welcome";
		wait.until(ExpectedConditions.urlContains(urlFragment));
	}

	private void userSignOut() {
		element = wait.until(ExpectedConditions.visibilityOf(
				driver.findElement(By.cssSelector(String.format("div.idxHeaderPrimary a[title='%s']", username)))));
		assertThat(element, notNullValue());
		assertThat(element.getText(), is(username));
		element.click();
		element = wait.until(
				ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector("div.dijitPopup.dijitMenuPopup"))));
		assertThat(element, notNullValue());
		highlight(element);
		if (debug)
			System.err.println("User Sign out Popup: " + element.getAttribute("innerHTML").substring(0, 100));

		elements = element
				.findElements(By.xpath(".//td[contains(@class, 'dijitMenuItemLabel')][contains(text(),'Sign Out')]"));
		assertThat(elements.size(), greaterThan(0));
		element = elements.get(0);
		highlight(element);
		element.click();
	}

	private void navigateToResourceTree() {

		element = wait.until(ExpectedConditions.visibilityOf(
				driver.findElement(By.cssSelector("a.tab.linkPointer[href = '#main/resources'] span.tabLabel"))));
		assertThat(element, notNullValue());
		highlight(element);
		element.click();
		element = wait.until(ExpectedConditions.visibilityOf(driver.findElement(
				By.cssSelector("*[id *= 'uniqName_'] > div.selectableTable.webextTable.treeTable > table"))));
		assertThat(element, notNullValue());
		if (debug)
			System.err.println("table: " + element.getAttribute("innerHTML").substring(0, 100));

		elements = element.findElements(By.cssSelector("tr.noPrint.tableFilterRow input[class *= 'dijitInputInner']"));
		assertThat(elements, notNullValue());
		assertThat(elements.size(), greaterThan(1));
		element = elements.get(0);
		// select group by name
		fastSetText(element, groupName);
		highlight(element);
		element.sendKeys(Keys.ENTER);
		sleep(1000);
		// TODO: improve the selector
		element = wait.until(ExpectedConditions.visibilityOf(driver.findElement(
				By.cssSelector("*[id *= 'uniqName_'] > div.selectableTable.webextTable.treeTable > table"))));
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
		// if (debug) {
		// System.err.println(element.getAttribute("innerHTML"));
		// }
		element.click();
		element = wait.until(ExpectedConditions
				.visibilityOf(driver.findElement(By.cssSelector("div.masterContainer div.containerLabel"))));
		assertThat(element, notNullValue());
		highlight(element);
		assertThat(element.getText(), is("Subresources"));
	}

	// Navigate to versions tab and click Import button
	private void launchVersionImport() {
		element = wait.until(ExpectedConditions.visibilityOf(driver.findElement(
				By.cssSelector("a.tab.linkPointer[href ^= '#component'][href $= '/versions'] span.tabLabel"))));
		assertThat(element, notNullValue());
		assertThat(element.getText(), is("Versions"));
		highlight(element);
		if (debug) {
			System.err.println("Click on tab label: " + element.getText());
		}
		element.click();
		element = wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector(
				"div.versions div.listTopButtons span[role = 'presentation'] span[id^='dijit_form_Button']"))));
		assertThat(element, notNullValue());
		assertThat(element.getText(), is("Import New Versions"));
		highlight(element);
		element.click();
		sleep(1000);
		String dialogCssSelector = "div[class*='dijitDialogFocused']";
		// inputs
		element = wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector(String.format("%s %s",
				dialogCssSelector, "input[class*='dijitInputInner'][name='versionOrTag'][type='text']")))));
		assertThat(element, notNullValue());
		element.sendKeys("tag_name");
		element = wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector(String.format("%s %s",
				dialogCssSelector, "input[class*='dijitInputInner'][name='versionName'][type='text']")))));
		assertThat(element, notNullValue());
		element.sendKeys("version_name");

		// close element
		element = wait.until(ExpectedConditions.visibilityOf(
				driver.findElement(By.cssSelector(String.format("%s %s", dialogCssSelector, "span.closeDialogIcon")))));
		assertThat(element, notNullValue());
		assertThat(element.getAttribute("title"), is("Cancel"));
		highlight(element);
		element.click();
	}

	private void navigateToComponentVersionIportHistory() {
		String url = driver.getCurrentUrl();
		if (debug) {
			// capture the id
			//
			System.err.println("Current url to capture the id: " + url);
		}
		String compinentUUID = "1754e2f7-2349-1777-786d-c9e8b15643b6";
		element = wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector(String
				.format("a.tab.linkPointer[href = '#component/%s/configuration'] span.tabLabel", compinentUUID)))));
		assertThat(element, notNullValue());
		assertThat(element.getText(), is("Configuration"));
		highlight(element);
		if (debug) {
			System.err.println("Click on tab label: " + element.getText());
		}
		element.click();

		String panelCeeSelector = "div.twoPaneContainer div.twoPaneList";
		element = wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector(panelCeeSelector))));
		assertThat(element, notNullValue());
		element = driver.findElements(By.cssSelector("div.twoPaneContainer div.twoPaneList div.twoPaneEntry")).stream()
				.filter(o -> o.getText().equals("Version Import History")).findFirst().get();
		assertThat(element, notNullValue());
		highlight(element);
	}

	private void navigateToApplication() {

		// switch to Applications
		element = wait.until(ExpectedConditions.visibilityOf(
				driver.findElement(By.cssSelector("a.tab.linkPointer[href = '#main/applications'] span.tabLabel"))));
		assertThat(element, notNullValue());
		highlight(element);
		element.click();
		// locate the filter input
		element = wait.until(ExpectedConditions.visibilityOf(driver.findElement(
				By.cssSelector("*[id *= 'uniqName_'] > div.selectableTable.webextTable.treeTable > table"))));
		assertThat(element, notNullValue());
		if (debug) {
			System.err.println("table: " + element.getAttribute("innerHTML").substring(0, 100));
		}
		elements = element.findElements(By.cssSelector("tr.noPrint.tableFilterRow input[class *= 'dijitInputInner']"));
		assertThat(elements, notNullValue());
		assertThat(elements.size(), greaterThan(1));
		element = elements.get(0);
		if (debug) {
			System.err.println("Application link: " + element.getAttribute("outerHTML"));
		}
		// select Application by name
		fastSetText(element, applicationName);
		element.sendKeys(Keys.ENTER);
		// NOTE: do we need the following
		actions.moveToElement(element).click().perform();
		sleep(1000);
		if (debug)
			System.err.println("Selected Application by name: " + applicationName);
		element = null;

		element = wait.until(ExpectedConditions.visibilityOf(driver.findElement(
				By.cssSelector("*[id *= 'uniqName_'] > div.selectableTable.webextTable.treeTable > table"))));
		elements = element.findElements(By
				.cssSelector("tbody.treeTable-body > tr > td:nth-child(2) div.inlineBlock a[href *= '#application']"));
		assertThat(elements.size(), is(1));
		element = elements.get(0);
		highlight(element);

		assertThat(element.getText(), is(applicationName));
		if (debug)
			System.err.println("Selected Application link with the expected text: " + applicationName);

		href = element.getAttribute("href").replaceAll("^.*#application/", "");
		if (debug)
			System.err.println("Click the application link: " + element.getText() + " = " + href);

		actions.moveToElement(element).click().build().perform();
		wait.until(ExpectedConditions.urlContains(href));
		sleep(1000);
	}

	// expects the exact snapshot name to be passed via global snapshotName
	// member
	private void navigateToSnapshot() {
		// switch to Applications
		navigateToApplication();
		element = wait.until(ExpectedConditions.visibilityOf(
				driver.findElement(By.cssSelector("a.tab.linkPointer[href ^= '#application/'][href $= 'snapshots']"))));

		assertThat(element, notNullValue());
		highlight(element);
		// NOTE: captures do not work here
		href = element.getAttribute("href").replaceAll("^.*#application/", "#application/");

		elements = element.findElements(By.cssSelector("span.tabLabel"));

		assertThat(elements, notNullValue());
		element2 = elements.get(0);
		highlight(element2);
		assertThat(element2.getText(), is("Snapshots"));
		if (debug)
			System.err.println("Click the snapshots link: " + element2.getText() + " = " + href);

		element2.click();
		// actions.moveToElement(element).click().build().perform();
		wait.until(ExpectedConditions.urlContains(href));

		element = wait.until(ExpectedConditions.visibilityOf(driver.findElement(
				By.cssSelector("*[id *= 'uniqName_'] > div.selectableTable.webextTable.treeTable > table"))));
		assertThat(element, notNullValue());
		if (debug)
			System.err.println("table: " + element.getAttribute("innerHTML").substring(0, 100));
		highlight(element);

		elements = element.findElements(By.cssSelector("tr.noPrint.tableFilterRow input[class *= 'dijitInputInner']"));
		assertThat(elements, notNullValue());
		assertThat(elements.size(), greaterThan(0));
		element = elements.get(0);
		// filter snapshot by name
		fastSetText(element, snapshotName);
		highlight(element);
		element.sendKeys(Keys.ENTER);
		// TODO: simplify the overly detailed selector
		element = wait.until(ExpectedConditions.visibilityOf(driver.findElement(
				By.cssSelector("*[id *= 'uniqName_'] > div.selectableTable.webextTable.treeTable > table"))));
		assertThat(element, notNullValue());
		highlight(element);
		/*
		 * if (debug) System.err.println(element.getAttribute("innerHTML"));
		 */
		highlight(element);
		sleep(1000);
		elements = element.findElements(By.cssSelector("div.inlineBlock a.actionsLink[href ^= '#snapshot']"));
		assertThat(elements.size(), greaterThan(0));
		element = elements.get(0);
		href = element.getAttribute("href").replaceAll("^.*#snapshot/", "#snapshot/");
		if (debug)
			System.err.println("Click the snapshot link: " + element.getText() + " = " + href);

		highlight(element);
		element.click();
		wait.until(ExpectedConditions.urlContains(href));
		sleep(10000);

	}

	// expects the exact component name to be passed via global componentName
	// member
	private void navigateToComponent() {
		element = wait.until(ExpectedConditions.visibilityOf(
				driver.findElement(By.cssSelector("a.tab.linkPointer[href = '#main/components'] span.tabLabel"))));
		assertThat(element, notNullValue());
		highlight(element);
		element.click();
		element = wait.until(ExpectedConditions.visibilityOf(driver.findElement(
				By.cssSelector("*[id *= 'uniqName_'] > div.selectableTable.webextTable.treeTable > table"))));
		assertThat(element, notNullValue());
		if (debug)
			System.err.println("table: " + element.getAttribute("innerHTML").substring(0, 100));

		elements = element.findElements(By.cssSelector("tr.noPrint.tableFilterRow input[class *= 'dijitInputInner']"));
		assertThat(elements, notNullValue());
		assertThat(elements.size(), greaterThan(1));
		element = elements.get(0);
		// filter component by name
		fastSetText(element, componentName);
		highlight(element);
		element.sendKeys(Keys.ENTER);
		sleep(1000);
		// TODO: simplify the overly detailed selector
		element = wait.until(ExpectedConditions.visibilityOf(driver.findElement(
				By.cssSelector("*[id *= 'uniqName_'] > div.selectableTable.webextTable.treeTable > table"))));
		assertThat(element, notNullValue());
		highlight(element);
		// TODO: engage jsoup ?
		if (debug)
			System.err.println(element.getAttribute("innerHTML"));

		elements = element.findElements(By.cssSelector("div.inlineBlock > a[href ^= '#component']"));
		assertThat(elements.size(), greaterThan(0));
		element = elements.get(0);
		WebElement parentElement = element;
		boolean found = false;
		while (!found) {
			try {
				parentElement = element.findElement(By.xpath(".."));
				if (parentElement.getTagName().toLowerCase().indexOf("tr") == 0) {
					found = true;
					break;
				} else {
					element = parentElement;
				}
			} catch (InvalidSelectorException e) {
				break;
			}
		}
		if (debug) {
			System.err.println(
					String.format("Parent element: \"%s\" %b", parentElement.getTagName().toLowerCase(), found));
		}
		//
		element = parentElement.findElement(By.cssSelector("input[type='checkbox']"));
		assertThat(element, notNullValue());
		highlight(element);
		element.sendKeys(Keys.SPACE);
		element = elements.get(0);
		highlight(element);
		assertThat(element.getText(), is(componentName));
		// System.err.println(element.getAttribute("innerHTML"));
		element.click();
		element = wait.until(ExpectedConditions
				.visibilityOf(driver.findElement(By.cssSelector("div.masterContainer div.containerLabel"))));
		assertThat(element, notNullValue());
		highlight(element);
		assertThat(element.getText(), is("Inventory For Component"));
	}

	private void navigateToLaunchDialog() {
		// switch to Applications
		navigateToApplication();
		// TODO: integrate with stop dalog

		// open launch dialog
		element = wait.until(ExpectedConditions.visibilityOf(driver
				.findElement(By.xpath(String.format("//a[contains(@href, '#environment')][contains(text(), '%s')]",
						/* applicationName */ environmentName)))));
		assertThat(element, notNullValue());
		if (debug) {
			System.err.println("Launcher: " + element.getAttribute("innerHTML"));
		}
		// title="test - (test environment)"
		elements = element.findElements(By.xpath("../.."));
		assertThat(elements.size(), is(1));
		element = elements.get(0).findElement(By.cssSelector("div.request-process"));
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
