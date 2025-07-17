package com.ded.misle.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

public class KeyHandler {
	public static final KeyListener listener = new KeyListener() {
		private static final Map<KeyEvent, Boolean> canPress = new HashMap<>();

		@Override
		public void keyTyped(KeyEvent e) {}

		@Override
		public void keyPressed(KeyEvent e) {
			if (canPress.computeIfAbsent(e, ev -> true)) KeyRegistry.trigger(e, KeyInputType.ON_PRESS);
			canPress.put(e, false);

			KeyRegistry.trigger(e, KeyInputType.ON_HOLD);
		}

		@Override
		public void keyReleased(KeyEvent e) {
			KeyRegistry.trigger(e, KeyInputType.ON_RELEASE);
			canPress.put(e, true);
		}
	};
}
