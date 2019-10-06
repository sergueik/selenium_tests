package com.github.sergueik.selenium;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.annotations.Test;

import org.yaml.snakeyaml.Yaml;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

public class Yaml2JSONTest {

	private final static boolean directConvertrsion = true;

	private static Gson gson = directConvertrsion ? new Gson()
			: new GsonBuilder().registerTypeAdapter(Artist.class, new ArtistSerializer()).create();

	// OK for reporting of compound rowsets of data,
	// but does not assemble an array of JSON correctly
	@Test(enabled = true)
	public void conversionWrongTest() throws Exception {

		String fileName = "group.yaml";

		List<String> group = new ArrayList<>();
		try {
			InputStream in = Files.newInputStream(Paths.get(String.join(System.getProperty("file.separator"),
					Arrays.asList(System.getProperty("user.dir"), "src", "test", "resources", fileName))));
			@SuppressWarnings("unchecked")
			ArrayList<LinkedHashMap<Object, Object>> members = (ArrayList<LinkedHashMap<Object, Object>>) new Yaml()
					.load(in);
			ArtistSerializer serializer = new ArtistSerializer();
			for (LinkedHashMap<Object, Object> row : members) {
				Artist artist = new Artist((int) row.get("id"), (String) row.get("name"), (String) row.get("plays"));

				JsonElement rowJson = serializer.serialize(artist, null, null);
				String rowStr = gson.toJson(artist);
				group.add(rowStr);
				System.err.println("JSON serialization of one row:\n" + rowJson.toString());
			}
		} catch (IOException e) {
			System.err.println("Excption (ignored) " + e.toString());
		}
	}

	@Test(enabled = false)
	public void conversionCorrectedTest() throws Exception {
		String fileName = "group.yaml";
		String encoding = "UTF-8";
		List<JsonElement> group = new ArrayList<>();
		try {
			FileOutputStream fos = new FileOutputStream("report.json");
			OutputStreamWriter writer = new OutputStreamWriter(fos, encoding);

			InputStream in = Files.newInputStream(Paths.get(String.join(System.getProperty("file.separator"),
					Arrays.asList(System.getProperty("user.dir"), "src", "test", "resources", fileName))));
			@SuppressWarnings("unchecked")
			ArrayList<LinkedHashMap<Object, Object>> members = (ArrayList<LinkedHashMap<Object, Object>>) new Yaml()
					.load(in);
			ArtistSerializer serializer = new ArtistSerializer();
			for (LinkedHashMap<Object, Object> row : members) {
				Artist artist = new Artist((int) row.get("id"), (String) row.get("name"), (String) row.get("plays"));

				// TODO: refacor avoiding need to explicitly set the hard to
				// instantiate
				// argument of the type java.reflection.Type to expliticly
				// invoke
				// serialize
				// https://www.programcreek.com/java-api-examples/index.php?api=com.google.gson.JsonSerializer
				JsonElement rowJson = serializer.serialize(artist, null, null);
				group.add(rowJson);
				System.err.println("JSON serialization or artist:\n" + rowJson.toString());

			}
			System.err.println("JSON serialization or one group:\n" + gson.toJson(group));
			writer.write(gson.toJson(group));
			writer.flush();
			writer.close();
		} catch (IOException e) {
			System.err.println("Excption (ignored) " + e.toString());
		}
	}

