package com.ded.misle.chests;

import com.ded.misle.items.ItemData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.ded.misle.items.ItemGetter.getParameterizedItems;

public class ChestTables {
	public static int getChestDropID(String chestType) {
		List<ItemData> itemsInBundle = getParameterizedItems("bundle", chestType);
		List<ItemData> weightedItems = new ArrayList<>();

		// Calculate total weight
		int totalWeight = 0;
		for (ItemData item : itemsInBundle) {
			int weight = item.getBundles().get(chestType);  // get weight for specific bundle
			totalWeight += weight;
			// Add each item as many times as its weight for easier random selection
			for (int i = 0; i < weight; i++) {
				weightedItems.add(item);
			}
		}

		// Select random item based on weight
		int randomIndex = new Random().nextInt(weightedItems.size());
		return weightedItems.get(randomIndex).getId();
	}
}
