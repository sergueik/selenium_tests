package com.github.sergueik.selenium;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import static org.hamcrest.Matchers.hasKey;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.path.json.exception.JsonPathException;
import groovy.json.JsonException;
import com.github.sergueik.selenium.BaseTest;

// https://stackoverflow.com/questions/20197783/how-to-use-hamcrest-to-inspect-map-items
// https://www.baeldung.com/rest-assured-tutorial
// TODO: Ruby style chains https://devqa.io/parse-json-response-rest-assured/
public class RestAssuredTest {
	private static final StringBuffer verificationErrors = new StringBuffer();
	private static final boolean debug = true;
	private static String baseUrl = null;
	private static String data = "{\n" + "    \"message\": \"The given data was invalid.\",\n" + "    \"errors\": {\n"
			+ "        \"client.email\": [\n" + "            \"invalid email\"\n" + "        ]\n" + "    }\n" + "}";

	@Test(enabled = true)
	public void test1() {
		RestAssured.defaultParser = Parser.JSON;
		baseUrl = "https://jsonplaceholder.typicode.com/users";
		Response response = RestAssured.get(baseUrl);
		Map<String, String> messages = response.jsonPath().getMap("company[0]");
		assertThat(messages, hasKey("name"));
		String value = messages.get("name");
		assertThat(value, notNullValue());
	}

	@Test(enabled = true)
	public void test2() {
		RestAssured.defaultParser = Parser.JSON;
		baseUrl = "https://www.flickr.com/services/feeds/photos_public.gne?tags=soccer&format=json";
		// NOTE: wraps the response in jsonFlickrFeed(...)
		Response response = RestAssured.get(baseUrl);
		System.err.println(response.prettyPrint());
		try {
			List<Map<String, Object>> messages = response.jsonPath().getList("items");
			assertThat(messages.size(), greaterThan(0));
			assertThat(messages.get(0), hasKey("title"));
			String value = messages.get(0).get("title").toString();
			assertThat(value, notNullValue());
		} catch (JsonException | JsonPathException e) {
			System.err.println("Exception(ignored): " + e.toString());

		}
	}

	// NOTE: http://echo.jsontest.com/ does not produce complex json
	// NOTE: local file URI cannot be used for RestAssured testing
	// connection refused
	// switch to bundled JsonPath
	@Test(enabled = true)
	public void test3() {
		data = BaseTest.getScriptContent("data.json");
		JsonPath jsonPath = new JsonPath(data);
		Map<String, List<String>> messages = jsonPath.getMap("errors");
		assertThat(messages, hasKey("client.email"));
		String value = messages.get("client.email").get(0);
		assertThat(value, notNullValue());
	}

	@Test(enabled = true)
	public void test4() {
		JsonPath jsonPath = new JsonPath(data);
		Map<String, List<String>> messages = jsonPath.getMap("errors");
		assertThat(messages, hasKey("client.email"));
		String value = messages.get("client.email").get(0);
		assertThat(value, notNullValue());
	}

}