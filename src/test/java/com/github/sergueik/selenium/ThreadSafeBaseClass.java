package com.github.sergueik.selenium;

import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
// import org.testng.annotations.Listeners;

import org.openqa.selenium.Platform;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// origin: https://github.com/Ardesco/Selenium-Maven-Template/blob/master/src/test/java/com/lazerycode/selenium/DriverBase.java
// trimmed to ThreadLocal logic alone 
public class ThreadSafeBaseClass {

	private static List<DriverFactory> webDriverThreadPool = Collections
			.synchronizedList(new ArrayList<DriverFactory>());
	private static ThreadLocal<DriverFactory> driverFactoryThread;

	@BeforeSuite(alwaysRun = true)
	public static void instantiateDriverObject() {
		driverFactoryThread = ThreadLocal.withInitial(() -> {
			DriverFactory driverFactory = new DriverFactory();
			webDriverThreadPool.add(driverFactory);
			return driverFactory;
		});
	}

	public static RemoteWebDriver getDriver() throws Exception {
		return driverFactoryThread.get().getDriver();
	}

	@AfterMethod(alwaysRun = true)
	public static void clearCookies() {
		try {
			driverFactoryThread.get().getStoredDriver().manage().deleteAllCookies();
		} catch (Exception ignored) {
			System.out
					.println("Unable to clear cookies, driver object is not viable...");
		}
	}

	@AfterSuite(alwaysRun = true)
	public static void closeDriverObjects() {
		for (DriverFactory driverFactory : webDriverThreadPool) {
			driverFactory.quitDriver();
		}
	}

	private static class DriverFactory {

		private RemoteWebDriver driver;
		private String selectedDriverType;

		private final String operatingSystem = System.getProperty("os.name")
				.toUpperCase();
		private final String systemArchitecture = System.getProperty("os.arch");
		private final boolean useRemoteWebDriver = Boolean
				.getBoolean("remoteDriver");

		public DriverFactory() {
			String driverType = "firefox";
			String browser = System.getProperty("browser", driverType.toString())
					.toUpperCase();
			try {
				driverType = browser;
			} catch (IllegalArgumentException ignored) {
				System.err.println(
						"Unknown driver specified, defaulting to '" + driverType + "'...");
			} catch (NullPointerException ignored) {
				System.err.println(
						"No driver specified, defaulting to '" + driverType + "'...");
			}
			selectedDriverType = driverType;
		}

		public RemoteWebDriver getDriver() throws Exception {
			if (null == driver) {
				instantiateWebDriver(selectedDriverType);
			}

			return driver;
		}

		public RemoteWebDriver getStoredDriver() {
			return driver;
		}

		public void quitDriver() {
			if (null != driver) {
				driver.quit();
				driver = null;
			}
		}

		private void instantiateWebDriver(String driverType)
				throws MalformedURLException {
			// TODO add in a real logger instead of System.out
			System.err.println(
					"Operating System: " + operatingSystem + "\n" + "Architecture: "
							+ systemArchitecture + "\n" + "Browser: " + selectedDriverType
							+ "\n" + "Connecting to Selenium Grid: " + useRemoteWebDriver);

			DesiredCapabilities desiredCapabilities = new DesiredCapabilities();

			if (useRemoteWebDriver) {
				URL seleniumGridURL = new URL(System.getProperty("gridURL"));
				String desiredBrowserVersion = System
						.getProperty("desiredBrowserVersion");
				String desiredPlatform = System.getProperty("desiredPlatform");

				if (null != desiredPlatform && !desiredPlatform.isEmpty()) {
					desiredCapabilities
							.setPlatform(Platform.valueOf(desiredPlatform.toUpperCase()));
				}

				if (null != desiredBrowserVersion && !desiredBrowserVersion.isEmpty()) {
					desiredCapabilities.setVersion(desiredBrowserVersion);
				}

				desiredCapabilities.setBrowserName(selectedDriverType.toString());
				driver = new RemoteWebDriver(seleniumGridURL, desiredCapabilities);
			} else {
				FirefoxOptions options = new FirefoxOptions();
				options.merge(desiredCapabilities);

				driver = new FirefoxDriver(options);
			}
		}
	}

}
