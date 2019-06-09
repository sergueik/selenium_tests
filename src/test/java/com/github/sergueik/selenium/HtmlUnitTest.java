package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

// NOTE: jars laid under '~/.m2/repository/com/fasterxml/jackson'
// do not quite follow maven package naming conventions
// one is required to fully re-downloaded explicitly
// via mvn dependency:purge-local-repository
// This will first resolve the entire dependency tree, then
// delete the contents from the local repository, and then
// re-resolve the dependencies from the remote repository
// and is *very* time-consuming
// more info:
// http://tutorials.jenkov.com/java-json/jackson-installation.html#jackson-maven-dependencies
// https://stackoverflow.com/questions/50236951/the-import-com-fasterxml-jackson-databind-objectmapper-cannot-be-resolved/50237405

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

// import com.fasterxml.jackson.core.JsonGenerationException;
// import com.fasterxml.jackson.map.JsonMappingException;

import java.math.BigDecimal;

/**
* Sample test scenario for web page scraping with HTMLUnit
based on https://github.com/ksahin/introWebScraping
* @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
*/

// See also:
// http://htmlunit.sourceforge.net/gettingStarted.html

public class HtmlUnitTest extends BaseTest {

	private static boolean debug = false;

	private static ObjectMapper mapper = new ObjectMapper();
	private static WebClient client = new WebClient();
	private static final Logger log = LogManager.getLogger(HtmlUnitTest.class);
	private static String baseUrl;
	private static final String searchQuery = "laptop";
	private static HtmlPage page;
	private static HtmlInput inputSearch;
	private static String pageXML;

	private static String itemName;
	private static String itemUrl;
	private static String itemPrice;

	private static final String rowXpath = "//li[@class='result-row']";
	private static final String infoXpath = ".//p[@class='result-info']/a";
	private static final String priceXpath = ".//a/span[@class='result-price']";

	private static final String rowSelector = "li.result-row";
	private static final String infoSelector = "p.result-info > a";
	private static final String priceSelector = "a > span.result-price";

	// retrievable thru Selenium
	private static List<WebElement> elements;
	private WebElement element;
	private String pageSource = null;

	// retrievable thru HTMLUnit XML methods
	private static List<HtmlElement> rowsHtmlElementList;
	private static HtmlElement rowHtmlElement;
	private static HtmlElement priceHtmlElement;
	private static HtmlAnchor infoHtmlAnchor;

	// retrievable thru HTMLUnit CSS Selector methods
	private static DomNodeList<DomNode> rowsDomNodeList;
	private static Iterator<DomNode> rowsDomNodeIterator;
	private static DomNode rowDomNode;
	private static DomNode infoDomNode;
	private static DomNode priceDomNode;

	@BeforeClass
	public void beforeClass() throws IOException {
		super.setBrowser("chrome");
		// TODO: stop the chrome browser hanging in waiting for use.typekit.net
		super.beforeClass();
		assertThat(driver, notNullValue());

		client.getOptions().setCssEnabled(false);
		client.getOptions().setJavaScriptEnabled(false);

	}

