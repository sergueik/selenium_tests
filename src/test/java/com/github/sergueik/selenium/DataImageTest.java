package com.github.sergueik.selenium;
/**
 * Copyright 2021 Serguei Kouzmine
 */

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.InvalidSelectorException;

import java.lang.ClassCastException;

import java.util.Map;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.w3c.dom.Attr;
import java.lang.RuntimeException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

/**
 * Selected test scenarios for Selenium WebDriver
 * 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class DataImageTest extends BaseTest {

	private static WebElement element;
	private static Object result;
	private static Attr attr;
	private static Map<String, Object> data;

	// $x("//img[@id='data_image']/@src")[0]
	private static final String script1 = "var path = arguments[0]; \n"
			+ "try { \n"
			+ "var element = document.evaluate(path, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue; \n"
			+ "return element; } \n" + "catch (e) { return e.toString()}";
	// NOTE: script2 will not work
	private static final String script2 = "function getAttributeAsJSON(path) {\n"
			+ "    try {\n"
			+ "        var element = document.evaluate(path, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;\n"
			+ "            return JSON.stringify(element)\n" + "    } catch (e) {\n"
			+ "        return JSON.stringify({\n"
			+ "            \"exception\": e.toString()\n" + "        });\n"
			+ "    }\n" + "}\n" + "var path = arguments[0];\n"
			+ "return getAttributeAsJSON(path);";
	private static final String script3 = getScriptContent(
			"getAttributeAsJSON.js");

	private static final Gson gson = new Gson();
	private static final Gson gsonPrinter = new GsonBuilder().setPrettyPrinting()
			.create();

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
	}

	@BeforeMethod
	public void loadPage() {
		// Arrange
		driver.navigate().to(getPageContent("data_image.html"));
	}

	@Test
	public void test1() {

		// Act
		element = driver.findElement(By.cssSelector("img#data_image"));
		assertThat(element, notNullValue());
		assertThat(element.getAttribute("src"), notNullValue());

		assertThat(element.getAttribute("src").length(), greaterThan(0));
		System.err.println("test1:\nimage source: "
				+ element.getAttribute("src").substring(0, 30) + "...");
	}

	@Test
	public void test2() {
		// Act
		try {
			result = driver.findElement(By.xpath("//img[@id='data_image']/@src"));
			assertThat(result, notNullValue());
			System.err.println("test2:\nimage source: "
					+ result.toString().substring(0, 30) + "...");
		} catch (InvalidSelectorException e) {
			// The result of the xpath expression "//img[@id='data_image']/@src"
			// is: [object Attr]. It should be an element."
			System.err.println("Exception (ignored): " + e.toString());
		}
	}

	@Test
	public void test3() {
		// Act
		result = executeScript(script1, "//img[@id='data_image']/@src");
		assertThat(result, notNullValue());
		System.err.println("test3:\nDOM call result: "
				+ result.toString().substring(0, 100) + "...");

	}

	@Test
	public void test4() {
		// Act
		try {
			attr = (Attr) executeScript(script1, "//img[@id='data_image']/@src");
			assertThat(attr, notNullValue());
			System.err.println("test4:\nAttr result: " + attr.getValue());
		} catch (ClassCastException e) {
			System.err.println("Exception (ignored): " + e.toString());
			// com.google.common.collect.Maps$TransformedEntriesMap
			// cannot be cast to
			// org.w3c.dom.Attr;
		}

	}

	@Test
	@SuppressWarnings("unchecked")
	public void test5() {
		// Act
		data = (Map<String, Object>) executeScript(script1,
				"//img[@id='data_image']/@src");
		assertThat(data, notNullValue());
		assertThat(data, hasKey("nodeValue"));
		System.err.println("test5:\ndata[\"nodeValue\"] : "
				+ data.get("nodeValue").toString().substring(0, 30) + "...");

	}

	@Test
	@SuppressWarnings("unchecked")
	public void test6() {
		// Act
		data = (Map<String, Object>) executeScript(script1,
				"//img[@id='data_image']/@src");
		assertThat(data, notNullValue());
		System.err.println("test1:\n");
		data.keySet().stream().map(key -> {
			Object value = data.get(key);
			return String.format("%s=%s", key,
					(value == null ? "null" : value.toString()));
		}).forEach(System.err::println);
	}

	// serialzing directly appears to fail
	@Test(enabled = true)
	public void test7() {
		// Act
		result = executeScript(script2, "//img[@id='data_image']/@src");

		assertThat(result, notNullValue());
		System.err.println("test7\nraw JSON : " + result.toString());
		attr = (Attr) gson.fromJson(result.toString(), Attr.class);
		assertThat(attr, notNullValue());
		assertThat(attr.getNodeValue(), nullValue());
	}

	@Test(enabled = true)
	public void test8() {
		// Act
		try {
			result = executeScript(script3, "//img[@id='data_image']/@src");
			assertThat(result, notNullValue());
			System.err.println("test8\nraw JSON : " + result.toString());
			attr = (Attr) gson.fromJson(result.toString(), org.w3c.dom.Attr.class);
			assertThat(attr, notNullValue());
		} catch (RuntimeException e) {
			System.err.println("Exception (ignored): " + e.toString());
			// java.lang.RuntimeException: Unable to invoke no-args constructor for
			// interface org.w3c.dom.Attr. Registering an InstanceCreator with Gson
			// for this type may fix this problem.
		}

	}

	@Test
	public void test9() {
		// Act
		result = executeScript(script3, "//img[@id='data_image']/@src");

		assertThat(result, notNullValue());
		System.err.println("test9\nraw JSON : " + result.toString());
		attr = (Attr) gson.fromJson(result.toString(), Attr.class);
		assertThat(attr, notNullValue());
		System.err.println(
				"test9\npretty-printed JSON : " + gsonPrinter.toJson(attr) + "\n");

		assertThat(attr.getNodeValue(), notNullValue());
		System.err.println("test9\nattr.getNodeValue() : "
				+ attr.getNodeValue().substring(0, 20) + "...");

	}

	private static class Attr implements org.w3c.dom.Attr {

		@Override
		public Node appendChild(Node arg0) throws DOMException {
			return null;
		}

		@Override
		public Node cloneNode(boolean arg0) {

			return null;
		}

		@Override
		public short compareDocumentPosition(Node arg0) throws DOMException {

			return 0;
		}

		@Override
		public NamedNodeMap getAttributes() {

			return null;
		}

		@Override
		public String getBaseURI() {

			return null;
		}

		@Override
		public NodeList getChildNodes() {

			return null;
		}

		@Override
		public Object getFeature(String arg0, String arg1) {

			return null;
		}

		@Override
		public Node getFirstChild() {

			return null;
		}

		@Override
		public Node getLastChild() {

			return null;
		}

		@Override
		public String getLocalName() {

			return null;
		}

		@Override
		public String getNamespaceURI() {

			return null;
		}

		@Override
		public Node getNextSibling() {
			return null;
		}

		@Override
		public String getNodeName() {
			return null;
		}

		@Override
		public short getNodeType() {

			return 0;
		}

		private String nodeValue = null;

		@Override
		public String getNodeValue() throws DOMException {
			return nodeValue;
		}

		@Override
		public Document getOwnerDocument() {

			return null;
		}

		@Override
		public Node getParentNode() {

			return null;
		}

		@Override
		public String getPrefix() {

			return null;
		}

		@Override
		public Node getPreviousSibling() {

			return null;
		}

		@Override
		public String getTextContent() throws DOMException {

			return null;
		}

		@Override
		public Object getUserData(String arg0) {

			return null;
		}

		@Override
		public boolean hasAttributes() {
			return false;
		}

		@Override
		public boolean hasChildNodes() {

			return false;
		}

		@Override
		public Node insertBefore(Node arg0, Node arg1) throws DOMException {

			return null;
		}

		@Override
		public boolean isDefaultNamespace(String namespaceURI) {

			return false;
		}

		@Override
		public boolean isEqualNode(Node arg) {

			return false;
		}

		@Override
		public boolean isSameNode(Node other) {

			return false;
		}

		@Override
		public boolean isSupported(String feature, String version) {
			return false;
		}

		@Override
		public String lookupNamespaceURI(String prefix) {
			return null;
		}

		@Override
		public String lookupPrefix(String namespaceURI) {
			return null;
		}

		@Override
		public void normalize() {
		}

		@Override
		public Node removeChild(Node oldChild) throws DOMException {

			return null;
		}

		@Override
		public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
			return null;
		}

		@Override
		public void setNodeValue(String nodeValue) throws DOMException {
			this.nodeValue = nodeValue;
		}

		@Override
		public void setPrefix(String prefix) throws DOMException {
		}

		@Override
		public void setTextContent(String textContent) throws DOMException {
		}

		@Override
		public Object setUserData(String key, Object data,
				UserDataHandler handler) {
			return null;
		}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public Element getOwnerElement() {
			return null;
		}

		@Override
		public TypeInfo getSchemaTypeInfo() {
			return null;
		}

		@Override
		public boolean getSpecified() {
			return false;
		}

		@Override
		public String getValue() {
			return null;
		}

		@Override
		public boolean isId() {
			return false;
		}

		@Override
		public void setValue(String arg0) throws DOMException {
		}
	};
}

