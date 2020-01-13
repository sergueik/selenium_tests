package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Key;
import org.sikuli.script.Match;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;

import static java.lang.System.err;
import static java.lang.System.out;

/**
 * Selected test scenarios for Selenium WebDriver
 * see also: https://intoli.com/blog/chrome-extensions-with-selenium/
 * https://www.blazemeter.com/blog/6-easy-steps-testing-your-chrome-extension-selenium/
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

// based on
// https://stackoverflow.com/questions/25557533/open-a-chrome-extension-through-selenium-webdriver
// https://www.blazemeter.com/blog/6-easy-steps-testing-your-chrome-extension-selenium
// https://stackoverflow.com/questions/25557533/open-a-chrome-extension-through-selenium-webdriver
// http://www.software-testing-tutorials-automation.com/2016/05/how-to-get-browser-and-os-details-on.html
// http://screenster.io/running-tests-from-selenium-ide-in-chrome/ screenster
// http://screenster.io/running-tests-from-selenium-ide-in-chrome/ screenster
public class ChromeExtensionLoadTest extends BaseTest {

	private String baseURL = "https://auth-demo.aerobatic.io/";
	// private String baseURL = "https://www.wikipedia.org/";
	private static List<String> chromeExtensions = new ArrayList<>();
	// To find the installed extensions
	// e.g. mooikfkahbdckldjjndioackbalphokd
	static {
		// without .crx extension
		chromeExtensions.add("chropath");
		chromeExtensions.add("CryptoPro Extension for CAdES Browser Plug-in");
		chromeExtensions.add("Web Performance Timing API");
	}

	@BeforeClass
	public void beforeClass() throws IOException {
		// calling static method of the super class
		super.setExtensionDir(String.format("%s\\Downloads",
				getPropertyEnv("USERPROFILE", "C:\\users\\sergueik")));
		for (String crx : chromeExtensions) {
			err.println("Adding extension " + crx);
			super.addChromeExtension(crx);
		}
		super.beforeClass();
		assertThat(driver, notNullValue());
		driver.get(baseURL);
	}

	@Test(priority = 1, enabled = true)
	public void openExtensionTest() {
		/*
		 org.openqa.selenium.WebDriverException:
		unknown error: cannot process extension #1
		org.openqa.selenium.WebDriverException:
		unknown error: cannot process extension #1
		from unknown error: CRX verification failed: 3
		
		 */

		driver.get(
				"chrome-extension://nllipdabkglnhmanndddgcihbcmjpfej/manifest.json");
		// this willl be a regular JSON
		String source = driver.findElement(By.cssSelector("pre")).getText();
		err.println("Loaded manifest.json:" + source);
		driver
				.get("chrome-extension://nllipdabkglnhmanndddgcihbcmjpfej/popup.html");
		// this caan interact
		WebElement element = driver
				.findElement(By.cssSelector("body > span.header.label"));
		assertThat(element.getText(), containsString("Web Performance API Timing"));
		sleep(100);
	}
}