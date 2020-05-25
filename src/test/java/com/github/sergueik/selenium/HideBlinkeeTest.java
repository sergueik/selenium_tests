package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

// converted from Python test
// https://github.com/sergueik/powershell_selenium/blob/master/python/hide_blinkee.py
public class HideBlinkeeTest {

	public int scriptTimeout = 5;
	public int flexibleWait = 60; // too long
	public int implicitWait = 1;
	public int pollingInterval = 500;
	private WebDriver driver;
	private Wait<WebDriver> wait;
	private static DesiredCapabilities capabilities;
	@SuppressWarnings("unused")
	private JavascriptExecutor js;
	private static WebElement element = null;
	private static final String osName = BaseTest.getOSName();
	private static final String driverBinary = osName.equals("windows")
			? "chromedriver.exe" : "chromedriver";
	private final String chromeDriverPath = Paths
			.get(System.getProperty("user.home")).resolve("Downloads")
			.resolve(driverBinary).toAbsolutePath().toString();
	private boolean debug = false;

	private static final String baseURL = "https://blinkee.com";
	private static final String xpath = "//img[@class=\"theme_logo\"]";
	private static Map<String, Object> data = new HashMap<>();
	private Object result = null;
	private String script = "";

	@BeforeMethod
	public void BeforeMethod() {
	}

	@AfterMethod
	public void AfterMethod(ITestResult result) {
		System.err.println("Closing browser after: " + result.getName());
		if (driver != null) {
			try {
				driver.get("about:blank");
				driver.close();
				driver.quit();
				driver = null;
			} catch (Exception e) {
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Test(enabled = true)
	public void test1() {
		driver = setupDriver(true);
		assertThat(driver, notNullValue());
		if (debug) {
			System.err.println("Navigate to " + baseURL);
		}
		driver.get(baseURL);
		wait.until(ExpectedConditions.urlContains(baseURL));
		if (debug) {
			System.err.println("Done wait for url to contain " + baseURL);
		}
		element = wait
				.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
		if (debug) {
			System.err.println("Done wait for presence of element " + xpath);
		}
		element = driver.findElement(By.xpath(xpath));
		assertThat(element, notNullValue());
		if (debug) {
			System.err.println(
					String.format("Found\n%s", element.getAttribute("outerHTML")));
		}

		if (debug) {
			System.err.println("Element offsetWidth attribute value: "
					+ element.getAttribute("offsetWidth"));
			script = "var element = arguments[0];\n"
					+ "var propertyName = arguments[1];\n" + "var debug = arguments[2];\n"
					+ "var result = window.document.defaultView.getComputedStyle(element,null).getPropertyValue(propertyName);\n"
					+ "if (debug) { alert(propertyName + ' ' + result ); }\n"
					+ "return result";
			result = js.executeScript(script, element, "width", false);
			try {
				System.err.println("Element width (computed): " + result.toString());
			} catch (NullPointerException e) {
				System.err.println("Script does not return a value : " + e.toString());
			}
		}
		if (debug) {
			script = "var element = arguments[0];\n"
					+ "var computedStyles = window.document.defaultView.getComputedStyle(element,null);\n"
					+ "var len = computedStyles.length;\n" + "var result = {};\n"
					+ "for (var i=0;i< len;i++) { var style = computedStyles[i];\n"
					+ "console.log(style + ' : ' + computedStyles.getPropertyValue(style));\n"
					+ "result[style] = computedStyles.getPropertyValue(style);\n" + "}\n"
					+ "return JSON.stringify(result)";
			result = js.executeScript(script, element);
			try {
				System.err.println("Element computed styles: " + result.toString());
			} catch (NullPointerException e) {
				System.err.println("Script " + script
						+ " failed to enumerate computed styles : " + e.toString());
			}
		}
		if (debug) {

			script = "var element = arguments[0];\n"
					+ "var computedStyles = window.document.defaultView.getComputedStyle(element,null);\n"
					+ "var len = computedStyles.length; \n" + "var result = {};\n"
					+ "for (var i=0;i< len;i++) {\n" + "var style = computedStyles[i];\n"
					+ "console.log(style + ' : ' + computedStyles.getPropertyValue(style));\n"
					+ "result[style] = computedStyles.getPropertyValue(style);\n" + "}\n"
					+ "return result;";

			data = new HashMap<>();
			data = (Map<String, Object>) js.executeScript(script, element, false);
			try {
				System.err.println("Element computed styles: " + data.keySet());
			} catch (NullPointerException e) {
				System.err.println("Script " + script
						+ " failed to enumerate computed styles : " + e.toString());
			}
		}

		/*
		assertThat("element should not be displayed", element.getRect().width,
				is(0));
				*/
	}

	@Test(enabled = true)
	public void test2() {
		driver = setupDriver(false);
		assertThat(driver, notNullValue());
		if (debug) {
			System.err.println("Navigate to " + baseURL);
		}
		driver.get(baseURL);
		wait.until(ExpectedConditions.urlToBe(baseURL + "/"));
		if (debug) {

			System.err.println("Done wait for url to be " + driver.getCurrentUrl());
		}
		element = wait
				.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
		if (debug) {
			System.err.println("Done wait for presence of element " + xpath);
		}
		assertThat(element, notNullValue());
		if (debug) {
			System.err.println(
					String.format("Found\n%s", element.getAttribute("outerHTML")));
		}
		System.err.println("Displayed: " + element.isDisplayed());
		// may fail
		// assertThat("element should be displayed", element.isDisplayed(),
		// is(true));
	}

	// this test is slower
	@Test(enabled = true)
	public void test3() {
		driver = setupDriver(false);
		assertThat(driver, notNullValue());
		if (debug) {
			System.err.println("Navigate to " + baseURL);
		}
		driver.get(baseURL);
		wait.until(ExpectedConditions.urlToBe(baseURL + "/"));
		if (debug) {
			System.err.println("Done wait for url to be " + driver.getCurrentUrl());
		}
		// NOTE: ExpectedConditions.visibilityOfElementLocated is hanging
		String xpath1 = "//div[@class=\"tp-parallax-wrap\"]";
		System.err.println("Wait for visibility of element by xpath: " + xpath1);
		element = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath1)));
		System.err
				.println("Done wait for visibility of element by xpath: " + xpath1);
		assertThat(element, notNullValue());
		System.err
				.println(String.format("Found\n%s", element.getAttribute("outerHTML")));
		assertThat("element should be displayed", element.isDisplayed(), is(true));
	}

