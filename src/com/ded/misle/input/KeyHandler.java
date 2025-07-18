package com.ded.misle.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;

public class KeyHandler implements KeyListener {
	private static final Set<Integer> heldKeys = new HashSet<>();
	private static final Set<Integer> toTrigger = new HashSet<>();
	private static final Set<Integer> triggeredHeldKeys = new HashSet<>();

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (!KeyRegistry.isValid(keyCode)) return;
		KeyRegistry.trigger(e, KeyInputType.ON_PRESS);
		if (!heldKeys.contains(keyCode)) setToTrigger(keyCode, true);
		setKeyHeld(keyCode, true);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (!KeyRegistry.isValid(keyCode)) return;
		setKeyHeld(keyCode, false);
		if (triggeredHeldKeys.contains(keyCode)) {
			setToTrigger(keyCode, false);
			setKeyTriggered(keyCode, false);
			return;
		}
		setKeyTriggered(keyCode, false);
		KeyRegistry.trigger(e, KeyInputType.ON_RELEASE);
	}

	public static void setKeyHeld(int keyCode, boolean held) {
		if (held) heldKeys.add(keyCode);
		else heldKeys.remove(keyCode);
	}

	public static void setToTrigger(int keyCode, boolean held) {
		if (held) toTrigger.add(keyCode);
		else toTrigger.remove(keyCode);
	}

	public static void setKeyTriggered(int keyCode, boolean held) {
		if (held) triggeredHeldKeys.add(keyCode);
		else triggeredHeldKeys.remove(keyCode);
	}

	public static boolean isHeld(int keyCode) {
		return heldKeys.contains(keyCode);
	}

	public static void triggerAllHeld() {
		for (Key key : KeyRegistry.getKeys()) {
			if (key.keyInputType() == KeyInputType.ON_HOLD && isHeld(key.keyCode())) {
				if (toTrigger.contains(key.keyCode())) {
					key.applyInitialCooldown(true);
					setToTrigger(key.keyCode(), false);
				}
				if (!key.onCooldown() && key.action().canExecute(key.parameter())) {
					setKeyTriggered(key.keyCode(), true);
					KeyRegistry.trigger(key.keyCode(), KeyInputType.ON_HOLD);
					key.recountCooldown();
				}
			}
		}
	}
}
