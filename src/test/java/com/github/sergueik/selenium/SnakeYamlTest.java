package com.github.sergueik.selenium;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;

public class SnakeYamlTest {
	public static void main(String[] args) throws IOException {

		InputStream inputStream = new FileInputStream(args[0]);

		// https://www.programcreek.com/java-api-examples/org.yaml.snakeyaml.Yaml#11
		DumperOptions dumperOptions = new DumperOptions();
		dumperOptions.setDefaultFlowStyle(FlowStyle.BLOCK);
		Yaml yaml = new Yaml(dumperOptions);
		Map<String, Object> obj = yaml.load(inputStream);

		System.out.println(obj);
	}
}
