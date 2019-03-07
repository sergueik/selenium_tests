package com.github.sergueik.selenium;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Keyboard;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/*
 * Practice Selenium browser zoom through keyboard 
 * NOTE: does not work equally well with all browsers 
 */

public class KeyboardZoomTest extends BaseTest {

	private String baseURL = "https://www.google.com/gmail/about/#";

	@BeforeMethod
	public void beforeMethod() {
		System.err.println("Navigate to URL: " + baseURL);
		driver.get(baseURL);
	}

	// converted from Powershell/C# test, but does not appear to work
	// [void]$selenium.Keyboard.SendKeys([System.Windows.Forms.SendKeys]::SendWait('^0'))
	// https://github.com/sergueik/powershell_selenium/blob/master/powershell/zoom.ps1
	// see also:
	// https://www.programcreek.com/java-api-examples/?class=org.openqa.selenium.interactions.Keyboard&method=sendKeys
	@Test
	public void keyboardZoomTest1() {
		Keyboard keyboard = ((HasInputDevices) driver).getKeyboard();
		List<String> keys = new ArrayList<>();
		String[] keysToSend = keys.toArray(new String[0]);
		for (int cnt = 0; cnt != 4; cnt++) {
			// keyboard.sendKeys(Keys.CONTROL + "-");
			keyboard.sendKeys(Keys.chord(Keys.CONTROL, Keys.ADD));
			System.err.println("Sent CTLR -");
			sleep(1000);
		}
		// keyboard.sendKeys(Keys.CONTROL + "0");
		keyboard.sendKeys(Keys.chord(Keys.CONTROL, "0"));
		System.err.println("Sent CTLR 0");
		sleep(1000);

	}

}
