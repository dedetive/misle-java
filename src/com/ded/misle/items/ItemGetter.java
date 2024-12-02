package com.ded.misle.items;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.ded.misle.items.ItemLoader.loadItems;


public class ItemGetter {

	public enum ParameterKey {
		ID,
		RARITY,
		BUNDLE
	}

	public static List<ItemData> getParameterizedItems(ParameterKey key, String value) {
		List<ItemData> items = new ArrayList<>();
		List<ItemData> allItems = new ArrayList<>();
		try {
			allItems = loadItems();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (ItemData item : allItems) {
			switch (key) {
				case ID:
					if (Objects.equals(Integer.toString(item.getId()), value)) {
						items.add(item);
					}
					break;
				case RARITY:
					if (Objects.equals(item.getRarity(), value)) {
						items.add(item);
					}
					break;
				case BUNDLE:
					// Check if the item has the specified bundle key
					if (item.getBundles().containsKey(value)) {
						items.add(item);
					}
					break;
			}
		}

		return items;
	}

}
