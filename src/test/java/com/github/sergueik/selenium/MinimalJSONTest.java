package com.github.sergueik.selenium;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonObject.Member;
import com.eclipsesource.json.JsonValue;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.testng.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;

// https://github.com/ralfstx/minimal-json/blob/master/com.eclipsesource.json/src/test/java/com/eclipsesource/json/JsonObject_Test.java
public class MinimalJSONTest {
	private static final StringBuffer verificationErrors = new StringBuffer();
	private static final boolean debug = true;
	private static final String fileName = "params.json";
	private static final File file = new File(
			String.join(System.getProperty("file.separator"),
					Arrays.asList(System.getProperty("user.dir"), "src", "test",
							"resources", fileName)));
	private static final StringBuffer contents = new StringBuffer();
	private static BufferedReader reader = null;
	private static String text = null;
	private static String data = null;

	@BeforeMethod
	public void loadPage() throws IOException {
		reader = new BufferedReader(new FileReader(file));
		contents.setLength(0);
		while ((text = reader.readLine()) != null) {
			contents.append(text).append(System.getProperty("line.separator"));
		}
		data = contents.toString();
	}

	@Test(enabled = true)
	public void loadParamsTest() {
		System.err.println("Read id: "
				+ readSideData(data, Optional.<Map<String, JsonValue>> empty(),
						"(?:id|name|success|result)"));
	}

	// https://eclipsesource.com/blogs/2013/04/18/minimal-json-parser-for-java/
	@Test(enabled = true)
	public void loadJsonObjectTest() {

		JsonObject jsonObject = JsonObject.readFrom(data);
		assertThat(jsonObject, notNullValue());
		assertThat(jsonObject.isEmpty(), is(false));
		Iterator<Member> jsonObjectIterator = jsonObject.iterator();

		while (jsonObjectIterator.hasNext()) {
			Member jsonObjectMember = jsonObjectIterator.next();
			System.err.println("Found member: " + jsonObjectMember.getName());
		}

		Map<String, Object> params = new HashMap<>();

		params.put("id", jsonObject.get("id").asInt());
		params.put("name", jsonObject.get("name").asString());
		params.put("status", jsonObject.get("status").asBoolean());
		params.put("result", jsonObject.get("result").asInt());
		System.err.println("Loaded params: " + params);
	}

	@AfterMethod
	public void afterMethod(ITestResult result) throws IOException {
		reader.close();
		if (verificationErrors.length() != 0) {
			throw new RuntimeException(String.format("Error(s) in the method %s : %s",
					result.getMethod().getMethodName(), verificationErrors.toString()));
		}
	}

	private String readSideData(String payload,
			Optional<Map<String, JsonValue>> parameters, String acceptedKeys) {
		if (debug) {
			System.err.println("Accepted keys: " + acceptedKeys);
		}
		Map<String, JsonValue> collector = (parameters.isPresent())
				? parameters.get() : new HashMap<>();

		String data = (payload == null)
				? "{\"foo\":\"bar\", \"result\":true,\"id\":42 }" : payload;

		if (debug) {
			// System.err.println("Processing payload: " + data.replaceAll(",",
			// ",\n"));
			System.err.println("Processing payload: [ " + data + "]");
		}

		JsonObject jsonObject = JsonObject.readFrom(data);
		assertThat(jsonObject, notNullValue());
		assertThat(jsonObject.isEmpty(), is(false));
		Iterator<Member> jsonObjectIterator = jsonObject.iterator();

		while (jsonObjectIterator.hasNext()) {
			Member jsonObjectMember = jsonObjectIterator.next();
			System.err.println("Found member: " + jsonObjectMember.getName());
			String propertyKey = jsonObjectMember.getName();
			if (!propertyKey.matches(acceptedKeys)) {
				System.err.println("Ignoring key: " + propertyKey);
				continue;
			}
			if (debug) {
				System.err.println("Processing key: " + propertyKey);
			}
			Boolean found = false;
			try {
				JsonValue propertyVal = jsonObject.get(propertyKey);
				if (debug) {
					System.err
							.println("Loaded string: " + propertyKey + ": " + propertyVal);
				}
				collector.put(propertyKey, propertyVal);
				found = true;
			} catch (Exception e) {
				System.err.println("Exception (ignored, continue): " + e.toString());
			}
		}
		return Integer.toString(collector.get("id").asInt());
	}
}