	@Test(enabled = true)
	public void conversionRefactoredTest() throws Exception {
		String fileName = "group.yaml";
		String encoding = "UTF-8";
		List<JsonElement> group = new ArrayList<>();
		try {
			FileOutputStream fos = new FileOutputStream("report.json");
			OutputStreamWriter writer = new OutputStreamWriter(fos, encoding);

			InputStream in = Files.newInputStream(Paths.get(String.join(System.getProperty("file.separator"),
					Arrays.asList(System.getProperty("user.dir"), "src", "test", "resources", fileName))));
			@SuppressWarnings("unchecked")
			ArrayList<LinkedHashMap<Object, Object>> members = (ArrayList<LinkedHashMap<Object, Object>>) new Yaml()
					.load(in);

			for (LinkedHashMap<Object, Object> row : members) {
				Artist artist = new Artist((int) row.get("id"), (String) row.get("name"), (String) row.get("plays"));

				// https://www.programcreek.com/java-api-examples/index.php?api=com.google.gson.JsonSerializer
				Gson gson = new GsonBuilder().registerTypeAdapter(Artist.class, new JsonSerializer<Artist>() {
					@Override
					public JsonElement serialize(final Artist data, final Type type,
							final JsonSerializationContext context) {
						JsonObject result = new JsonObject();
						int id = data.getId();
						if (id != 0) {
							result.add("id", new JsonPrimitive(id));
						}

						@SuppressWarnings("unused")
						String name = data.getName();
						// filter what to (not) serialize

						String plays = data.getPlays();
						if (plays != null && !plays.isEmpty()) {
							result.add("plays", new JsonPrimitive(plays));
						}
						return result;
					}
				}).setFieldNamingStrategy(new FieldNamingStrategy() {
					Pattern iPattern = Pattern.compile("i([A-Z])(.*)");

					@Override
					public String translateName(Field f) {
						Matcher matcher = iPattern.matcher(f.getName());
						if (matcher.matches())
							return matcher.group(1).toLowerCase() + matcher.group(2);
						else
							return f.getName();
					}
				}).setPrettyPrinting().create();
				JsonElement rowJson = gson.toJsonTree(artist, Artist.class);

				group.add(rowJson);
				System.err.println("JSON serialization or artist:\n" + rowJson.toString());

			}
			System.err.println("JSON serialization or one group:\n" + gson.toJson(group));
			writer.write(gson.toJson(group));
			writer.flush();
			writer.close();
		} catch (IOException e) {
			System.err.println("Excption (ignored) " + e.toString());
		}
	}

	// origin:
	// https://www.thejavaprogrammer.com/convert-json-to-xml-or-xml-to-json-in-java/
	// https://chillyfacts.com/java-program-convert-json-xml/
	@Test(enabled = true)
	public void conversionJSONXMLTest() {
		// String xmlInData = "<student><name>Neeraj Mishra</name><age>22</age></student>";
		// https://readlearncode.com/microservices/tomcat-server-xml-example/
		String xmlInData = "<?xml version='1.0' encoding='utf-8'?><Server> <Service name='Catalina'> <Engine name='Catalina' defaultHost='localhost'> <Host name='localhost' appBase='webapps' unpackWARs='true' autoDeploy='true'> <Valve className='org.apache.catalina.valves.AccessLogValve' directory='logs' /> </Host> </Engine> </Service> </Server>";
		String xmlOut = null;
		String jsonData = null;
		// convert XML to JSON
		JSONObject jsonObj;
		try {
			jsonObj = XML.toJSONObject(xmlInData);
			jsonData = jsonObj.toString();
			System.err.println("JSON data: " + jsonData);
		} catch (JSONException e) {
			System.err.println("Exception (ignored): " + e.toString());
		}

		try {
			jsonObj = new JSONObject(jsonData);
			xmlOut = XML.toString(jsonObj);
			System.err.println("XML data: " + xmlOut);
			
		} catch (JSONException e) {
			System.err.println("Exception (ignored): " + e.toString());
		}
	}

	// https://stackoverflow.com/questions/11038553/serialize-java-object-with-gson
	public static class ArtistSerializer implements JsonSerializer<Artist> {
		@Override
		public JsonElement serialize(final Artist data, final Type type, final JsonSerializationContext context) {
			JsonObject result = new JsonObject();
			int id = data.getId();
			if (id != 0) {
				result.add("id", new JsonPrimitive(id));
			}
			// added static info from the serialized class
			// NPE
			if (type != null) {
				result.add("staticInfo", new JsonPrimitive(((Artist) type).getStaticInfo()));
			} else {
				String staticInfo = data.getStaticInfo();
				System.err.println("Static info: " + staticInfo);
				if (staticInfo != null) {
					result.add("staticInfo", new JsonPrimitive(staticInfo));
				}
			}

			@SuppressWarnings("unused")
			String name = data.getName();
			// filter what to (not) serialize

			String plays = data.getPlays();
			if (plays != null && !plays.isEmpty()) {
				result.add("plays", new JsonPrimitive(plays));
			}
			/*
			 * Float price = data.getPrice(); result.add("price", new
			 * JsonPrimitive(price));
			 */
			return result;
		}
	}

	public static class Artist {

		private String name;
		private String plays;
		private static String staticInfo;
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
ar
		public Artist() {
			staticInfo = UUID.randomUUID().toString();
		}

		public /* static */ String getStaticInfo() {
			return Artist.staticInfo;
		}

		public Artist(int id, String name, String plays) {
			super();
			if (Artist.staticInfo == null) {
				Artist.staticInfo = UUID.randomUUID().toString();
			}
			this.name = name;
			this.id = id;
			this.plays = plays;
		}

	}
}
