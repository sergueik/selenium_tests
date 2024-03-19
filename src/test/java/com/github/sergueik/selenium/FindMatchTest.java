package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.util.ArrayList;
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
	private Map<String, String> results;
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
			System.err.println("backgroundColorAttribute:" + backgroundColorAttribute);

			pattern = Pattern.compile(patternExression);
			matcher = pattern.matcher(backgroundColorAttribute);
			if (matcher.find()) {
				red = Integer.parseInt(matcher.group("red").toString());
				green = Integer.parseInt(matcher.group("green").toString());
				blue = Integer.parseInt(matcher.group("blue").toString());
				assertThat(green, greaterThan(128));
				System.err.println("green:" + green);
			}

		}
	}

	@Test(enabled = true)
	public void test2() {

		results = findMatch(backgroundColorAttribute, patternExression,
				Arrays.asList(new String[] { "red", "green", "blue" }));
		assertThat(results, notNullValue());
		assertThat(results.keySet().size(), is(3));
		for (String name : results.keySet()) {
			int result = Integer.parseInt(results.get(name).toString());
			System.err.println(String.format("%s: %d", name, result));
		}
	}

	@Test(enabled = true)
	public void test3() {

		results = findMatch(backgroundColorAttribute, patternExression);
		assertThat(results, notNullValue());
		assertThat(results.keySet().size(), is(3));
		for (String name : results.keySet()) {
			int result = Integer.parseInt(results.get(name).toString());
			System.err.println(String.format("%s: %d", name, result));
		}
	}

	@Test(enabled = true)
	public void test4() {
		List<String> groups = resolveGroups(patternExression);
		assertThat(groups, notNullValue());
		assertThat(groups.size(), is(3));

	}

	private Map<String, String> findMatch(String data, String patternExression) {
		return findMatch(data, patternExression, resolveGroups(patternExression));
	}

	// NOTE: capturing just one of each groups
	private Map<String, String> findMatch(String data, String patternExression, List<String> groups) {
		Map<String, String> matches = new HashMap<>();
		pattern = Pattern.compile(patternExression);
		matcher = pattern.matcher(data);
		if (matcher.find()) {
			System.err.println("data:" + data);
			for (String name : groups) {
				String value = matcher.group(name).toString();
				matches.put(name, value);
			}
		}
		return matches;
	}

	public List<String> resolveGroups(String patternExression) {
		List<String> groups = new ArrayList<>();
		if (null == patternExression) {
			return null;
		}
		Pattern p = Pattern.compile(tagMatcher);
		Matcher m = p.matcher(patternExression);
		while (m.find()) {
			String name = m.group("result");
			groups.add(name);
		}
		System.err.println("data:" + groups.toString());
		return groups;
	}

}
