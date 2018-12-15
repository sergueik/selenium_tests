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

	private boolean xpathComprehensiveTokenTest(String xpathString) {

		final String tokenValidator = xpathValidator.getTokenValidator();
		Pattern pattern = Pattern.compile(tokenValidator);
		Matcher match = pattern.matcher(xpathString);

		boolean foundToken = true;
		boolean foundRemainder = true;
		boolean found = false;
		List<String> tokenBuffer = new ArrayList<>();
		List<String> tailBuffer = new ArrayList<>();
		int cnt = 0; // paranoid
		while (match.find() && foundToken && foundRemainder && cnt < 100) {

			if (match.group(1) == null || match.group(1) == "") {
				foundToken = false;
			}
			if (match.group(2) == null || match.group(2) == "") {
				foundRemainder = false;
			}
			if (foundToken) {
				tokenBuffer.add(match.group(1));
				if (debug) {
					System.err.println(
							String.format("Extracted token: \"%s\"", tokenBuffer.get(cnt)));
				}
			}
			if (foundRemainder) {
				String remainder = match.group(2);
				tailBuffer.add(remainder);
				if (debug) {
					System.err.println(
							String.format("Remaining of the xpath: \"%s\"", remainder));
				}
				if (remainder.length() == 0) {
					if (debug) {
						System.err.println("Reached the end of the xpath string.");
					}
					found = true; // reached the end of the cssSelectorString string.
												// Grammar is matched
				} else {
					match = pattern.matcher(remainder);
				}
			} else {
				if (debug) {
					System.err.println("Remainder of the string fails to match. ");
				}
			}
			cnt++;
		}
		// assertTrue(found);
		// Condition Extractor not implemented in XPathValidator yet
		return found;
	}

	@SuppressWarnings("unused")
	@Test(enabled = true)
	public void xpathComprehensiveTokenTest2() {
		String xpathString = "a[@class='main']/b//c[@class='main']";
		assertTrue(xpathComprehensiveTokenTest(xpathString));
	}

	@SuppressWarnings("unused")
	@Test(enabled = true)
	public void xpathComprehensiveTokenTest3() {
		String cssSelectorString = "body > h1[name='hello'] h2:nth-of-type(1) div";
		assertFalse(xpathComprehensiveTokenTest(cssSelectorString));
	}

	@SuppressWarnings("unused")
	@Test(enabled = true)
	public void xpathComprehensiveTokenTest1() {
		// TODO: convert into xpathValidator.ComprehensiveTokenTest public method
		String xpathString = "a[@class='main']/b//c[@class='main']";
		if (false) {
			final String tailMatchString = "(\\s*//?\\s*[^ /\\[].*)$"; // one modifier
			Pattern tailPattern = Pattern.compile(tailMatchString);
			Matcher tailMatch = tailPattern.matcher(xpathString);
			if (tailMatch.find()) {
				System.err.println("Tail match: " + tailMatch.group(1));

			} else {
				System.err.println("Tail match failed. ");
			}

		}
		final String tokenValidator = xpathValidator.getTokenValidator();
		Pattern pattern = Pattern.compile(tokenValidator);
		Matcher match = pattern.matcher(xpathString);

		boolean foundToken = true;
		boolean foundRemainder = true;
		boolean found = false;
		List<String> tokenBuffer = new ArrayList<>();
		List<String> tailBuffer = new ArrayList<>();
		int cnt = 0; // paranoid
		while (match.find() && foundToken && foundRemainder && cnt < 100) {

			if (match.group(1) == null || match.group(1) == "") {
				foundToken = false;
			}
			if (match.group(2) == null || match.group(2) == "") {
				foundRemainder = false;
			}
			if (foundToken) {
				tokenBuffer.add(match.group(1));
				if (debug) {
					System.err.println(
							String.format("Extracted token: \"%s\"", tokenBuffer.get(cnt)));
				}
			}
			if (foundRemainder) {
				String remainder = match.group(2);
				tailBuffer.add(remainder);
				if (debug) {
					System.err.println(
							String.format("Remaining of the xpath: \"%s\"", remainder));
				}
				if (remainder.length() == 0) {
					if (debug) {
						System.err.println("Reached the end of the xpath string.");
					}
					found = true; // reached the end of the cssSelectorString string.
												// Grammar is matched
				} else {
					match = pattern.matcher(remainder);
				}
			} else {
				if (debug) {
					System.err.println("Remainder of the string fails to match. ");
				}
			}
			cnt++;
		}
		assertTrue(found);
		// Condition Extractor not implemented in XPathValidator yet
	}

	@Test(enabled = true)
	public void cssSelectorComprehensiveTokenTest1() {
		// TODO: convert into cssValidator.ComprehensiveToken public method
		String cssSelectorString = "body > h1[name='hello'] h2:nth-of-type(1) div";
		final String tokenValidator = cssValidator.getTokenValidator();
		Pattern pattern = Pattern.compile(tokenValidator);
		Matcher match = pattern.matcher(cssSelectorString);
		boolean found = false;
		boolean foundToken = true;
		boolean foundRemainder = true;
		List<String> tokenBuffer = new ArrayList<>();
		List<String> tailBuffer = new ArrayList<>();
		int cnt = 0; // paranoid
		while (match.find() && foundToken && foundRemainder && cnt < 100) {

			if (match.group(1) == null || match.group(1) == "") {
				foundToken = false;
			}
			if (match.group(2) == null /* || match.group(2) == "" */) {
				foundRemainder = false;
			}

			if (foundToken) {
				String token = match.group(1);
				tokenBuffer.add(token);
				if (debug) {
					System.err.println(
							String.format("Extracted token = \"%s\"", tokenBuffer.get(cnt)));
				}
			}
			// NOTE the difference between cssSelector and xpath tokens: a valid
			// cssSelectoron can not start with
			// DOM nav, so we chop it away explcitly from the remainder
			if (foundRemainder) {
				String remainderWithNavPrefix = match.group(2);
				String navSeparator = cssValidator.getNavSeparator();
				String remainder = remainderWithNavPrefix.replaceAll(navSeparator, "");
				tailBuffer.add(remainder);
				if (debug) {
					System.err
							.println(String.format("Remaining of the CssSelector: \"%s\"",
									remainder /* tailBuffer.get(cnt) */));
				}
				if (remainder.length() == 0) {
					if (debug) {
						System.err.println("Reached the end of the cssSelector string.");
					}
					found = true; // reached the end of the cssSelectorString string.
												// Grammar is matched
				} else {
					match = pattern.matcher(remainder);
				}
			} else {
				if (debug) {
					System.err.println("Remainder of the string fails to match. ");
				}
			}
			cnt++;
		}
		// assertTrue(found);
		final String attributeValidator = cssValidator.getAttributeValidator();
		for (String cssSelectorTokenString : tokenBuffer) {
			assertTrue(cssSelectorTokenString.matches(attributeValidator));
		}
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
