package com.ded.misle.input.interaction;

public sealed interface InputInteraction permits KeyboardInteraction, MouseInteraction {
	int keyCode();
	default <T> boolean checkValidity(T args) { return true; }
}