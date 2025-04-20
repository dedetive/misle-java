package com.ded.misle.world.chests;

import com.ded.misle.items.ItemData;
import com.ded.misle.items.ItemGetter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.ded.misle.items.ItemGetter.getParameterizedItems;

public class DropTable {
	public static DropTable POTION_CHEST = new DropTable("potion_chest");
	public static DropTable GOBLIN = new DropTable("goblin");

	String dropTableName;

	DropTable(String dropTableName) {
		this.dropTableName = dropTableName;
	}

	public int[] getDropTableItemID() {
		List<ItemData> itemsInBundle = getParameterizedItems(ItemGetter.ParameterKey.BUNDLE, this.dropTableName);
		List<ItemData> weightedItems = new ArrayList<>();
		int count = 1;

		// Calculate total weight
		int totalWeight = 0;
		for (ItemData item : itemsInBundle) {
			int weight = item.getBundles().get(dropTableName);  // get weight for specific bundle
			totalWeight += weight;
			// Add each item as many times as its weight for easier random selection
			for (int i = 0; i < weight; i++) {
				weightedItems.add(item);
			}
		}

		// Select random item based on weight
		int randomIndex = new Random().nextInt(weightedItems.size());
		int maxCount = weightedItems.get(randomIndex).getBundleCount().get(dropTableName);
		if (maxCount > 1) {
			count = (int) ((Math.random() * (maxCount - 1)) + 1);
		}
		return new int[]{weightedItems.get(randomIndex).getId(), count};
	}
}
