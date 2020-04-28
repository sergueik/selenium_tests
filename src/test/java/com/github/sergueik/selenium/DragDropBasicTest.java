package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.interactions.internal.Locatable;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Selected test scenarios for Selenium WebDriver
 * 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

// based on:
// https://software-testing.ru/forum/index.php?/topic/39180-drag-and-drop-element-peretaskivaetsia-k-kursoru/
// see also: https://codepen.io/Goldfsh/pen/zBbOqm
public class DragDropBasicTest extends BaseTest {
	private static final StringBuffer verificationErrors = new StringBuffer();
	private static String baseURL = "https://www.seleniumeasy.com/test/drag-and-drop-demo.html";
	private static WebElement sourceElement;
	private static WebElement targetElement;

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
		driver.get(baseURL);
	}

	@BeforeMethod
	public void beforeMethod() {
		sourceElement = driver.findElement(By.cssSelector("#todrag > span:nth-child(2)"));
		assertThat(sourceElement, notNullValue());
		targetElement = driver.findElement(By.id("mydropzone"));
		assertThat(targetElement, notNullValue());
	}

	@AfterClass
	public void afterClass() {
		try {
			super.afterClass();
		} catch (Exception e) {
			System.err.println("Exception (ignored): " + e.toString());
		}
		if (verificationErrors.length() != 0) {
			System.err.println("Errors in tests: " + verificationErrors.toString());
		}
	}

	@Test(priority = 1, enabled = true)
	public void test1() {

		// Act
		Coordinates source_coords = ((Locatable) sourceElement).getCoordinates();
		Coordinates target_coords = ((Locatable) targetElement).getCoordinates();
		String simulateDragDropScript = getScriptContent("simulateDragDrop.js");
		System.err.println(String.format("Simulate drag an drop by: (%-4d, %-4d)",
				target_coords.inViewPort().x - source_coords.inViewPort().x,
				target_coords.inViewPort().y - source_coords.inViewPort().y));

		executeScript(simulateDragDropScript, sourceElement, target_coords.inViewPort().x,
				target_coords.inViewPort().y);

		sleep(1000);
		System.err.println("Result: " + targetElement.getAttribute("innerHTML"));
		// Assert

		actions.dragAndDrop(sourceElement, targetElement);
		actions.build();
		actions.perform();
		sleep(10000);
	}

	@Test(priority = 2, enabled = true)
	public void test2() {
		System.err.println("dragByOffset: " + (targetElement.getLocation().getX() - sourceElement.getLocation().getX())
				+ "," + (targetElement.getLocation().getY() - sourceElement.getLocation().getY()));
		actions.dragAndDropBy(sourceElement, targetElement.getLocation().getX() - sourceElement.getLocation().getX(),
				targetElement.getLocation().getY() - sourceElement.getLocation().getY());
		actions.build();
		actions.perform();
		sleep(1000);
	}

	@Test(priority = 3, enabled = true)
	public void test3() {
		// NOTE: with actions always first build() then perform()
		// because perform alone does not work in Firefox
		actions.dragAndDrop(sourceElement, targetElement);
		actions.build();
		actions.perform();
		sleep(1000);
	}

}
