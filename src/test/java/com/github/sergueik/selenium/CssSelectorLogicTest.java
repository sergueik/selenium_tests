package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.CoreMatchers.is;
import java.io.IOException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * Selected test scenarios for Selenium WebDriver
 * 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

// this illustrates the "or" syntax of css selector
// https://www.w3schools.com/cssref/css_selectors.asp

public class CssSelectorLogicTest extends BaseTest {

	private String text = null;
	private final static String selector = "#rightColumn p.proxyid,#leftColumn p.proxyid";
	private JSONArray data = null;

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
	}

	@BeforeMethod
	public void loadPage() {
		driver.navigate().to(getPageContent("grid_console.html"));
	}

	@Test(enabled = true)
	public void test1() {
		// Arrange
		List<WebElement> elements = driver.findElements(By.cssSelector(selector));
		// Assert
		assertThat(elements, notNullValue());
		assertThat(elements.size(), greaterThan(0));
		elements.stream().map(element -> element.getText())
				.forEach(System.err::println);

	}

	// https://developer.mozilla.org/en-US/docs/Web/API/HTMLParagraphElement
	@Test(enabled = true)
	public void test2() throws JSONException {
		// Arrange
		String script = getScriptContent("get_queryselectorall_element_type.js");
		List<String> result = (List<String>) js.executeScript(script, selector);
		// Assert
		System.err.println(
				String.format("test2 result (raw data): %s", result.toString()));

		assertThat(result, notNullValue());
		data = new JSONArray(result);
		assertThat(data, notNullValue());
		assertThat(data.length(), is(4));
		for (int cnt = 0; cnt != data.length(); cnt++) {
			System.err.println(data.getString(cnt));
		}

	}

	@Test(enabled = true)
	public void test3() throws JSONException {
		// Arrange
		String script = String.format(scriptTemplate, "innerHTML");
		Object result = js.executeScript(script, selector);
		// Assert
		assertThat(result, notNullValue());
		System.err.println(
				String.format("test3 result (raw data): %s", result.toString()));
		data = new JSONArray(result.toString());
		assertThat(data, notNullValue());
		assertThat(data.length(), is(4));
		for (int cnt = 0; cnt != data.length(); cnt++) {
			System.err.println(data.getString(cnt));
		}
	}

	private final String scriptTemplate = "var selector = arguments[0]\n"
			+ "var elements = document.querySelectorAll(selector);\n"
			+ "let results = [];\n"
			+ "// origin: https://developer.mozilla.org/en-US/docs/Web/API/NodeList/forEach \n"
			+ "// Polyfill\n"
			+ "if (window.NodeList && !NodeList.prototype.forEach) {\n"
			+ "    NodeList.prototype.forEach = function (callback, thisArg) {\n"
			+ "        thisArg = thisArg || window;\n"
			+ "        for (var i = 0; i < this.length; i++) {\n"
			+ "            callback.call(thisArg, this[i], i, this);\n"
			+ "        }\n" + " 	   };\n" + "  }\n" +

			"  elements.forEach(\n"
			+ "  function(currentValue, currentIndex, listObj) {\n"
			+ "  results.push(currentValue.%s);\n" + "  },\n" + "  ''\n" + ");\n"
			+ "console.log(JSON.stringify(results));\n"
			+ "return JSON.stringify(results);";

	@Test(enabled = true)
	public void test4() throws JSONException {
		// Arrange
		String script = String.format(scriptTemplate, "innerText");
		// java.lang.ClassCastException: java.lang.String cannot be cast to
		// java.util.List
		String result = (String) js.executeScript(script, selector);
		// Assert
		assertThat(result, notNullValue());
		System.err.println(String.format("test4 result (raw data): %s", result));
		data = new JSONArray(result);
		assertThat(data, notNullValue());
		assertThat(data.length(), is(4));
		for (int cnt = 0; cnt != data.length(); cnt++) {
			System.err.println(data.getString(cnt));
		}

	}

}
