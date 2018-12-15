package com.github.sergueik.selenium;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.IOException;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * XPath Lexer-style test scenarios for NSelene Selenium WebDriver wrapper
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class ValidatorsTest {

	private boolean debug = false;
	private static final CssValidator cssValidator = CssValidator.getInstance();
	private static final XPathValidator xpathValidator = XPathValidator
			.getInstance();

	@BeforeClass
	public void beforeClass() throws IOException {
		xpathValidator.setDebug(debug);
		cssValidator.setDebug(debug);
	}

	@Test(dataProvider = "Valid XPaths", enabled = true)
	public void xpathComprehensiveTokenPositiveTest(String xpath) {
		assertTrue(xpathValidator.comprehensiveTokenTest(xpath),
				String.format("\"%s\"to be valid XPath", xpath));
	}

	@Test(dataProvider = "Valid CssSelectors", enabled = true)
	public void xpathComprehensiveTokenNegativeTest(String cssSelector) {
		assertFalse(xpathValidator.comprehensiveTokenTest(cssSelector),
				String.format("the CSS selector \"%s\"to be valid XPath (not really)",
						cssSelector));
	}

	@Test(dataProvider = "Texts", enabled = true)
	public void xpathComprehensiveTokenNegativeTextTest(String textString) {
		assertFalse(xpathValidator.comprehensiveTokenTest(textString));
	}

	@Test(dataProvider = "Valid CssSelectors", enabled = true)
	public void cssSelectorComprehensiveTokenPositiveTest(String cssSelector) {
		assertFalse(cssValidator.comprehensiveTokenTest(cssSelector),
				String.format("\"%s\"to be valid CSS selector", cssSelector));
	}

	@Test(dataProvider = "Valid XPaths", enabled = true)
	public void cssSelectorComprehensiveTokenNegativeTest(String xpath) {
		assertFalse(cssValidator.comprehensiveTokenTest(xpath), String.format(
				"the XPath \"%s\"to be valid CSS selector (not really)", xpath));
	}

	// NOTE: The test will fail to fail unless
	// one enforces some ad.hoc conditions on cssSelector to be
	// distinguishable from the plain English text,
	// like e.g. enforcing token to always contain common
	// page tag names "a", "td", "div", "span", "input" etc.
	// or have a ".className", "#id" or a condition "[attibute = value]" attached.
	@Test(enabled = false)
	public void cssSelectorComprehensiveTokenNegativeTextTest() {
		String textString = "hello world";
		assertFalse(cssValidator.comprehensiveTokenTest(textString));
	}

	@Test(dataProvider = "Valid XPaths", enabled = true)
	public void xPathTokenTest(String xpathString) {
		assertTrue(xpathString.matches(xpathValidator.getTokenValidator()),
				String.format("\"%s\"to be valid XPath", xpathString));
	}

	@Test(dataProvider = "Valid CssSelectors", enabled = true)
	public void cssSelectorTokenTest(String cssSelector) {
		assertTrue(cssSelector.matches(cssValidator.getTokenValidator()));
	}

	// NOTE: passes because it is assumed to be single token.
	// WORKAROUND: Perform XPath probing first
	@Test(dataProvider = "Valid XPath Conditions", enabled = false)
	public void CssLocatorTokenBadTest(String xpathCondition) {
		assertFalse(xpathCondition.matches(cssValidator.getTokenValidator()));
	}

	@Test(dataProvider = "Valid CssSelector Conditions", enabled = true)
	public void cssSelectorConditionTest(String cssSelectorCondition) {
		assertTrue(
				cssSelectorCondition.matches(cssValidator.getAttributeValidator()),
				String.format("\"%s\"to be valid condition in CSS selector",
						cssSelectorCondition));
	}

	// Valid cssSelectors data
	@DataProvider(name = "Valid CssSelectors")
	public Object[][] Data1() {
		return new Object[][] { { "a.class > b#id c:nth-of-type(1)" },
				{ "div.class ~ input#id" },
				{ "body > h1[name='hello'] h2:nth-of-type(1) div" }, };
	}

	// Valid cssSelector condition
	@DataProvider(name = "Valid CssSelector Conditions")
	public Object[][] Data2() {
		return new Object[][] { { "div:nth-of-type(1)" },
				{ "input[type='submit']" }, { "div[class*='ng']" },
				{ "h1[name='hello']" }, { "input[name^='Pass']" }, };
	}

	// TODO: need to modify Condition extractor to allow simple id and class
	// TODO: also mis-recognized as a valid XPath
	@DataProvider(name = "Unrecognized Valid CssSelector Conditions")
	public Object[][] Data2B() {
		return new Object[][] { { "input#id" }, { "div.class" },
				// derivatives
				{ "input#id[name^='Pass']" }, { "input.class[name^='Pass']" }, };
	}

	// Valid XPath data
	@DataProvider(name = "Valid XPaths")
	public Object[][] Data10() {
		return new Object[][] { { "a[@class='main']/b//c[@class='main']" },
				{ "/body//td/following-sibling::td[1]" }, { "/tr[0]/../th" } };
	}

	// Valid XPath condition
	@DataProvider(name = "Valid XPath Conditions")
	public Object[][] Data3() {
		return new Object[][] { { "a[contains(text(), 'home')]" }, { "//a/b[1]/c" },
				{ "//a[contains(@class,'main')]/b[@id]/d[1]/.." }, };
	}

	// Plain Text samples
	@DataProvider(name = "Texts")
	public Object[][] Data5() {
		return new Object[][] { { "Hello World" } };
	}
}
