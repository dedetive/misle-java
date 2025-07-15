package com.ded.misle.world.data.items;

import java.io.IOException;
import java.util.*;

import static com.ded.misle.world.data.items.ItemLoader.loadItems;

public class ItemGetter {

	public enum ParameterKey {
		ID,
		RARITY,
		BUNDLE
	}

	private static final Map<CacheKey, List<ItemData>> cache = new HashMap<>();

	public static List<ItemData> getParameterizedItems(ParameterKey key, String value) {
		CacheKey cacheKey = new CacheKey(key, value);
		if (cache.containsKey(cacheKey)) {
			return cache.get(cacheKey);
		}

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
					if (item.getBundles().containsKey(value)) {
						items.add(item);
					}
					break;
			}
		}

		cache.put(cacheKey, items);
		return items;
	}

	private record CacheKey(ParameterKey key, String value) {}
}