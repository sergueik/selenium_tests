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

import com.fasterxml.jackson.databind.SequenceWriter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
* Sample test scenario for YAML file loading intended to use outside
* based on https://dzone.com/articles/read-yaml-in-java-with-jackson
* target\lib
* jackson-annotations-2.9.9.jar
* jackson-core-2.9.9.jar
* jackson-databind-2.9.9.jar
* jackson-dataformat-yaml-2.9.9.jar
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
	private static ObjectMapper objectMapper = new ObjectMapper(yamlFactory);

	private static String yamlString = null;

	private static final Logger log = LogManager
			.getLogger(JacksonDummyHtmlUnitTest.class);

	@Test(enabled = true)
	public void testSilentWithSelector() {

		try {
			User user = objectMapper
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
