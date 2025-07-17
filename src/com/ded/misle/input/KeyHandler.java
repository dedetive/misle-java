package com.ded.misle.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;

public class KeyHandler implements KeyListener {
	private static final Set<Integer> heldKeys = new HashSet<>();

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (!KeyRegistry.isValid(keyCode)) return;
		KeyRegistry.trigger(e, KeyInputType.ON_PRESS);
		setKeyHeld(keyCode, true);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (!KeyRegistry.isValid(keyCode)) return;
		KeyRegistry.trigger(e, KeyInputType.ON_RELEASE);
		setKeyHeld(keyCode, false);
	}

	public static void setKeyHeld(int keyCode, boolean held) {
		if (held) heldKeys.add(keyCode);
		else heldKeys.remove(keyCode);
	}

	public static void triggerAllHeld() {
		for (Key key : KeyRegistry.getKeys()) {
			if (key.keyInputType() == KeyInputType.ON_HOLD && heldKeys.contains(key.keyCode()) && !key.onCooldown()) {
				key.action().execute();
				key.lastTimeActivated = System.currentTimeMillis();
			}
		}
	}
}
