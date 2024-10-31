package com.ded.misle.items;

import java.util.Map;

public class Item {
	private final int id;
	private final String name;
	private final String description;
	private final String rarity;
	private final String type;
	private final Map<String, Object> attributes; // Holds dynamic attributes
	private int count; // Changed to lowercase for conventional naming

	// Constructor that takes only ID and sets default count to 1
	public Item(int id) throws Exception {
		this.id = id;

		// Load item details from ItemLoader
		ItemData itemDetails = ItemLoader.loadItemDataById(id);
		if (itemDetails != null) {
			this.name = itemDetails.getName();
			this.description = itemDetails.getDescription();
			this.rarity = itemDetails.getRarity();
			this.type = itemDetails.getType();
			this.attributes = itemDetails.getAttributes();
		} else {
			throw new Exception("Item with ID " + id + " not found.");
		}

		this.count = 1; // Default count is set to 1
	}

	// Optional constructor to allow explicit count setting
	public Item(int id, int count) throws Exception {
		this(id); // Call the first constructor
		this.count = count; // Set count explicitly
	}

	public int getId() { return id; }
	public String getName() { return name; }
	public String getDescription() { return description; }
	public String getRarity() { return rarity; }
	public String getType() { return type; }
	public Map<String, Object> getAttributes() { return attributes; }
	public int getCount() { return count; }
	public void setCount(int count) { this.count = count; }

	@Override
	public String toString() {
		return "Item{id=" + id + ", name='" + name + "', description='" + description + "', rarity='" + rarity + "', type='" + type + "', attributes=" + attributes + ", count=" + count + "}";
	}

	public static Item createItem(int id) {
		try {
			return new Item(id); // Attempt to create an Item
		} catch (Exception e) {
			// Handle the exception
			System.err.println(e.getMessage());
			return null; // or return a default Item
		}
	}

	public static Item createItem(int id, int Count) {
		try {
			return new Item(id, Count); // Attempt to create an Item
		} catch (Exception e) {
			// Handle the exception
			System.err.println(e.getMessage());
			return null; // or return a default Item
		}
	}
}
