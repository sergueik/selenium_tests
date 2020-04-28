package com.github.sergueik.selenium;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Keyboard;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.UnsupportedCommandException;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/*
 * Practice Selenium browser zoom through keyboard 
 * NOTE: does not work equally well with all browsers 
 */

public class KeyboardZoomTest extends BaseTest {

	// private String baseURL = "https://www.google.com/gmail/about/#";
	private String baseURL = "https://www.google.com/#";
	private static WebElement element;
	private static Keyboard keyboard;
	private static final String zoomIn = Keys.chord(Keys.CONTROL, Keys.ADD);
	private static final String zoomOut = Keys.chord(Keys.CONTROL, Keys.SUBTRACT);
	private static final String zoomReset = Keys.chord(Keys.CONTROL, "0");
	private static List<String> keys = new ArrayList<>();
	private static String[] keysToSend = keys.toArray(new String[0]);

	@BeforeMethod
	public void beforeMethod() {
		System.err.println("Navigate to URL: " + baseURL);
		driver.get(baseURL);
		wait.until(ExpectedConditions.urlContains("google"));
		keyboard = ((HasInputDevices) driver).getKeyboard();
		element = driver.findElement(By.tagName("body"));
	}

	// converted from Powershell/C# test, but appears to not work
	// [void]$selenium.Keyboard.SendKeys([System.Windows.Forms.SendKeys]::SendWait('^0'))
	// https://github.com/sergueik/powershell_selenium/blob/master/powershell/zoom.ps1
	// see also:
	// https://www.programcreek.com/java-api-examples/?class=org.openqa.selenium.interactions.Keyboard&method=sendKeys
	@Test
	public void keyboardZoomTest1() {

		for (int cnt = 0; cnt != 4; cnt++) {
			sendKeys(zoomIn, "Sent CTLR +");
			sleep(1000);
		}
		sendKeys(zoomReset, "Sent CTLR 0");
		sleep(1000);
		for (int cnt = 0; cnt != 4; cnt++) {
			sendKeys(keyboard, element, zoomOut, "Sent CTLR -");
			sleep(1000);
		}
		sendKeys(keyboard, element, zoomReset, "Sent CTLR 0");
		sleep(1000);
	}

	private static void sendKeys(Keyboard keyboard, WebElement element, String keys, String message) {
		try {
			keyboard.sendKeys(keys);
		} catch (UnsupportedCommandException e) {
			element.sendKeys(keys);
		}
		System.err.println(message);
	}

	private static void sendKeys(String keys, String message) {
		try {
			keyboard.sendKeys(keys);
		} catch (UnsupportedCommandException e) {
			element.sendKeys(keys);
		}
		System.err.println(message);
	}
}
