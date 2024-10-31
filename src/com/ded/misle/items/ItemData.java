package com.ded.misle.items;

import java.util.Map;

public class ItemData {
	private final int id;
	private final String name;
	private final String type;
	private final Map<String, Object> attributes;

	public ItemData(int id, String name, String type, Map<String, Object> attributes) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.attributes = attributes;
	}

	public int getId() { return id; }
	public String getName() { return name; }
	public String getType() { return type; }
	public Map<String, Object> getAttributes() { return attributes; }
}
