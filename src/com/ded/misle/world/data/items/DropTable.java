package com.ded.misle.world.data.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.ded.misle.world.data.items.ItemGetter.getParameterizedItems;

public record DropTable(String name, List<ItemData> items) {
	private static final ArrayList<DropTable> dropTables = new ArrayList<>();

	public static final DropTable POTION_CHEST = new DropTable("potion_chest");
	public static final DropTable GOBLIN = new DropTable("goblin_drop");
	public static final DropTable MUNI = new DropTable("muni_drop");

	public DropTable(String name, List<ItemData> items) {
		this.name = name;
		this.items = items;
	}

	public DropTable(String dropTableName) {
		this(dropTableName, getParameterizedItems(ItemGetter.ParameterKey.BUNDLE, dropTableName));
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

	public List<ItemData> getItemDatas() {
		return items();
	}

	public int[] getAllIDs() {
		int[] ids = new int[items().size()];
		for (int i = 0; i < items().size(); i++) {
			ids[i] = items().get(i).getId();
		}

		return ids;
	}

	public String[] getAllItemNames() {
		String[] names = new String[items().size()];
		for (int i = 0; i < items().size(); i++) {
			names[i] = items().get(i).getName();
		}

		return names;
	}

	/**
	 *
	 * @return an array containing the selected randomized ID and its randomized count.
	 */
	public int[] getRandomItemID() {
		List<ItemData> weightedItems = new ArrayList<>();
		int count = 1;

		// Calculate total weight
		int totalWeight = 0;
		for (ItemData item : items()) {
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

	@Override
	public String toString() {
		return "DropTable{" +
			"name=" + name +
			", size=" + items().size() +
			", ids=" + Arrays.toString(getAllIDs()) +
//			", items=\n" + Arrays.toString(getAllItemNames()) +
			"}";
	}
}
