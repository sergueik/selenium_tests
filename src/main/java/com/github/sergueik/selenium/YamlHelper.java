package com.github.sergueik.selenium;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

/**
 * Common utilities for YAML configuration file for Jsoup and HTML::TagParser
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class YamlHelper {

	private static DumperOptions options = new DumperOptions();
	private static Yaml yaml = null;
	private static Calendar calendar;
	private static String yamlFile = null;
	private static boolean debug = true;

	public void setYamlFile(String data) {
		this.yamlFile = data;
	}

	public String getYamlFile() {
		return yamlFile;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Map<String, List<String>>> loadData(
			String fileName) {
		if (yaml == null) {
			options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
			yaml = new Yaml(options);
		}

		Map<String, Map<String, List<String>>> data = new HashMap<>();
		try (InputStream in = Files.newInputStream(Paths.get(fileName))) {
			// NOTE: unchecked conversion
			// required: Map<String,Map<String,List<String>>>
			// found: capture#1 of ? extends java.util.Map
			data = yaml.loadAs(in, data.getClass());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	// TODO: too constrainded
	public static Configuration loadConfiguration(String fileName) {
		if (yaml == null) {
			options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
			yaml = new Yaml(options);
		}
		Configuration config = null;
		try (InputStream in = Files.newInputStream(Paths.get(fileName))) {
			config = yaml.loadAs(in,
					com.github.sergueik.selenium.Configuration.class);
			// TODO: better method naming
			YamlHelper.saveConfiguration(config);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return config;
	}

	public static void printConfiguration(Object config) {
		saveConfiguration(config);
	}

	public static void saveConfiguration(Object config) {
		saveConfiguration(config, null);
	}

	@SuppressWarnings("deprecation")
	public static void saveConfiguration(Object config, String fileName) {
		if (yaml == null) {
			options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
			yaml = new Yaml(options);
		}
		/*
		String pattern = "yyyy-MM-dd";
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		Date date = new Date();
		try {
			config.updated = format.parse(String.format("%4d-%2d-%2d", date.getYear(),
					date.getMonth(), date.getDay()));
		} catch (java.text.ParseException e) {
			config.updated = date;
		}
		*/
		calendar = new GregorianCalendar();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				((SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT,
						Locale.US)).toPattern().replaceAll("\\byy\\b", "yyyy")
								.replaceAll("\\bM\\b", "MM").replaceAll("\\bd\\b", "dd"));

		// System.err.println("Testing date format: " + dateFormat.toPattern());

		if (config instanceof com.github.sergueik.selenium.Configuration) {
			try {
				System.err.println("Adding save date: "
						+ dateFormat.parse(dateFormat.format(calendar.getTime())));
				((Configuration) config).setUpdated(
						dateFormat.parse(dateFormat.format(calendar.getTime())));
			} catch (java.text.ParseException e) {
				System.err.println("Ignoring date parse exception: " + e.toString());
				((com.github.sergueik.selenium.Configuration) config)
						.setUpdated(new Date());
			}
		}
		if (fileName != null) {
			try {
				Writer out = new OutputStreamWriter(new FileOutputStream(fileName),
						"UTF8");
				System.err.println("Dumping the config to: " + fileName);

				yaml.dump(config, out);
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.err.println("Dumping the config: \n" + yaml.dump(config));
		}
	}

}