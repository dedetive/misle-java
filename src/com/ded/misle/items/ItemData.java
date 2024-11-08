package com.ded.misle.items;

import java.awt.*;
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
	private final Color nameColor;

	public ItemData(int id, String name, int countLimit, String rarity, String type, int resourceID, Map<String, Object> attributes, Map<String, Integer> bundles, Color nameColor) {
		this.id = id;
		this.name = name;
		this.countLimit = countLimit;
		this.rarity = rarity;
		this.type = type;
		this.resourceID = resourceID;
		this.attributes = attributes;
		this.bundles = bundles;
		this.nameColor = nameColor;
	}

	public int getId() { return id; }
	public String getName() { return name; }
	public int countLimit() { return countLimit; }
	public String getRarity() { return rarity; }
	public String getType() { return type; }
	public int getResourceID() { return resourceID; }
	public Map<String, Object> getAttributes() { return attributes; }
	public Map<String, Integer> getBundles() { return bundles; }
	public Color getNameColor() { return nameColor; }
}
