package com.ded.misle.renderer.menu.menus;

import com.ded.misle.core.LanguageManager;
import com.ded.misle.renderer.menu.core.Menu;
import com.ded.misle.renderer.ui.core.UIRegistry;
import com.ded.misle.renderer.ui.elements.*;
import com.ded.misle.renderer.ui.elements.Button;

import java.awt.*;
import java.util.*;
import java.util.List;

public class SettingsMenu implements Menu {
	private final UIRegistry registry = new UIRegistry();

	private static final HashMap<SettingState, Button> SETTING_STATE_BUTTONS = new HashMap<>();
	private static final List<Button> PERSISTENT_BUTTONS = new ArrayList<>();

	@Override
	public void draw(Graphics2D g2d) {
		registry.drawActive(g2d);
	}

	@Override
	public void init() {
		registry.add(MainBackground.class);
		registry.add(new Title(LanguageManager.getText("settings_menu_options")));

		SettingState.values(); // load values
		for (Button b : PERSISTENT_BUTTONS) {
			registry.add(b);
		}
	}

	enum SettingState {
		EMPTY,
		GENERAL(new Button(
				LanguageManager.getText("settings_menu_general"), new Rectangle(42, 220, 50, 31)
		)),
		GRAPHICS(new Button(
				LanguageManager.getText("settings_menu_graphics"), new Rectangle(107, 220, 50, 31)
		)),
		AUDIO(new Button(
				LanguageManager.getText("settings_menu_audio"), new Rectangle(172, 220, 50, 31)
		)),
		GAMEPLAY(new Button(
				LanguageManager.getText("settings_menu_gameplay"), new Rectangle(237, 220, 50, 31)
		)),

		;

		SettingState(Button button) {
			PERSISTENT_BUTTONS.add(button);
		}

		SettingState() {}

		static SettingState getStateByOrder(int order) {
			return switch (order) {
				case -2 -> GAMEPLAY;
				case -1 -> GAMEPLAY;
				case 0 -> GENERAL;
				case 1 -> GRAPHICS;
				case 2 -> AUDIO;
				case 3 -> GAMEPLAY;
				case 4 -> GENERAL;
				default -> GENERAL;
			};
		}
	}
}
