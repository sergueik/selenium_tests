package com.github.sergueik.selenium;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

/**
 * Selected test scenarios for NSelene WebDriver wrapper .net project
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

// based on:
// https://github.com/sam-rosenthal/java-cssSelector-to-xpath/blob/master/src/main/java/org/sam/rosenthal/cssselectortoxpath/utilities/CssSelectorStringSplitter.java
// https://github.com/sam-rosenthal/java-cssSelector-to-xpath/blob/master/src/main/java/org/sam/rosenthal/cssselectortoxpath/utilities/CssElementAttributeParser.java
public class CssValidator {

	private boolean debug = false;
	// origin: CssSelectorStringSplitter.java
	private static final String COMBINATORS = " ~+>";
	//  fixed
	private static final String COMBINATOR_RE = "[" + COMBINATORS + "]+";

	private static final String ELEMENT_AND_ATTRIBUTE = "([^" + COMBINATORS
			+ "\\[]*(\\[[^\\]]+\\])*)";
	private static final String ELEMENT_AND_ATTRIBUTE_FOLLOWED_BY_COMBINATOR_AND_REST_OF_LINE = "^"
			+ ELEMENT_AND_ATTRIBUTE + "($|(\\s*(" + COMBINATOR_RE + ")\\s*" + "([^"
			+ COMBINATORS + "].*)$))";

	// origin: CssElementAttributeParser.java
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

	private static final String CSS_ATTRIBUTE_PATTERN = STARTING_ELEMENT_RE
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

	public void setDebug(boolean value) {
		this.debug = value;
	}

	public String getTokenTest() {
		return ELEMENT_AND_ATTRIBUTE_FOLLOWED_BY_COMBINATOR_AND_REST_OF_LINE;
	}

	public String getAttributeTest() {
		return CSS_ATTRIBUTE_PATTERN;
	}

}
