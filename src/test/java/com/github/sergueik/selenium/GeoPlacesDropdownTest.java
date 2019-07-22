package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
* Sample test scenario for working with geocoder dynamic searcch results 
* Based on
* http://software-testing.ru/forum/index.php?/topic/38215-webdriverio-google-dropdown-autocomplete-places-geocoder/
* @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
*/

public class GeoPlacesDropdownTest extends BaseTest {

	private String baseURL = "https://developers.google.com/maps/documentation/javascript/examples/places-placeid-geocoder";

	private static final int maxItems = 7;
	private static final String searchText = "New";

	private static String text;
	private static WebElement element;
	private static Iterator<WebElement> iteratorElements;
	private static List<WebElement> elements;
	private static WebDriver iframeDriver;
	private static int cnt;

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
	}

	@BeforeMethod
	public void BeforeMethod() {
		driver.get(baseURL);
		// locating map container page element
		element = driver.findElement(
				By.cssSelector("#gc-wrapper article > div.devsite-article-body"));

		assertThat(element, notNullValue());
		highlight(element, 200);
		elements = element.findElements(By.tagName("iframe"));
		assertThat(elements.size(), greaterThan(0));
		iteratorElements = elements.iterator();

		while (iteratorElements.hasNext()) {
			element = iteratorElements.next();
			System.err.println("Found iframe: " + element.getAttribute("src"));
		}

		iframeDriver = driver.switchTo().frame(elements.get(0));
		sleep(500);
	}

	@AfterMethod
	@Override
	public void afterMethod() {
		driver.get("about:blank");
	}

	@Test(enabled = true)
	public void testDropdownSelect() {
		// Arrange

		// work with the address input
		element = iframeDriver.findElement(By.id("pac-input"));
		assertThat(element, notNullValue());

		// Act
		element.sendKeys(searchText);

		sleep(1000);

		// scroll few items down through the search results menu
		actions = new Actions(iframeDriver);
		for (int cnt = 0; cnt != 10; cnt++) {
			actions.sendKeys(Keys.ARROW_DOWN).perform();
			sleep(500);
		}

		// the dynamic div is visible in Firefox, not in Chrome or Vivaldi -
		// Shadow DOM present ?
		/*
		<div class="pac-container pac-logo hdpi" style="width: 398px; position: absolute; left: 17px; top: 39px; display: none;">
		  <div class="pac-item">
		    <span class="pac-icon pac-icon-marker"/>
		    <span class="pac-item-query">
		      <span class="pac-matched">Miami Airport</span>
		    </span>
		    <span><span class="pac-matched">Mia</span>mi, FL, USA</span>
		  </div>
		  <div class="pac-item">
		    <span class="pac-icon pac-icon-marker"/>
		    <span class="pac-item-query">
		      <span class="pac-matched">Miami International Airport (MIA)</span>
		    </span>
		    <span>Northwest 42nd Avenue, Miami, <span class="pac-matched">Mia</span>mi-Dade, FL, USA</span>
		  </div>
		</div>
		*/

		// Assert
		elements = iframeDriver.findElements(By.className("pac-item"));
		assertThat(elements, notNullValue());
		iteratorElements = elements.iterator();
		cnt = 0;
		while (iteratorElements.hasNext()) {
			cnt++;
			element = iteratorElements.next();
			// System.err.println(element.getAttribute("outerHTML"));
			text = null;
			try {
				text = element.getText();
				assertThat(text, notNullValue());
				System.err
						.println(String.format("geocode search result %2d: %s", cnt, text));
			} catch (NoSuchElementException e) {
				System.err.println("Exception (ignored): " + e.getMessage());
			}
		}
		// count results added to the page
		assertThat(cnt, not(greaterThan(maxItems)));
	}
	/*
	// fragment of the page:
	
	// This sample requires the Places library. Include the libraries=places
	// parameter when you first load the API. For example:
	// <script
	// src="https://maps.googleapis.com/maps/api/js?key=YOUR_API_KEY&libraries=places">
	function initMap() {
	    var map = new google.maps.Map(
	        document.getElementById('map'), {
	            center: {
	                lat: -33.8688,
	                lng: 151.2195
	            },
	            zoom: 13
	        });
	
	    var input = document.getElementById('pac-input');
	
	    var autocomplete = new google.maps.places.Autocomplete(input);
	
	    autocomplete.bindTo('bounds', map);
	
	    // Specify just the place data fields that you need.
	    autocomplete.setFields(['place_id', 'geometry', 'name', 'formatted_address']);
	
	    map.controls[google.maps.ControlPosition.TOP_LEFT].push(input);
	
	    var infowindow = new google.maps.InfoWindow();
	    var infowindowContent = document.getElementById('infowindow-content');
	    infowindow.setContent(infowindowContent);
	
	    var geocoder = new google.maps.Geocoder;
	
	    var marker = new google.maps.Marker({
	        map: map
	    });
	    marker.addListener('click', function() {
	        infowindow.open(map, marker);
	    });
	
	    autocomplete.addListener('place_changed', function() {
	        infowindow.close();
	        var place = autocomplete.getPlace();
	
	        if (!place.place_id) {
	            return;
	        }
	        geocoder.geocode({
	            'placeId': place.place_id
	        }, function(results, status) {
	            if (status !== 'OK') {
	                window.alert('Geocoder failed due to: ' + status);
	                return;
	            }
	
	            map.setZoom(11);
	            map.setCenter(results[0].geometry.location);
	
	            // Set the position of the marker using the place ID and location.
	            marker.setPlace({
	                placeId: place.place_id,
	                location: results[0].geometry.location
	            });
	
	            marker.setVisible(true);
	
	            infowindowContent.children['place-name'].textContent = place.name;
	            infowindowContent.children['place-id'].textContent = place.place_id;
	            infowindowContent.children['place-address'].textContent =
	                results[0].formatted_address;
	
	            infowindow.open(map, marker);
	        });
	    });
	}
	*/
}