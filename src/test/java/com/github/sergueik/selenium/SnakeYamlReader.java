package com.github.sergueik.selenium;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.BaseConstructor;

// NOTE: not really a test

public class SnakeYamlReader {
	public static void main(String[] args) {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(args[0]);
		} catch (IOException e) {
			return;
		}

		// https://www.programcreek.com/java-api-examples/org.yaml.snakeyaml.Yaml#11
		// see also:
		// https://ngeor.com/2018/11/28/yaml-schema-validation-with-maven.html
		DumperOptions dumperOptions = new DumperOptions();
		LoaderOptions loaderOptions = new LoaderOptions();
		Representer representer = new Representer(dumperOptions );
		// BaseConstructor constructor = new BaseConstructor();
		// need to implement
		// see
		// https://www.codota.com/web/assistant/code/rs/5c6569901095a500014c0d4a#L82
		loaderOptions.setAllowDuplicateKeys(false);
		dumperOptions.setDefaultFlowStyle(FlowStyle.BLOCK);
		// Yaml yaml = new Yaml(constructor, representer, dumperOptions,
		// loaderOptions);
		Yaml yaml = new Yaml(loaderOptions);
		try {
			Map<String, Object> obj = yaml.load(inputStream);

			System.out.println(obj);
		} catch (Exception e) {
			System.err.println("Exception: " + e.toString());
		}
	}
}
