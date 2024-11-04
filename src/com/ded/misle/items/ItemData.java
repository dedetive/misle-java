package com.ded.misle.items;

import java.util.Map;

public class ItemData {
	private final int id;
	private final String name;
	private final int countLimit;
	private final String rarity;
	private final String type;
	private final int resourceID;
	private final Map<String, Object> attributes;
	private final Map<String, Integer> bundles;

	public ItemData(int id, String name, int countLimit, String rarity, String type, int resourceID, Map<String, Object> attributes, Map<String, Integer> bundles) {
		this.id = id;
		this.name = name;
		this.countLimit = countLimit;
		this.rarity = rarity;
		this.type = type;
		this.resourceID = resourceID;
		this.attributes = attributes;
		this.bundles = bundles;
	}

	public int getId() { return id; }
	public String getName() { return name; }
	public int countLimit() { return countLimit; }
	public String getRarity() { return rarity; }
	public String getType() { return type; }
	public int getResourceID() { return resourceID; }
	public Map<String, Object> getAttributes() { return attributes; }
	public Map<String, Integer> getBundles() { return bundles; }
}
