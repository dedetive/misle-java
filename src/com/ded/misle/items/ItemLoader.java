package com.ded.misle.items;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ded.misle.ChangeSettings.getPath;

public class ItemLoader {
	private static final Path FILE_PATH = getPath().resolve("items/items.json");

	// Load all items from the JSON file
	public static List<ItemData> loadItems() throws IOException {
		List<ItemData> items = new ArrayList<>();
		StringBuilder jsonContent = new StringBuilder();

		// Using Files.newBufferedReader for easier handling of the Path type
		try (BufferedReader reader = Files.newBufferedReader(FILE_PATH)) {
			String line;
			while ((line = reader.readLine()) != null) {
				jsonContent.append(line.trim());
			}
		}

		// Remove array brackets
		String jsonText = jsonContent.toString();
		jsonText = jsonText.substring(1, jsonText.length() - 1); // Remove "[" and "]"
		String[] itemBlocks = jsonText.split("},\\s*\\{");

		for (String block : itemBlocks) {
			block = block.replace("{", "").replace("}", "").replace("\"", "");
			int itemId = 0;
			String name = null;
			int countLimit = 0;
			String rarity = null;
			String type = null;
			int resourceID = 0;
			Map<String, Object> attributes = new HashMap<>();

			String[] pairs = block.split(",");
			for (String pair : pairs) {
				String[] keyValue = pair.split(":");
				String key = keyValue[0].trim();
				String value = keyValue[1].trim();

				switch (key) {
					case "id":
						itemId = Integer.parseInt(value);
						break;
					case "name":
						name = value;
						break;
					case "countLimit":
						countLimit = Integer.parseInt(value);
						break;
					case "rarity":
						rarity = value;
						break;
					case "type":
						type = value;
						break;
					case "resourceID":
						resourceID = Integer.parseInt(value);
						break;
					default:
						// Parse other attributes as dynamic fields
						try {
							attributes.put(key, Integer.parseInt(value));
						} catch (NumberFormatException e) {
							attributes.put(key, value);
						}
						break;
				}
			}

			if (name != null && type != null) {
				items.add(new ItemData(itemId, name, countLimit, rarity, type, resourceID, attributes)); // Store ItemData instead
			}
		}

		return items;
	}

	// Load item by ID
	public static ItemData loadItemDataById(int id) throws IOException {
		List<ItemData> items = loadItems(); // Load all items
		for (ItemData item : items) {
			if (item.getId() == id) {
				return item; // Return item details
			}
		}
		return null; // Return null if not found
	}
}
