package com.ded.misle.input;

import java.awt.*;

public record MouseInteraction(Rectangle pos, MouseButton button) {
	public enum MouseButton {
		LEFT, RIGHT, MIDDLE, NONE
	}

	public static MouseInteraction anywhereOf(MouseButton button) {
		return new MouseInteraction(null, button);
	}

	public static MouseInteraction of(Rectangle pos, MouseButton button) {
		return new MouseInteraction(pos, button);
	}

}