package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.IsNot.not;

import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Selected test scenarios for Selenium WebDriver
 * 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com) 
 */

// based on https://groups.google.com/forum/#!topic/selenium-users/ac_PixK1a8c
// see also:
// https://automated-testing.info/t/poisk-teksta-v-elemente-bez-uchyota-dochernih-elementov/24285/7
// https://automated-testing.info/t/kak-poluchit-tekst-tega-bez-vlozhennyh-tegov-v-selenide/23607/11
// executeJavaScript("return document.querySelector('div').firstChild");

// By design, one can not select a text node in Selenium using XPath
// even with an XPath query that would be valid on an XML file
// transformation, because XPath was not intended to be an element
// location strategy on HTML documents, only on much stricter XML
// documents.

// The exception that the result must strictly resolve to an element type is not
// a Selenium bug.

public class FindingTextObjectTest extends BaseTest {

	private static final Logger log = LogManager
			.getLogger(FindingTextObjectTest.class);
	private String text = "A2300";

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
	}

	@BeforeMethod
	public void loadPage() {
		driver.navigate().to(getPageContent("text_resource.htm"));
	}

	/* 
	@Test(enabled = true,expectedExceptions = {
			org.openqa.selenium.InvalidSelectorException.class }  )
			*/
	@Test(enabled = true)
	// inspired by:
	// https://software-testing.ru/forum/index.php?/topic/38819-poluchit-tekst-vnutri-span/
	// Note that pure XML tools handle this correclty with no effort:
	// xmllint --xpath '/span/text()' /tmp/a.xml
	// "Text without the Number"
	/*
	 * 
	 * Famous
	 * https://github.com/seleniumhq/selenium-google-code-issue-archive/issues/5459
	 * issue invalid selector: The result of the xpath expression is [object Text].
	 * It should be an element.
	 */
	public void plainFailingXPathTest() {
		// Arrange
		String selector = "//span/text()";
		Object textElement = null;

		try {
			// Type mismatch: cannot convert from WebElement to String
			// String text = driver.findElement(By.xpath(selector));
			textElement = driver.findElement(By.xpath(selector));
			// Assert
			assertThat(textElement, notNullValue());
			System.err.println(String.format("%s finds %s", selector, textElement));
		} catch (org.openqa.selenium.InvalidSelectorException e) {
			// invalid selector: The result of the xpath expression "//span/text()"
			// is:
			// [object Text].
			// It should be an element.
			System.err.println("Exception (ignored): " + e.toString());
			// see also:
			// https://stackoverflow.com/questions/25823493/invalidselectorerror-the-result-of-the-xpath-expression-is-object-text-it-sh
		}
		selector = "//span";
		WebElement element = driver.findElement(By.xpath(selector));
		assertThat(element, notNullValue());

		System.err.println(String.format("getText finds %s", getText(element)));

		// NOTE: getTextOnly with an extra argument isn't destructive.
		// After it run the getText will also find
		// the text only
		String resultTextOnly = getTextOnly(element, true);
		String resultText = element.getText();
		// Assert
		assertThat(resultTextOnly, equalTo("\n\nText without the Number\n"));
		assertThat(resultText, containsString("1 Text"));
		System.err.println(
				String.format("getTextOnly(...,true) finds %s", resultTextOnly));
		System.err.println(String.format("getText() finds %s", resultText));
		// NOTE: getTextOnly is destructive. After it run the getText will also find
		// the text only
		resultTextOnly = getTextOnly(element);
		resultText = element.getText();
		// Assert
		assertThat(resultTextOnly, equalTo("Text without the Number"));
		assertThat(resultText, not(containsString("1")));
		System.err.println(String.format("getTextOnly() finds %s", resultTextOnly));
		System.err.println(String.format("getText() finds %s", resultText));
	}

	// NOTE: some selectors intentionally invalid -
	// none would achieve the goal of locating
	// "text to find" using XPath with stock Selenium `findElement` method
	@Test(enabled = false)
	public void notFindingViaXPathTest() {
		// Arrange
		List<WebElement> elements = new ArrayList<>();

		List<String> selectors = new ArrayList<>(Arrays
				.asList(new String[] { "following-sibling::text()", "following-sibling",
						"following-sibling::*", "following-sibling::node()" }));

		elements = driver.findElements(By.xpath(String.format(
				"//table[@class='questionKeyHeader']//td/a[contains(text(), '%s')]",
				text)));
		assertTrue(elements.size() > 0);
		WebElement element = elements.get(0);
		highlight(element);
		// Act
		for (String selector : selectors) {
			try {
				WebElement textElement = element.findElement(By.xpath(selector));
				// Assert
				assertThat(textElement, notNullValue());
				System.err.println(String.format("%s finds %s", selector,
						textElement.getAttribute("outerHTML")));
				// flash(textElement);
			} catch (InvalidSelectorException e) {
				// org.openqa.selenium.InvalidSelectorError:
				// The result of the xpath expression
				// "following-sibling::text()"
				// is: [object Text].
				// It should be an element.
				System.err.println(String.format("%s leads to the exception: %s...",
						selector, e.toString().substring(0, 200)));
			} catch (NoSuchElementException e) {
				// org.openqa.selenium.NoSuchElementException:
				// Unable to locate element:
				// {"method":"xpath","selector":"following-sibling"}
				System.err.println(String.format("%s leads to the exception: %s...",
						selector, e.toString().substring(0, 200)));
			}
			sleep(100);
		}
	}

	// Javascript DOM method successfully finds the text Element
	// NOTE: compact querySelector locator expressions for sibling
	// is challenging to construct, but a find + a call DOM method call is easy
	// https://stackoverflow.com/questions/27571808/find-next-cell-contained-in-sibling-row-with-queryselector
	// https://plainjs.com/javascript/traversing/get-siblings-of-an-element-40/
	// https://developer.mozilla.org/en-US/docs/Web/API/Element/querySelectorAll
	@Test(enabled = true)
	public void findingViaDOMMethodCallTest() {
		// Arrange
		WebElement element = driver.findElement(By.xpath(String.format(
				"//table[@class='questionKeyHeader']//td/a[contains(text(), '%s')]",
				text)));
		highlight(element);
		// Act
		String script = "var element = arguments[0]; return element.nextSibling.textContent.trim()";

		String result = (String) js.executeScript(script, element);

		// Assert
		assertThat(result, equalTo("text to find"));
		System.err.println(String.format("%s finds %s", script, result));

	}

}
