package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
* Sample test scenario for building strongly typed data object extracted from web page via joup
* @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
*/

// Based on
// https://toster.ru/q/645771
public class JsoupProbe3Test extends BaseTest {

	private static final Logger log = LogManager.getLogger(JsoupProbe3Test.class);
	private String filePath = "vk_snapshot.html";
	// fragment of: https://vk.com/team
	private String pageSource = null;
	private static Document jsoupDocument;
	private static Elements jsoupElements;
	private static Document childDocument;

	private static String attributeName;
	private static Elements jsoupElementAttributes;
	private static String attributeValue;
	private static List<Article> articleList = new ArrayList<>();

	@BeforeClass
	public void beforeClass() throws IOException {
		// super.beforeClass();
		// assertThat(driver, notNullValue());
	}

	@BeforeMethod
	public void loadPage() {
		pageSource = getScriptContent(filePath);
	}

	@Test(enabled = true)
	public void testJsoupParseSelect() {
		jsoupDocument = Jsoup.parse(pageSource);

		jsoupElements = jsoupDocument.getElementsByAttributeValue("class",
				"people_cell");
		assertThat(jsoupElements.size(), greaterThan(0));
		jsoupElements.forEach(link -> {
			Element imgElement = link.getElementsByTag("img").get(0);
			assertThat(imgElement, notNullValue());
			// NOTE: url has empty string in html()

			attributeValue = imgElement.attr("src");
			assertThat(attributeValue, startsWith("https://"));
			String url = attributeValue.replaceAll(".jpg?.*$", "");
			articleList.add(new Article(url));
		});
		articleList.forEach(System.err::println);
	}

	private static class Article {
		private String url;

		public Article(String url) {
			this.url = url;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		@Override
		public String toString() {
			return "Article{" + "url='" + url + '\'' + '}';
		}
	}
}