	@BeforeMethod
	public void loadPage() {
		try {
			baseUrl = "https://miami.craigslist.org/search/sss?query="
					+ URLEncoder.encode(searchQuery, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}

		try {
			// NOTE: INFO: I/O exception (java.net.SocketException) caught when
			// processing request to
			// {s}->https://miami.craigslist.org:443: Connection reset
			// org.apache.http.impl.execchain.RetryExec execute
			// INFO: Retrying request to {s}->https://miami.craigslist.org:443
			page = client.getPage(baseUrl);
			// NOTE: require a recent version of htmlunit
			// with 2.25 com.gargoylesoftware.htmlunit.html.HtmlPage did not have
			// getElementsById
			inputSearch = (HtmlInput) (page.getElementsById("query").get(0));
			pageXML = page.asXml();
		} catch (FailingHttpStatusCodeException | IOException e) {
			e.printStackTrace();
		}
		assertThat(page, notNullValue());
		// confirm the page contains the search input
		assertThat(inputSearch, notNullValue());
		assertThat(pageXML, notNullValue());
		driver.navigate().to(baseUrl);
		// HTMLUnit does not support loading page source ?
		pageSource = driver.getPageSource();
	}

	@Test(enabled = true)
	public void testVisual() {
		elements = driver.findElements(By.xpath(rowXpath));
		assertThat(elements, notNullValue());
		assertThat(elements.size(), greaterThan(0));
		element = elements.get(0).findElement(By.xpath(infoXpath));
		assertThat(element, notNullValue());
		highlight(element);
		itemName = element.getText();
		System.err
				.println(String.format("Locating with Selenium: \"%s\"", rowXpath));
		System.err.println("Data: " + itemName);
	}

	@Test(enabled = true)
	public void testSilent() {
		rowsHtmlElementList = page.getByXPath(rowXpath);
		// implicit cast taking place of List<Object> to List<HtmlElement>
		assertThat(rowsHtmlElementList, notNullValue());
		assertThat(rowsHtmlElementList.size(), greaterThan(0));
		rowsHtmlElementList.stream()
				.forEach(element -> assertThat(element.getFirstByXPath(infoXpath),
						notNullValue()));
		rowHtmlElement = rowsHtmlElementList.get(0);
		infoHtmlAnchor = (HtmlAnchor) rowHtmlElement.getFirstByXPath(infoXpath);
		priceHtmlElement = rowHtmlElement.getFirstByXPath(priceXpath);

		itemName = infoHtmlAnchor.asText();
		itemUrl = infoHtmlAnchor.getHrefAttribute();

		// It is possible that an item doesn't have any price
		itemPrice = priceHtmlElement == null ? "0.0" : priceHtmlElement.asText();
		System.err.println(String.format("Name: %s\nPrice: %s\nUrl : %s", itemName,
				itemPrice, itemUrl));

	}

	@Test(enabled = true)
	public void testSilentWithSelector() {

		rowsDomNodeList = page.querySelectorAll(rowSelector);

		assertThat(rowsDomNodeList, notNullValue());
		assertThat(rowsDomNodeList.size(), greaterThan(0));

		rowsDomNodeIterator = rowsDomNodeList.iterator();
		assertTrue(rowsDomNodeIterator.hasNext());

		while (rowsDomNodeIterator.hasNext()) {
			rowDomNode = rowsDomNodeIterator.next();
			infoDomNode = rowDomNode.querySelector(infoSelector);
			assertThat(infoDomNode, notNullValue());
			itemName = infoDomNode.asText();
		}
		rowDomNode = rowsDomNodeList.get(0);
		infoDomNode = rowDomNode.querySelector(infoSelector);
		itemName = infoDomNode.asText();
		org.w3c.dom.NamedNodeMap infoNodeAttributes = infoDomNode.getAttributes();
		itemUrl = infoNodeAttributes.getNamedItem("href").getNodeValue();
		priceDomNode = rowDomNode.querySelector(priceSelector);
		// It is possible that an item doesn't have any price
		itemPrice = priceDomNode == null ? "0.0" : priceDomNode.asText();
		System.err.println(String.format("Name: %s\nPrice: %s\nUrl : %s", itemName,
				itemPrice, itemUrl));
		Item item = new Item();

		item.setTitle(itemName);
		item.setUrl(baseURL + itemUrl);
		item.setPrice(new BigDecimal(itemPrice.replace("$", "")));
		String jsonString = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			// one is required to reload project in eclipse to fix intellisense
			// getting
			// The method writeValueAsString(Object) from the type ObjectMapper refers
			// to the missing type JsonProcessingException
			jsonString = mapper.writeValueAsString(item);
		} catch (JsonProcessingException e) {

		}

		System.err.println(jsonString);

	}

	public static class Item {
		private String title;
		private BigDecimal price;
		private String url;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public BigDecimal getPrice() {
			return price;
		}

		public void setPrice(BigDecimal price) {
			this.price = price;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}
	}
}
