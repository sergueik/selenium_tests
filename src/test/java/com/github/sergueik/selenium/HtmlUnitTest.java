package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.testng.Assert.assertTrue;

import java.net.URLEncoder;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.core.JsonProcessingException;

// https://stackoverflow.com/questions/3763937/gson-and-deserializing-an-array-of-objects-with-arrays-in-it
// https://futurestud.io/tutorials/gson-mapping-of-arrays-and-lists-of-objects

// for YAML alone
// import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
* Sample test scenario typical for web page scraping with HTMLUnit
* based on https://github.com/ksahin/introWebScraping
* @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
*/

// See also:
// http://htmlunit.sourceforge.net/gettingStarted.html
// https://ksah.in/scraping-e-commerce-product-data/
// https://ksah.in/how-to-log-in-to-almost-any-websites/
public class HtmlUnitTest extends BaseTest {

	private static boolean debug = false;

	private final static boolean directConversion = true;
	// plain 1/1 conversion
	// https://futurestud.io/tutorials/gson-getting-started-with-java-json-serialization-deserializationhttps://futurestud.io/tutorials/gson-getting-started-with-java-json-serialization-deserializationhttps://futurestud.io/tutorials/gson-getting-started-with-java-json-serialization-deserialization
	Gson gson = directConversion ? new Gson()
			: new GsonBuilder()
					.registerTypeAdapter(ObjectItem.class, new ObjectItemSerializer())
					.create();

	private static JsonObject result = new JsonObject();
	private static ObjectMapper mapper = new ObjectMapper();

	private static final YAMLFactory yamlFactory = new YAMLFactory();
	static {
		yamlFactory.configure(YAMLGenerator.Feature.USE_NATIVE_TYPE_ID, false)
				.configure(YAMLGenerator.Feature.MINIMIZE_QUOTES, true)
				.configure(YAMLGenerator.Feature.ALWAYS_QUOTE_NUMBERS_AS_STRINGS, true);
	}
	private static ObjectMapper mapper2Yaml = new ObjectMapper(yamlFactory);

	private static final boolean prettyPrint = true;
	private static ObjectItem objItem;
	private static String jsonString = null;
	private static String yamlString = null;

	private static WebClient client = new WebClient();
	private static final Logger log = LogManager.getLogger(HtmlUnitTest.class);
	// TODO: drop inherited parent value: "about:blank"
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

	// collect via HTMLUnit XML methods
	private static List<HtmlElement> rowsHtmlElementList;
	private static HtmlElement rowHtmlElement;
	private static HtmlElement priceHtmlElement;
	private static HtmlAnchor infoHtmlAnchor;

	// collect via HTMLUnit CSS Selector methods
	private static DomNodeList<DomNode> rowsDomNodeList;
	private static Iterator<DomNode> rowsDomNodeIterator;
	private static DomNode rowDomNode;
	private static DomNode infoDomNode;
	private static DomNode priceDomNode;

	@BeforeClass
	public void beforeClass() throws IOException {
		super.setBrowser("chrome");
		// TODO: prevent Chrome browser from hanging in a wait for use.typekit.net
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
			e.printStackTrace();
			return;
		}

