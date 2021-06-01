package com.github.sergueik.selenium;

import static java.lang.System.err;
import static java.lang.System.out;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.io.IOException;
import java.net.MalformedURLException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

// https://www.baeldung.com/junit-before-beforeclass-beforeeach-beforeall

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SvgLogoTest extends BaseTest {

	private final static String baseUrl = "https://qna.habr.com/";
	// Element with CSS #js-canvas svg.icon_svg.icon_logo is not present on screen
	// private static final String urlLocator = "#js-canvas a.logo > *[viewBox] >
	// ";

	@BeforeMethod
	public void beforMethod() {
		driver.navigate().to(baseUrl);
	}

	// NOTE: Testng has no "Assume"
	// test should be run as mvn clean test -Dwebdriver.driver=firefox
	@Test(enabled = true)
	public void test1() {
		final String urlLocator = "#js-canvas a.logo > *[viewBox]";

		WebElement element = wait.until(ExpectedConditions
				.visibilityOf(driver.findElement(By.cssSelector(urlLocator))));
		System.err.println(element.getAttribute("outerHTML"));
		sleep(5000);
	}

	@Test(enabled = true, expectedExceptions = { NoSuchElementException.class,
			NullPointerException.class })
	public void test2() {
		final String urlLocator = "#js-canvas a.logo path";
		WebElement element = wait.until(ExpectedConditions
				.visibilityOf(driver.findElement(By.cssSelector(urlLocator))));
		System.err.println(element.getAttribute("outerHTML"));
	}

	@Test(enabled = true, expectedExceptions = { NoSuchElementException.class })
	// Unable to locate element:
	/*
	{
		"method":"xpath",
		"selector":"//*[@id ='js-canvas']//a[contains(@class ,'logo')]//*[local-name()='svg']/*[local-name()='path']"}
	}
	*/
	public void test3() {
		String xpath = "//*[@id = 'js-canvas']//a[contains(@class , 'logo')]";
		WebElement element = wait.until(
				ExpectedConditions.visibilityOf(driver.findElement(By.xpath(xpath))));
		System.err.println(element.getAttribute("outerHTML"));
		sleep(1000);
		xpath = "//*[@id = 'js-canvas']//a[contains(@class , 'logo')]//*[local-name()='svg']";
		element = wait.until(
				ExpectedConditions.visibilityOf(driver.findElement(By.xpath(xpath))));
		System.err.println(element.getAttribute("outerHTML"));
		sleep(1000);
		xpath = "//*[@id = 'js-canvas']//a[contains(@class , 'logo')]//*[local-name()='svg']/*[local-name()='path']";
		element = wait.until(
				ExpectedConditions.visibilityOf(driver.findElement(By.xpath(xpath))));
		System.err.println(element.getAttribute("outerHTML"));
		sleep(5000);
	}

}
