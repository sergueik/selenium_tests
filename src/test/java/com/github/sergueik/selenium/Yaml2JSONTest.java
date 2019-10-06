package com.github.sergueik.selenium;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.annotations.Test;
import org.xml.sax.SAXException;
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

// convert falsely mapped child elements to attributes

import javax.xml.XMLConstants;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

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
	public void conversionJSONXMLTest() throws SAXException {
		// https://readlearncode.com/microservices/tomcat-server-xml-example/
		String xmlInData = "<?xml version='1.0' encoding='utf-8'?><Server> <Service name='Catalina'> <Engine name='Catalina' defaultHost='localhost'> <Host name='localhost' appBase='webapps' unpackWARs='true' autoDeploy='true'> <Valve className='org.apache.catalina.valves.AccessLogValve' directory='logs' /> </Host> </Engine> </Service> </Server>";
		/*
		 * input: 
		 <?xml version="1.0" encoding="utf-8"?> <Server> 
		 <Service name="Catalina"> <Engine name="Catalina" defaultHost="localhost">
		 <Host name="localhost" appBase="webapps" unpackWARs="true" autoDeploy="true"> 
		 <Valve className="org.apache.catalina.valves.AccessLogValve" directory="logs"/>
		 </Host>
		 </Engine>
		 </Service>
		 </Server>
		 */
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
			/*
			 * becomes: 
			 <?xml version="1.0" encoding="utf-8"?> <Server>
			 <Service> <name>Catalina</name> 
			 <Engine>
			 <defaultHost>localhost</defaultHost> 
			 <name>Catalina</name> <Host>
			 <autoDeploy>true</autoDeploy> 
			 <appBase>webapps</appBase>
			 <name>localhost</name> 
			 <unpackWARs>true</unpackWARs> <Valve>
			 <className>org.apache.catalina.valves.AccessLogValve</className>
			 <directory>logs</directory> 
			 </Valve> 
			 </Host> 
			 </Engine> 
			 </Service>
			 </Server>
			 */
			// http://www.java2s.com/Tutorials/Java/Java_XML/0200__Java_XSLT_Intro.htm

			// https://stackoverflow.com/questions/655411/converting-xml-elements-to-xml-attributes-using-xslt
			/*
			 <xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'> 
			 <xsl:template match='Service'>
			 <Server>
			 <xsl:apply-templates/>
			 </Server>
			 </xsl:template> 
			 <xsl:template match='Service'> <Service>
			 <xsl:for-each select='name'> <xsl:attribute name='{name()}'>
			 <xsl:value-of select='text()'/> 
			 </xsl:attribute> 
			 </xsl:for-each>
			 * </Service> </xsl:template> </xsl:stylesheet>
			 */
			String xsl = "<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'> <xsl:template match='Service'> <Server> <xsl:apply-templates/> </Server> </xsl:template> <xsl:template match='Service'> <Service> <xsl:for-each select='name'> <xsl:attribute name='{name()}'> <xsl:value-of select='text()'/> </xsl:attribute> </xsl:for-each> </Service> </xsl:template> </xsl:stylesheet> ";
			StreamSource xmlDataSource = new StreamSource(xmlOut);
			StreamSource styleSource = new StreamSource(xsl);

			// https://www.yegor256.com/2015/02/02/xsl-transformations-in-java.html
			// final XSL xslDocument = new XSLDocument(
			try {
				// Schema schema = sf.newSchema(styleSource);
				TransformerFactory factory = TransformerFactory.newInstance();
				StreamResult result = new StreamResult(System.err);
				Transformer transformer = factory.newTransformer(styleSource);
				if (transformer != null) {
					transformer.transform(xmlDataSource, result);
				}
			} catch (TransformerException e) {
				System.err.println("Exception (ignored): " + e.toString());
				//
				// e.printStackTrace();
			}
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
