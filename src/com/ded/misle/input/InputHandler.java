package com.ded.misle.input;

import java.awt.event.*;
import java.util.*;

import static com.ded.misle.input.interaction.MouseInteraction.MOUSE_SERIAL_CODE;

public class InputHandler implements KeyListener, MouseListener, MouseMotionListener {
	private static final Set<Integer> heldKeys = new HashSet<>();
	private static final Set<Integer> toTrigger = new HashSet<>();
	private static final Set<Integer> triggeredHeldKeys = new HashSet<>();

	@Override
	public void keyTyped(KeyEvent ignored) {}

	@Override
	public void keyPressed(KeyEvent e) {
		keyPressed(e.getKeyCode(), null);
	}
	public void keyPressed(int keyCode) {
		keyPressed(keyCode, null);
	}
	public void keyPressed(int keyCode, MouseEvent mouseEvent) {
		if (!KeyRegistry.isValid(keyCode)) return;
		KeyRegistry.trigger(keyCode, KeyInputType.ON_PRESS, mouseEvent);
		if (!heldKeys.contains(keyCode)) setToTrigger(keyCode, true);
		setKeyHeld(keyCode, true);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keyReleased(e.getKeyCode(), null);
	}
	public void keyReleased(int keyCode) {
		keyReleased(keyCode, null);
	}
	public void keyReleased(int keyCode, MouseEvent mouseEvent) {
		if (!KeyRegistry.isValid(keyCode)) return;
		setKeyHeld(keyCode, false);
		if (triggeredHeldKeys.contains(keyCode)) {
			setToTrigger(keyCode, false);
			setKeyTriggered(keyCode, false);
			return;
		}
		setKeyTriggered(keyCode, false);
		KeyRegistry.trigger(keyCode, KeyInputType.ON_RELEASE, mouseEvent);
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

	@Override
	public void mouseClicked(MouseEvent ignored) {}

	@Override
	public void mousePressed(MouseEvent e) {
		keyPressed(e.getButton() + MOUSE_SERIAL_CODE, e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		keyReleased(e.getButton() + MOUSE_SERIAL_CODE, e);
	}

	@Override
	public void mouseEntered(MouseEvent ignored) {}

	@Override
	public void mouseExited(MouseEvent ignored) {}

	@Override
	public void mouseDragged(MouseEvent ignoredTemp) {
		// TODO: add support for dragging
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		keyPressed(e.getButton() + MOUSE_SERIAL_CODE);
	}
}
