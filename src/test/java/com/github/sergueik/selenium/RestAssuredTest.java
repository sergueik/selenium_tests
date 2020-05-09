package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.equalTo;

import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import groovy.json.JsonException;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.path.json.JsonPath;
import io.restassured.path.json.exception.JsonPathException;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.response.ValidatableResponseOptions;

import static io.restassured.RestAssured.given;

// see also:
// https://www.baeldung.com/rest-assured-tutorial
// https://devqa.io/parse-json-response-rest-assured/
public class RestAssuredTest {
	private static final boolean debug = true;
	private static String baseUrl = null;
	//@formatter:off
	private static String data = "{\n"
			+ "    \"message\": \"The given data was invalid.\",\n" 
			+ "    \"errors\": {\n"
			+ "        \"email\": [\n" 
			+ "            \"invalid email\"\n" 
			+ "        ]\n" 
			+ "    }\n" 
			+ "}";

	//@formatter:on
	private static Response response;

	@Test(enabled = true)
	public void test1() {
		baseUrl = "https://jsonplaceholder.typicode.com/users";
		response = RestAssured.get(baseUrl);
		Map<String, String> messages = response.jsonPath().getMap("company[0]");
		assertThat(messages, hasKey("name"));
		String value = messages.get("name");
		assertThat(value, notNullValue());
	}

	// strongly-typed object serialization-style
	// NOTE: wraps the response in jsonFlickrFeed(...)
	@Test(enabled = false)
	public void test2() {
		baseUrl = "https://www.flickr.com/services/feeds/photos_public.gne?tags=soccer&format=json";
		response = RestAssured.get(baseUrl);
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

	// strongly-typed object serialization-style
	// NOTE: local file URI cannot be used for RestAssured testing
	// - will throw connection refused
	// switch to bundled JsonPath
	@Test(enabled = true)
	public void test3() {
		data = BaseTest.getScriptContent("data.json");
		JsonPath jsonPath = new JsonPath(data);
		Map<String, List<String>> messages = jsonPath.getMap("errors");
		assertThat(messages, hasKey("email"));
		String value = messages.get("email").get(0);
		assertThat(value, notNullValue());
	}

	@Test(enabled = true)
	public void test4() {
		JsonPath jsonPath = new JsonPath(data);
		Map<String, List<String>> messages = jsonPath.getMap("errors");
		assertThat(messages, hasKey("email"));
		String value = messages.get("email").get(0);
		assertThat(value, notNullValue());
		assertThat(jsonPath.get("errors.email"), notNullValue());
		// System.err.println("Result: " + jsonPath.get("errors.email"));
		assertThat(jsonPath.get("errors.email[0]"), notNullValue());
		// System.err.println("Result: " + jsonPath.get("errors.email[0]"));
	}

	// combination of strongly typed and method-heavy
	@Test(enabled = true)
	public void test5() {
		baseUrl = "https://jsonplaceholder.typicode.com/users";
		RestAssured.defaultParser = Parser.JSON;
		Object value = given()
				.headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
				.when().get(baseUrl).then().contentType(ContentType.JSON).extract()
				.response().jsonPath().getMap("company[0]").get("name");
		assertThat(value, notNullValue());
	}

	// Ruby TDD-style or Javascript Jasmine-style method call-heavy
	// ValidatableResponse chains
	@Test(enabled = true)
	public void test6() {
		baseUrl = "https://jsonplaceholder.typicode.com/users";
		RestAssured.defaultParser = Parser.JSON;
		given()
				.headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
				.when().get(baseUrl).then().contentType(ContentType.JSON)
				.statusCode(200).assertThat().body("id[0]", equalTo(1));
		given().when().get(baseUrl).then().assertThat().body("company[6].name",
				equalTo("Johns Group"));
	}

	// vararg-heavy
	@Test(enabled = true)
	public void test7() {
		baseUrl = "https://jsonplaceholder.typicode.com/users";
		RestAssured.defaultParser = Parser.JSON;
		given()
				.headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
				.when().get(baseUrl).then().assertThat().body("username[0]",
						equalTo("Bret"), "address[0].city", equalTo("Gwenborough"),
						"address[0].country", nullValue());
	}

	// NOTE: http://echo.jsontest.com/ does not produce complex json
	// and throttling override-prone leading to
	// This application is temporarily over its serving quota.
	// Please try again later." response
}