	@SuppressWarnings("deprecation")
	private WebDriver setupDriver(boolean disableDynamic) {
		capabilities = DesiredCapabilities.chrome();
		System.setProperty("webdriver.chrome.driver", chromeDriverPath);
		ChromeOptions chromeOptions = new ChromeOptions();
		if (osName.equals("windows")) {
			if (System.getProperty("os.arch").contains("64")) {
				String[] paths = new String[] {
						"C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe",
						"C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe" };
				// probe file existence
				for (String path : paths) {
					File exe = new File(path);
					System.err.println("Inspecting browser path: " + path);
					if (exe.exists()) {
						chromeOptions.setBinary(path);
					}
				}
			} else {
				chromeOptions.setBinary(
						"c:\\Program Files\\Google\\Chrome\\Application\\chrome.exe");
			}
		}
		Map<String, Object> chromePrefs = new HashMap<>();
		chromePrefs.put("profile.default_content_settings.popups", 0);

		chromePrefs.put("profile.managed_default_content_settings.javascript", 2);
		chromePrefs.put("profile.managed_default_content_settings.images", 2);
		chromePrefs.put("profile.managed_default_content_settings.mixed_script", 2);
		chromePrefs.put("profile.managed_default_content_settings.media_stream", 2);
		chromePrefs.put("profile.managed_default_content_settings.stylesheets", 2);

		if (disableDynamic) {
			chromeOptions.setExperimentalOption("prefs", chromePrefs);
		}
		for (String optionAgrument : (new String[] {
				"--user-agent=Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20120101 Firefox/33.0",
				"--allow-running-insecure-content", "--allow-insecure-localhost",
				"--enable-local-file-accesses", "--disable-notifications",
				"--disable-save-password-bubble", "--disable-default-app",
				"disable-infobars", "--no-sandbox ", "--browser.download.folderList=2",
				"--disable-web-security", "--disable-translate",
				"--disable-popup-blocking", "--ignore-certificate-errors",
				"--no-proxy-server",
				"--browser.helperApps.neverAsk.saveToDisk=image/jpg,text/csv,text/xml,application/xml,application/vnd.ms-excel,application/x-excel,application/x-msexcel,application/excel,application/pdf", })) {
			chromeOptions.addArguments(optionAgrument);
		}
		capabilities.setCapability("chrome.binary", chromeDriverPath);

		capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);

