package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.html5.LocalStorage;
import org.openqa.selenium.html5.SessionStorage;
import org.openqa.selenium.html5.WebStorage;
import org.openqa.selenium.remote.Augmenter;
import org.testng.annotations.Test;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

/**
* Sample test scenario for 
* based on https://www.programcreek.com/java-api-examples/index.php?api=org.openqa.selenium.html5.WebStorage
* See also https://medium.com/@jonashavers/accessing-sessionstorage-and-localstorage-with-selenium-remotewebdriver-f3935d8d7d9b
* @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
*/
public class WebStorageTest extends BaseTest {

	private static boolean debug = false;
	private String baseURL = "http://html5demos.com/storage";

	@Test(enabled = true)
	public void testLocalStore() {
		driver.get(baseURL);

		driver.findElement(By.id("local")).sendKeys("myLocal");
		driver.findElement(By.id("session")).sendKeys("mySession");
		driver.findElement(By.tagName("code")).click();

		String item = getItemFromLocalStorage("value");
		System.out.println(item);
		item = null;
		item = getItemFromLocalStorageWithWebStorage("value");
		System.out.println(item);
	}

	public String getItemFromLocalStorage(String key) {
		return (String) executeScript(
				String.format("return window.localStorage.getItem('%s');", key));
	}

	public String getItemFromLocalStorageWithWebStorage(String key) {
		String value = null;
		if (driver instanceof WebStorage) {
			WebStorage webStorage = (WebStorage) driver;
			SessionStorage sessionStorage = webStorage.getSessionStorage();
			value = sessionStorage.getItem(key);

			Properties properties = new Properties();
			for (String storageKey : sessionStorage.keySet()) {
				properties.setProperty(storageKey, sessionStorage.getItem(storageKey));
			}
		}

		return value;
	}

	// example from https://stackoverflow.com/questions/29734136/webdriver-get-item-from-local-storage, not exercised
	public void getItemFromLocalStorageWithWebStorage2(String key, String email,
			String password) {

		WebStorage webStorage = (WebStorage) new Augmenter().augment(driver);
		LocalStorage localStorage = webStorage.getLocalStorage();

		String user_data_remember = localStorage.getItem("user_data_remember");
		String emailAfterLogout;
		String passwordAfterLogout;

		if (!user_data_remember.equals("")) {

			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(user_data_remember);

				Boolean remember = jsonObject.getBoolean("remember");

				if (remember) {
					emailAfterLogout = jsonObject.getString("email");
					passwordAfterLogout = jsonObject.getString("password");

					if (emailAfterLogout.equals(email)
							&& passwordAfterLogout.equals(password)) {
						System.out.println("Remember me is working properly.");
					} else {
						System.out.println("Remember me is not working.");
					}

				} else {
					System.out.println("Remember me checkbox is not clicked.");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}