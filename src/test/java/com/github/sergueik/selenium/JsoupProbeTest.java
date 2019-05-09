package com.github.sergueik.selenium;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
//import org.openqa.selenium.opera.OperaDriver;
//import org.openqa.selenium.opera.OperaOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeMethod;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;

import static org.junit.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import static org.hamcrest.Matchers.greaterThan;
import java.util.regex.Pattern;

/**
* Sample test scenario for web page scraping via joup based on chained  fast track node attribute scan
* that is a lot less code than chained browse of immediate (grand-) children 
* @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
*/

// Based on
//
// see also:
// https://www.baeldung.com/java-with-jsoup
// https://www.programcreek.com/java-api-examples/?class=org.jsoup.nodes.Document&method=getElementsByAttributeValue
public class JsoupProbeTest extends BaseTest {

	private static final Logger log = LogManager.getLogger(JsoupProbeTest.class);
	private String filePath = "links.htm";
	private final static boolean peekTargetParent = false;
	// private String selector = "#acListWrap > div:nth-child(3) > div >
	// div.productListingMiddle > div.lower.tabsection";
	private String selector = "#acListWrap div.productListing h2.listSubtitle > a[href*='/item/'][title='Click to View']";
	private String pageSource = null;
	private static final LinkedHashMap<String, String> attrMap = new LinkedHashMap<>();
	private static Document jsoupDocument;
	private static List<WebElement> elements;
	// match)
	private final List<String> attrKeys = Arrays.asList(new String[] { "class",
			"class", "id", "class", "class", "class", "title" });
	private final List<String> attrValuesExact = Arrays.asList(new String[] {
			"et_pb_module et_pb_text et_pb_text_0 et_pb_bg_layout_light  et_pb_text_align_left",
			"et_pb_text_inner", "acListWrap", "auctionList", "productListing",
			"listSubtitle", "Click to View" });

	private final List<String> attrValuesPartial = Arrays
			.asList(new String[] { "et_pb_text", "et_pb_text_inner", "acListWrap",
					"auctionList", "productListing", "listSubtitle", "Click to View" });

	private static Document parentDocument;
	private static Elements jsoupElements;
	private static Document childDocument;

