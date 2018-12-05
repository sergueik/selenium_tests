package com.github.sergueik.selenium;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

// based: https://github.com/mkolisnyk/V08632/blob/master/src/main/java/com/sample/framework/Driver.java
// which exercises thread isolation from Packt's Automated UI Testing in Android
// see also https://www.swtestacademy.com/selenium-parallel-tests-grid-testng/
// http://www.jitendrazaa.com/blog/java/performing-load-testing-in-salesforce-using-selenium-and-testng
// https://automated-testing.info/t/parallelnyj-zapusk-v-neskolkih-brauzerah-selenide-testng-gradle-allure-kakoj-normalnyj-pattern/21914/19
// https://github.com/iljapavlovs/selenium-testng-allure-maven
// https://github.com/kowalcj0/parallel-selenium-with-testng

public class DriverWrapper extends RemoteWebDriver {

	private static String hubUrl = null;

	private DriverWrapper() {
	}

	private DriverWrapper(String hubUrl) {
		DriverWrapper.hubUrl = hubUrl;
	}

	public static void setHubUrl(String value) {
		DriverWrapper.hubUrl = value;
	}

	private static ConcurrentHashMap<String, RemoteWebDriver> driverThreadMap = new ConcurrentHashMap<String, RemoteWebDriver>();

	@SuppressWarnings("deprecation")
	public static void add(String browser, Capabilities capabilities) {
		RemoteWebDriver driver = null;
		if (browser.trim().equalsIgnoreCase("remote")) {
			try {
				driver = new RemoteWebDriver(new URL(hubUrl), capabilities);
			} catch (MalformedURLException e) {
				System.err.println("Exception: " + e.toString());
				throw new RuntimeException(e);
			}
			driverThreadMap.put(getThreadName(), driver);
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

	private static String getThreadName() {
		return Thread.currentThread().getName() + "-"
				+ Thread.currentThread().getId();
	}
}
