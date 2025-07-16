package com.ded.misle.input;

import java.awt.event.KeyEvent;
import java.util.*;

public class KeyRegistry {
	private static final List<Key> keys = new ArrayList<>();
	private static final Map<Integer, Action> actionMap = new HashMap<>();

	public static void addKey(Key key) {
		keys.add(key);
		actionMap.put(key.keyCode(), key.action());
	}

	public static void removeKey(Key key) {
		keys.remove(key);
		actionMap.remove(key.keyCode());
	}

	public static void trigger(KeyEvent keyEvent) {
		Action action = actionMap.get(keyEvent.getKeyCode());
		if (action != null) {
			action.execute();
		}
	}

	public static <T> void trigger(KeyEvent keyEvent, T parameter) {
		Action action = actionMap.get(keyEvent.getKeyCode());
		if (action != null) {
			action.execute(parameter);
		}
	}

	public static List<Key> getKeys() {
		return Collections.unmodifiableList(keys);
	}

	public static Collection<Action> getActions() {
		return Collections.unmodifiableCollection(actionMap.values());
	}
}
