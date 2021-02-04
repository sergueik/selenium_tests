package com.github.sergueik.selenium;
/**
 * Copyright 2021 Serguei Kouzmine
 */

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import java.lang.reflect.Type;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Ignore;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

// import static com.google.gson.Gson.DEFAULT_PRETTY_PRINT;

/**
 * Selected test scenarios for Selenium WebDriver based on
 * https://github.com/sunnylost/navigation-timing
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class PageTimingTest extends BaseTest {

	private static final StringBuffer verificationErrors = new StringBuffer();
	private static final Logger log = LogManager.getLogger(PageTimingTest.class);
	private static final String baseURL = "https://www.uat.edu/";

	@SuppressWarnings("unused")
	private static Pattern pattern;
	private static Matcher matcher;

	private static final boolean debug = false;
	private static final boolean remote = Boolean
			.parseBoolean(getPropertyEnv("REMOTE", "false"));

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
	}

	@BeforeMethod
	public void loadPage() {

	}
	@Ignore
	@Test
	public void test1() {
		driver.navigate().to(baseURL);
		Map<String, String> param = new HashMap<>();
		param.put("ladder", "true");
		String result = (String) executeScript(
				getScriptContent("compute-timing.js"), param);
		System.err.println(result);
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(Phase.class, new PhaseSerializer())
				.setPrettyPrinting().create();
		Phase[] data = gson.fromJson(result, Phase[].class);
		Arrays.asList(data).stream().map(o -> gson.toJson(o))
				.forEach(System.err::println);

	}

	@Ignore
	@Test
	public void test2() {
		driver.navigate().to(baseURL);
		Map<String, String> param = new HashMap<>();
		param.put("ladder", "true");
		String result = (String) executeScript(
				getScriptContent("compute-timing.js"));
		System.err.println(result);
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(Phase.class, new PhaseSerializer())
				.setPrettyPrinting().create();
		Phase[] data = gson.fromJson(result, Phase[].class);
		Arrays.asList(data).stream().map(o -> gson.toJson(o))
				.forEach(System.err::println);

	}

	private static class PhaseSerializer implements JsonSerializer<Phase> {
		@Override
		public JsonElement serialize(final Phase data, final Type type,
				final JsonSerializationContext context) {
			JsonObject result = new JsonObject();
			String id = data.getId();
			if (id != null && !id.isEmpty()) {
				result.add("id", new JsonPrimitive(id));
			}
			// added static info from the serialized class
			// result.add("staticInfo", new JsonPrimitive(Phase.getStaticInfo()));

			// filter what to (not) serialize
			String name = data.getName();
			if (name != null && !name.isEmpty()) {
				result.add("name", new JsonPrimitive(name));
			}
			String start = data.getStart();
			if (start != null && !start.isEmpty()) {
				result.add("start", new JsonPrimitive(start));
			}
			String end = data.getEnd();
			if (end != null && !end.isEmpty()) {
				result.add("end", new JsonPrimitive(end));
			}
			int index = data.getIndex();
			result.add("index", new JsonPrimitive(index));
			int value = data.getValue();
			result.add("value", new JsonPrimitive(value));

			Float width = data.getWidth();
			result.add("width", new JsonPrimitive(width));
			Float left = data.getLeft();
			result.add("left", new JsonPrimitive(left));

			return result;
		}
	}

	@SuppressWarnings("unused")
	private static class Phase {
		private String id;
		private String name;
		private String start;
		private String end;
		private int index;
		private int value;
		// TODO: make optional
		private float left;
		private float width;

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public void setName(String data) {
			name = data;
		}

		public String getStart() {
			return start;
		}

		public void setStart(String data) {
			start = data;
		}

		public String getEnd() {
			return end;
		}

		public void setEnd(String data) {
			end = data;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int data) {
			index = data;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int data) {
			value = data;
		}

		public float getWidth() {
			return width;
		}

		public void setWidth(float data) {
			width = data;
		}

		public float getLeft() {
			return left;
		}

		public void setLeft(float data) {
			left = data;
		}

		public Phase() {
			id = UUID.randomUUID().toString();
		}

	}

}
