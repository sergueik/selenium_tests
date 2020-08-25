package com.github.sergueik.selenium;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

/**
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class TranslitTest {

	private static final StringBuffer verificationErrors = new StringBuffer();
	private static final Logger log = LogManager.getLogger(TranslitTest.class);

	private static String data = "абвгдежзийклмнопрстуфхцчшщэюя";

	@Test(enabled = true)
	public void test1() {

		System.err.println(String.format("|%s|%s|%s|", data,
				BaseTest.Translit.toAscii(data), BaseTest.Translit.toAscii(data)));
		log.info(data + " | " + BaseTest.Translit.toAscii(data, BaseTest.Translit.alphabetResources));
	}
}
