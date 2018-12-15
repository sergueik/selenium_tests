package com.github.sergueik.selenium;

import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.annotations.Test;

/**
 * XPAth NBF based Lexer-style validator for NSelene WebDriver wrapper .net project
 * https://www.w3.org/2002/11/xquery-xpath-applets/xpath-jjdoc.html
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

// following on (only tokenzation part is finished):
// https://github.com/sam-rosenthal/java-cssSelector-to-xpath/blob/master/src/main/java/org/sam/rosenthal/cssselectortoxpath/utilities/CssSelectorStringSplitter.java
// https://github.com/sam-rosenthal/java-cssSelector-to-xpath/blob/master/src/main/java/org/sam/rosenthal/cssselectortoxpath/utilities/CssElementAttributeParser.java

public class XPathValidator {

	// singleton style
	private static XPathValidator instance = new XPathValidator();

	private XPathValidator() {
	}

	public static XPathValidator getInstance() {
		return instance;
	}

	// origin: CssSelectorStringSplitter.java
	// The following appears, because of excessive grouping
	// to neither work too well nor is really needed for task at hand

	private static final String DOM_NAVS = " /";
	private static final String DOM_NAV_RE = "(?://?)";
	private static final String ROOT_DOM_NAV_RE = "(?:/?/?)";

	// @formatter:off
	private static final String ELEMENT_WITH_CONDITION = "([^" + DOM_NAVS + "\\[]*(\\[[^\\]]+\\])*)";
	private static final String ELEMENT_WITH_CONDITION_FOLLOWED_BY_DOM_NAV_EXTRACTOR = "^" + ELEMENT_WITH_CONDITION + "($|(\\s*(" + DOM_NAV_RE + ")\\s*" + "([^" + DOM_NAVS + "].*)$))";
	// @formatter:on

	// Shortened grammar expression:

	// @formatter:off
	private static final String ELEMENT_WITH_CONDITION_FOLLOWED_BY_DOM_NAV_EXTRACTOR_FIXED = "^\\s*(/?/?\\s*[^ /\\[]+(?:\\[[^\\]]+\\])*)($|\\s*//?\\s*[^ /\\[]+.*$)";
	// @formatter:on

	// copied from: CssElementAttributeParser.java
	// may be unneeded, currently unused, need more work

	private static final String QUOTES_RE = "([\"\'])";
	private static final String ATTRIBUTE_VALUE_RE = "([-_.#a-zA-Z0-9:\\/ ]+)";
	private static final String ATTRIBUTE_VALUE_RE_NO_SPACES = "([-_.#a-zA-Z0-9:\\/]+)";
	private static final String ATTRIBUTE_TYPE_RE = createElementAttributeNameRegularExpression();
	private static final String ELEMENT_ATTRIBUTE_NAME_RE = "(-?[_a-zA-Z]+[_a-zA-Z0-9-]*)";
	private static final String STARTING_ELEMENT_RE = "^("
			+ ELEMENT_ATTRIBUTE_NAME_RE + "|([*]))?";
	private static final String PSUEDO_RE = "(:[a-z][a-z\\-]*([(][^)]+[)])?)";
	private static final String ATTRIBUTE_RE = "(" + PSUEDO_RE + "|(\\[" + "\\s*"
			+ ELEMENT_ATTRIBUTE_NAME_RE + "\\s*" + ATTRIBUTE_TYPE_RE + "\\s*" + "(("
			+ QUOTES_RE + ATTRIBUTE_VALUE_RE + QUOTES_RE + ")|("
			+ ATTRIBUTE_VALUE_RE_NO_SPACES + "))?" + "\\s*" + "\\]))";

	private static final String XPATH_ATTRIBUTE_PATTERN = STARTING_ELEMENT_RE
			+ ATTRIBUTE_RE + "*$";

	private static String createElementAttributeNameRegularExpression() {
		StringBuilder builder = new StringBuilder();
		for (CssAttributeValueType type : CssAttributeValueType.values()) {
			if (builder.length() == 0) {
				builder.append("((");
			} else {
				builder.append(")|(");
			}
			builder.append("\\").append(type.getEqualStringName());
		}
		builder.append("))?");
		// System.out.println("elementAttributeRE="+builder);
		return builder.toString();
	}

	// end imported code
	@SuppressWarnings("unused")
	private boolean debug = false;

	public void setDebug(boolean value) {
		this.debug = value;
	}

	// NOTE: cannot reference a field "before" it is defined.
	// private static final String tokenValidator =
	// ELEMENT_WITH_CONDITION_FOLLOWED_BY_DOM_NAV_EXTRACTOR;
	private static final String tokenValidator = ELEMENT_WITH_CONDITION_FOLLOWED_BY_DOM_NAV_EXTRACTOR_FIXED;
	private static final String attributeValidator = XPATH_ATTRIBUTE_PATTERN;

	// control logging
	private boolean reportedTokenValidator = false;
	private boolean reportedAttributeValidator = false;

	public String getTokenValidator() {
		if (debug) {
			if (!reportedTokenValidator) {
				System.err.println("XPath Token validator: " + tokenValidator);
				reportedTokenValidator = true;
			}
		}
		return tokenValidator;
	}

	public String getAttributeValidator() {
		if (debug) {
			if (!reportedAttributeValidator) {
				System.err.println("XPath Attribute validator: " + attributeValidator);
				reportedAttributeValidator = true;
			}
		}
		return attributeValidator;
	}

	public boolean comprehensiveTokenTest(String xpathString) {

		Pattern pattern = Pattern.compile(tokenValidator);
		Matcher match = pattern.matcher(xpathString);

		boolean foundToken = true;
		boolean foundRemainder = true;
		boolean found = false;
		List<String> tokenBuffer = new ArrayList<>();
		List<String> tailBuffer = new ArrayList<>();
		int tokenCnt = 0;
		int maxTokenCnt = 100; // paranoid
		while (match.find() && foundToken && foundRemainder
				&& tokenCnt < maxTokenCnt) {

			if (match.group(1) == null || match.group(1) == "") {
				foundToken = false;
			}
			if (match.group(2) == null || match.group(2) == "") {
				foundRemainder = false;
			}
			if (foundToken) {
				tokenBuffer.add(match.group(1));
				if (debug) {
					System.err.println(String.format("Extracted token: \"%s\"",
							tokenBuffer.get(tokenCnt)));
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
			tokenCnt++;
		}
		// Condition Extractor not implemented in XPathValidator yet
		return found;
	}

}
