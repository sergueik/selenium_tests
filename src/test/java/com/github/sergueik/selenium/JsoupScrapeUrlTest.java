package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Sample test scenario for scraping via jsoup
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class JsoupScrapeUrlTest {

	private static final Logger logger = LogManager
			.getLogger(JsoupScrapeUrlTest.class);

	private static Document jsoupDocument;
	private static Elements jsoupElements;

	private static String attributeValue;
	private static List<SearchResult> searchResultList = new ArrayList<>();

	private static final String baseUrl = "https://www.google.com/";
	private static final String queryTerm = "appium";
	// disable Javascript
	// in Chrome, interactively through
	// chrome://settings/content/javascript?search=script
	// in Firefox, interactively through about:config
	//
	// then collect the useragent through http://useragentstring.com/
	// see also:
	// https://stackoverflow.com/questions/48249/is-there-a-way-to-embed-a-browser-in-java
	private static final String userAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:40.0) Gecko/20100101 Firefox/40.0";
	// private static final String cssSelector = "h3.r > a";
	private static final String cssSelector = "h3.r > a.l[href]";

	@BeforeClass
	public void loadUrl() {
		try {
			jsoupDocument = Jsoup
					.connect(baseUrl + String.format("search?q=%s",
							URLEncoder.encode(queryTerm, "UTF-8")))
					.userAgent(userAgent).get();
			// chop the unneeded part of the query
			// &gbv=1&sei=Y52KXYaxFoiW5wLy2KiQBA
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.toString());
		} catch (IOException e) {
			throw new RuntimeException(e.toString());
		}
		// if a 403 is returned the test stops right here
		assertThat(jsoupDocument, notNullValue());
		// System.err.println(jsoupDocument.outerHtml());
		// "Error 403 (Forbidden)"
	}

	@Test(enabled = true)
	public void testJsoupParseSelect() {

		jsoupElements = jsoupDocument.select(cssSelector);
		assertThat(jsoupElements.size(), greaterThan(0));

		jsoupElements.forEach(link -> {

			final String title = link.text();
			assertThat(title, notNullValue());
			attributeValue = link.attr("href");
			if (!title.matches("Cached")) {
				if (!attributeValue.isEmpty()) {
					String url = attributeValue.replaceFirst("^/url\\?q=", "")
							.replaceFirst("/search\\?q=related:", "");
					if (url.startsWith("http")) {
						searchResultList.add(new SearchResult(title, url));
					}
				}
			}
		});
		searchResultList.forEach(System.err::println);
	}

	@Test(enabled = true)
	public void testAttributeValueContaining() {
		jsoupElements = jsoupDocument.getElementsByAttributeValueContaining("class",
				"l");
		assertThat(jsoupElements.size(), greaterThan(0));

		jsoupElements.forEach(link -> {
			final String title = link.text();
			assertThat(title, notNullValue());
			if (!title.matches("Cached")) {
				attributeValue = link.attr("href");
				if (!attributeValue.isEmpty()) {
					String url = attributeValue.replaceFirst("^/url\\?q=", "")
							.replaceFirst("/search\\?q=related:", "");
					if (url.startsWith("http")) {
						searchResultList.add(new SearchResult(title, url));
					}
				}
			}
		});
		searchResultList.forEach(logger::info);
	}

	@Test(enabled = true)
	public void testjsoupProxyMethod() {

		final String ip = "167.71.254.71";
		final int port = 3128;
		final String url = "http://www.example.com/";
		try {
			/*
			 Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
			 Jsoup.connect(url).proxy(proxy)
			 */
			jsoupDocument = Jsoup.connect(url).proxy(ip, port).userAgent(userAgent)
					.header("Content-Language", "en-US").get();
			assertThat(jsoupDocument, notNullValue());
			System.err.println(jsoupDocument.html());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e.toString());
		} catch (IOException e) {
			throw new RuntimeException(e.toString());
		}

	}

	// NOTE: http://proxylist.hidemyass.com is redirecting to proxy trial page
	// https://www.hidemyass.com/en-us/index
	// NOTE: entries offered by
	// https://free-proxy-list.net/
	// https://free.proxy-sale.com/
	// are non-responding in various ways
	@Test(enabled = true)
	public void testProxy() throws IOException {

		URL url = new URL("http://www.example.com/");
		// java.net.ConnectException: conection timeout
		// java.net.SocketException: Unexpected end of file from server
		// https://free.proxy-sale.com/
		final String ip = "167.71.254.71";
		final int port = 3128;

		HttpURLConnection httpURLConnection = (HttpURLConnection) url
				.openConnection(
						new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port)));

		httpURLConnection.connect();

		String line = null;
		StringBuffer tmp = new StringBuffer();
		BufferedReader in = new BufferedReader(
				new InputStreamReader(httpURLConnection.getInputStream()));
		while ((line = in.readLine()) != null) {
			tmp.append(line);
		}

		Document doc = Jsoup.parse(String.valueOf(tmp));
		logger.info(doc.body().text());
	}

	// http://httpbin.org/#/Response_formats
	// http://httpbin.org/get
	private static class SearchResult {
		private String title;
		private String url;

		public SearchResult(String title, String url) {
			this.title = title;
			this.url = url;
		}

		/*
		 * public String getUrl() { return url; }
		 * 
		 * public void setUrl(String value) { this.url = value; }
		 * 
		 * public String getTitle() { return title; }
		 * 
		 * public void setTitle(String value) { this.title = value; }
		 * 
		 */
		@Override
		public String toString() {
			return "SearchResult{" + "url=\"" + url + '"' + ',' + "title=\"" + title
					+ '"' + '}';
		}
	}
}
