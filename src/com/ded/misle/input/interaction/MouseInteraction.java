package com.ded.misle.input.interaction;

import java.awt.*;
import java.awt.event.MouseEvent;

import static com.ded.misle.game.GamePanel.getWindowScale;

public record MouseInteraction(Rectangle pos, MouseButton button) implements InputInteraction {
	public static final int MOUSE_SERIAL_CODE = 1657077265;

	@Override
	public int keyCode() {
		return button.keyCode;
	}

	public enum MouseButton {
		NONE(0 + MOUSE_SERIAL_CODE),
		LEFT(1 + MOUSE_SERIAL_CODE),
		RIGHT(2 + MOUSE_SERIAL_CODE),
		MIDDLE(3 + MOUSE_SERIAL_CODE);

		public final int keyCode;

		MouseButton(int keyCode) {
			this.keyCode = keyCode;
		}
	}

	public static MouseInteraction anywhereOf(MouseButton button) {
		return new MouseInteraction(null, button);
	}

	public static MouseInteraction of(Rectangle pos, MouseButton button) {
		return new MouseInteraction(pos, button);
	}

	public boolean isAnywhere() {
		return this.pos == null;
	}

	@Override
	public <T> boolean checkValidity(T args) {
		return isAnywhere() || args instanceof MouseEvent &&
				pos.contains(
						new Point((int) (((MouseEvent) args).getX() / getWindowScale()),
				(int) (((MouseEvent) args).getY() / getWindowScale())));
	}
}