	private static String attributeName;
	private static String attributeValue;

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
	}

	@BeforeMethod
	public void loadPage() {
		pageSource = getScriptContent(filePath);
	}

	@Test(enabled = false)
	public void testPageVisual() {
		driver.navigate().to(getPageContent(filePath));
		pageSource = driver.getPageSource();
		elements = driver.findElements(By.cssSelector(selector));
		assertThat(elements, notNullValue());
		assertThat(elements.size(), greaterThan(1));
		System.err.println(String.format("Processing visually %s: %s", selector,
				elements.get(0).getText()));
	}

	// temporarily disable to reduce logging
	@Test(enabled = false)
	public void testOneCallPageSource() {
		jsoupDocument = Jsoup.parse(pageSource);

		attributeName = "class";
		attributeValue = "productListing";

		jsoupElements = jsoupDocument.getElementsByAttributeValue(attributeName,
				attributeValue);
		assertThat(jsoupElements, notNullValue());
		assertThat(jsoupElements.iterator().hasNext(), is(true));
		assertThat(jsoupElements.eachText().size(), greaterThan(1));
		System.err.println(String.format("Processing attribute(\"%s\") = \"%s\" %s",
				attributeName, attributeValue, jsoupElements.first().text()));
	}

	@Test(enabled = false)
	public void testDeepPageSource() {
		jsoupDocument = Jsoup.parse(pageSource);

		parentDocument = jsoupDocument;
		int cnt = 0;
		for (int pos = 0; pos != attrKeys.size(); pos++) {
			attributeName = attrKeys.get(pos);
			attributeValue = attrValuesExact.get(pos);
			System.err.println("Processing " + attributeName + ", " + attributeValue);
			if (cnt > -1) {
				System.err.println(
						"Scanning " + parentDocument.childNodes().size() + " child nodes");
				for (Node childNode : parentDocument.childNodes()) {
					String childHTML = childNode.outerHtml();
					String childHTMLDisplay = undoOuterHtmlDecor(childHTML);
					// NOTE: unsafe
					if (cnt > 0) {
						childDocument = childNode.ownerDocument();
					} else {
						childDocument = Jsoup.parse(childHTML);
					}
					if (childHTMLDisplay.length() > 120) {
						childHTMLDisplay = childHTMLDisplay.substring(0, 120) + "...";
					}

					jsoupElements = childDocument
							.getElementsByAttributeValue(attributeName, attributeValue);
					if (jsoupElements != null && jsoupElements.size() > 0) {
						System.err.println("Found " + attributeName + "=" + attributeValue
								+ " in child: " + childHTMLDisplay);
					}
				}
			}
			cnt++;
			jsoupElements = parentDocument.getElementsByAttributeValue(attributeName,
					attributeValue);

			assertThat(jsoupElements, notNullValue());
			assertThat(jsoupElements.iterator().hasNext(), is(true));
			assertThat(jsoupElements.eachText().size(), greaterThan(0));

			String innerHTML = jsoupElements.first().outerHtml();
			System.err
					.println(String.format("Processing attribute(\"%s\") = \"%s\" %s...",
							attributeName, attributeValue, innerHTML.substring(0, 160)));
			// NOTE: not the ownerDocument - is is the wrong thing
			// parentDocument = jsoupElements.first().ownerDocument();
			parentDocument = Jsoup.parse(innerHTML);

		}
	}

	@Test(enabled = true)
	public void testDeepFunPageSource() {
		jsoupDocument = Jsoup.parse(pageSource);

		parentDocument = jsoupDocument;
		Document nextParentDocument = null;
		boolean rootDocument = true;
		for (int pos = 0; pos != attrKeys.size(); pos++) {
			attributeName = attrKeys.get(pos);
			attributeValue = attrValuesExact.get(pos);
			System.err.println("Processing " + attributeName + ", " + attributeValue);
			nextParentDocument = parentDocument;
			do {
				System.err.println(
						"Scanning " + parentDocument.childNodes().size() + " child nodes");
				nextParentDocument = getNextNode(parentDocument, attributeName,
						attributeValue, !rootDocument);
				if (nextParentDocument != null) {
					System.err.println("Remained: "
							+ nextParentDocument.childNodes().size() + " child nodes");
				}
				if (nextParentDocument == null
						|| (nextParentDocument.childNodes().size() == 1)) {
					break;
				}
			} while (nextParentDocument.childNodes().size() >= 1);
			rootDocument = false;
			try {

				jsoupElements = nextParentDocument
						.getElementsByAttributeValue(attributeName, attributeValue);
			} catch (NullPointerException e) {
				System.err
						.println(String.format("Failed to handle %s=\"%s\", using parent",
								attributeName, attributeValue));
				jsoupElements = parentDocument
						.getElementsByAttributeValue(attributeName, attributeValue);
			}
			assertThat(jsoupElements, notNullValue());
			assertThat(jsoupElements.iterator().hasNext(), is(true));
			assertThat(jsoupElements.eachText().size(), greaterThan(0));

			String innerHTML = jsoupElements.first().outerHtml();
			System.err.println(String.format("For %s = \"%s\" found %s...",
					attributeName, attributeValue, innerHTML.substring(0, 160)));
			// NOTE: the ownerDocument - is the wrong thing
			parentDocument = Jsoup.parse(innerHTML);

		}
	}

	@Test(enabled = true, expectedExceptions = AssertionError.class)
	public void testDeepFun2PageSource() {
		jsoupDocument = Jsoup.parse(pageSource);

		parentDocument = jsoupDocument;
		Document nextParentDocument = null;
		boolean rootDocument = true;
		for (int pos = 0; pos != attrKeys.size(); pos++) {
			attributeName = attrKeys.get(pos);
			attributeValue = attrValuesPartial.get(pos);
			System.err.println("Processing " + attributeName + ", " + attributeValue);
			nextParentDocument = parentDocument;
			do {
				System.err.println(
						"Scanning " + parentDocument.childNodes().size() + " child nodes");
				nextParentDocument = getNextNode(parentDocument, attributeName,
						attributeValue, !rootDocument, true);
				if (nextParentDocument != null) {
					System.err.println("Remained: "
							+ nextParentDocument.childNodes().size() + " child nodes");
				}
				if (nextParentDocument == null
						|| (nextParentDocument.childNodes().size() == 1)) {
					break;
				}
			} while (nextParentDocument.childNodes().size() >= 1);
			rootDocument = false;
			try {

				jsoupElements = nextParentDocument
						.getElementsByAttributeValueContaining(attributeName,
								attributeValue);
			} catch (NullPointerException e) {
				System.err
						.println(String.format("Failed to handle %s=\"%s\", using parent",
								attributeName, attributeValue));
				jsoupElements = parentDocument.getElementsByAttributeValueContaining(
						attributeName, attributeValue);
			}
			assertThat("there should be result", jsoupElements, notNullValue());
			assertThat(
					String.format(
							"there should be a collection of elements found by %s=\"%s\"",
							attributeName, attributeValue),
					jsoupElements.iterator().hasNext(), is(true));
			assertThat(jsoupElements.eachText().size(), greaterThan(0));

			String innerHTML = jsoupElements.first().outerHtml();
			System.err.println(String.format("For %s = \"%s\" found %s...",
					attributeName, attributeValue, innerHTML.substring(0, 160)));
			// NOTE: the ownerDocument - is the wrong thing
			parentDocument = Jsoup.parse(innerHTML);

		}
	}

	private String undoOuterHtmlDecor(String rawData) {
		String cleanData;
		String regex = "<html>\\s*<head>\\s*</head>\\s*<body>\\s*(.*)\\s*</body>\\s*</html>";
		Pattern pattern = Pattern.compile(regex,
				Pattern.MULTILINE | Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher matcher = pattern.matcher(rawData);
		if (matcher.find()) {
			cleanData = matcher.group(1);
		} else {
			cleanData = rawData;
		}
		return cleanData;
	}

	private Document getNextNode(Document parentDocument, String attributeName,
			String attributeNameValue, boolean useOwnerDocument) {
		return getNextNode(parentDocument, attributeName, attributeNameValue,
				useOwnerDocument, false);
	}

	private Document getNextNode(Document parentDocument, String attributeName,
			String attributeNameValue, boolean useOwnerDocument,
			boolean useContaining) {
		Document nodeDodument = null;
		for (Node childNode : parentDocument.childNodes()) {
			String childHTML = childNode.outerHtml();
			String childHTMLDisplay = undoOuterHtmlDecor(childHTML);
			// NOTE: unsafe
			if (useOwnerDocument) {
				childDocument = childNode.ownerDocument();
			} else {
				childDocument = Jsoup.parse(childHTML);
			}

			if (childHTMLDisplay.length() > 120) {
				childHTMLDisplay = childHTMLDisplay.substring(0, 120) + "...";
			}

			jsoupElements = useContaining
					? childDocument.getElementsByAttributeValueContaining(attributeName,
							attributeValue)
					: childDocument.getElementsByAttributeValue(attributeName,
							attributeValue);

			if (jsoupElements != null && jsoupElements.size() > 0) {
				if (peekTargetParent) {
					Node parentNode = jsoupElements.get(0).parentNode();
					if (parentNode != null) {
						System.err.println("Parent node of the target: " + attributeName
								+ " = " + attributeValue + " \n" + parentNode.outerHtml());
					}
				}
				System.err.println("Found " + attributeName + "=" + attributeValue
						+ " in child: " + childHTMLDisplay);
				nodeDodument = childDocument;
			}
		}
		return nodeDodument;
	}

}