		WebDriver driver;
		try {
			driver = new ChromeDriver(capabilities);
		} catch (SessionNotCreatedException e) {
			throw new RuntimeException(e.toString());
		}
		wait = new FluentWait<>(driver).withTimeout(flexibleWait, TimeUnit.SECONDS)
				.pollingEvery(pollingInterval, TimeUnit.SECONDS)
				.ignoring(NoSuchElementException.class);
		js = ((JavascriptExecutor) driver);
		return driver;
	}
}

/*
 {
  "animation-delay": "0s",
  "animation-direction": "normal",
  "animation-duration": "0s",
  "animation-fill-mode": "none",
  "animation-iteration-count": "1",
  "animation-name": "none",
  "animation-play-state": "running",
  "animation-timing-function": "ease",
  "background-attachment": "scroll",
  "background-blend-mode": "normal",
  "background-clip": "border-box",
  "background-color": "rgba(0, 0, 0, 0)",
  "background-image": "none",
  "background-origin": "padding-box",
  "background-position": "0% 0%",
  "background-repeat": "repeat",
  "background-size": "auto",
  "border-bottom-color": "rgb(85, 85, 85)",
  "border-bottom-left-radius": "0px",
  "border-bottom-right-radius": "0px",
  "border-bottom-style": "none",
  "border-bottom-width": "0px",
  "border-collapse": "separate",
  "border-image-outset": "0",
  "border-image-repeat": "stretch",
  "border-image-slice": "100%",
  "border-image-source": "none",
  "border-image-width": "1",
  "border-left-color": "rgb(85, 85, 85)",
  "border-left-style": "none",
  "border-left-width": "0px",
  "border-right-color": "rgb(85, 85, 85)",
  "border-right-style": "none",
  "border-right-width": "0px",
  "border-top-color": "rgb(85, 85, 85)",
  "border-top-left-radius": "0px",
  "border-top-right-radius": "0px",
  "border-top-style": "none",
  "border-top-width": "0px",
  "bottom": "0px",
  "box-shadow": "none",
  "box-sizing": "border-box",
  "break-after": "auto",
  "break-before": "auto",
  "break-inside": "auto",
  "caption-side": "top",
  "clear": "none",
  "clip": "auto",
  "color": "rgb(85, 85, 85)",
  "content": "normal",
  "cursor": "pointer",
  "direction": "ltr",
  "display": "block",
  "empty-cells": "show",
  "float": "none",
  "font-family": "\"Josefin Sans\", sans-serif",
  "font-kerning": "auto",
  "font-optical-sizing": "auto",
  "font-size": "13px",
  "font-stretch": "100%",
  "font-style": "normal",
  "font-variant": "normal",
  "font-variant-ligatures": "normal",
  "font-variant-caps": "normal",
  "font-variant-numeric": "normal",
  "font-variant-east-asian": "normal",
  "font-weight": "300",
  "height": "0px",
  "image-orientation": "from-image",
  "image-rendering": "auto",
  "isolation": "auto",
  "justify-items": "normal",
  "justify-self": "auto",
  "left": "0px",
  "letter-spacing": "normal",
  "line-height": "13px",
  "list-style-image": "none",
  "list-style-position": "outside",
  "list-style-type": "disc",
  "margin-bottom": "0px",
  "margin-left": "0px",
  "margin-right": "0px",
  "margin-top": "0px",
  "max-height": "none",
  "max-width": "100%",
  "min-height": "auto",
  "min-width": "auto",
  "mix-blend-mode": "normal",
  "object-fit": "fill",
  "object-position": "50% 50%",
  "offset-distance": "0px",
  "offset-path": "none",
  "offset-rotate": "auto 0deg",
  "opacity": "1",
  "orphans": "2",
  "outline-color": "rgb(85, 85, 85)",
  "outline-offset": "0px",
  "outline-style": "none",
  "outline-width": "0px",
  "overflow-anchor": "auto",
  "overflow-wrap": "normal",
  "overflow-x": "visible",
  "overflow-y": "visible",
  "padding-bottom": "0px",
  "padding-left": "0px",
  "padding-right": "0px",
  "padding-top": "0px",
  "pointer-events": "auto",
  "position": "relative",
  "resize": "none",
  "right": "0px",
  "scroll-behavior": "auto",
  "speak": "normal",
  "table-layout": "auto",
  "tab-size": "8",
  "text-align": "left",
  "text-align-last": "auto",
  "text-decoration": "none solid rgb(85, 85, 85)",
  "text-decoration-line": "none",
  "text-decoration-style": "solid",
  "text-decoration-color": "rgb(85, 85, 85)",
  "text-decoration-skip-ink": "auto",
  "text-underline-position": "auto",
  "text-indent": "0px",
  "text-rendering": "auto",
  "text-shadow": "none",
  "text-size-adjust": "100%",
  "text-overflow": "clip",
  "text-transform": "none",
  "top": "0px",
  "touch-action": "auto",
  "transition-delay": "0s",
  "transition-duration": "0.35s",
  "transition-property": "all",
  "transition-timing-function": "ease-out",
  "unicode-bidi": "normal",
  "vertical-align": "baseline",
  "visibility": "visible",
  "white-space": "normal",
  "widows": "2",
  "width": "150px",
  "will-change": "auto",
  "word-break": "normal",
  "word-spacing": "0px",
  "z-index": "auto",
  "zoom": "1",
  "-webkit-appearance": "none",
  "backface-visibility": "visible",
  "-webkit-border-horizontal-spacing": "0px",
  "-webkit-border-image": "none",
  "-webkit-border-vertical-spacing": "0px",
  "-webkit-box-align": "stretch",
  "-webkit-box-decoration-break": "slice",
  "-webkit-box-direction": "normal",
  "-webkit-box-flex": "0",
  "-webkit-box-ordinal-group": "1",
  "-webkit-box-orient": "horizontal",
  "-webkit-box-pack": "start",
  "-webkit-box-reflect": "none",
  "column-count": "auto",
  "column-gap": "normal",
  "column-rule-color": "rgb(85, 85, 85)",
  "column-rule-style": "none",
  "column-rule-width": "0px",
  "column-span": "none",
  "column-width": "auto",
  "backdrop-filter": "none",
  "align-content": "normal",
  "align-items": "normal",
  "align-self": "auto",
  "flex-basis": "auto",
  "flex-grow": "0",
  "flex-shrink": "1",
  "flex-direction": "row",
  "flex-wrap": "nowrap",
  "justify-content": "normal",
  "-webkit-font-smoothing": "auto",
  "grid-auto-columns": "auto",
  "grid-auto-flow": "row",
  "grid-auto-rows": "auto",
  "grid-column-end": "auto",
  "grid-column-start": "auto",
  "grid-template-areas": "none",
  "grid-template-columns": "none",
  "grid-template-rows": "none",
  "grid-row-end": "auto",
  "grid-row-start": "auto",
  "row-gap": "normal",
  "-webkit-highlight": "none",
  "hyphens": "manual",
  "-webkit-hyphenate-character": "auto",
  "-webkit-line-break": "auto",
  "-webkit-line-clamp": "none",
  "-webkit-locale": "\"en-US\"",
  "-webkit-mask-box-image": "none",
  "-webkit-mask-box-image-outset": "0",
  "-webkit-mask-box-image-repeat": "stretch",
  "-webkit-mask-box-image-slice": "0 fill",
  "-webkit-mask-box-image-source": "none",
  "-webkit-mask-box-image-width": "auto",
  "-webkit-mask-clip": "border-box",
  "-webkit-mask-composite": "source-over",
  "-webkit-mask-image": "none",
  "-webkit-mask-origin": "border-box",
  "-webkit-mask-position": "0% 0%",
  "-webkit-mask-repeat": "repeat",
  "-webkit-mask-size": "auto",
  "order": "0",
  "perspective": "none",
  "perspective-origin": "75px 0px",
  "-webkit-print-color-adjust": "economy",
  "-webkit-rtl-ordering": "logical",
  "shape-outside": "none",
  "shape-image-threshold": "0",
  "shape-margin": "0px",
  "-webkit-tap-highlight-color": "rgba(0, 0, 0, 0)",
  "-webkit-text-combine": "none",
  "-webkit-text-decorations-in-effect": "none",
  "-webkit-text-emphasis-color": "rgb(85, 85, 85)",
  "-webkit-text-emphasis-position": "over right",
  "-webkit-text-emphasis-style": "none",
  "-webkit-text-fill-color": "rgb(85, 85, 85)",
  "-webkit-text-orientation": "vertical-right",
  "-webkit-text-security": "none",
  "-webkit-text-stroke-color": "rgb(85, 85, 85)",
  "-webkit-text-stroke-width": "0px",
  "transform": "none",
  "transform-origin": "75px 0px",
  "transform-style": "flat",
  "-webkit-user-drag": "auto",
  "-webkit-user-modify": "read-only",
  "user-select": "auto",
  "-webkit-writing-mode": "horizontal-tb",
  "-webkit-app-region": "none",
  "buffered-rendering": "auto",
  "clip-path": "none",
  "clip-rule": "nonzero",
  "mask": "none",
  "filter": "none",
  "flood-color": "rgb(0, 0, 0)",
  "flood-opacity": "1",
  "lighting-color": "rgb(255, 255, 255)",
  "stop-color": "rgb(0, 0, 0)",
  "stop-opacity": "1",
  "color-interpolation": "srgb",
  "color-interpolation-filters": "linearrgb",
  "color-rendering": "auto",
  "fill": "rgb(0, 0, 0)",
  "fill-opacity": "1",
  "fill-rule": "nonzero",
  "marker-end": "none",
  "marker-mid": "none",
  "marker-start": "none",
  "mask-type": "luminance",
  "shape-rendering": "auto",
  "stroke": "none",
  "stroke-dasharray": "none",
  "stroke-dashoffset": "0px",
  "stroke-linecap": "butt",
  "stroke-linejoin": "miter",
  "stroke-miterlimit": "4",
  "stroke-opacity": "1",
  "stroke-width": "1px",
  "alignment-baseline": "auto",
  "baseline-shift": "0px",
  "dominant-baseline": "auto",
  "text-anchor": "start",
  "writing-mode": "horizontal-tb",
  "vector-effect": "none",
  "paint-order": "normal",
  "d": "none",
  "cx": "0px",
  "cy": "0px",
  "x": "0px",
  "y": "0px",
  "r": "0px",
  "rx": "auto",
  "ry": "auto",
  "caret-color": "rgb(85, 85, 85)",
  "line-break": "auto"
}
 
*/