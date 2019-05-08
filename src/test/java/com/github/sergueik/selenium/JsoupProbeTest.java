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
	// private String selector = "#acListWrap > div:nth-child(3) > div >
	// div.productListingMiddle > div.lower.tabsection";
	private String selector = "#acListWrap div.productListing h2.listSubtitle > a[href*='/item/'][title='Click to View']";
	private String pageSource = null;
	private static final LinkedHashMap<String, String> attrMap = new LinkedHashMap<>();
	private static Document jsoupDocument;
	private static List<WebElement> elements;
	private static final List<String> attrKeys1 = Arrays.asList(
			new String[] { "id", "class", "class", "class", "class", "title" });

	// TODO: switch to jsoupDocument.getElementsByAttributeValueContaining(key,
	// match)
	private final List<String> attrKeys = Arrays.asList(
			new String[] { "class", "class", "id", "class", "class", "title" });
	private final List<String> attrValues = Arrays.asList(new String[] {
			"et_pb_module et_pb_text et_pb_text_0 et_pb_bg_layout_light  et_pb_text_align_left",
			"et_pb_text_inner", "acListWrap", "auctionList", "productListing",
			"listSubtitle", "Click to View" });

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

		Elements divjsoupElements = jsoupDocument
				.getElementsByAttributeValue(attributeName, attributeValue);
		// Assert
		// will fail
		assertThat(divjsoupElements, notNullValue());
		assertThat(divjsoupElements.iterator().hasNext(), is(true));
		assertThat(divjsoupElements.eachText().size(), greaterThan(1));
		System.err.println(String.format("Processing attribute(\"%s\") = \"%s\" %s",
				attributeName, attributeValue, divjsoupElements.first().text()));
	}

	@Test(enabled = false)
	public void testDeepPageSource() {
		jsoupDocument = Jsoup.parse(pageSource);

		parentDocument = jsoupDocument;
		int cnt = 0;
		for (int pos = 0; pos != attrKeys.size(); pos++) {
			System.err.println(
					"Processing " + attrKeys.get(pos) + ", " + attrValues.get(pos));
			attributeName = attrKeys.get(pos);
			attributeValue = attrValues.get(pos);
			// TODO: attribute("id") = "acListWrap" found in too many child nodes
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
		boolean rootDocument = true;
		for (int pos = 0; pos != attrKeys.size(); pos++) {
			attributeName = attrKeys.get(pos);
			attributeValue = attrValues.get(pos);
			System.err.println("Processing " + attributeName + ", " + attributeValue);
			do {
				System.err.println(
						"Scanning " + parentDocument.childNodes().size() + " child nodes");
				parentDocument = getNextNode(parentDocument, attributeName,
						attributeValue, !rootDocument);
				if (parentDocument != null) {
					System.err.println("Remained: " + parentDocument.childNodes().size()
							+ " child nodes");
				}
				if (parentDocument == null
						|| (parentDocument.childNodes().size() == 1)) {
					break;
				}
			} while (parentDocument.childNodes().size() >= 1);
			rootDocument = false;
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

	// LinkedHashMap does not work: as keys (DOM node attribute names) must be
	// unique
	@Test(enabled = false)
	public void testBrokenLinkedHashMapUsage() {
		jsoupDocument = Jsoup.parse(pageSource);

		attrMap.clear();
		attrMap.put("id", "acListWrap");
		attrMap.put("class", "productListing");
		attrMap.put("class", "listSubtitle");
		attrMap.put("title", "Click to View");

		final List<String> attrKeys = new ArrayList<>();
		for (Entry<String, String> o : attrMap.entrySet()) {
			attrKeys.add(o.getKey());
		}
		System.err.println(attrKeys.toString());
	}

	// List of HashMap need to be refactored - does not work because of
	// initialization/synchronization problems
	@Test(enabled = false)
	public void testBrokenListMapUsage() {
		jsoupDocument = Jsoup.parse(pageSource);

		final List<Map<String, String>> attrMap = new ArrayList<>();
		Map<String, String> attrStep = new HashMap<>();
		attrStep.put("id", "acListWrap");
		attrMap.add(attrStep);
		attrStep.remove("id");

		attrStep.put("class", "productListing");
		attrMap.add(attrStep);
		attrStep.remove("class");
		attrStep.put("class", "listSubtitle");
		attrMap.add(attrStep);
		attrStep.remove("class");
		attrStep.put("title", "Click to View");
		attrMap.add(attrStep);
		attrStep.remove("title");
		// makes attrMap full of empty entries
		System.err.println("Processing " + attrMap.toString());

		Iterator<Map<String, String>> attrMapIterator = attrMap.iterator();
		while (attrMapIterator.hasNext()) {
			attrStep = attrMapIterator.next();
			System.err.println("Processing " + attrStep.toString());
			/*
			Set<String> attributeKeys = attrStep.keySet();
			assertThat(attributeKeys.size(), greaterThan(0));
			String attributeName = attributeKeys.toArray()[0].toString();
			String attributeValue = attrStep.get(attributeName);
			*/
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

			jsoupElements = childDocument.getElementsByAttributeValue(attributeName,
					attributeValue);
			if (jsoupElements != null && jsoupElements.size() > 0) {
				Node parentNode = jsoupElements.get(0).parentNode();
				if (parentNode != null) {
					System.err.println("Parent node of the target: " + attributeName
							+ " = " + attributeValue + " \n" + parentNode.outerHtml());
				}
				System.err.println("Found " + attributeName + "=" + attributeValue
						+ " in child: " + childHTMLDisplay);
				nodeDodument = childDocument;
			}
		}
		return nodeDodument;
	}

}