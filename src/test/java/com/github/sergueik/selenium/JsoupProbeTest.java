package com.github.sergueik.selenium;

import static java.lang.System.err;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.io.FileWriter;
import java.io.IOException;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.assertTrue;

/**
 * Sample test scenarios for web page scraping via joup based on chained
 * fasttrack node attribute scan which seems to take a lot less code than the
 * chained browsing of immediate (grand-) children
 * 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

// Based on
//
// see also:
// https://www.baeldung.com/java-with-jsoup
// https://www.programcreek.com/java-api-examples/?class=org.jsoup.nodes.Document&method=getElementsByAttributeValue
// https://jsoup.org/cookbook/extracting-data/selector-syntax
public class JsoupProbeTest extends BaseTest {

	private static final Logger log = LogManager.getLogger(JsoupProbeTest.class);
	private String filePath = "links.htm";
	// origin:http://www.louisianaoutdoorproperties.com
	private final static boolean peekTargetParent = false;
	private String selector = "#acListWrap div.productListing h2.listSubtitle > a[href*='/item/'][title='Click to View']";
	private String pageSource = null;
	private static final LinkedHashMap<String, String> attrMap = new LinkedHashMap<>();
	private static Document jsoupDocument;
	private static List<WebElement> elements;
	private List<String> jsoupSelectors = Arrays.asList(
			new String[] { "#acListWrap .productListing", ".productListing" });
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
	public void testJsoupSelect() {
		jsoupDocument = Jsoup.parse(pageSource);
		for (String jsoupSelector : jsoupSelectors) {
			jsoupElements = jsoupDocument.select(jsoupSelector);
			assertThat(jsoupElements, notNullValue());
			assertThat(jsoupElements.iterator().hasNext(), is(true));
			assertThat(jsoupElements.eachText().size(), greaterThan(1));
			System.err.println(String.format("Processing jsoup selector \"%s\" %s",
					jsoupSelector, jsoupElements.first().text()));
		}
	}

	@Test(enabled = true)
	public void testJsoupFlatten() {
		JsonParser parser = new JsonParser();
		JsonObject jsonObject = parser.parse(getScriptContent("mixed.json"))
				.getAsJsonObject();
		assertTrue(jsonObject.isJsonObject());
		String result = jsonObject.toString();
		System.err.println("Result: " + result);
		/*
		Gson gson = new GsonBuilder().create();
		try {
			JsonWriter jsonWriter = gson.newJsonWriter(new FileWriter("result.json"));
			jsonWriter.setIndent(null);
			jsonWriter.setLenient(true);
			// for low level object to stream serialization
			// https://static.javadoc.io/com.google.code.gson/gson/2.6.2/com/google/gson/stream/JsonWriter.html
		} catch (IOException e) {
			err.println("Exception (ignored): " + e.toString());
		}
		*/
	}

	@Test(enabled = true)
	public void testPageSourceElementsGet() {
		// NOTE: too big
		// System.err.println("Page Source: " + pageSource);
		jsoupDocument = Jsoup.parse(pageSource);

		attributeName = "type";
		attributeValue = "text/javascript"; // can be part of

		// NOTE: getElementsByTag does not traverse the DOM
		// jsoupDocument.getElementsByTag("div").get(0);
		Element jsoupElement = jsoupDocument
				.getElementsByAttributeValueContaining(attributeName, attributeValue)
				.get(0);
		attributeName = "href";
		attributeValue = "/auction/"; // can be part of

		Elements jsoupElements = jsoupDocument
				.getElementsByAttributeValueContaining(attributeName, attributeValue);

		Comparator<Element> comp = (aElement, bElement) -> aElement.ownText()
				.compareTo(bElement.ownText());
		jsoupElement = jsoupElements.stream().sorted(comp)
				.collect(Collectors.toList()).get(4);
		System.err.println(
				"Found through attribute match and sorting: " + jsoupElement.text());
		// https://dzone.com/articles/java-8-comparator-how-to-sort-a-list
		// System.err.println("Element: " + jsoupElement.html());
		System.err.println(
				"Element: " + jsoupElement.outerHtml().substring(0, 100) + "...");
		attributeName = "";
		attributeValue = "container"; // can be one of classes
		jsoupElement = jsoupDocument
				.select(String.format("%s.%s", attributeName, attributeValue)).get(1);
		System.err.println(
				"Element: " + jsoupElement.outerHtml().substring(0, 100) + "...");

		String tagName = "div";
		jsoupElement = jsoupDocument.select(tagName).get(1);
		System.err.println(
				"Element: " + jsoupElement.outerHtml().substring(0, 100) + "...");
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
					String childHTMLDisplay = undoOuterHtmlDecor(childNode.html(null));
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

	@Test(enabled = false)
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

	/*
	 * This can be used with Perl #!/usr/bin/perl
	 * 
	 * use warnings; use strict;
	 * 
	 * use Getopt::Long; use Data::Dumper qw(Dumper); use HTML::TagParser;
	 * 
	 * use vars qw($DEBUG);
	 * 
	 * $element = ( HTML::TagParser->new('links.htm')->getElementsByAttribute(
	 * 'id', 'acListWrap' ) )[0]->subTree();
	 * 
	 * sub getData($$$) { my ( $e, $n, $v ) = @_; my @e =
	 * $e->getElementsByAttribute( $n, $v ); my @d = map { my $t =
	 * $_->innerText; $t =~ s|\s+| |g; $t } @e; \@d; }
	 * 
	 * print Dumper \{ 'price' => getData( $element, 'class', 'acListPrice' ),
	 * 'title' => getData( $element, 'class', 'listSubtitle' ), 'description'=>
	 * getData( $element, 'class', 'lower tabsection' ), };
	 */
	@Test(enabled = false, expectedExceptions = AssertionError.class)
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
								+ " = " + attributeValue + " \n" + parentNode.html(null));
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
