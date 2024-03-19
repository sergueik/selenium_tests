package com.github.sergueik.selenium;

import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.annotations.Test;

public class FindMatchTest {
	private final String backgroundColorAttribute = "(100,150,250)";

	private final String patternExression = "\\(\\s*(?<red>\\d+),\\s*(?<green>\\d+),\\s*(?<blue>\\d+)\\)";
	private Pattern pattern;
	private Matcher matcher;
	int red = 0, green = 0, blue = 0;

	private final String tagMatcher = "(?:<(?<result>[^>]+)>)";

	// origin:
	// see also:
	// https://stackoverflow.com/questions/415580/regex-named-groups-in-java
	@Test(enabled = true)
	public void test1() {

		pattern = Pattern.compile("\\(\\s*(\\d+),\\s*(\\d+),\\s*(\\d+)\\)");
		matcher = pattern.matcher(backgroundColorAttribute);
		if (matcher.find()) {
			System.err
					.println("backgroundColorAttribute:" + backgroundColorAttribute);

			pattern = Pattern.compile(patternExression);
			matcher = pattern.matcher(backgroundColorAttribute);
			if (matcher.find()) {
				red = Integer.parseInt(matcher.group("red").toString());
				green = Integer.parseInt(matcher.group("green").toString());
				blue = Integer.parseInt(matcher.group("blue").toString());
				assertTrue(green >= 128);
				System.err.println("green:" + green);
			}

		}
	}

	@Test(enabled = true)
	public void test2() {

		pattern = Pattern.compile("\\(\\s*(\\d+),\\s*(\\d+),\\s*(\\d+)\\)");
		Map<String, String> results = findMatch(backgroundColorAttribute,
				patternExression,
				Arrays.asList(new String[] { "red", "green", "blue" }));
		for (String name : results.keySet()) {
			int result = Integer.parseInt(results.get(name).toString());
			System.err.println(String.format("%s: %d", name, result));
		}
	}

	private Map<String, String> findMatch(String data, String patternExression,
			List<String> groups) {
		Map<String, String> matches = new HashMap<>();
		pattern = Pattern.compile(patternExression);
		matcher = pattern.matcher(data);
		if (matcher.find()) {
			System.err.println("data:" + data);
			for (String name : groups) {
				// String value = Integer.parseInt(matcher.group(name).toString());
				String value = matcher.group(name).toString();
				matches.put(name, value);
			}
		}
		return matches;
	}
	// String generated_tag = matchPattern.FindMatch("(?:<(?<result>[^>]+)>)",
	// "result");
}

