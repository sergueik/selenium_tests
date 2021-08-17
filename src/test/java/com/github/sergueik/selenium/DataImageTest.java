package com.github.sergueik.selenium;
/**
 * Copyright 2021 Serguei Kouzmine
 */

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Selected test scenarios for Selenium WebDriver
 * 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class DataImageTest extends BaseTest {

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
	}

	@Test
	public void test1() {
		driver.navigate().to(getPageContent("example_data_image.html"));
		// Arrange
		final WebElement element = driver.findElement(By.cssSelector("img#data_image"));
		assertThat(element, notNullValue());
		assertThat(element.getAttribute("src"), notNullValue());

		assertThat(element.getAttribute("src").length(), greaterThan(0));
		System.err.println("image source: " + element.getAttribute("src").substring(0, 100) + "...");
	}

}
