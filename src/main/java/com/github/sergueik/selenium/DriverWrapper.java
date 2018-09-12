package com.github.sergueik.selenium;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

//based: https://github.com/mkolisnyk/V08632/blob/master/src/main/java/com/sample/framework/Driver.java
//exercises thread isolation from Packt's Automated UI Testing in Android
//see also https://www.swtestacademy.com/selenium-parallel-tests-grid-testng/

public class DriverWrapper extends RemoteWebDriver {

	private static String url = null;

	private DriverWrapper() {
	}

	private DriverWrapper(String url) {
		DriverWrapper.url = url;
	}

	private static ConcurrentHashMap<String, RemoteWebDriver> driverThreadMap = new ConcurrentHashMap<String, RemoteWebDriver>();

	@SuppressWarnings("deprecation")
	public static void add(String browser, Capabilities capabilities) {
		RemoteWebDriver driver = null;
		if (browser.trim().equalsIgnoreCase("remote")) {
			try {
				driver = new RemoteWebDriver(new URL(url), capabilities);
			} catch (MalformedURLException e) {
				System.err.println("Exception: " + e.toString());
				throw new RuntimeException(e);
			}
		} else {
			if (browser == "firefox") {
				driver = new FirefoxDriver(capabilities);
			}

			if (browser == "chrome") {
				driver = new ChromeDriver(capabilities);
			}
			driverThreadMap.put(getThreadName(), driver);
		}
	}

	public static RemoteWebDriver current() {
		return driverThreadMap.get(getThreadName());
	}

	public static String getThreadName() {
		return Thread.currentThread().getName() + "-"
				+ Thread.currentThread().getId();
	}
}
