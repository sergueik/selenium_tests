package com.github.sergueik.selenium;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.annotations.Test;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * XPath Lexer-style test scenarios for NSelene Selenium WebDriver wrapper
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class ValidatorsTest {

	private boolean debug = true;
	private static final CssValidator cssValidator = CssValidator.getInstance();
	private static final XPathValidator xpathValidator = XPathValidator
			.getInstance();

	@SuppressWarnings("deprecation")
	@BeforeClass
	public void beforeClass() throws IOException {
		xpathValidator.setDebug(debug);
		cssValidator.setDebug(debug);
	}

	@SuppressWarnings("unused")
	@Test(enabled = true)
	public void xpathComprehensiveTokenPositiveTest() {
		String xpathString = "a[@class='main']/b//c[@class='main']";
		assertTrue(xpathValidator.comprehensiveTokenTest(xpathString));
	}

	@SuppressWarnings("unused")
	@Test(enabled = true)
	public void xpathComprehensiveTokenNegativeTest() {
		String cssSelectorString = "body > h1[name='hello'] h2:nth-of-type(1) div";
		assertFalse(xpathValidator.comprehensiveTokenTest(cssSelectorString));
	}

	@Test(enabled = true)
	public void xpathComprehensiveTokenNegativeTextTest() {
		String textString = "hello world";
		assertFalse(xpathValidator.comprehensiveTokenTest(textString));
	}

	@Test(enabled = true)
	public void cssSelectorComprehensiveTokenPositiveTest() {
		String cssSelectorString = "body > h1[name='hello'] h2:nth-of-type(1) div";
		assertFalse(cssValidator.comprehensiveTokenTest(cssSelectorString));
	}

	@Test(enabled = true)
	public void cssSelectorComprehensiveTokenNegativeTest() {
		String xpathString = "a[@class='main']/b//c[@class='main']";
		assertFalse(cssValidator.comprehensiveTokenTest(xpathString));
	}

	// NOTE: will fail to fail unless
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

	@Test(enabled = false)
	public void xPathTokenTest() {
		String xpathString = "a/b//c";
		assertTrue(xpathString.matches(xpathValidator.getTokenValidator()));
	}

	@Test(enabled = false)
	public void xPathTokenTest2() {
		// TODO: passes because it is assumed to be one token
		String xpathString = "a/b//c[@class='main']";
		assertTrue(xpathString.matches(xpathValidator.getTokenValidator()));
	}

	@Test(enabled = false)
	public void xPathTokenTest3() {
		// TODO: passes because it is assumed to be one token
		String xpathString = "//a[@class='main']";
		assertTrue(xpathString.matches(xpathValidator.getTokenValidator()));
	}

	@Test(enabled = false)
	public void xPathTokenTest4() {
		String xpathString = "//a[contains(@class,'main')]/b[@id]/d[1]/..";
		assertTrue(xpathString.matches(xpathValidator.getTokenValidator()));
	}

	// TODO: finish
	@Test(enabled = false)
	public void xpathAttributeConditonTest() {
		String xpath = "a[@name = 'home']";
		assertFalse(xpath.matches(xpathValidator.getAttributeValidator()));
	}

	// TODO: finish
	@Test(enabled = false)
	public void xpathTextConditonTest() {
		String xpath = "a[contains(text(), 'home')]";
		assertTrue(xpath.matches(xpathValidator.getAttributeValidator()));
	}

	@Test(enabled = false)
	public void cssSelectorTokenTest() {
		String cssSelector = "a.class > b#id c:nth-of-type(1)";
		assertTrue(cssSelector.matches(cssValidator.getTokenValidator()));
	}

	// NOTE: fails to fail
	// passes because it is assumed to be one token
	@Test(enabled = false)
	public void xPathTokenBadTest() {
		String xpath = "a/b//c[@class='main']";
		assertFalse(xpath.matches(cssValidator.getTokenValidator()));
	}

	@Test(enabled = false)
	public void cssSelectorAttributeTest() {
		String cssSelector = "a[name*='home']";
		assertTrue(cssSelector.matches(cssValidator.getAttributeValidator()));
	}

	@Test(enabled = false)
	public void xpathAttributeTest1() {
		String xpath = "a[@name = 'home']";
		assertFalse(xpath.matches(cssValidator.getAttributeValidator()));
	}

	@Test(enabled = false)
	public void xpathAttributeTest2() {
		String xpath = "a[contains(text(), 'home')]";
		assertFalse(xpath.matches(cssValidator.getAttributeValidator()));
	}
}
