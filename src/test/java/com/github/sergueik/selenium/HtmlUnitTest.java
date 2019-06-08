package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
* Sample test scenario for web page scraping with HTMLUnit
// http://htmlunit.sourceforge.net/
* @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
*/

// See also:
// https://github.com/ksahin/introWebScraping

public class HtmlUnitTest extends BaseTest {

	private static boolean debug = false;

	private static WebClient client = new WebClient();
	private static final Logger log = LogManager.getLogger(HtmlUnitTest.class);
	private static final String rowSelector = "//li[@class='result-row']";
	private static String baseUrl;
	private static final String searchQuery = "laptop";
	private static HtmlPage page;
	private static String pageXML;
	private static String itemName;
	private static String itemUrl;
	private static String itemPrice;

	private final String infoXpath = ".//p[@class='result-info']/a";
	private final String priceXpath = ".//a/span[@class='result-price']";

	private String pageSource = null;
	private static List<HtmlElement> htmlItems;
	private static HtmlElement htmlItem;
	private static List<WebElement> elements;
	private WebElement element;

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
			page = client.getPage(baseUrl);
			pageXML = page.asXml();
		} catch (FailingHttpStatusCodeException | IOException e) {
			e.printStackTrace();
		}
		assertThat(page, notNullValue());
		assertThat(pageXML, notNullValue());
		driver.navigate().to(baseUrl);
		// HTMLUnit does not support loading page source ?
		pageSource = driver.getPageSource();
	}
	
	@Test(enabled = true)
	public void testVisual() {
		elements = driver.findElements(By.xpath(rowSelector));
		assertThat(elements, notNullValue());
		assertThat(elements.size(), greaterThan(0));
		element = elements.get(0).findElement(By.xpath(infoXpath));
		assertThat(element, notNullValue());
		highlight(element);
		itemName = element.getText();
		System.err
				.println(String.format("Locating with Selenium: \"%s\"", rowSelector));
		System.err.println("Data: " + itemName);
	}

	@Test(enabled = true)
	public void testSilent() {
		htmlItems = page.getByXPath(rowSelector);
		// implicit cast taking place of List<Object> to List<HtmlElement>
		assertThat(htmlItems, notNullValue());
		assertThat(htmlItems.size(), greaterThan(0));
		htmlItems.stream().forEach(element -> {
			HtmlAnchor itemAnchor = ((HtmlAnchor) element.getFirstByXPath(infoXpath));
			assertThat(itemAnchor, notNullValue());
		}
		);
		htmlItem = htmlItems.get(0);
		HtmlAnchor itemAnchor = ((HtmlAnchor) htmlItem.getFirstByXPath(infoXpath));

		HtmlElement spanPrice = ((HtmlElement) htmlItem
				.getFirstByXPath(priceXpath));

		itemName = itemAnchor.asText();
		itemUrl = itemAnchor.getHrefAttribute();

		// It is possible that an item doesn't have any price
		itemPrice = spanPrice == null ? "0.0" : spanPrice.asText();
		System.err.println(String.format("Name: %s\nPrice: %s\nUrl : %s", itemName,
				itemPrice, itemUrl));

	}

}
