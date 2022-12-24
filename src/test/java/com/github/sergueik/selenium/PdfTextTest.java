package com.github.sergueik.selenium;

/**
 * Copyright 2022 Serguei Kouzmine
 */
// https://piotrga.wordpress.com/2009/03/27/hamcrest-regex-matcher/
// https://stackoverflow.com/questions/8505153/assert-regex-matches-in-junit
// import static org.hamcrest.CoreMatchers.matchesPattern;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.text.PDFTextStripper;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebElement;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Selected test scenarios for Selenium WebDriver
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

// based on: https://github.com/codeborne/pdf-test
public class PdfTextTest extends BaseTest {

	private static String baseURL = "about:blank";

	private static final Logger log = LogManager.getLogger(PdfTextTest.class);

	private boolean debug = true;
	private static PDF pdf;

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
		driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
	}

	@Override
	@AfterClass
	public void afterClass() {
		try {
			driver.close();
		} catch (NoSuchWindowException e) {

		}
		driver.quit();
		driver = null;
	}

	@BeforeMethod
	public void loadBaseURL() {
		driver.get(baseURL);
	}

	@Test(enabled = false, expectedExceptions = { NoSuchFileException.class })
	public void test1() throws IOException {
		pdf = new PDF(new File("src/test/resources/invalid-file.pdf"));
	}

	@Test
	public void test4() throws IOException {
		baseURL = "https://github.com/codeborne/pdf-test/blob/main/src/test/resources/statement.pdf";
		driver.get(baseURL);
		WebElement element = driver.findElement(
				By.xpath("//a[contains(@data-permalink-href, 'statement.pdf')]"));
		// NOTE: sometimes, https://raw.githubusercontent.com/
		String href = "https://github.com"
				+ element.getAttribute("data-permalink-href");
		if (debug) {
			log.info("Downloading: {}", href);
		}
		pdf = new PDF(new URL(href));
		// NOTE: locale UTF8
		assertThat(pdf.text, containsString("Выписка"));
		assertThat(pdf.encrypted, is(false));
		assertThat(pdf.numberOfPages, equalTo(1));
		assertThat(pdf.producer, containsString("iText"));
		assertThat(pdf.subject, nullValue());
	}

	// Server returned HTTP response code: 403 for URL:
	@Test(enabled = false, expectedExceptions = { java.io.IOException.class })
	public void test3() throws IOException {
		baseURL = "https://intellipaat.com/blog/tutorial/selenium-tutorial/selenium-cheat-sheet/";
		driver.get(baseURL);
		WebElement element = driver.findElement(
				By.xpath("//a[contains(@href, 'Selenium-Cheat-Sheet-2022.pdf')]"));

		pdf = new PDF(new URL(element.getAttribute("href")));
	}

	@Test(enabled = false)
	public void test2() throws IOException {

		pdf = new PDF(getClass().getClassLoader().getResource("minimal.pdf"));
		assertThat(pdf.encrypted, is(false));
		assertThat(pdf.keywords, nullValue());
		assertThat(pdf.numberOfPages, equalTo(1));
		assertThat(pdf.producer, nullValue());
		assertThat(pdf.subject, nullValue());
		assertThat(pdf.text, containsString("Hello World"));

		assertThat(pdf.title, nullValue());
		assertThat(pdf.signed, is(false));
		assertThat(pdf.signerName, nullValue());
		assertThat(pdf.creator, nullValue());

	}

	public static class PDF {
		public final byte[] content;

		public final String text;
		public final int numberOfPages;
		public final String author;
		public final String creator;
		public final String keywords;
		public final String producer;
		public final String subject;
		public final String title;
		public final boolean encrypted;
		public final boolean signed;
		public final String signerName;

		private PDF(String name, byte[] content) {
			this(name, content, 1, Integer.MAX_VALUE);
		}

		private PDF(String name, byte[] content, int startPage, int endPage) {
			this.content = content;

			try (InputStream inputStream = new ByteArrayInputStream(content)) {
				try (PDDocument pdf = PDDocument.load(inputStream)) {
					PDFTextStripper pdfTextStripper = new PDFTextStripper();
					pdfTextStripper.setStartPage(startPage);
					pdfTextStripper.setEndPage(endPage);
					this.text = pdfTextStripper.getText(pdf);
					this.numberOfPages = pdf.getNumberOfPages();
					this.author = pdf.getDocumentInformation().getAuthor();
					// this.creationDate = pdf.getDocumentInformation().getCreationDate();
					this.creator = pdf.getDocumentInformation().getCreator();
					this.keywords = pdf.getDocumentInformation().getKeywords();
					this.producer = pdf.getDocumentInformation().getProducer();
					this.subject = pdf.getDocumentInformation().getSubject();
					this.title = pdf.getDocumentInformation().getTitle();
					this.encrypted = pdf.isEncrypted();

					PDSignature signature = pdf.getLastSignatureDictionary();
					this.signed = signature != null;
					this.signerName = signature == null ? null : signature.getName();
				}
			} catch (Exception e) {
				throw new IllegalArgumentException("Invalid PDF file: " + name, e);
			}
		}

		public PDF(File pdfFile) throws IOException {
			this(pdfFile.getAbsolutePath(),
					Files.readAllBytes(Paths.get(pdfFile.getAbsolutePath())));
		}

		public PDF(URL url) throws IOException {
			this(url.toString(), readBytes(url));
		}

		public PDF(byte[] content) {
			this("", content);
		}

		public PDF(InputStream inputStream) throws IOException {
			this(readBytes(inputStream));
		}

		private static byte[] readBytes(URL url) throws IOException {
			try (InputStream inputStream = url.openStream()) {
				return readBytes(inputStream);
			}
		}

		private static byte[] readBytes(InputStream inputStream)
				throws IOException {
			ByteArrayOutputStream result = new ByteArrayOutputStream(2048);
			byte[] buffer = new byte[2048];

			int nRead;
			while ((nRead = inputStream.read(buffer, 0, buffer.length)) != -1) {
				result.write(buffer, 0, nRead);
			}

			return result.toByteArray();
		}
	}
}
