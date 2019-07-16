package com.github.sergueik.selenium;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.Test;
import org.yaml.snakeyaml.Yaml;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class Yaml2JSONTest {

	private final static boolean directConvertrsion = true;

	private static Gson gson = directConvertrsion ? new Gson()
			: new GsonBuilder()
					.registerTypeAdapter(Artist.class, new ArtistSerializer()).create();

	// OK for reporting of compound rowsets of data,
	// but does not assemble an array of JSON correctly
	@Test(enabled = true)
	public void conversionWrongTest() throws Exception {

		String fileName = "group.yaml";

		String encoding = "UTF-8";
		List<String> group = new ArrayList<>();
		try {
			InputStream in = Files.newInputStream(
					Paths.get(String.join(System.getProperty("file.separator"),
							Arrays.asList(System.getProperty("user.dir"), "src", "test",
									"resources", fileName))));
			@SuppressWarnings("unchecked")
			ArrayList<LinkedHashMap<Object, Object>> members = (ArrayList<LinkedHashMap<Object, Object>>) new Yaml()
					.load(in);
			ArtistSerializer serializer = new ArtistSerializer();
			for (LinkedHashMap<Object, Object> row : members) {
				Artist artist = new Artist((int) row.get("id"),
						(String) row.get("name"), (String) row.get("plays"));

				JsonElement rowJson = serializer.serialize(artist, null, null);
				String rowStr = gson.toJson(artist);
				group.add(rowStr);
				System.err
						.println("JSON serialization of one row:\n" + rowJson.toString());
			}
		} catch (IOException e) {
			System.err.println("Excption (ignored) " + e.toString());
		}
	}

	@Test(enabled = true)
	public void conversionCorrectedTest() throws Exception {
		String fileName = "group.yaml";
		String encoding = "UTF-8";
		List<JsonElement> group = new ArrayList<>();
		try {
			FileOutputStream fos = new FileOutputStream("report.json");
			OutputStreamWriter writer = new OutputStreamWriter(fos, encoding);

			InputStream in = Files.newInputStream(
					Paths.get(String.join(System.getProperty("file.separator"),
							Arrays.asList(System.getProperty("user.dir"), "src", "test",
									"resources", fileName))));
			@SuppressWarnings("unchecked")
			ArrayList<LinkedHashMap<Object, Object>> members = (ArrayList<LinkedHashMap<Object, Object>>) new Yaml()
					.load(in);
			ArtistSerializer serializer = new ArtistSerializer();
			for (LinkedHashMap<Object, Object> row : members) {
				Artist artist = new Artist((int) row.get("id"),
						(String) row.get("name"), (String) row.get("plays"));

				// TODO: provide accurate java.reflection.Type argument to serialize
				/// https://www.programcreek.com/java-api-examples/index.php?api=com.google.gson.JsonSerializer
				// JsonElement rowJson = serializer.serialize(artist, null, null);
				JsonElement rowJson = serializer.serialize(artist, null, null);
				group.add(rowJson);
				System.err
						.println("JSON serialization or artist:\n" + rowJson.toString());

			}
			System.err
					.println("JSON serialization or one group:\n" + gson.toJson(group));
			writer.write(gson.toJson(group));
			writer.flush();
			writer.close();
		} catch (IOException e) {
			System.err.println("Excption (ignored) " + e.toString());
		}
	}

	// https://stackoverflow.com/questions/11038553/serialize-java-object-with-gson
	public static class ArtistSerializer implements JsonSerializer<Artist> {
		@SuppressWarnings("static-access")
		@Override
		public JsonElement serialize(final Artist data, final Type type,
				final JsonSerializationContext context) {
			JsonObject result = new JsonObject();
			int id = data.getId();
			if (id != 0) {
				result.add("id", new JsonPrimitive(id));
			}
			// added static info from the serialized class
			// NPE
			if (type != null) {
				result.add("staticInfo",
						new JsonPrimitive(((Artist) type).getStaticInfo()));
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
			Float price = data.getPrice();
			result.add("price", new JsonPrimitive(price));
			*/
			return result;
		}
		// TODO: java.lang.ClassCastException: java.lang.Class cannot be cast to
		// com.github.sergueik.selenium.Yaml2JSONTest$Artist
		/*
				public JsonElement serialize(final Artist data,
						final JsonSerializationContext context) {
					// return serialize(data, new TypeVariable<String>(), context);
				}
		*/
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
