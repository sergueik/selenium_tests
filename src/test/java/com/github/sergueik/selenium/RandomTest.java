package com.github.sergueik.selenium;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

import org.testng.annotations.Test;

/**
 * Selected test scenarios for Selenium WebDriver
 * based on: https://qna.habr.com/q/851955
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class RandomTest extends BaseTest {

	private static String baseURL = "https://www.transavia.com/en-EU/home/";
	private static String selector = "form#contact_form > fieldset div.form-group div.input-group textarea.form-control";
	private static final StringBuffer verificationErrors = new StringBuffer();
	private final String osName = super.getOSName();
	private Map<String, String> browserDrivers = new HashMap<>();
	private Map<String, String> browserDriverSystemProperties = new HashMap<>();
	private DriverWrapper driverWrapper = new DriverWrapper();

	private static class RandomMethod {

		// TODO: deal with void methods
		private final static List<Function<Integer, String>> functionMap = Arrays
				.asList(i -> {
					System.err.println("launching test1 for " + i);
					test1();
					return "returned from test 1";
				}, i -> {
					System.err.println("launching test2 for " + i);
					test2();
					return "returned from test 2";
				}, i -> {
					System.err.println("launching test3 for " + i);
					test3();
					return "returned from test 3";
				});

		public static String getRandomMethod(Integer argument) {
			// var cannot be resolved to a type pre-java 10
			Random random = new Random();
			Function<Integer, String> method = functionMap
					.get(random.nextInt(functionMap.size()));
			return method.apply(argument);
		}
	}

	@Test(enabled = true)
	public void randomTest() {
		Integer argument = 42;
		RandomMethod.getRandomMethod(argument);
	}

	private static void test1() {
		return;
	}

	private static void test2() {
		return;
	}

	private static void test3() {
		return;
	}
}
