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
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
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
 * https://github.com/mismith/session-replay
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class SessionReplayTest extends BaseTest {

	private static final StringBuffer verificationErrors = new StringBuffer();
	private static final Logger log = LogManager
			.getLogger(SessionReplayTest.class);
	private static final String baseURL = "https://www.wikipedia.org/";
	private static Map<String, Object> params = new HashMap<>();
	private static Map<String, Object> data = new HashMap<>();
	private static String result = null;

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
		driver.navigate().to("about:blank");

	}

	@Test(enabled = true)
	public void test1() {
		driver.navigate().to(baseURL);
		executeScript(getScriptContent("session-replay-embed.js"), new Object[] {});

		// https://developer.mozilla.org/en-US/docs/Web/API/Document/currentScript
		// result = (String) executeScript("return document.currentScript.src;");
		// System.err.println("Result: " + result);

		WebElement element = driver
				.findElement(By.cssSelector("#js-link-box-en > strong"));
		sleep(1000);
		actions.moveToElement(element).contextClick().build().perform();
		sleep(1000);
		element = driver.findElement(By.id("session-replay_events"));
		assertThat(element, notNullValue());
		result = element.getAttribute("innerHTML");
		System.err.println("Raw result: " + result.substring(0, 200) + "...");
	}

	@Test(enabled = true)
	public void test2() {
		driver.navigate().to(baseURL);
		executeScript(getScriptContent("session-replay-embed.js"), new Object[] {});
		WebElement element = driver
				.findElement(By.cssSelector("#js-link-box-en > strong"));
		actions.moveToElement(element).contextClick().build().perform();
		sleep(1000);
		result = (String) executeScript(
				"return document.getElementById('session-replay_events').innerHTML;");
		System.err.println("Raw result: " + result.substring(0, 800) + "...");
	}

	@Test(enabled = true)
	public void test3() {
		driver.navigate().to(baseURL);
		params = new HashMap<>();
		data = new HashMap<>();
		data.put("name", "value");
		params.put("skip_dom", true);
		params.put("setting", data);

		executeScript(getScriptContent("session-replay-embed.js"), params);
		WebElement element = driver
				.findElement(By.cssSelector("#js-link-box-en > strong"));
		actions.moveToElement(element).contextClick().build().perform();
		sleep(1000);
		// NOTE:attempt to carry assertion on their end is risky of not getting any
		// data at all
		// org.openqa.selenium.JavascriptException:
		// javascript error: Unexpected number in JSON at position 208544
		result = (String) executeScript(
				"return JSON.stringify(JSON.parse(document.getElementById('session-replay_events').innerHTML));");

		Gson gson = new GsonBuilder()
				.registerTypeAdapter(SessionEvent.class, new SessionEventSerializer())
				.setPrettyPrinting().create();
		SessionEvent[] data = gson.fromJson(result, SessionEvent[].class);
		Arrays.asList(data).stream().filter(o -> !o.event.equals("init")).limit(10)
				.map(o -> gson.toJson(o)).forEach(System.err::println);

	}

	private static class SessionEventSerializer
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
			String timestamp = data.getTimestamp();
			if (timestamp != null && !timestamp.isEmpty()) {
				result.add("timestamp", new JsonPrimitive(timestamp));
			}
			String targetId = data.getTargetId();
			if (targetId != null && !targetId.isEmpty()) {
				result.add("targetId", new JsonPrimitive(targetId));
			}
			int keyCode = data.getKeyCode();
			result.add("keyCode", new JsonPrimitive(keyCode));
			int scrollTop = data.getScrollTop();
			result.add("scrollTop", new JsonPrimitive(scrollTop));

			Float x = data.getX();
			result.add("x", new JsonPrimitive(x));
			Float y = data.getY();
			result.add("y", new JsonPrimitive(y));

			return result;
		}
	}

	@SuppressWarnings("unused")
	private static class SessionEvent {
		private String id;
		private String event;
		private String timestamp;
		private String targetId;
		private int keyCode;
		private int scrollTop;
		// TODO: make optional
		private float y;
		private float x;

		public String getId() {
			return id;
		}

		public String getEvent() {
			return event;
		}

		public void setEvent(String data) {
			event = data;
		}

		public String getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(String data) {
			timestamp = data;
		}

		public String getTargetId() {
			return targetId;
		}

		public void setTargetId(String data) {
			targetId = data;
		}

		public int getKeyCode() {
			return keyCode;
		}

		public void setKeyCode(int data) {
			keyCode = data;
		}

		public int getScrollTop() {
			return scrollTop;
		}

		public void setScrollTop(int data) {
			scrollTop = data;
		}

		public float getX() {
			return x;
		}

		public void setX(float data) {
			x = data;
		}

		public float getY() {
			return y;
		}

		public void setY(float data) {
			y = data;
		}

		public SessionEvent() {
			id = UUID.randomUUID().toString();
		}

	}

}

