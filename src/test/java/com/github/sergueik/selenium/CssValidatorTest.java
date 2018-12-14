package com.github.sergueik.selenium;

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
 * CssSelector Lexer test scenarios for NSelene WebDriver wrapper
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class CssValidatorTest {

	private boolean debug = true;
	private static final CssValidator cssValidator = CssValidator.getInstance();

	@SuppressWarnings("deprecation")
	@BeforeClass
	public void beforeClass() throws IOException {
		cssValidator.setDebug(debug);
	}

	@Test(enabled = true)
	public void cssSelectorComprehensiveTokenTest() {
		// TODO: make cssValidator public method
		String cssSelectorString = "body > h1[name='hello'] h2:nth-of-type(1)";
		final String tokenValidator = cssValidator.getTokenValidator();
		Pattern pattern = Pattern.compile(tokenValidator);
		Matcher match = pattern.matcher(cssSelectorString);

		boolean foundToken = true;
		boolean foundRemainder = true;
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
							String.format("Extracted token = \"%s\"", tokenBuffer.get(cnt)));
				}
			}
			if (foundRemainder) {
				String remainder = match.group(2);
				tailBuffer.add(remainder);
				if (debug) {
					System.err
							.println(String.format("Remaining of the CssSelector: \"%s\""
									, remainder /* tailBuffer.get(cnt) */));
				}
				match = pattern.matcher(remainder);
			}
			cnt++;
		}

		final String attributeValidator = cssValidator.getAttributeValidator();
		for (String cssSelectorTokenString : tokenBuffer) {
			assertTrue(cssSelectorTokenString.matches(attributeValidator));
		}
	}

	@Test(enabled = true)
	public void cssSelectorTokenTest() {
		String cssSelector = "a.class > b#id c:nth-of-type(1)";
		assertTrue(cssSelector.matches(cssValidator.getTokenValidator()));
	}

	// NOTE: fails to fail
	// passes because it is assumed to be one token
	@Test(enabled = false)
	public void xPathTokenTest() {
		String xpath = "a/b//c[@class='main']";
		assertFalse(xpath.matches(cssValidator.getTokenValidator()));
	}

	@Test(enabled = true)
	public void cssSelectorAttributeTest() {
		String cssSelector = "a[name*='home']";
		assertTrue(cssSelector.matches(cssValidator.getAttributeValidator()));
	}

	@Test(enabled = true)
	public void xpathAttributeTest1() {
		String xpath = "a[@name = 'home']";
		assertFalse(xpath.matches(cssValidator.getAttributeValidator()));
	}

	@Test(enabled = true)
	public void xpathAttributeTest2() {
		String xpath = "a[contains(text(), 'home')]";
		assertFalse(xpath.matches(cssValidator.getAttributeValidator()));
	}
}
