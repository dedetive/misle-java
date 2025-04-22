package com.ded.misle.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.ded.misle.items.ItemGetter.getParameterizedItems;

public class DropTable {
	private static final ArrayList<DropTable> dropTables = new ArrayList<>();

	public static DropTable POTION_CHEST = new DropTable("potion_chest");
	public static DropTable GOBLIN = new DropTable("goblin_drop");

	public String name;

	DropTable(String dropTableName) {
		this.name = dropTableName;
		dropTables.add(this);
	}

	public static DropTable getDropTableByName(String name) {
		for (DropTable dropTable : dropTables) {
			if (dropTable.name.equals(name)) {
				return dropTable;
			}
		}
		return null;
	}

	public int[] getDropTableItemID() {
		List<ItemData> itemsInBundle = getParameterizedItems(ItemGetter.ParameterKey.BUNDLE, this.name);
		List<ItemData> weightedItems = new ArrayList<>();
		int count = 1;

		// Calculate total weight
		int totalWeight = 0;
		for (ItemData item : itemsInBundle) {
			int weight = item.getBundles().get(name);  // get weight for specific bundle
			totalWeight += weight;
			// Add each item as many times as its weight for easier random selection
			for (int i = 0; i < weight; i++) {
				weightedItems.add(item);
			}
		}

		// Select random item based on weight
		int randomIndex = new Random().nextInt(weightedItems.size());
		int maxCount = weightedItems.get(randomIndex).getBundleCount().get(name);
		if (maxCount > 1) {
			count = (int) ((Math.random() * (maxCount - 1)) + 1);
		}
		return new int[]{weightedItems.get(randomIndex).getId(), count};
	}
}
