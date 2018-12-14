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

/**
 * XPath Lexer-style test scenarios for NSelene Selenium WebDriver wrapper
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class XPathValidatorTest {

	private boolean debug = true;
	private static final XPathValidator xpathValidator = XPathValidator
			.getInstance();

	@SuppressWarnings("deprecation")
	@BeforeClass
	public void beforeClass() throws IOException {
		xpathValidator.setDebug(debug);
	}

	@SuppressWarnings("unused")
	@Test(enabled = true)
	public void xpathComprehensiveTokenTest() {
		// TODO: convert into xpathValidator public method
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

		boolean found_token = true;
		boolean foundRemainder = true;
		List<String> tokenBuffer = new ArrayList<>();
		List<String> tailBuffer = new ArrayList<>();
		int cnt = 0; // paranoid
		while (match.find() && found_token && foundRemainder && cnt < 100) {

			if (match.group(1) == null || match.group(1) == "") {
				found_token = false;
			}
			if (match.group(2) == null || match.group(2) == "") {
				foundRemainder = false;
			}
			if (found_token) {
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
				match = pattern.matcher(remainder);
			} else {

				if (debug) {
					System.err.println("Tail fails to match. ");
				}

			}
			cnt++;
		}
		/*
				final String attributeValidator = xpathValidator.getAttributeValidator();
				for (String xpathTokenString : tokenBuffer) {
					assertTrue(xpathTokenString.matches(attributeValidator));
				}
				*/
	}

	@Test(enabled = true)
	public void xPathTokenTest() {
		String xpathString = "a/b//c";
		assertTrue(xpathString.matches(xpathValidator.getTokenValidator()));
	}

	@Test(enabled = true)
	public void xPathTokenTest2() {
		// TODO: passes because it is assumed to be one token
		String xpathString = "a/b//c[@class='main']";
		assertTrue(xpathString.matches(xpathValidator.getTokenValidator()));
	}

	@Test(enabled = true)
	public void xPathTokenTest3() {
		// TODO: passes because it is assumed to be one token
		String xpathString = "//a[@class='main']";
		assertTrue(xpathString.matches(xpathValidator.getTokenValidator()));
	}

	@Test(enabled = true)
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
}
