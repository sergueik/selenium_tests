package com.github.sergueik.selenium;

import static java.lang.System.err;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class Base64PropertyValueTest {
	private static final StringBuffer verificationErrors = new StringBuffer();

	private static final boolean debug = true;

	@Test(enabled = true)
	public void selectFeedsTest() {
		// TODO: load prepared profile
		// -DDATA=YT1iIGM9ZCBlPWYgZz1oCg==
		// 	-DDATA=eyJuYW1lIjoidmFsdWUiLCAic3VjY2VzcyI6dHJ1ZSwicmVzdWx0Ijo0MiwiaWQiOjAgfQo=
		String data = decodePropertyArgument("DATA", "ERROR");
		err.println("Data: " + data);
		// Data: {"name":"value", "success":true,"result":42,"id":0 }
		readSideData(data, Optional.<Map<String, Object>> empty(), "(?:id|name|success|result)");
	}

	@AfterMethod
	public void afterMethod(ITestResult result) {
		if (verificationErrors.length() != 0) {
			throw new RuntimeException(String.format("Error(s) in the method %s : %s",
					result.getMethod().getMethodName(), verificationErrors.toString()));
		}
	}

	// based on:
	// https://www.programcreek.com/java-api-examples/org.apache.commons.codec.binary.Base64
	public String decodePropertyArgument(String argument, String defaultValue) {
		String encodedArgument = null;
		String decodedArgument = null;
		if (argument != null) {
			try {
				encodedArgument = getPropertyEnv(argument, "");
				decodedArgument = new String(
						Base64.decodeBase64(encodedArgument.getBytes("UTF8")));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				encodedArgument = defaultValue;
			}
		}
		return decodedArgument;
	}

	// origin:
	// https://github.com/TsvetomirSlavov/wdci/blob/master/code/src/main/java/com/seleniumsimplified/webdriver/manager/EnvironmentPropertyReader.java
	public static String getPropertyEnv(String name, String defaultValue) {
		String value = System.getProperty(name);
		if (value == null || value.length() == 0) {
			value = System.getenv(name);
			if (value == null || value.length() == 0) {
				value = defaultValue;
			}
		}
		return value;
	}

	public String readSideData(String payload,
			Optional<Map<String, Object>> parameters, String acceptedKeys) {
		if (debug) {
			System.err.println("Accepted keys: " + acceptedKeys);
		}

		Map<String, Object> collector = (parameters.isPresent()) ? parameters.get()
				: new HashMap<>();

		String data = (payload == null)
				? "{\"foo\":\"bar\", \"result\":true,\"id\":42 }" : payload;
		if (debug) {
			System.err.println("Processing payload: " + data.replaceAll(",", ",\n"));
		}
		try {
			JSONObject elementObj = new JSONObject(data);
			@SuppressWarnings("unchecked")
			Iterator<String> propIterator = elementObj.keys();
			while (propIterator.hasNext()) {

				String propertyKey = propIterator.next();
				if (!propertyKey.matches(acceptedKeys /* "(?:id|name|url|tests)" */)) {
					System.err.println("Ignoring key: " + propertyKey);
					continue;
				}
				if (debug) {
					System.err.println("Processing key: " + propertyKey);
				}
				Boolean found = false;
				try {
					String propertyVal = (String) elementObj.getString(propertyKey);
					// logger.info(propertyKey + ": " + propertyVal);
					if (debug) {
						System.err
								.println("Loaded string: " + propertyKey + ": " + propertyVal);
					}
					collector.put(propertyKey, propertyVal);
					found = true;
				} catch (JSONException e) {
					System.err.println("Exception (ignored, continue): " + e.toString());
				}
				if (found) {
					continue;
				}
				try {
					org.json.JSONArray propertyArrayVal = elementObj
							.getJSONArray(propertyKey);
					int length = propertyArrayVal.length();
					if (debug) {
						System.err.println("Can process array of size: " + length);
					}
					StringBuffer innerData = new StringBuffer();
					for (int index = 0; index < length; index++) {
						JSONObject rowObject = propertyArrayVal.getJSONObject(index);
						if (debug) {
							System.err.println("Can process object: " + rowObject.toString());
						}
						// "comment,id,value,command,target"
						readSideData(rowObject.toString(),
								Optional.<Map<String, Object>> empty(),
								"(?:comment|id|value|command|target)");

						Iterator<String> rowObjectIterator = rowObject.keys();

						while (rowObjectIterator.hasNext()) {
							String rowObjectKey = rowObjectIterator.next();
							innerData.append(String.format("%s,", rowObjectKey));
							if (debug) {
								System.err.println("Processing Row key: " + rowObjectKey);
							}
						}
					}
					collector.put(propertyKey, innerData.toString());
					found = true;
				} catch (JSONException e) {
					System.err.println("Exception (ignored, continue): " + e.toString());
				}
			}
		} catch (JSONException e) {
			System.err.println("Exception (ignored, aborting): " + e.toString());
			return null;
		}
		return (String) collector.get("id");
	}

}