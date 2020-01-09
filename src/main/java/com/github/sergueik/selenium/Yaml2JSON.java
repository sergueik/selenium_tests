package com.github.sergueik.selenium;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.yaml.snakeyaml.Yaml;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class Yaml2JSON {

	private final static boolean directConvertrsion = true;

	private static Gson gson = directConvertrsion ? new Gson()
			: new GsonBuilder()
					.registerTypeAdapter(Artist.class, new ArtistSerializer()).create();

	public static void main(String[] argv) throws Exception {
		String fileName = "#{yaml_file}";

		String encoding = "UTF-8";
		List<String> group = new ArrayList<>();
		try {
			FileOutputStream fos = new FileOutputStream("#{report}");
			OutputStreamWriter writer = new OutputStreamWriter(fos, encoding);

			InputStream in = Files.newInputStream(Paths.get(fileName));
			@SuppressWarnings("unchecked")
			ArrayList<LinkedHashMap<Object, Object>> members = (ArrayList<LinkedHashMap<Object, Object>>) new Yaml()
					.load(in);
			ArtistSerializer serializer = new ArtistSerializer();
			System.out.println(
					String.format("Loaded %d members of the group", members.size()));
			for (LinkedHashMap<Object, Object> row : members) {
				System.out.println(String.format("Loaded %d propeties of the artist",
						row.keySet().size()));
				Artist artist = new Artist((int) row.get("id"),
						(String) row.get("name"), (String) row.get("plays"));

				JsonElement rowJson = serializer.serialize(artist, null, null);
				String rowStr = gson.toJson(artist);
				group.add(rowStr);
				System.err
						.println("JSON serialization with gson:\n" + rowJson.toString());

			}
			writer.close();
		} catch (IOException e) {
			System.err.println("Excption (ignored) " + e.toString());
		}
	}

	// https://stackoverflow.com/questions/11038553/serialize-java-object-with-gson
	public static class ArtistSerializer implements JsonSerializer<Artist> {
		@Override
		public JsonElement serialize(final Artist data, final Type type,
				final JsonSerializationContext context) {
			JsonObject result = new JsonObject();
			int id = data.getId();
			if (id != 0) {
				result.add("id", new JsonPrimitive(id));
			}
			// added static info from the serialized class
			result.add("staticInfo", new JsonPrimitive(Artist.getStaticInfo()));

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
	}

	public static class Artist {

		private String name;
		private String plays;
		private static String staticInfo;
		private int id;

		@SuppressWarnings("unused")
		public String getName() {
			return name;
		}

		@SuppressWarnings("unused")
		public void setName(String data) {
			this.name = data;
		}

		@SuppressWarnings("unused")
		public String getPlays() {
			return plays;
		}

		@SuppressWarnings("unused")
		public void setPlays(String data) {
			this.plays = data;
		}

		@SuppressWarnings("unused")
		public int getId() {
			return id;
		}

		@SuppressWarnings("unused")
		public void setId(int data) {
			this.id = data;
		}

		@SuppressWarnings("unused")
		public Artist() {
			staticInfo = UUID.randomUUID().toString();
		}

		@SuppressWarnings("unused")
		public static String getStaticInfo() {
			return staticInfo;
		}

		public Artist(int id, String name, String plays) {
			super();
			this.name = name;
			this.id = id;
			this.plays = plays;
		}

	}
}
