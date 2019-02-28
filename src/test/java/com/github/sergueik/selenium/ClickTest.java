package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Selected test scenarios for Selenium WebDriver
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 * based on discussion https://automated-testing.info/t/v-selenium-ne-klikaet-na-elementy-na-stranicze-po-id-ili-cssselector/22261/5
 */

public class ClickTest extends BaseTest {

	private static final Logger log = LogManager.getLogger(ClickTest.class);
	private String baseUrl = "http://new.villagio-vip.ru/realty/objects";

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
	}

	@BeforeMethod
	public void beforeMethod(Method method) {
		super.beforeMethod(method);
		driver.get(baseUrl);
	}

	@AfterMethod
	@Override
	public void afterMethod() {
		driver.get("about:blank");
	}

	private static final String id1 = "Filter__List";
	private static final String id2 = "Filter__Map";
	private static List<String> xpaths = Arrays.asList(new String[] {
			"//main/div[@class='Search']", String.format("//*[@id='%s']/..", id1),
			String.format("//*[@id='%s']/following-sibling::*", id1),
			String.format("//*[@id='%s']/following-sibling::*", id2) });
	private static String xpath;
	private static WebElement element = null;
	private static List<WebElement> elements = new ArrayList<>();

	@Test(enabled = false)
	public void visibilityTest() {

		// Arrange
		// NOTE: can not use static class variable as the loop variable, it appears
		for (String xpath : xpaths) {
			elements = driver.findElements(By.xpath(xpath));
			assertTrue(elements.size() > 0);
			element = elements.get(0);
			highlight(element);
			// repeat with potential exception
			element = wait.until(
					ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
			highlight(element);
			// System.err.println(String.format("%s finds %s", xpath,
			// element.getAttribute("outerHTML")));
		}
	}

	@Test(enabled = true)
	public void clickTest() {
		// Arrange
		element = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath(String.format("//div[@class='%s']", "InputSearchWrapper"))));
		highlight(element);
		element.click();
		sleep(100);
		elements = element.findElements(By.xpath(
				"./div[contains(@class, 'basic-multi-select')]//div[contains(@id,'react-select')]"));
		assertTrue(elements.size() > 0);
		elements.stream().limit(5).forEach(reactElement -> {
			try {
				highlight(reactElement);
				System.err.println(
						BaseTest.Translit.toAscii(reactElement.getAttribute("outerHTML")));
			} catch (StaleElementReferenceException e) {
				// simply ignore
			}
		});
	}

	@Test(enabled = true)
	public void siblingClassTest() {
		// Arrange
		element = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath(String.format("//div[@class='%s']", "InputSearchWrapper"))));
		highlight(element);
		elements = element.findElements(By.xpath("../div"));
		assertTrue(elements.size() > 0);
		elements.stream().limit(5).forEach(e -> {
			highlight(e);
			System.err.println(ClickTest.Translit.toAscii(e.getAttribute("class")));
		});
	}

	// TODO: merge projects
	// from
	// https://github.com/sergueik/selenium_tests/tree/master/src/test/java/com/github/sergueik/selenium/TariffTest.java
	public static class Translit {

		private static final Charset UTF8 = Charset.forName("UTF-8");
		private static final char[] alphabetCyrillic = { ' ', 'а', 'б', 'в', 'г',
				'д', 'е', 'ё', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р',
				'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю',
				'я', 'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л',
				'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ',
				'Ъ', 'Ы', 'Б', 'Э', 'Ю', 'Я', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
				'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
				'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
				'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
				'Y', 'Z', '<', '>', '"', '=', '-', '.', '0', '1', '2', '3', '4', '5',
				'6', '7', '8', '9' };

		private static final String[] alphabetTranslit = { " ", "a", "b", "v", "g",
				"d", "e", "e", "zh", "z", "i", "y", "k", "l", "m", "n", "o", "p", "r",
				"s", "t", "u", "f", "h", "ts", "ch", "sh", "sch", "", "i", "", "e",
				"ju", "ja", "A", "B", "V", "G", "D", "E", "E", "Zh", "Z", "I", "Y", "K",
				"L", "M", "N", "O", "P", "R", "S", "T", "U", "F", "H", "Ts", "Ch", "Sh",
				"Sch", "", "I", "", "E", "Ju", "Ja", "a", "b", "c", "d", "e", "f", "g",
				"h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u",
				"v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I",
				"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W",
				"X", "Y", "Z", "<", ">", "\"", "=", "-", ".", "0", "1", "2", "3", "4",
				"5", "6", "7", "8", "9" };

		private static String toAscii(final String input) {
			final CharsetEncoder charsetEncoder = UTF8.newEncoder();
			final char[] decomposed = Normalizer
					.normalize(input, Normalizer.Form.NFKD).toCharArray();
			final StringBuilder sb = new StringBuilder(decomposed.length);

			// NOTE: evaluating the character charcount is unnecessary with Cyrillic
			for (int i = 0; i < decomposed.length;) {
				final int codePoint = Character.codePointAt(decomposed, i);
				final int charCount = Character.charCount(codePoint);

				if (charsetEncoder
						.canEncode(CharBuffer.wrap(decomposed, i, charCount))) {
					sb.append(decomposed, i, charCount);
				}

				i += charCount;
			}

			StringBuilder builder = new StringBuilder();

			for (int i = 0; i < sb.length(); i++) {
				for (int x = 0; x < alphabetCyrillic.length; x++)
					if (sb.charAt(i) == alphabetCyrillic[x]) {
						builder.append(alphabetTranslit[x]);
					}
			}
			return builder.toString();

		}
	}
}
