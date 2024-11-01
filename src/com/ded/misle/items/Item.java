package com.ded.misle.items;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Map;
import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import static com.ded.misle.ChangeSettings.getPath;

public class Item {
	private final int id;
	private final String name;
	private final String description;
	private final int countLimit;
	private final String rarity;
	private final String type;
	private int resourceID;
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
			this.countLimit = itemDetails.countLimit();
			this.rarity = itemDetails.getRarity();
			this.type = itemDetails.getType();
			this.resourceID = itemDetails.getResourceID();
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
	public int getCountLimit() { return countLimit; }
	public String getRarity() { return rarity; }
	public String getType() { return type; }
	public Map<String, Object> getAttributes() { return attributes; }
	public int getCount() { return count; }
	public void setCount(int count) { this.count = count; }

	@Override
	public String toString() {
		return "Item{id=" + id + ", name='" + name + "', description='" + description + "', countLimit=" + countLimit +", rarity='" + rarity + "', type='" + type + "', attributes=" + attributes + ", count=" + count + "}";
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

	public BufferedImage getIcon() {
		Path basePath = getPath().resolve("resources/images/items"); // Directory where images are stored
		Path filePath = basePath.resolve(resourceID + ".png");  // Assuming the icon files are named based on resourceID

		try {
			return ImageIO.read(filePath.toFile());
		} catch (IOException e) {
			e.printStackTrace(); // Log or handle if image is not found
			return null; // Return null if image fails to load
		}
	}

	public void setIcon(int resourceID) {
		this.resourceID = resourceID;
	}

}
