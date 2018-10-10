package com.github.sergueik.selenium;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.internal.Nullable;

/**
 * Selected test scenarios for Selenium WebDriver
 * Use XPath ancestor axis and CSSSelector 'closest' method for navigation and manipulating heavily styled page.
 * based on: https://testerslittlehelper.wordpress.com/
 * 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class TariffTest extends BaseTest {

	private static final StringBuffer verificationErrors = new StringBuffer();
	private static final Logger log = LogManager
			.getLogger(XPathNavigationTest.class);

	private static String baseURL = "about:blank";

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
	}

	@BeforeMethod
	public void BeforeMethod() {
		driver.get(baseURL);
	}

	// NOTE: this test is unstable
	// 1. does not work very well with headless browser
	// even in the visible browser test scenario gets forcibly redirected to
	// https://spb.rt.ru/fault-browser/
	// https://stackoverflow.com/questions/41430406/what-is-the-meaning-of-lt-in-if-lt-ie-9
	/*
	 <!--[if lt IE 9]>
	    <script type="text/javascript">window.location = '/fault-browser/'</script>
	<![endif]-->
	 */
	@Test(enabled = true)
	public void tariffsTest1() {
		// Arrange
		String baseURL = "https://spb.rt.ru/packages/tariffs";
		// driver.get(baseURL);
		driver.navigate().to(baseURL);
		// Сожалеем, но сайт RT.ru несовместим с вашим браузером
		wait.until(ExpectedConditions.urlMatches(baseURL));
		List<WebElement> elements = new ArrayList<>();
		elements = driver.findElements(By.cssSelector("*[data-fee]"));

		List<String> fees = elements.stream().map(_e -> _e.getAttribute("data-fee"))
				.collect(Collectors.toList());
		fees.stream().forEach(System.err::println);
		List<WebElement> buttons = fees.stream()
				.filter(fee -> Integer.parseInt(fee) > 0).map(fee -> {
					String xpath = String.format(
							"//*[@data-fee='%s']/ancestor::div[contains(@class,'js-price')]//a[contains(@class, 'button')]",
							fee);
					WebElement buttonElement = null;
					try {
						buttonElement = driver.findElement(By.xpath(xpath));
						executeScript(
								"arguments[0].scrollIntoView({ behavior: \"smooth\" });",
								buttonElement);
						highlight(buttonElement.findElement(By.xpath("..")));
						// System.err.println(buttonElement.getAttribute("outerHTML"));
						// System.err.println(buttonElement.findElement(By.xpath("..")).getAttribute("outerHTML"));
						System.err.println(String.format("Connection fee: %s", fee));
						// NOTE: funny console output of cyrillic word:
						// Подключить
						// чить
						// ь
						assertThat(buttonElement.getText(), equalTo("Подключить"));
						System.err.println(
								String.format("Button Text: |%s|", buttonElement.getText()));
						System.err.println(xpathOfElement(buttonElement));
					} catch (Exception e) {
						// temporarily catch all exceptions.
						System.err.println("Exception: " + e.toString());
					}
					return buttonElement;
				}).collect(Collectors.toList());
	}

	// a debug version of test1.
	// NOTE: slower
	@Test(enabled = false)
	public void tariffsTest2() {
		// Arrange
		String baseURL = "https://spb.rt.ru/packages/tariffs";
		driver.get(baseURL);
		List<WebElement> elements = new ArrayList<>();
		elements = driver.findElements(By.cssSelector("*[data-fee]"));

		List<WebElement> buttons = elements.stream().map(_element -> {
			String fee = _element.getAttribute("data-fee");
			WebElement containerElement = null;
			WebElement buttonElement = null;
			if (Integer.parseInt(fee) > 0) {
				String xpath = String
						.format("ancestor::div[contains(@class,'js-price')]", fee);
				try {
					containerElement = _element.findElement(By.xpath(xpath));
					if (containerElement != null) {

						// System.err.println("Container element: "
						// + containerElement.getAttribute("innerHTML"));
						try {
							buttonElement = containerElement
									.findElement(By.cssSelector("a[class *= 'button']"));
							if (buttonElement != null) {
								executeScript(
										"arguments[0].scrollIntoView({ behavior: \"smooth\" });",
										buttonElement);
								highlight(buttonElement.findElement(By.xpath("..")));
								System.err.println(String.format("Connection fee: %s", fee));
								assertThat(buttonElement.getText(), equalTo("Подключить"));
								// https://stackoverflow.com/questions/5806690/is-there-an-iconv-with-translit-equivalent-in-java
								System.err.println(
										String.format("Button Text assertion passed: |%s|%s|%s|",
												buttonElement.getText(), "Подключить",
												Translit.toAscii("Подключить")));
								System.err.println(String.format("Button Text: |%s|",
										buttonElement.getText()));
								System.err.println(cssSelectorOfElement(buttonElement));

							}
						} catch (TimeoutException e2) {
							System.err.println(
									"Exception finding the button element: " + e2.toString());
						}
					}
				} catch (TimeoutException e1) {
					System.err.println(
							"Exception finding the container element: " + e1.toString());
				}
			}
			return buttonElement;
		}).collect(Collectors.toList());
	}

	// https://habr.com/company/ruvds/blog/416539/
	// https://developer.mozilla.org/en-US/docs/Web/API/Element/closest
	@Test(enabled = false)
	public void tariffsTest3() {
		// Arrange
		String baseURL = "https://spb.rt.ru/packages/tariffs";
		driver.get(baseURL);

		List<WebElement> elements = new ArrayList<>();
		elements = driver.findElements(By.cssSelector("*[data-fee]"));

		List<String> fees = elements.stream().map(_e -> _e.getAttribute("data-fee"))
				.collect(Collectors.toList());
		fees.stream().forEach(System.err::println);
		fees.stream().filter(fee -> Integer.parseInt(fee) > 0).forEach(fee -> {
			String xpath = String.format("//*[@data-fee='%s']", fee);
			WebElement element = driver.findElement(By.xpath(xpath));

			boolean debug = false;
			List<String> scripts = new ArrayList<>();
			if (debug) {
				scripts = new ArrayList<>(Arrays.asList(new String[] {
						// immediate ancestor, not the one test is looking for, but
						// helped finding the following one
						"var element = arguments[0];\n"
								+ "var locator = 'div.tariff-desc__cost_m-cell';"
								+ "var targetElement = element.closest(locator);\n"
								+ "targetElement.scrollIntoView({ behavior: 'smooth' });\n"
								+ "return targetElement.outerHTML;",
						// next in the ancestor chain, located and printed the outerHTML of
						// element for debugging purposes
						"var element = arguments[0];\n"
								+ "var locator = 'div.tariff-desc__cost.tariff-desc__cost_reset.js-price-blocks';"
								+ "var targetElement = element.closest(locator);\n"
								+ "targetElement.scrollIntoView({ behavior: 'smooth' });\n"
								+ "return targetElement.outerHTML;",
						// relevant ancestor chain, chained with a quesySelector call
						// but with full classes making it hard to read and fragile
						"var element = arguments[0];\n"
								+ "var locator = 'div.tariff-desc__cost.tariff-desc__cost_reset.js-price-blocks';"
								+ "var targetElement = element.closest(locator).querySelector('a.button-3');\n"
								+ "targetElement.scrollIntoView({ behavior: 'smooth' });\n"
								+ "return targetElement.innerHTML;",
						// final selector
						"var element = arguments[0];\n"
								+ "var locator = 'div.js-price-blocks';"
								+ "var targetElement = element.closest(locator).querySelector('a.button-3');\n"
								+ "targetElement.scrollIntoView({ behavior: 'smooth' });\n"
								+ "return targetElement.innerHTML;" }));
				for (String script : scripts) {
					System.err.println("Running the script:\n" + script);
					try {
						String result = (String) js.executeScript(script, element);
						System.err.println("Found:\n" + result);
						// assertThat(result, equalTo("text to find"));
					} catch (Exception e) {
						// temporarily catch all exceptions.
						System.err.println("Exception: " + e.toString());
					}
				}
			} else {
				// convert to function
				String script = "var element = arguments[0];\n"
						+ "var ancestorLocator = arguments[1];"
						+ "var targetElementLocator = arguments[2];"
						+ "/* alert('ancestorLocator = ' + ancestorLocator); */"
						+ "var targetElement = element.closest(ancestorLocator).querySelector(targetElementLocator);\n"
						+ "targetElement.scrollIntoView({ behavior: 'smooth' });\n"
						+ "return targetElement.text;";
				try {
					System.err.println("Running the script:\n" + script);
					String result = (String) js.executeScript(script, element,
							"div.js-price-blocks", "a.button-3");
					System.err.println("Found:\n" + result);
					// assertThat(result, equalTo("text to find"));
				} catch (Exception e) {
					// temporarily catch all exceptions.
					System.err.println("Exception: " + e.toString());
				}

			}
		});
	}

	// based on
	// https://stackoverflow.com/questions/5806690/is-there-an-iconv-with-translit-equivalent-in-java
	// and
	// http://tocrva.blogspot.com/2015/03/java-transliterate-cyrillic-to-latin.html
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
				'Y', 'Z' };

		private static final String[] alphabetTranslit = { " ", "a", "b", "v", "g",
				"d", "e", "e", "zh", "z", "i", "y", "k", "l", "m", "n", "o", "p", "r",
				"s", "t", "u", "f", "h", "ts", "ch", "sh", "sch", "", "i", "", "e",
				"ju", "ja", "A", "B", "V", "G", "D", "E", "E", "Zh", "Z", "I", "Y", "K",
				"L", "M", "N", "O", "P", "R", "S", "T", "U", "F", "H", "Ts", "Ch", "Sh",
				"Sch", "", "I", "", "E", "Ju", "Ja", "a", "b", "c", "d", "e", "f", "g",
				"h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u",
				"v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I",
				"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W",
				"X", "Y", "Z" };

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
