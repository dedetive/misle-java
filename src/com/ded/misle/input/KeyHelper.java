package com.ded.misle.input;

import static com.ded.misle.game.GamePanel.player;
import static com.ded.misle.renderer.FontManager.dialogNPCText;

public final class KeyHelper {
	private KeyHelper() {}

	public static char removeExtraChars(char s) {
		if (dialogNPCText.canDisplay(s)) return s;
		else return '\0';
	}

	public static void pressUseButton() {
		player.inv.useItem();
	}
}
