package com.github.sergueik.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Selected test scenarios for Selenium WebDriver
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

// inspired by
// https://software-testing.ru/forum/index.php?/topic/40460-perestali-zapuskatsia-testy-na-jenkins-posle-perekhod/

public class MethodExceptionTest extends BaseTest {
	String baseURL = "https://www.wikipedia.org/";

	@BeforeClass
	public void before() {
		driver.navigate().to(baseURL);
	}

	@Test(enabled = true)
	// https://github.com/SeleniumHQ/selenium/blob/trunk/java/src/org/openqa/selenium/remote/server/handler/FindElements.java
	public void test1() {
		driver.findElements(By.cssSelector("#searchInput"));
	}

	@Test(enabled = true)
	// https://github.com/SeleniumHQ/selenium/blob/trunk/java/src/org/openqa/selenium/remote/server/handler/FindElements.java
	public void test2() {
		driver.findElements(By.cssSelector("#none"));
		// no eception thrown
	}

	@Test(enabled = true)
	// https://github.com/SeleniumHQ/selenium/blob/trunk/java/src/org/openqa/selenium/remote/server/handler/FindElement.java
	public void test3() throws NoSuchElementException {

		driver.findElement(By.cssSelector("#searchInput"));

	}

	@Test(enabled = true, expectedExceptions = {
			org.openqa.selenium.NoSuchElementException.class })
	// https://github.com/SeleniumHQ/selenium/blob/trunk/java/src/org/openqa/selenium/remote/server/handler/FindElements.java
	public void test4() throws NoSuchElementException {
		driver.findElement(By.cssSelector("#none"));
		// exception thrown
	}

}