package com.github.sergueik.selenium;

import static org.testng.Assert.assertTrue;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.testng.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TenminemailTest extends BaseTest {

	// based on discussion:
	// https://automated-testing.info/t/kak-dostat-tekst-iz-polya-kogda-znachenie-polya-ne-hranitsya-v-html-elemente/23318/4
	// https://10minemail.com/en/
	private static String baseURL = "https://10minemail.com/en/";
	private static final StringBuffer verificationErrors = new StringBuffer();
	private static String defaultScript = null;

	@BeforeMethod
	public void BeforeMethod(Method method) {
		super.beforeMethod(method);
		driver.get(baseURL);
	}

	@AfterMethod
	public void AfterMethod(ITestResult result) {
		if (verificationErrors.length() != 0) {
			throw new RuntimeException(String.format("Error(s) in the method %s : %s",
					result.getMethod().getMethodName(), verificationErrors.toString()));
		}
		driver.get("about:blank");
	}

	private final static String emailInputCssSelector = "#mail";

	//
	@Test(enabled = true)
	public void getNewRandomEmailTest() {
		// Arrange
		WebElement buttonElement;
		try {
			buttonElement = wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//*[@id='click-to-delete']")));
			// NOTE: unnoticed bad copy paste quotes in XPath
			// "//*[@id=“click-to-delete”]"
			// lead to TimeoutException waiting for visibility of element located
			// by By.xpath: //*[@id=?click-to-delete?]
			if (buttonElement != null) {
				actions.moveToElement(buttonElement).build().perform();
				buttonElement.click();
				sleep(1000);
			}
		} catch (TimeoutException e) {
			System.err.println("Exception (aborting) " + e.toString());
			return;
		}
		WebElement emailInputElement = driver
				.findElement(By.cssSelector(emailInputCssSelector));
		System.err.println(
				"Input element: " + emailInputElement.getAttribute("outerHTML"));

		String email = (String) executeScript(String
				.format("var element=document.querySelector('%s');" + "var elementText="
						+ "element.getAttribute('value').replace(/\\n/, ' ')" + "||"
						+ "element.textContent.replace(/\\n/, ' ')" + "||"
						+ "element.innerText.replace(/\\n/, ' ')" + "||"
						+ "element.getAttribute('placeholder')" + "||" + "'';"
						+ "return elementText;", emailInputCssSelector));
		System.err.println("input#mail text: " + email); // empty
		assertThat(email, is(""));
		emailInputElement.click(); // stale element reference is posible
		System.err.println("Clicked input");
		// sleep(1000);
		email = (String) executeScript("return window.getSelection().toString()");
		assertThat(email, notNullValue());
		System.err.println("example email (try 1): " + email); // not empty
		email = (String) executeScript("function getSelectionText() { "
				+ "var text = '';" + "if (window.getSelection) {"
				+ "    text = window.getSelection().toString();"
				+ " } else if (document.selection && document.selection.type != 'Control') {"
				+ "     text = document.selection.createRange().text;" + "  }"
				+ "   return text;" + "};return getSelectionText();");
		assertThat(email, notNullValue());
		System.err.println("example email (try 2): " + email); // not empty
		try {
			highlight(emailInputElement);
		} catch (StaleElementReferenceException e) {
			System.err.println("Exception (ignored) " + e.toString());
		}
	}

	//
	@Test(enabled = true)
	public void getStaleEmailElementReferenceTest() {
		// Arrange
		WebElement buttonElement;
		try {
			buttonElement = wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//*[@id='click-to-delete']")));
			if (buttonElement != null) {

				actions.moveToElement(buttonElement).build().perform();
				buttonElement.click();
				sleep(1000);
			}
		} catch (TimeoutException e) {
			System.err.println("Exception (ignored) " + e.toString());
		} catch (RuntimeException e) {
			System.err.println("Exception (ignored) " + e.toString());
			return;
		}
		WebElement emailInputElement = driver
				.findElement(By.cssSelector(emailInputCssSelector));
		emailInputElement.click();
		System.err.println("Clicked input");
		emailInputElement.click();
		sleep(10000);
		String email = (String) executeScript(
				"return window.getSelection().toString()");
		System.err.println("example email : " + email);
		// non-empty, but looking for stale element reference
		try {
			highlight(emailInputElement);
		} catch (StaleElementReferenceException e) {
			System.err.println("Exception * (ignored) " + e.toString());
		}

		//
	}
}
