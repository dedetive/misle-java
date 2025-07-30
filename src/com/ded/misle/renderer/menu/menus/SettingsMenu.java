package com.ded.misle.renderer.menu.menus;

import com.ded.misle.core.LanguageManager;
import com.ded.misle.input.Action;
import com.ded.misle.input.KeyRegistry;
import com.ded.misle.input.interaction.MouseInteraction;
import com.ded.misle.renderer.menu.core.Menu;
import com.ded.misle.renderer.menu.core.MenuManager;
import com.ded.misle.renderer.ui.core.UIRegistry;
import com.ded.misle.renderer.ui.elements.*;
import com.ded.misle.renderer.ui.elements.Button;

import java.awt.*;
import java.util.*;
import java.util.List;

public class SettingsMenu implements Menu {
	private static final List<Button> PERSISTENT_BUTTONS = new ArrayList<>();

	private static List<Button> tabButtons = new ArrayList<>();

	private SettingTab currentTab = SettingTab.EMPTY;

	private final UIRegistry registry = new UIRegistry();


	@Override
	public void draw(Graphics2D g2d) {
		registry.drawActive(g2d);
	}

	@Override
	public void init() {
		registry.add(MainBackground.class);
		registry.add(new Title(LanguageManager.getText("settings_menu_options")));

		SettingTab.values(); // load values
		for (Button b : PERSISTENT_BUTTONS) registry.add(b);
	}

	public SettingsMenu setCurrentTab(SettingTab tab) {
		if (tab == currentTab) return this;
		for (Button b : tabButtons) registry.remove(b);
		tabButtons = tab.tabButtons;
		for (Button b : tabButtons) registry.add(b);
		currentTab = tab;
		return this;
	}

	public SettingTab getCurrentTab() {
		return currentTab;
	}

	public enum SettingTab {
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
		));

		final List<Button> tabButtons = new ArrayList<>();

		SettingTab(Button button) {
			PERSISTENT_BUTTONS.add(button);
			KeyRegistry.addKey(button.addFunction(
					new Action(() -> ((SettingsMenu) MenuManager.getCurrent()).setCurrentTab(this),
							(ignored) -> MenuManager.getCurrent() instanceof SettingsMenu && !((SettingsMenu) MenuManager.getCurrent()).getCurrentTab().equals(this),
							false),
					MouseInteraction.MouseButton.LEFT
			));
		}

		SettingTab() {}

		private static SettingTab getTabByOrder(int order) {
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