		try {
			// NOTE: INFO: I/O exception (java.net.SocketException) caught when
			// processing request to
			// {s}->https://miami.craigslist.org:443: Connection reset
			// org.apache.http.impl.execchain.RetryExec execute
			// INFO: Retrying request to {s}->https://miami.craigslist.org:443
			page = client.getPage(baseUrl);
			assertThat(page, notNullValue());

			// confirm the page contains the search input
			// NOTE: requires recent version of htmlunit
			// 2.25 and prior com.gargoylesoftware.htmlunit.html.HtmlPage did not have
			// getElementsById method
			inputSearch = (HtmlInput) (page.getElementsById("query").get(0));
			assertThat(inputSearch, notNullValue());
			pageXML = page.asXml();
			assertThat(pageXML, notNullValue());
		} catch (FailingHttpStatusCodeException | IOException e) {
			e.printStackTrace();
		}
		driver.navigate().to(baseUrl);
		// TODO: doesn't HTMLUnit support loading page source ?
		pageSource = driver.getPageSource();
	}

	// temporarily disable
	@Test(enabled = false)
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
		// implicit cast from List<Object> to List<HtmlElement>
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

		// item may have no price
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
		// item may have no price
		itemPrice = priceDomNode == null ? "0.0" : priceDomNode.asText();
		System.err.println(String.format("Name: %s\nPrice: %s\nUrl : %s", itemName,
				itemPrice, itemUrl));
		jsonString = null;
		objItem = new ObjectItem();
		objItem.setTitle(itemName);
		// TODO: check against overriding with th superclass value of baseURL
		objItem.setUrl(itemUrl);
		objItem.setPrice(Float.parseFloat(itemPrice.replace("$", "")));

		try {
			// one is required to reload project in eclipse to fix intellisense
			// getting
			// The method writeValueAsString(Object) from the type ObjectMapper refers
			// to the missing type JsonProcessingException
			jsonString = prettyPrint
					? mapper.writerWithDefaultPrettyPrinter().writeValueAsString(objItem)
					: mapper.writeValueAsString(objItem);
			System.err.println("Processing JSON: " + jsonString);
			// based on https://toster.ru/q/659184

			// @formatter:off
			// Type type1 = new TypeToken<ObjectItem>().getType();
			// The constructor TypeToken<ObjectItem>() is not visible
			// hence the empty args
			Type type1 = new TypeToken<ObjectItem>() { }.getType();
			// @formatter:on
			try {
				// NOTE: creating fresh instance of json parser
				Gson parser = new Gson();
				@SuppressWarnings("unused")
				ObjectItem data1 = parser.fromJson(jsonString, type1);
				// parse data to
			} catch (JsonSyntaxException e) {
				// Expected BEGIN_OBJECT but was STRING
				System.err.println("Exception (ignored) " + e.toString());
			}

			// @formatter:off
			// create custom type
			Type type2 = new TypeToken<Map<String, ObjectItem[]>>() {}.getType();
			// @formatter:on
			try {
				// NOTE: creating fresh instance of json parser
				Gson parser = new Gson();
				@SuppressWarnings("unused")
				Map<String, ObjectItem[]> data2 = parser
						.fromJson(String.format("{\"data\": [%s]}", jsonString), type2);
				// parse data to
				assertThat(data2, notNullValue());
				assertThat(data2.keySet(), notNullValue());
				// assert that value is an array
				assertThat(data2.values().toArray()[0], notNullValue());
			} catch (JsonSyntaxException e) {
				// Expected BEGIN_ARRAY but was STRING
				// TODO : report line number
				System.err.println("Exception (ignored) " + e.toString());
			}
			// based on https://toster.ru/q/659184
			// create custom type
			// @formatter:off
			Type type3 = new TypeToken<Map<String, ObjectItem>>() {}.getType();
			// @formatter:on

			try {
				// NOTE: creating fresh instance of json parser
				Gson parser = new Gson();
				@SuppressWarnings("unused")

				Map<String, ObjectItem> data3 = parser
						.fromJson(String.format("{\"data\": %s}", jsonString), type3);
				// parse data to
				assertThat(data3, notNullValue());
				assertThat(data3.keySet(), notNullValue());
			} catch (JsonSyntaxException e3) {
				// TODO : report line number
				System.err.println("Exception (ignored) " + e3.toString());
				// Expected BEGIN_OBJECT but was STRING
			}
		} catch (JsonProcessingException e) {
			System.err.println("Exception (ignored) " + e.toString());
		}
		System.err.println(
				String.format("JSON serialization with Jackson: \n%s\n", jsonString));
		// See also plain snakeyaml covered in
		// https://www.baeldung.com/java-snake-yaml
		// https://stackoverflow.com/questions/27734153/use-jackson-to-write-yaml
		// https://www.programcreek.com/java-api-examples/?api=com.fasterxml.jackson.dataformat.yaml.YAMLFactory

		try {
			yamlString = mapper2Yaml.writeValueAsString(objItem);
		} catch (JsonProcessingException e) {
			// same exeption class is thrown during YAML serialization
			yamlString = null;
		}
		System.err.println(
				String.format("YAML serialization with Jackson: \n%s\n", yamlString));

		System.err
				.println("JSON serialization with gson:\n" + gson.toJson(objItem));
	}
}
