package com.ded.misle.items;

import java.util.Map;

public class ItemData {
	private final int id;
	private final String name;
	private final String description;
	private final String rarity;
	private final String type;
	private final int resourceID;
	private final Map<String, Object> attributes;

	public ItemData(int id, String name, String description, String rarity, String type, int resourceID, Map<String, Object> attributes) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.rarity = rarity;
		this.type = type;
		this.resourceID = resourceID;
		this.attributes = attributes;
	}

	public int getId() { return id; }
	public String getName() { return name; }
	public String getDescription() { return description; }
	public String getRarity() { return rarity; }
	public String getType() { return type; }
	public int getResourceID() { return resourceID; }
	public Map<String, Object> getAttributes() { return attributes; }
}
