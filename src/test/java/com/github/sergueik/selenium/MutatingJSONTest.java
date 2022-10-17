package com.github.sergueik.selenium;

/**
 * Copyright 2022 Serguei Kouzmine
 */

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;

/**
 * Selected test scenarios
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class MutatingJSONTest {

	private static final StringBuffer verificationErrors = new StringBuffer();
	private static final Logger log = LogManager
			.getLogger(MutatingJSONTest.class);
	private static Gson gson = new GsonBuilder()
			.registerTypeAdapter(SessionEvent.class, new SessionEventerializer())
			.registerTypeAdapter(SessionArrayEvent.class,
					new SessionArrayEventerializer())
			.setPrettyPrinting().create();

	@SuppressWarnings("unused")
	private static Pattern pattern;
	private static Matcher matcher;

	private static final boolean debug = false;

	@BeforeClass
	public void beforeClass() throws IOException {
	}

	@BeforeMethod
	public void loadPage() {
	}

	@Test(enabled = true)
	public void test1() {
		String result = "[{\"id\":\"1\",\"event\": \"test\"}]";
		SessionEvent[] data = gson.fromJson(result, SessionEvent[].class);
		System.err.println("test1:");
		Arrays.asList(data).stream().filter(o -> !o.event.equals("init")).limit(10)
				.map(o -> gson.toJson(o)).forEach(System.err::println);
	}

	@Test(enabled = true, expectedExceptions = JsonSyntaxException.class)
	public void test2() {
		String result = "[{\"id\":\"1\",\"event\": [\"test1\",\"test2\"]}]";
		SessionEvent[] data = gson.fromJson(result, SessionEvent[].class);
		System.err.println("test2:");
		Arrays.asList(data).stream().filter(o -> !o.event.equals("init")).limit(10)
				.map(o -> gson.toJson(o)).forEach(System.err::println);
	}

	@Test(enabled = true)
	public void test3() {
		String result = "[{\"id\":\"1\",\"event\": [\"test1\",\"test2\"]}]";

		SessionArrayEvent[] data = gson.fromJson(result, SessionArrayEvent[].class);
		System.err.println("test3:");
		Arrays.asList(data).stream().limit(10).map(o -> gson.toJson(o))
				.forEach(System.err::println);
	}

	// in Golang this is implemented via type switches -
	// language construct that permits several type assertions chained
	// https://go.dev/tour/methods/16
	@Test(enabled = true)
	public void test4() {
		String result = "[{\"id\":\"1\",\"event\": [\"test1\",\"test2\"]}]";
		Object data;

		try {
			System.err.println("test4: try read SessionEvent");
			data = gson.fromJson(result, SessionEvent[].class);
			// data will not fail
			if (((SessionEvent[]) data)[0].getEvent() == null) {
				System.err.println("test4: not the SessionEvent");
				throw new JsonSyntaxException(""); // null is ambiguos
			}
		} catch (JsonSyntaxException e) {
			System.err.println("test4: try read SessionArrayEvent");
			data = gson.fromJson(result, SessionArrayEvent[].class);
			if (((SessionArrayEvent[]) data)[0].getEvent() == null) {
				System.err.println("test4: not the SessionEvent");
				throw new JsonSyntaxException(""); // null is ambiguos
			}
		}
		System.err.println("test4:");
		Arrays.asList(data).stream().limit(10).map(o -> gson.toJson(o))
				.forEach(System.err::println);
	}

	// in Golang this is implemented via type switches -
	// language construct that permits several type assertions chained
	// https://go.dev/tour/methods/16
	@Test(enabled = true)
	public void test5() {
		for (String result : new String[] {
				"[{\"id\":\"1\",\"event\": [\"test1\",\"test2\"]}]",
				"[{\"id\":\"1\",\"event\": \"test\"}]" }) {
			Object untypedData = null;

			try {
				System.err.println("test5: try read SessionEvent");
				SessionEvent[] typedData = gson.fromJson(result, SessionEvent[].class);
				untypedData = typedData;
			} catch (JsonSyntaxException e) {

			}
			try {
				System.err.println("test5: try read SessionArrayEvent");
				SessionArrayEvent[] typedData = gson.fromJson(result,
						SessionArrayEvent[].class);
				untypedData = typedData;
			} catch (JsonSyntaxException e) {

			}
			System.err.println("test5:");
			Arrays.asList(untypedData).stream().limit(10).map(o -> gson.toJson(o))
					.forEach(System.err::println);
		}
	}

	private static class SessionEventerializer
			implements JsonSerializer<SessionEvent> {
		@Override
		public JsonElement serialize(final SessionEvent data, final Type type,
				final JsonSerializationContext context) {
			JsonObject result = new JsonObject();
			String id = data.getId();
			if (id != null && !id.isEmpty()) {
				result.add("id", new JsonPrimitive(id));
			}
			// added static info from the serialized class
			// result.add("staticInfo", new
			// JsonPrimitive(SessionEvent.getStaticInfo()));

			// filter what to (not) serialize
			String event = data.getEvent();
			if (event != null && !event.isEmpty()) {
				result.add("event", new JsonPrimitive(event));
			}
			return result;
		}
	}

	@SuppressWarnings("unused")
	private static class SessionEvent {
		private String id;
		private String event;

		public String getId() {
			return id;
		}

		public String getEvent() {
			return event;
		}

		public void setEvent(String data) {
			event = data;
		}

		public SessionEvent() {
			id = UUID.randomUUID().toString();
		}
	}

	// https://javadoc.io/doc/com.google.code.gson/gson/2.6.2/com/google/gson/JsonPrimitive.html
	// https://javadoc.io/doc/com.google.code.gson/gson/2.6.2/com/google/gson/JsonArray.html
	private static class SessionArrayEventerializer
			implements JsonSerializer<SessionArrayEvent> {
		@Override
		public JsonElement serialize(final SessionArrayEvent data, final Type type,
				final JsonSerializationContext context) {
			JsonObject result = new JsonObject();
			String id = data.getId();
			if (id != null && !id.isEmpty()) {
				result.add("id", new JsonPrimitive(id));
			}

			// NOTE: can serialize static info from the serialized class
			// result.add("staticInfo", new
			// JsonPrimitive(SessionEvent.getStaticInfo()));

			// filter what to (not) serialize
			List<String> events = data.getEvent();
			System.err.println("events: " + events);
			if (events != null && !events.isEmpty()) {
				JsonArray jsonArray = new JsonArray();
				result.add("event", jsonArray);
				for (String event : events) {
					jsonArray.add(new JsonPrimitive(event));
				}
			}
			return result;
		}
	}

	@SuppressWarnings("unused")
	private static class SessionArrayEvent {
		private String id;
		private List<String> event;

		public String getId() {
			return id;
		}

		public List<String> getEvent() {
			return event;
		}

		public void setEvent(List<String> data) {
			event = data;
		}

		public SessionArrayEvent() {
			id = UUID.randomUUID().toString();
		}
	}
}

