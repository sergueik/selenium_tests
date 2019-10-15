package com.github.sergueik.selenium;

import static org.testng.Assert.assertTrue;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.testng.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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

import com.github.sergueik.selenium.BaseTest;

public class TenminemailTest extends BaseTest {

	// based on discussion:
	// https://automated-testing.info/t/kak-dostat-tekst-iz-polya-kogda-znachenie-polya-ne-hranitsya-v-html-elemente/23318/4
	// https://10minemail.com/en/
	private static String baseURL = "https://10minemail.com/en/";
	private static final StringBuffer verificationErrors = new StringBuffer();
	private final static String cssSelector = "#mail";

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

	//
	@Test(enabled = true)
	public void getNewRandomEmailTest() {
		// Arrange
		WebElement buttonElement;
		try {
			buttonElement = wait
					.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='click-to-delete']")));
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
		WebElement emailInputElement = driver.findElement(By.cssSelector(cssSelector));
		System.err.println("Input element: " + emailInputElement.getAttribute("outerHTML"));

		String text = (String) executeScript(String.format(
				"var element=document.querySelector('%s');" + "var elementText="
						+ "element.getAttribute('value').replace(/\\n/, ' ')" + "||"
						+ "element.textContent.replace(/\\n/, ' ')" + "||" + "element.innerText.replace(/\\n/, ' ')"
						+ "||" + "element.getAttribute('placeholder')" + "||" + "'';" + "return elementText;",
				cssSelector));
		System.err.println("input#mail text: " + text);
		assertThat(text, is(""));
		emailInputElement.click(); // stale element reference is possible
		System.err.println("Clicked input");
		// sleep(1000);
		text = (String) executeScript("return window.getSelection().toString()");
		assertThat(text, notNullValue());
		System.err.println("Selection text email (try 1): " + text); // not empty
		text = (String) executeScript("function getSelectionText() { " + "var text = '';" + "if (window.getSelection) {"
				+ "    text = window.getSelection().toString();"
				+ " } else if (document.selection && document.selection.type != 'Control') {"
				+ "     text = document.selection.createRange().text;" + "  }" + "   return text;"
				+ "};return getSelectionText();");
		assertThat(text, notNullValue());
		System.err.println("Selection text email (try 2): " + text); // not empty
		text = getSelectionText();
		System.err.println("Selection text email (try 3): " + text);
		// non-empty, but still looking for triggering stale element reference
		text = (String) executeScript(getScriptContent("getText.js"), driver.findElement(By.cssSelector(cssSelector)),
				false);
		// innerText of input DOM element type is empty
		assertThat(text, is(""));
		System.err.println("Input text (try 1) : " + text);
		System.err.println(
				"Input value (try 1): " + driver.findElement(By.cssSelector(cssSelector)).getAttribute("value"));

		text = (String) executeScript(String.format("var element=document.querySelectorAll('%s')[0];"
				+ "var elementText=" + "element.value.replace(/\\n/, ' ')" + "||"
				+ "element.textContent.replace(/\\n/, ' ')" + "||" + "element.innerText.replace(/\\n/, ' ')" + "||"
				+ "element.getAttribute('placeholder')" + "||" + "'';" + "return elementText;", cssSelector));
		System.err.println("Text or value (try 1) : " + text); // empty

		@SuppressWarnings("unchecked")
		// example email text (try 5) : [aria-describedby, class,
		// data-original-title, data-placement, id, onclick, readonly, type,
		// value]
		Map<String, Object> attributes = (Map<String, Object>) executeScript(getScriptContent("getAttributes.js"),
				driver.findElement(By.cssSelector(cssSelector)));
		System.err.println("Attributes: ");
		for (Entry<String, Object> entry : attributes.entrySet()) {
			System.err.println(String.format("%s = \"%s\"", entry.getKey(), entry.getValue().toString()));
		}
		System.err.println("Input value (try 2): " + attributes.get("value"));
		try {
			highlight(emailInputElement);
		} catch (StaleElementReferenceException e) {
			System.err.println("Exception (ignored) " + e.toString());
		}
	}

}