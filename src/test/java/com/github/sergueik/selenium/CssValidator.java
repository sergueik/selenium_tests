package com.github.sergueik.selenium;

/**
 * CSS Lexer-based validator for NSelene WebDriver wrapper .net project
 * https://www.w3.org/TR/CSS21/grammar.html
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

// based on:
// https://github.com/sam-rosenthal/java-cssSelector-to-xpath/blob/master/src/main/java/org/sam/rosenthal/cssselectortoxpath/utilities/CssSelectorStringSplitter.java
// https://github.com/sam-rosenthal/java-cssSelector-to-xpath/blob/master/src/main/java/org/sam/rosenthal/cssselectortoxpath/utilities/CssElementAttributeParser.java

public class CssValidator {

	private static CssValidator instance = new CssValidator();

	private CssValidator() {
	}

	public static CssValidator getInstance() {
		return instance;
	}

	// origin: CssSelectorStringSplitter.java
	// The following appears, because of excessive grouping
	// to neither work too well nor is really needed for task at hand

	private static final String DOM_NAVS = " ~+>";
	private static final String DOM_NAV_RE = "[" + DOM_NAVS + "]+"; // TODO: debug

	// @formatter:off
	private static final String ELEMENT_AND_CONDITION = "([^" + DOM_NAVS + "\\[]*(\\[[^\\]]+\\])*)";
	private static final String ELEMENT_AND_CONDITION_FOLLOWED_BY_DOM_NAV_EXTRACTOR = "^" + ELEMENT_AND_CONDITION + "($|(\\s*(" + DOM_NAV_RE + ")\\s*" + "([^" + DOM_NAVS + "].*)$))";
	// @formatter:on

	private static final String ELEMENT_AND_CONDITION_FOLLOWED_BY_DOM_NAV_EXTRACTOR_FIXED = "^([^ ~+>\\[]*(?:\\[[^\\]]+\\])*)(?:\\s*[ ~+>]\\s*([^ ~+>\\[].*))*$";

	// origin: CssElementAttributeParser.java
	// The following appears, because of excessive grouping
	// to neither work too well nor is really needed for task at hand
	private static final String QUOTES_RE = "([\"\'])";
	private static final String ATTRIBUTE_VALUE_RE = "([-_.#a-zA-Z0-9:\\/ ]+)";
	private static final String ATTRIBUTE_VALUE_RE_NO_SPACES = "([-_.#a-zA-Z0-9:\\/]+)";
	private static final String ATTRIBUTE_TYPE_RE = createElementAttributeNameRegularExpression();
	private static final String ELEMENT_ATTRIBUTE_NAME_RE = "(-?[_a-zA-Z]+[_a-zA-Z0-9-]*)";
	// @formatter:off
	private static final String STARTING_ELEMENT_RE = "^(" + ELEMENT_ATTRIBUTE_NAME_RE + "|([*]))?";
	// @formatter:on
	private static final String PSUEDO_RE = "(:[a-z][a-z\\-]*([(][^)]+[)])?)";
	// @formatter:off
	private static final String ATTRIBUTE_RE = "(" + PSUEDO_RE + "|(\\[" + "\\s*" + ELEMENT_ATTRIBUTE_NAME_RE + "\\s*" + ATTRIBUTE_TYPE_RE + "\\s*" + "((" + QUOTES_RE + ATTRIBUTE_VALUE_RE + QUOTES_RE + ")|(" + ATTRIBUTE_VALUE_RE_NO_SPACES + "))?" + "\\s*" + "\\]))";
	// @formatter:on

	// @formatter:off
	private static final String CSS_CONDTION_EXTRACTOR = STARTING_ELEMENT_RE	+ ATTRIBUTE_RE + "*$";
	// @formatter:on

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

	// Shortened grammar expression:

// @formatter:off
	private static final String CSS_CONDTION_EXTRACTOR_FIXED = "(?i)^(-?[_a-z]+[_a-z0-9-]*|\\*)?(:[a-z][a-z\\-]*[(][^)]+[)]?|(\\[\\s*(-?[_a-z]+[_a-z0-9-]*)\\s*(\\=|\\~=|\\|=|\\^=|\\$=|\\*=)?\\s*([\"'][-_.#a-z0-9:\\/ ]+[\"']|[-_.#a-z0-9:\\/]+)?\\s*\\]))*$";
// @formatter:on

	@SuppressWarnings("unused")
	private boolean debug = false;

	public void setDebug(boolean value) {
		this.debug = value;
	}

	// NOTE: cannot reference a field "before" it is defined.
	private static final String tokenValidator = ELEMENT_AND_CONDITION_FOLLOWED_BY_DOM_NAV_EXTRACTOR_FIXED;
	private static final String attributeValidator = CSS_CONDTION_EXTRACTOR_FIXED;

	// control logging
	private boolean reportedTokenValidator = false;
	private boolean reportedAttributeValidator = false;

	public String getTokenValidator() {
		if (debug) {
			if (!reportedTokenValidator) {
				System.err.println("Token validator: " + tokenValidator);
				reportedTokenValidator = true;
			}
		}
		return tokenValidator;
	}

	public String getAttributeValidator() {
		if (debug) {
			if (!reportedAttributeValidator) {
				System.err.println("Attribute validator: " + attributeValidator);
				reportedAttributeValidator = true;
			}
		}
		return attributeValidator;
	}

}
