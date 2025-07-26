package com.ded.misle.input.interaction;

public record KeyboardInteraction(int keyCode) implements InputInteraction {
	@Override
	public int keyCode() {
		return keyCode;
	}

	public static KeyboardInteraction of(int keyCode) {
		return new KeyboardInteraction(keyCode);
	}
}