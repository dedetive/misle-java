package com.ded.misle.input;

import java.awt.event.KeyEvent;
import java.util.*;

public class KeyRegistry {
	private static final List<Key> keys = new ArrayList<>();
	private static final Map<Integer, List<Key>> keyMap = new HashMap<>();

	public static void addKey(Key key) {
		if (keys.stream().noneMatch(existing -> existing.equals(key))) {
			keys.add(key);
			keyMap.computeIfAbsent(key.keyCode(), k -> new ArrayList<>()).add(key);
		}
	}

	public static void removeKey(Key key) {
		keys.remove(key);
		List<Key> mappedKeys = keyMap.get(key.keyCode());
		if (mappedKeys != null) {
			mappedKeys.remove(key);
			if (mappedKeys.isEmpty()) keyMap.remove(key.keyCode());
		}
	}

	public static void trigger(KeyEvent keyEvent, KeyInputType inputType) {
		List<Key> mappedKeys = keyMap.getOrDefault(keyEvent.getKeyCode(), List.of());
		for (Key key : mappedKeys) {
			if (key.keyInputType() != inputType) continue;
			if (key.onCooldown()) continue;

			key.lastTimeActivated = System.currentTimeMillis();
			key.action().execute();
		}
	}

	public static <T> void trigger(KeyEvent keyEvent, KeyInputType inputType, T parameter) {
		List<Key> mappedKeys = keyMap.getOrDefault(keyEvent.getKeyCode(), List.of());
		for (Key key : mappedKeys) {
			if (key.keyInputType() != inputType) continue;
			if (key.onCooldown()) continue;

			key.lastTimeActivated = System.currentTimeMillis();
			key.action().execute(parameter);
		}
	}
}
