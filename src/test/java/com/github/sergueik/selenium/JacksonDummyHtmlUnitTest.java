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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.testng.annotations.Test;
import org.yaml.snakeyaml.Yaml;

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
		System.err.println(String.format("testYAMLtoJSON:\n%s\n", jsonString));
	}

	@Test(enabled = true)
	public void testLoadGenericYAML() {
		String fileName = String.join(System.getProperty("file.separator"),
				Arrays.asList(System.getProperty("user.dir"), "src", "test",
						"resources", "generic.yaml"));
		InputStream in;
		try {
			// load with snakeyaml
			in = Files.newInputStream(Paths.get(fileName));
			// hack: direct parsing into a real Java object fails with
			// SnakeYaml, Gson and Jackson due to strongly typed
			Map<String, Object> data = (Map<String, Object>) new Yaml().load(in);
			// java.lang.ClassCastException
			Map<String, Object> userData = (Map<String, Object>) data.get("user");
			ArrayList<String> roles = (ArrayList<String>) userData.get("roles");
			// Exception (ignored):
			// com.fasterxml.jackson.databind.JsonMappingException: java.lang.Integer
			// cannot be cast to java.lang.String (through reference chain:
			// com.github.sergueik.selenium.JacksonDummyHtmlUnitTest$User["address"]->java.util.LinkedHashMap["zip"])
			User user = new User((String) userData.get("name"),
					(String) userData.get("familyname"),
					// java.lang.ClassCastException: java.lang.Integer cannot be cast to
					// java.lang.String
					/* Integer.parseInt((String) userData.get("age")),*/
					(int) userData.get("age"),
					(Map<String, Object>) userData.get("address"),
					// java.lang.ClassCastException: java.util.ArrayList cannot be cast to
					// [Ljava.lang.String;
					(String[]) roles.toArray(new String[roles.size()]));
			// dump with Jackson
			jsonString = ouputObjectMapper.writeValueAsString(user);
			System.err.println(String.format(
					"Cherry-picking the user with SnakeYaml and Jackson: \n%s\n",
					jsonString));

		} catch (IOException e) {
			System.err.println("Exception (ignored): " + e.toString());
		}
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
			System.err.println("testLoadYAML:\n" + ReflectionToStringBuilder
					.toString(user, ToStringStyle.MULTI_LINE_STYLE));
		} catch (Exception e) {
			System.err.println("Exception (ignored): " + e.toString());
		}
	}

	private static class User {

		private String name;
		private String familyname;
		private int age;
		private Map<String, Object> address;
		private String[] roles;

		public String getFamilyname() {
			return familyname;
		}

		public void setFamilyname(String data) {
			this.familyname = data;
		}

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

		public Map<String, Object> getAddress() {
			return address;
		}

		public void setAddress(Map<String, Object> data) {
			this.address = data;
		}

		public String[] getRoles() {
			return roles;
		}

		public void setRoles(String[] data) {
			this.roles = data;
		}

		// default constructor neeeded for jackson
		public User() {

		}

		public User(String name, String familyname, int age,
				Map<String, Object> address, String[] roles) {
			super();
			this.name = name;
			this.familyname = familyname;
			this.age = age;
			this.address = address;
			this.roles = roles;
		}
	}

}
