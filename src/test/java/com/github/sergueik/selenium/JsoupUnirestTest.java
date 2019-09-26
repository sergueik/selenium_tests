package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.testng.annotations.Test;

import java.util.Map;

import com.mashape.unirest.http.HttpResponse;

import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

// for JSON  responses
import org.json.JSONException;
import org.json.JSONObject;

// alternatively for JSON  responses
import com.google.gson.Gson;

/**
 *
 * unirest example based on https://www.udemy.com/course/learn-web-scraping-with-java-in-just-1-hour/
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class JsoupUnirestTest {
	private static boolean debug = false;

	final private static String baseUrl = "https://www.peoplefinders.com/people/bill-gates/wa/redmond";
	// the AJAX call no longer uses that apiURL
	// now it is using autocomplete-cities, autocomplete-names etc.

	private static final String apiURL = "https://www.peoplefinders.com/api/widget/widgets?firstName=bill&lastName=gates&city=redmond&state=wa";
	// TODO: demonstrate the AJAX API
	// "https://https://www.peoplefinders.com/GetResults?";

	private static final String lastName = "gates";
	private static final String firstName = "bill";

	// process response as text
	@Test(enabled = true)
	public void uniRestTest1() throws UnirestException {
		final HttpResponse<String> response = Unirest.get(apiURL).asString();
		System.err.println(response.getBody().substring(0, 100));
	}

	// response is JSON
	@Test(enabled = true)
	public void uniRestTest2() throws UnirestException, JSONException {
		HttpResponse<JsonNode> response = Unirest.get(apiURL)
				.header("Referer", baseUrl).asJson();
		assertThat(response.getStatus(), is(200));
		System.err.println("Status:" + response.getStatus());

		if (debug) {
			System.err.println(response.getBody());
		}
		final JsonNode responseJsonNode = response.getBody();
		JSONObject responseJSONObject = responseJsonNode.getObject();
		try {
			System.err.println("Success:" + responseJSONObject.getBoolean("success"));
			// TODO: assertThat(objResponse, hasKey("success"));
		} catch (JSONException e) {
			// ignore
			System.err.println("Exception (ignored)" + e.toString());
		}

		// alternatively
		responseJSONObject = new JSONObject(response.getBody().toString());
		try {
			System.err.println("Success:" + responseJSONObject.getBoolean("success"));
			// TODO: assertThat(objResponse, hasKey("success"));
		} catch (JSONException e) {
			// ignore
			System.err.println("Exception (ignored)" + e.toString());
		}

		// alternatively
		Gson gson = new Gson();
		@SuppressWarnings("unchecked")
		Map<String, Object> obj2Response = gson
				.fromJson(response.getBody().toString(), Map.class);
		try {

			System.err.println("Success:" + obj2Response.get("success"));
		} catch (Exception e) {
			System.err.println("Exception (ignored)" + e.toString());
		}
	}

	// response is JSON with the error
	@Test(enabled = true)
	public void uniRestTest3() throws UnirestException {

		final HttpResponse<JsonNode> responseJsonNode = Unirest
				.get(apiURL.replaceAll("&lastName=(?:[^&]+)", "")).asJson();
		System.err.println(responseJsonNode.getBody().toString());
		//
		// Type type1 = new TypeToken<ObjectItem>() { }.getType();
		// TODO: extract the
		// {"SlotId":146,"SiteId":1,"IsSkipApi":false,"SlotNumber":2,"RequestedUrl":"http://partners.truthfinder.com/v1/teaser?first_name=bill&last_name=&city=redmond&state=wa&age=0&user_ip=192.168.8.6&sid=SB_MultiR&limit=5&output_format=json&access_key=BQzd5z7ETK&a=156&c=372&oc=27&dip=&page=s&s1=single","IsError":true,"PartnerId":5,"BannerTypeName":"BannerTypeTeaser","PartnerName":"TruthFinder.com","SlotUniqueGuid":"a5074822-0a32-472c-83b9-175d80389250","ErrorMessage":"Error
		// in GetResponse
		// http://partners.truthfinder.com/v1/teaser?first_name=bill&last_name=&city=redmond&state=wa&age=0&user_ip=192.168.8.6&sid=SB_MultiR&limit=5&output_format=json&access_key=BQzd5z7ETK&a=156&c=372&oc=27&dip=&page=s&s1=single:
		// {\"message\":\"Bad Request (400): URL parameter 'last_name' is required
		// for this client\"}"}
		final HttpResponse<String> response = Unirest.get(apiURL)
				.queryString("lastName", lastName).asString();
		System.err.println(response.getBody().substring(0, 100));

	}

}
