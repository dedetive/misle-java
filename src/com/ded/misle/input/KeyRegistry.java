package com.ded.misle.input;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
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
		trigger(keyEvent.getKeyCode(), inputType);
	}

	public static void trigger(int keyCode, KeyInputType inputType) {
		trigger(keyCode, inputType, null);
	}

	public static void trigger(int keyCode, KeyInputType inputType, MouseEvent mouseEvent) {
		List<Key> mappedKeys = keyMap.getOrDefault(keyCode, List.of());
		for (Key key : mappedKeys) {
			if (key.keyInputType() != inputType) continue;
			if (key.onCooldown()) continue;

			if (!dependenciesSatisfied(key)) continue;

			if (mouseEvent != null) {
				System.out.println(key.getInputIdentifier().checkValidity(mouseEvent));
				if (!key.getInputIdentifier().checkValidity(mouseEvent)) return;
			}

			key.recountCooldown();
			boolean earlyReturn = key.mayConflict() && key.action().canExecute(key.parameter());
			if (key.parameter() != null) key.action().execute(key.parameter());
			else key.action().execute();
			if (earlyReturn) return;
		}
	}

	public static boolean isValid(int keyCode) {
		return keyMap.containsKey(keyCode);
	}

	private static boolean dependenciesSatisfied(Key key) {
		if (key.dependencies() == null || key.dependencies().isEmpty()) return true;

		for (int requiredKey : key.dependencies()) {
			if (requiredKey > 0 && !InputHandler.isHeld(requiredKey)) return false;
			else if (requiredKey < 0 && InputHandler.isHeld(Math.abs(requiredKey))) return false;
		}
		return true;
	}

	public static List<Key> getKeys() {
		return keys;
	}
}
