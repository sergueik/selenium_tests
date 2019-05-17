package com.github.sergueik.selenium;
/**
 * Copyright 2019 Serguei Kouzmine
 */

import static java.lang.String.format;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Test configuration serializer class for Selenium WebDriver Elementor Tool (SWET)
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

final class Configuration {
	public Date created;
	public Date updated;
	public List<String> columns;
	public Map<String, Map<String, List<String>>> elements;
	public Map<String, String> plugins;

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date data) {
		this.created = data;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date data) {
		this.updated = data;
	}

	public List<String> getColumns() {
		return columns;
	}

	public void setColumns(List<String> data) {
		this.columns = data;
	}

	public Map<String, Map<String, List<String>>> getElements() {
		return elements;
	}

	public void setElements(Map<String, Map<String, List<String>>> data) {
		this.elements = data;
	}

	@Override
	public String toString() {
		return new StringBuilder().append(format("Created: %s\n", created))
				.append(format("Columns: %s\n", columns))
				.append(format("Elements: %s\n", elements)).toString();
	}
}
