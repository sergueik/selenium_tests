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
 * Selected test scenarios for Selenium WebDriver
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class CssValidatorTest {

	private boolean debug = false;
	private static final CssValidator cssValidator = new CssValidator();

	@SuppressWarnings("deprecation")
	@BeforeClass
	public void beforeClass() throws IOException {
		cssValidator.setDebug(debug);
	}

	@Test(enabled = true)
	public void cssSelectorComprehensiveTokenTest() {
		String cssSelectorString = "body > h1[name='hello'] h2:nth-of-type(1)";
		String tokenTest = cssValidator.getTokenTest();
		tokenTest = "^([^ ~+>\\[]*(?:\\[[^\\]]+\\])*)(?:\\s*[ ~+>]\\s*([^ ~+>\\[].*))*$";
		System.err.println(tokenTest);
		Pattern pattern = Pattern.compile(tokenTest);
		Matcher match = pattern.matcher(cssSelectorString);

		boolean found_token = true;
		boolean found_tail = true;
		List<String> tokenBuffer = new ArrayList<>();
		List<String> tailBuffer = new ArrayList<>();
		int cnt = 0;
		while (match.find() && found_token && found_tail && cnt < 100) {

			if (match.group(1) == null || match.group(1) == "") {
				found_token = false;
			}
			if (match.group(2) == null || match.group(2) == "") {
				found_tail = false;
			}
			if (found_token) {
				tokenBuffer.add(match.group(1));
				System.err
						.println(String.format("Token = \"%s\"", tokenBuffer.get(cnt)));
			}
			if (found_tail) {
				tailBuffer.add(match.group(2));
				System.err.println("Tail = " + tailBuffer.get(cnt));
				match = pattern.matcher(match.group(2));
			}
			cnt++;
		}
		for (String cssSelectorTokenString : tokenBuffer) {
			assertTrue(
					cssSelectorTokenString.matches(cssValidator.getAttributeTest()));

		}
	}

	@Test(enabled = true)
	public void cssSelectorTokenTest() {
		String cssSelector = "a > b > c";
		assertTrue(cssSelector.matches(cssValidator.getTokenTest()));
	}

	// fails
	@Test(enabled = false)
	public void xPathTokenTest() {
		// TODO: passes because it is assumed to be one token
		String xpath = "a/b//c[@class='main']";
		assertFalse(xpath.matches(cssValidator.getTokenTest()));
	}

	@Test(enabled = true)
	public void cssSelectorAttributeTest() {
		String cssSelector = "a[name*='home']";
		assertTrue(cssSelector.matches(cssValidator.getAttributeTest()));
	}

	@Test(enabled = true)
	public void xpathAttributeTest1() {
		String xpath = "a[@name = 'home']";
		assertFalse(xpath.matches(cssValidator.getAttributeTest()));
	}

	@Test(enabled = true)
	public void xpathAttributeTest2() {
		String xpath = "a[contains(text(), 'home')]";
		assertFalse(xpath.matches(cssValidator.getAttributeTest()));
	}
}
