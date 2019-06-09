package com.github.sergueik.selenium;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import com.github.sergueik.selenium.ObjectItem;

//https://stackoverflow.com/questions/11038553/serialize-java-object-with-gson
public class ObjectItemSerializer implements JsonSerializer<ObjectItem> {
	@Override
	public JsonElement serialize(final ObjectItem data, final Type type,
			final JsonSerializationContext context) {
		JsonObject result = new JsonObject();
		String id = data.getId();
		if (id != null && !id.isEmpty()) {
			result.add("id", new JsonPrimitive(id));
		}
		// added static info from the serialized class
		result.add("staticInfo", new JsonPrimitive(ObjectItem.getStaticInfo()));

		String url = data.getUrl();
		// filter what to (not) serialize
		/*
		if (url != null && !url.isEmpty()) {
			result.add("url", new JsonPrimitive(url));
		}
		*/
		String title = data.getTitle();
		if (title != null && !title.isEmpty()) {
			result.add("title", new JsonPrimitive(title));
		}
		Float price = data.getPrice();
		result.add("price", new JsonPrimitive(price));
		return result;
	}
}
