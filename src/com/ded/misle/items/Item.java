package com.ded.misle.items;

import com.ded.misle.LanguageManager;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Map;
import javax.imageio.ImageIO;
import java.io.IOException;

import static com.ded.misle.ChangeSettings.getPath;
import static com.ded.misle.boxes.BoxesHandling.addBoxItem;

public class Item {
	private final int id;
	private final String name;
	private String displayName;
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
			String normalizedName = itemDetails.getName().replaceAll(" ", "_").toLowerCase();
			this.displayName = LanguageManager.getText(normalizedName);
			this.description = LanguageManager.getText(normalizedName + "_DESC");
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
	public String getDisplayName() { return displayName; }
	public void setDisplayName(String displayName) { this.displayName = displayName; }
	public String getDescription() { return description; }
	public int getCountLimit() { return countLimit; }
	public String getRarity() { return rarity; }
	public String getType() { return type; }
	public Map<String, Object> getAttributes() { return attributes; }
	public int getCount() { return count; }
	public void setCount(int count) { this.count = count; }

	@Override
	public String toString() {
		return "Item{id=" + id + ", name='" + displayName + "', description='" + description + "', countLimit=" + countLimit +", rarity='" + rarity + "', type='" + type + "', attributes=" + attributes + ", count=" + count + "}";
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
			System.out.println("Can't find item texture " + filePath + "!");
			return null; // Return null if image fails to load
		}
	}

	public void setIcon(int resourceID) {
		this.resourceID = resourceID;
	}

	public static void createDroppedItem(double x, double y, int id) {
		addBoxItem(x, y, id);
	}
}
