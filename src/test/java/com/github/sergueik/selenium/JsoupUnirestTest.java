package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.testng.annotations.Test;

import java.util.Map;

import com.mashape.unirest.http.HttpResponse;

import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.apache.http.HttpHost;
// for JSON  responses
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

// alternatively for JSON  responses
import com.google.gson.Gson;

/**
 *
 * unirest example based on https://www.udemy.com/course/learn-web-scraping-with-java-in-just-1-hour/
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class JsoupUnirestTest {
	private static boolean debug = false;

	final private static String refererUrl = "https://www.peoplefinders.com/people/bill-gates/wa/redmond";
	// the AJAX call no longer uses that apiURL
	// now it is using autocomplete-cities, autocomplete-names etc.

	private static final String apiURL = "https://www.peoplefinders.com/api/widget/widgets?firstName=bill&lastName=gates&city=redmond&state=wa";
	// TODO: demonstrate the legacy AJAX API
	// "https://https://www.peoplefinders.com/GetResults?";

	private static final String browserUserAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:40.0) Gecko/20100101 Firefox/40.0";
	// alternaribely use googlebot user-agent
	// https://developers.whatismybrowser.com/browserUserAgents/explore/software_name/googlebot/
	private static final String googleBotUserAgent = "Googlebot/2.1 (+http://www.google.com/bot.html)";

	private static final String lastName = "gates";
	private static final String firstName = "bill";

	// process response as text
	@Test(enabled = false)
	public void uniRestTest1() throws UnirestException {
		final HttpResponse<String> response = Unirest.get(apiURL).asString();
		System.err.println(response.getBody().substring(0, 100));
	}

	// response is JSON
	// provide protective headers
	@Test(enabled = false)
	public void uniRestTest2() throws UnirestException, JSONException {
		HttpResponse<JsonNode> response = Unirest.get(apiURL)
				.header("Referer", refererUrl).header("User-Agent", browserUserAgent)
				.asJson();
		// TODO: WARNING: Invalid cookie header: "Set-Cookie:
		// pf.browserid=e75b47fa-9f87-4a0c-a0ae-276c3118b4e8; expires=Sun, 26 Sep
		// 2049 14:10:31 GMT; path=/; httponly". Invalid 'expires' attribute: Sun,
		// 26 Sep 2049 14:10:31 GMT
		assertThat(response.getStatus(), is(200));
		System.err.println("Status:" + response.getStatus());

		if (debug) {
			System.err.println(response.getBody());
		}
		// through JsonNode
		JSONObject responseJSONObject = response.getBody().getObject();
		try {
			System.err.println("Success:" + responseJSONObject.getBoolean("success"));
			// TODO: assertThat(objResponse, hasKey("success"));
		} catch (JSONException e) {
			// ignore
			System.err.println("Exception (ignored)" + e.toString());
		}

		// alternatively bypassing the jsoup JSON implementation
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

	// connect through public proxy without checking
	// https://free-proxy-list.net/
	@Test(enabled = true)
	public void proxyTest() throws UnirestException {

		final String proxy = "167.71.254.71";
		final int port = 3128;

		Unirest.setProxy(new HttpHost(proxy, port));
		try {
			final HttpResponse<JsonNode> responseJsonNode = Unirest.get(apiURL)
					.header("User-Agent", googleBotUserAgent).asJson();
			System.err.println(((JSONObject) responseJsonNode.getBody().getObject()
					.getJSONArray("partnerResponses").get(0)).getJSONArray("Items").get(0)
							.toString());
		} catch (UnirestException e) {
			System.err.println("Exception (ignored)" + e.toString());
		} catch (JSONException e) {
			System.err.println("Exception (ignored)" + e.toString());
		} finally {
			Unirest.clearDefaultHeaders();
			Unirest.setProxy(null);
		}
	}

	// NOTE: naturally, the public proxy services is unstable:
	// all free proxies listed in https://free.proxy-sale.com/
	// found to time outs, return no response, or
	// detect the proxy error protecting the scraper from JSON exception
	// https://free-proxy-list.net/
	@Test(enabled = true)
	public void failingProxyTest() {

		final String proxy = "120.132.52.27";
		final int port = 8888;
		boolean success = false;
		HttpResponse<String> response = null;

		Unirest.setProxy(new HttpHost(proxy, port));
		try {
			response = Unirest.get(apiURL).queryString("lastName", lastName)
					.asString();
			success = true;
		} catch (UnirestException e) {

			// org.apache.http.NoHttpResponseException:
			// www.peoplefinders.com:443 failed to respond
			// org.apache.http.conn.ConnectTimeoutException:
			// Connect to 91.202.240.208:51678 [/91.202.240.208] failed: connect timed
			// out
			System.err.println("Exception (caught): " + e.toString());
			success = false;
		}
		if (success) {
			if (response.getStatus() == 403) {
				System.err.println("Status: " + response.getStatus());
				final Document jsoupDocument = Jsoup.parse(response.getBody());
				String title = jsoupDocument.select("title").text();
				System.err.println(
						String.format("Proxy %s/%d error: %s", proxy, port, title));
				success = false;
			}
		}
		if (success) {
			try {
				final HttpResponse<JsonNode> responseJsonNode = Unirest.get(apiURL)
						.header("User-Agent", googleBotUserAgent).asJson();
				System.err.println(responseJsonNode.getBody().getObject().toString());
			} catch (UnirestException e) {
				System.err.println("Exception (ignored): " + e.toString());
			}
		}
		Unirest.clearDefaultHeaders();
		Unirest.setProxy(null);
	}

	// response is JSON with the error
	@Test(enabled = true)
	public void incompleteQueryTest() throws UnirestException {
		final String incompleteQueryUrl = apiURL.replaceAll("&lastName=(?:[^&]+)",
				"");
		final HttpResponse<JsonNode> responseJsonNode = Unirest
				.get(incompleteQueryUrl).header("User-Agent", googleBotUserAgent)
				.asJson();
		System.err.println(responseJsonNode.getBody().toString());

		final HttpResponse<String> response = Unirest.get(incompleteQueryUrl)
				.queryString("lastName", lastName).asString();
		System.err.println(response.getBody().substring(0, 600));

		//
		// Type type1 = new TypeToken<ObjectItem>() { }.getType();
		// TODO: extract the
		// {"SlotId":146,"SiteId":1,"IsSkipApi":false,"SlotNumber":2,"RequestedUrl":"http://partners.truthfinder.com/v1/teaser?first_name=bill&last_name=&city=redmond&state=wa&age=0&user_ip=192.168.8.6&sid=SB_MultiR&limit=5&output_format=json&access_key=BQzd5z7ETK&a=156&c=372&oc=27&dip=&page=s&s1=single","IsError":true,"PartnerId":5,"BannerTypeName":"BannerTypeTeaser","PartnerName":"TruthFinder.com","SlotUniqueGuid":"a5074822-0a32-472c-83b9-175d80389250","ErrorMessage":"Error
		// in GetResponse
		// http://partners.truthfinder.com/v1/teaser?first_name=bill&last_name=&city=redmond&state=wa&age=0&user_ip=192.168.8.6&sid=SB_MultiR&limit=5&output_format=json&access_key=BQzd5z7ETK&a=156&c=372&oc=27&dip=&page=s&s1=single:
		// {\"message\":\"Bad Request (400): URL parameter 'last_name' is required
		// for this client\"}"}

	}

}
