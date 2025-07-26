package com.ded.misle.input.interaction;

import java.awt.*;

public record MouseInteraction(Rectangle pos, MouseButton button) implements InputInteraction {
	@Override
	public int keyCode() {
		return button.keyCode;
	}

	public enum MouseButton {
		NONE(1),
		LEFT(2),
		RIGHT(4),
		MIDDLE(8);

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