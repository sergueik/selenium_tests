package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

/**
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class TranslitTest {

	private static final StringBuffer verificationErrors = new StringBuffer();
	private static final Logger log = LogManager.getLogger(TranslitTest.class);

	private static String data = "день"; // "абвгдеёжзийклмнопрстуфхцчшщэюя";

	@Test(enabled = true)
	public void test1() {

		System.err.println(String.format("|%s|%s|%s|", data,
				BaseTest.Translit.toAscii(data), BaseTest.Translit.toAscii(data)));
		log.info(data + " | "
				+ BaseTest.Translit.toAscii(data, BaseTest.Translit.alphabetResources));
	}

	@Test(enabled = false)
	public void test2() {
		assertThat(
				BaseTest.Translit.toAscii("ё", BaseTest.Translit.alphabetResources),
				is(not(BaseTest.Translit.toAscii("е",
						BaseTest.Translit.alphabetResources))));
	}

	@Test(enabled = false)
	public void test3() {
		assertThat(
				BaseTest.Translit.toAscii("й", BaseTest.Translit.alphabetResources),
				is(not(BaseTest.Translit.toAscii("и",
						BaseTest.Translit.alphabetResources))));
	}
}
