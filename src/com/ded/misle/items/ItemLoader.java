package com.ded.misle.items;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ded.misle.ChangeSettings.getPath;

public class ItemLoader {
	private static final String FILE_PATH = getPath() + "/items/items.json";

	// Load all items from the JSON file
	public static List<ItemData> loadItems() throws IOException {
		List<ItemData> items = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH));
		StringBuilder jsonContent = new StringBuilder();

		String line;
		while ((line = reader.readLine()) != null) {
			jsonContent.append(line.trim());
		}
		reader.close();

		// Remove array brackets
		String jsonText = jsonContent.toString();
		jsonText = jsonText.substring(1, jsonText.length() - 1); // Remove "[" and "]"
		String[] itemBlocks = jsonText.split("},\\s*\\{");

		for (String block : itemBlocks) {
			block = block.replace("{", "").replace("}", "").replace("\"", "");
			int itemId = 0;
			String name = null;
			String description = null;
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
					case "description":
						description = value;
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
				items.add(new ItemData(itemId, name, description, rarity, type, resourceID, attributes)); // Store ItemData instead
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
