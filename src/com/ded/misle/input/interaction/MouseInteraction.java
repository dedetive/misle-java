package com.ded.misle.input.interaction;

import java.awt.*;
import java.util.Random;

public record MouseInteraction(Rectangle pos, MouseButton button) implements InputInteraction {
	public static final int MOUSE_SERIAL_CODE = new Random().nextInt();

	@Override
	public int keyCode() {
		return button.keyCode;
	}

	public enum MouseButton {
		NONE(1 + MOUSE_SERIAL_CODE),
		LEFT(2 + MOUSE_SERIAL_CODE),
		RIGHT(4 + MOUSE_SERIAL_CODE),
		MIDDLE(8 + MOUSE_SERIAL_CODE);

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
}