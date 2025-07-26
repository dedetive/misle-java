package com.ded.misle.input.interaction;

public sealed interface InputInteraction permits KeyboardInteraction, MouseInteraction {
	int keyCode();
}
