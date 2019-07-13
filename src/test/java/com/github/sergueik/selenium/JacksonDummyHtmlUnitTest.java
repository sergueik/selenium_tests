package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
// import avax.xml.parsers.SAXParserFactory;

import com.google.gson.Gson;

/**
* Sample test scenario for YAML file loading intended to use outside
* based on https://dzone.com/articles/read-yaml-in-java-with-jackson
* @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
*/
public class JacksonDummyHtmlUnitTest {

	private static boolean debug = false;

	private static final String dataFileName = "user.yaml";
	private static final YAMLFactory yamlFactory = new YAMLFactory();
	// cannot find symbol
	// [ERROR] symbol: method
	// configure(com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature,boolean)
	/*
	static {
		yamlFactory.configure(YAMLGenerator.Feature.USE_NATIVE_TYPE_ID, false)
				.configure(YAMLGenerator.Feature.MINIMIZE_QUOTES, true)
				.configure(YAMLGenerator.Feature.ALWAYS_QUOTE_NUMBERS_AS_STRINGS, true);
	}
	*/
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
			User user = inputObjectMapper.readValue(
					new File(buildPathtoResourceFile(dataFileName)), User.class);
			jsonString = ouputObjectMapper.writeValueAsString(user);
		} catch (/* IOException e|| JsonParseException e || JsonMappingException e */ Exception e) {

		}
		System.err.println(String.format("testYAMLtoJSON:\n%s\n", jsonString));
	}

	// would fail with composite
	@SuppressWarnings("unchecked")
	@Test(enabled = false)
	public void testLoadAnchorReferencedYAMLWithJackson() {

		final String testName = "testLoadAnchorReferencedYAMLWithJackson";
		String fileName = buildPathtoResourceFile("anchor_reference.yaml");
		InputStream in;
		try {
			// load with Jackson
			Map<String, Object> data = Collections.EMPTY_MAP;
			data = (Map<String, Object>) inputObjectMapper.readValue(
					new File(fileName), new TypeReference<Map<String, Object>>() {
					});
			System.err.println(testName + ":\n" + ReflectionToStringBuilder
					.toString(data, ToStringStyle.MULTI_LINE_STYLE));
			Map<String, Object> userData = (Map<String, Object>) data.get("user");
			System.err.println(testName + ": userData keys\n"
					+ Arrays.asList(userData.keySet().toArray()));
			// testLoadGenericYAMLWithJackson: userData keys [name, <<, roles]
			ArrayList<String> roles = (ArrayList<String>) userData.get("roles");
			System.err.println(testName + ": roles:\n" + Arrays.asList(roles));
			System.err
					.println(testName + ": name:\n" + (String) userData.get("name"));
			System.err.println(testName + ": age:\n" + (int) userData.get("age"));
			Map<String, Object> address = (Map<String, Object>) userData
					.get("address");
			System.err.println(
					testName + ": address city:\n" + (String) address.get("city"));
			User user = new User((String) userData.get("name"),
					(String) userData.get("familyname"), (int) userData.get("age"),
					(Map<String, Object>) userData.get("address"),
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

	@SuppressWarnings("unchecked")
	@Test(enabled = true)
	public void testLoadGenericYAMLWithJackson() {

		String fileName = buildPathtoResourceFile("generic.yaml");
		InputStream in;
		try {
			// load with Jackson
			Map<String, Object> data = Collections.EMPTY_MAP;
			data = (Map<String, Object>) inputObjectMapper.readValue(
					new File(fileName), new TypeReference<Map<String, Object>>() {
					});
			System.err.println(
					"testLoadGenericYAMLWithJackson:\n" + ReflectionToStringBuilder
							.toString(data, ToStringStyle.MULTI_LINE_STYLE));
			Map<String, Object> userData = (Map<String, Object>) data.get("user");
			System.err.println("testLoadGenericYAMLWithJackson: userData keys\n"
					+ Arrays.asList(userData.keySet().toArray()));
			// testLoadGenericYAMLWithJackson: userData keys [name, <<, roles]
			ArrayList<String> roles = (ArrayList<String>) userData.get("roles");
			System.err.println(
					"testLoadGenericYAMLWithJackson: roles:\n" + Arrays.asList(roles));
			System.err.println("testLoadGenericYAMLWithJackson: name:\n"
					+ (String) userData.get("name"));
			System.err.println(
					"testLoadGenericYAMLWithJackson: age:\n" + (int) userData.get("age"));
			Map<String, Object> address = (Map<String, Object>) userData
					.get("address");
			System.err.println("testLoadGenericYAMLWithJackson: address city:\n"
					+ (String) address.get("city"));
			User user = new User((String) userData.get("name"),
					(String) userData.get("familyname"), (int) userData.get("age"),
					(Map<String, Object>) userData.get("address"),
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
	public void testYAMLtoGson() {
		String fileName = buildPathtoResourceFile("group.yaml");
		InputStream in;
		try {
			// load with snakeyaml
			in = Files.newInputStream(Paths.get(fileName));
			@SuppressWarnings("unchecked")
			ArrayList<LinkedHashMap<Object, Object>> members = (ArrayList<LinkedHashMap<Object, Object>>) new Yaml()
					.load(in);
			System.err.println(
					String.format("Loaded %d members of the group", members.size()));
			for (LinkedHashMap<Object, Object> row : members) {
				System.err.println(String.format("Loaded %d propeties of the artist",
						row.keySet().size()));
				jsonString = ouputObjectMapper.writeValueAsString(row.values());
				System.err.println(jsonString);
				// Artist artist = (Artist) row;
			}
			System.err.println("Serialize as JSON:" + new Gson().toJson(members));
		} catch (IOException e) {
		}
	}

	// based on:
	// https://stackoverflow.com/questions/5936003/write-html-file-using-java
	// https://docs.oracle.com/javase/7/docs/api/javax/xml/parsers/SAXParserFactory.html
	// https://mvnrepository.com/artifact/xerces/xerces/2.4.0
	@Test(enabled = true)
	public void testRenderYAMLtoHTMLReport() {
		String fileName = buildPathtoResourceFile("group.yaml");
		InputStream in;

		String encoding = "UTF-8";
		try {
			FileOutputStream fos = new FileOutputStream("report.html");
			OutputStreamWriter writer = new OutputStreamWriter(fos, encoding);
			StreamResult streamResult = new StreamResult(writer);

			SAXTransformerFactory saxFactory = (SAXTransformerFactory) TransformerFactory
					.newInstance();
			TransformerHandler transformerHandler = saxFactory
					.newTransformerHandler();
			transformerHandler.setResult(streamResult);

			Transformer transformer = transformerHandler.getTransformer();
			transformer.setOutputProperty(OutputKeys.METHOD, "html");
			transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			writer.write("<!DOCTYPE html>\n");
			writer.flush();
			transformerHandler.startDocument();
			transformerHandler.startElement("", "", "html", new AttributesImpl());
			transformerHandler.startElement("", "", "head", new AttributesImpl());
			transformerHandler.startElement("", "", "title", new AttributesImpl());
			transformerHandler.characters("Hello".toCharArray(), 0, 5);
			transformerHandler.endElement("", "", "title");
			transformerHandler.endElement("", "", "head");
			transformerHandler.startElement("", "", "body", new AttributesImpl());
			transformerHandler.startElement("", "", "p", new AttributesImpl());
			transformerHandler.characters("5 > 3".toCharArray(), 0, 5);
			transformerHandler.endElement("", "", "p");
			// note '>' character

			// load with snakeyaml
			in = Files.newInputStream(Paths.get(fileName));
			@SuppressWarnings("unchecked")
			ArrayList<LinkedHashMap<Object, Object>> members = (ArrayList<LinkedHashMap<Object, Object>>) new Yaml()
					.load(in);
			System.err.println(
					String.format("Loaded %d members of the group", members.size()));
			for (LinkedHashMap<Object, Object> row : members) {
				transformerHandler.startElement("", "", "tr", new AttributesImpl());
				System.err.println(String.format("Loaded %d propeties of the artist",
						row.keySet().size()));
				jsonString = ouputObjectMapper.writeValueAsString(row.values());
				System.err.println(jsonString);
				// Artist artist = (Artist) row;
				for (Object key : row.keySet()) {
					transformerHandler.startElement("", "", "td", new AttributesImpl());
					if (row.get(key) != null) {
						String value = row.get(key).toString();
						transformerHandler.characters(value.toCharArray(), 0,
								value.length());
					}
					transformerHandler.endElement("", "", "td");
				}
				transformerHandler.endElement("", "", "tr");
			}
			transformerHandler.endElement("", "", "body");
			transformerHandler.endElement("", "", "html");
			transformerHandler.endDocument();
			writer.close();
		} catch (IOException e) {
		} catch (TransformerConfigurationException e) {
		} catch (SAXException e) {

		}
	}

	// https://www.programcreek.com/java-api-examples/?api=com.fasterxml.jackson.dataformat.yaml.snakeyaml.Yaml
	@Test(enabled = true)
	public void testLoadModernYAMLWithSnakeAndJackson() {
		String fileName = buildPathtoResourceFile("anchor_reference.yaml");
		InputStream in;
		try {
			// load with snakeyaml
			in = Files.newInputStream(Paths.get(fileName));
			Map<String, Object> data = (Map<String, Object>) new Yaml().load(in);
			Map<String, Object> userData = (Map<String, Object>) data.get("user");
			ArrayList<String> roles = (ArrayList<String>) userData.get("roles");
			User user = new User((String) userData.get("name"),
					(String) userData.get("familyname"), (int) userData.get("age"),
					(Map<String, Object>) userData.get("address"),
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

	@Test(enabled = false)
	public void testLoadYAML() {

		try {
			User user = inputObjectMapper.readValue(
					new File(buildPathtoResourceFile(dataFileName)), User.class);
			assertThat(user.getName(), is("Test User"));
			// Expected: null
			// but: was "~"
			assertThat(user.getAddress().get("line2"), nullValue());
			assertThat(user.getRoles().length, greaterThan(1));
			System.err.println("testLoadYAML:\n" + ReflectionToStringBuilder
					.toString(user, ToStringStyle.MULTI_LINE_STYLE));
		} catch (Exception e) {
			System.err.println("Exception (ignored): " + e.toString());
		}
	}

	private static final String buildPathtoResourceFile(String fileName) {
		return String.join(System.getProperty("file.separator"), Arrays.asList(
				System.getProperty("user.dir"), "src", "test", "resources", fileName));
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

		// default constructor needed for jackson
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

	private static class Artist {

		private String name;
		private String plays;
		private int id;

		public String getName() {
			return name;
		}

		public void setName(String data) {
			this.name = data;
		}

		public String getPlays() {
			return plays;
		}

		public void setPlays(String data) {
			this.plays = data;
		}

		public int getId() {
			return id;
		}

		public void setId(int data) {
			this.id = data;
		}

		public Artist() {

		}

		public Artist(String name, String role, int id) {
			super();
			this.name = name;
			this.id = id;
			this.plays = plays;
		}
	}

}
