package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

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
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 *
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class JsoupUnirestTest {
	final private static String baseUrl = "https://www.peoplefinders.com/people/bill-gates/wa/redmond";
	// the AJAX call no longer uses that apiURL
	// now it is using autocomplete-cities, autocomplete-names etc.

	// final private static String apiURL =
	// "https://https://www.peoplefinders.com/GetResults?";
	final String lastName = "gates";
	private static String apiURL = null;

	// response is JSON
	@Test(enabled = true)
	public void uniRestTest2() throws UnirestException, JSONException {
		apiURL = "https://www.peoplefinders.com/api/widget/widgets?firstName=bill&city=redmond&state=wa";
		HttpResponse<JsonNode> response = Unirest.get(apiURL).asJson();
		System.err.println("Status:" + response.getStatus());
		assertThat(response.getStatus(), is(200));
		System.err.println(response.getBody());
		JSONObject objResponse = new JSONObject(response.getBody().toString());
		// effectively confirming that response JSON is loadable
		try {

			System.err.println("Success:" + objResponse.getBoolean("success"));
			// TODO: assertThat(objResponse, hasKey("success"));
		} catch (JSONException e) {
			// ignore
			System.err.println("Exception (ignored)" + e.toString());
		}
		Gson gson = new Gson();
		Map<String, Object> obj2Response = gson.fromJson(response.getBody().toString(), Map.class);
		try {

			System.err.println("Success:" + obj2Response.get("success"));
		} catch (Exception e) {
			System.err.println("Exception (ignored)" + e.toString());
		}

		// Type type1 = new TypeToken<ObjectItem>() { }.getType();
		// TODO: extract the {\"message\":\"Bad Request (400): URL parameter
		// 'last_name' is required for this client\"}"}
		/*
		 * { "partnerResponses": [ { "SlotId": 81, "SiteId": 1, "IsSkipApi":
		 * false, "SlotNumber": 1, "RequestedUrl":
		 * "http://teaser.api.beenverified.com/?fn=bill&ln=&city=redmond&state=wa&age=0&exporttype=jsonv&rc=5&strict=true&api=6JdWj2RPjFLVwwWvdXg4cD7U&utm_source=peoplefinders&utm_medium=affiliate&utm_campaign=PF_Single_Results&utm_content=api",
		 * "IsError": false, "PartnerId": 3, "BannerTypeName":
		 * "BannerTypeTeaser", "Json":
		 * "{\"response\":{\"Header\":{\"Status\":\"0\",\"TransactionId\":\"51066731R1001939\"},\"RecordCount\":\"0\",\"Records\":[]},\"removedrecords\":\"0\",\"removedrelatives\":\"0\",\"link\":\"https:\\/\\/www.beenverified.com\\/?fn=bill&amp;ln=&amp;city=redmond&amp;state=wa&amp;bvid=&amp;bvcid=&amp;utm_source=peoplefinders&amp;utm_medium=affiliate&amp;utm_campaign=PF_Single_Results&amp;utm_content=api\",\"exact\":\"0\",\"time\":\"1.0979228019714\",\"dataTime\":\"1.0916969776154\"}",
		 * "PartnerName": "BeenVerified.com", "SlotUniqueGuid":
		 * "a687656b-58a2-4db0-8650-741685703d0b" }, { "SlotId": 146, "SiteId":
		 * 1, "IsSkipApi": false, "SlotNumber": 2, "RequestedUrl":
		 * "http://partners.truthfinder.com/v1/teaser?first_name=bill&last_name=&city=redmond&state=wa&age=0&user_ip=192.168.8.6&sid=SB_MultiR&limit=5&output_format=json&access_key=BQzd5z7ETK&a=156&c=372&oc=27&dip=&page=s&s1=single",
		 * "IsError": true, "PartnerId": 5, "BannerTypeName":
		 * "BannerTypeTeaser", "PartnerName": "TruthFinder.com",
		 * "SlotUniqueGuid": "a5074822-0a32-472c-83b9-175d80389250",
		 * "ErrorMessage":
		 * "Error in GetResponse http://partners.truthfinder.com/v1/teaser?first_name=bill&last_name=&city=redmond&state=wa&age=0&user_ip=192.168.8.6&sid=SB_MultiR&limit=5&output_format=json&access_key=BQzd5z7ETK&a=156&c=372&oc=27&dip=&page=s&s1=single: {\"message\":\"Bad Request (400): URL parameter 'last_name' is required for this client\"}"
		 * }, { "SlotId": 85, "SiteId": 1, "IsSkipApi": false, "SlotNumber": 3,
		 * "RequestedUrl":
		 * "http://teaser.api.beenverified.com/?fn=bill&ln=&city=redmond&state=wa&age=0&exporttype=jsonv&rc=5&strict=true&api=NIyZP1PwYEqCzOpczTFT0dHB&utm_source=peoplefinders&utm_medium=affiliate&utm_campaign=PeopleLooker_PF_Single_Results&utm_content=api",
		 * "IsError": false, "PartnerId": 14, "BannerTypeName":
		 * "BannerTypeTeaser", "Json":
		 * "{\"response\":{\"Header\":{\"Status\":\"0\",\"TransactionId\":\"51066761R682566\"},\"RecordCount\":\"0\",\"Records\":[]},\"removedrecords\":\"0\",\"removedrelatives\":\"0\",\"link\":\"https:\\/\\/www.peoplelooker.com\\/?fn=bill&amp;ln=&amp;city=redmond&amp;state=wa&amp;bvid=&amp;bvcid=&amp;utm_source=peoplefinders&amp;utm_medium=affiliate&amp;utm_campaign=PeopleLooker_PF_Single_Results&amp;utm_content=api\",\"exact\":\"0\",\"time\":\"1.0634160041809\",\"dataTime\":\"1.0578570365906\"}",
		 * "PartnerName": "PeopleLooker.com", "SlotUniqueGuid":
		 * "635e046d-efe0-4e92-8611-f3399d90aefa" }, { "SlotId": 87, "SiteId":
		 * 1, "IsSkipApi": true, "SlotNumber": 4, "RequestedUrl": "", "IsError":
		 * false, "PartnerId": 18, "BannerTypeName": "BannerTypeTeaser",
		 * "PartnerName": "Persopo.com", "SlotUniqueGuid":
		 * "e7b7d8c3-79e7-4401-89ab-d54444783903" } ], "success": true,
		 * "requestId": "a1af7ddb-d2ca-4e3d-a72a-4eae7c0eeaaf", "errors": null }
		 * 
		 */
	}

	// response is JSON
	@Test(enabled = false)
	public void uniRestTest3() throws UnirestException {
		apiURL = "https://www.peoplefinders.com/api/widget/widgets?firstName=bill&lastName=gates&city=redmond&state=wa";
		HttpResponse<String> response = Unirest.get(apiURL).queryString("lastName", lastName).asString();
		System.err.println(response.getBody());
	}

	// response is JSON
	@Test(enabled = false)
	public void uniRestTest1() throws UnirestException {
		apiURL = "https://www.peoplefinders.com/api/widget/widgets?firstName=bill&lastName=gates&city=redmond&state=wa";
		// NOTE: non typical case
		final HttpResponse<JsonNode> responseJsonNode = Unirest.get(apiURL).asJson();
		System.err.println(responseJsonNode.getBody());
		//
	}
}
