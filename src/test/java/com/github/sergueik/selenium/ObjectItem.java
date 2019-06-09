package com.github.sergueik.selenium;

import java.util.UUID;

public class ObjectItem {
	private String title;
	private Float price;
	private String url;
	private String id = null;

	public String getTitle() {
		return title;
	}

	public void setId(String data) {
		this.id = data;
	}

	public String getId() {
		return id;
	}

	public void setTitle(String data) {
		this.title = data;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float data) {
		this.price = data;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String data) {
		this.url = data;
	}

	private static final String staticInfo = "static info";

	public static String getStaticInfo() {
		return staticInfo;
	}

	public ObjectItem() {
		id = UUID.randomUUID().toString();
	}

}
