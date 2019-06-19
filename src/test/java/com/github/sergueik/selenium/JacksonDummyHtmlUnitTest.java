package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import static org.testng.Assert.assertTrue;

import java.util.Map;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import java.io.UnsupportedEncodingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

// for converting into json
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
* Sample test scenario for YAML file loading intended to use outside
* based on https://dzone.com/articles/read-yaml-in-java-with-jackson
* @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
*/
public class JacksonDummyHtmlUnitTest {

	private static boolean debug = false;

	private static final String dataFileName = "user.yaml";
	private static final YAMLFactory yamlFactory = new YAMLFactory();
	static {
		yamlFactory.configure(YAMLGenerator.Feature.USE_NATIVE_TYPE_ID, false)
				.configure(YAMLGenerator.Feature.MINIMIZE_QUOTES, true)
				.configure(YAMLGenerator.Feature.ALWAYS_QUOTE_NUMBERS_AS_STRINGS, true);
	}
	private static ObjectMapper inputObjectMapper = new ObjectMapper(yamlFactory);
	private static ObjectMapper ouputObjectMapper = new ObjectMapper();
	// fallback to JSON

	private static String yamlString = null;
	private static String jsonString = null;

	private static final Logger log = LogManager
			.getLogger(JacksonDummyHtmlUnitTest.class);

	@Test(enabled = true)
	public void testYAMLtoJSON() {
		try {
			User user = inputObjectMapper
					.readValue(new File(String.join(System.getProperty("file.separator"),
							Arrays.asList(System.getProperty("user.dir"), "src", "test",
									"resources", dataFileName))),
							User.class);
			jsonString = ouputObjectMapper.writeValueAsString(user);
		} catch (/* IOException e|| JsonParseException e || JsonMappingException e */ Exception e) {

		}
		System.err.println(
				String.format("JSON serialization with Jackson: \n%s\n", jsonString));
	}

	@Test(enabled = true)
	public void testLoadYAML() {

		try {
			User user = inputObjectMapper
					.readValue(new File(String.join(System.getProperty("file.separator"),
							Arrays.asList(System.getProperty("user.dir"), "src", "test",
									"resources", dataFileName))),
							User.class);
			assertThat(user.getName(), is("Test User"));
			assertThat(user.getAddress().get("line2"), nullValue());
			assertThat(user.getRoles().length, greaterThan(1));
			System.err.println(ReflectionToStringBuilder.toString(user,
					ToStringStyle.MULTI_LINE_STYLE));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static class User {
		private String name;
		private int age;
		private Map<String, String> address;
		private String[] roles;

		public String getName() {
			return name;
		}

		public void setName(String data) {
			this.name = data;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int data) {
			this.age = data;
		}

		public Map<String, String> getAddress() {
			return address;
		}

		public void setAddress(Map<String, String> data) {
			this.address = data;
		}

		public String[] getRoles() {
			return roles;
		}

		public void setRoles(String[] data) {
			this.roles = data;
		}
	}
}
