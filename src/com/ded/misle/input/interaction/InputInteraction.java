package com.ded.misle.input.interaction;

public sealed interface InputInteraction permits MouseInteraction {
	int keyCode();
}
