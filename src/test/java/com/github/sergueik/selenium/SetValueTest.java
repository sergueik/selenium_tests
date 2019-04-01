package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Selected test scenarios for Selenium WebDriver
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class SetValueTest extends BaseTest {

	private static String baseURL = "https://www.seleniumeasy.com/test/input-form-demo.html";
	private static String text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum";
	private static String selector = "form#contact_form fieldset div.input-group textarea.form-control[name='comment']";
	private static String parentXpath = "//form[@id = 'contact_form']//div[@class='input-group'][textarea[@name='comment']]";
	private static final StringBuffer verificationErrors = new StringBuffer();

	@BeforeMethod
	public void BeforeMethod(Method method) {
		super.beforeMethod(method);
		driver.get(baseURL);
		ExpectedCondition<Boolean> urlChange = driver -> driver.getCurrentUrl()
				.matches(String.format("^%s.*", baseURL));
		wait.until(urlChange);
		System.err.println("Current  URL: " + driver.getCurrentUrl());
	}

	@AfterMethod
	public void AfterMethod(ITestResult result) {
		if (verificationErrors.length() != 0) {
			throw new RuntimeException(String.format("Error(s) in the method %s : %s",
					result.getMethod().getMethodName(), verificationErrors.toString()));
		}
		driver.get("about:blank");
	}

	@Test(enabled = true)
	public void sendKeysTest() {
		WebElement element = wait.until(ExpectedConditions
				.visibilityOf(driver.findElement(By.cssSelector(selector))));
		assertThat(element, notNullValue());
		assertThat(element.getText(), is(""));
		String value = text.substring(0, 100);
		element.sendKeys(value);
		element = driver.findElement(By.cssSelector(selector));
		assertThat(element.getAttribute("value"), is(value));
		System.err
		.println(String.format("Returned: %s", element.getAttribute("value")));
		sleep(1000);
		highlight(element.findElement(By.xpath("../..")), 1000);
	}

	@Test(enabled = true)
	public void fastSetTextTest() {

		WebElement element = wait.until(ExpectedConditions
				.visibilityOf(driver.findElement(By.cssSelector(selector))));
		assertThat(element, notNullValue());
		assertThat(element.getText(), is(""));
		String value = text.substring(0, 100);
		super.fastSetText(element, value);
		element = driver.findElement(By.cssSelector(selector));
		assertThat(element.getAttribute("value"), is(value));
		System.err
		.println(String.format("Returned: %s", element.getAttribute("value")));
		sleep(1000);
		highlight(element.findElement(By.xpath("../..")), 1000);
		WebElement parentElement = driver.findElement(By.xpath(parentXpath));
		assertThat(parentElement, notNullValue());
		System.err.println(String.format("Parent Element: %s",
				parentElement.getAttribute("outerHTML")));
		highlight(parentElement, 1000);
		parentElement = getParentBlockElement(element);
		assertThat(parentElement, notNullValue());
		highlight(parentElement, 1000);
		jqueryHover(selector);
	}

	@Test(enabled = true)
	public void fastSetTextSelectorTest() {

		WebElement element = wait.until(ExpectedConditions
				.visibilityOf(driver.findElement(By.cssSelector(selector))));
		assertThat(element, notNullValue());
		assertThat(element.getText(), is(""));
		String value = text.substring(0, 100);
		super.fastSetText(selector, value);
		element = driver.findElement(By.cssSelector(selector));
		assertThat(element.getAttribute("value"), is(value));
		System.err
		.println(String.format("Returned: %s", element.getAttribute("value")));
		sleep(1000);
		highlight(element.findElement(By.xpath("..")), 1000);
		jqueryHover(selector);
	}
}
