package com.ded.misle.renderer.menu.menus;

import com.ded.misle.core.LanguageManager;
import com.ded.misle.game.GamePanel;
import com.ded.misle.input.*;
import com.ded.misle.input.interaction.MouseInteraction;
import com.ded.misle.renderer.menu.core.Menu;
import com.ded.misle.renderer.menu.core.MenuManager;
import com.ded.misle.renderer.smoother.SmoothValue;
import com.ded.misle.renderer.smoother.modifiers.SineWaveModifier;
import com.ded.misle.renderer.ui.core.UIRegistry;
import com.ded.misle.renderer.ui.elements.Button;
import com.ded.misle.renderer.ui.elements.MainBackground;
import com.ded.misle.renderer.ui.elements.Title;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.ded.misle.game.GamePanel.*;
import static com.ded.misle.renderer.MainRenderer.gameStart;

public class MainMenu implements Menu {
	private final static Rectangle START_BUTTON_RECTANGLE = new Rectangle(
			(originalScreenWidth - 128) / 2,
			129,
			128,
			30);
	private final static Rectangle QUIT_BUTTON_RECTANGLE = new Rectangle(
			(originalScreenWidth - 128) / 2 + 68,
			129 + 35,
			60,
			30);

	private final UIRegistry registry = new UIRegistry();
	private final Title title = new Title(LanguageManager.getText("misle")).setRainbowness(0.005f);
	private final Button startButton = new Button(LanguageManager.getText("main_menu_play"), START_BUTTON_RECTANGLE);
	private final Key startButtonFunc = startButton.addFunction(new KeyBuilder(
			MouseInteraction.of(START_BUTTON_RECTANGLE, MouseInteraction.MouseButton.LEFT),
			new Action(() -> gameStart(1), (ignored) -> MenuManager.getCurrent().equals(this), false),
			KeyInputType.ON_RELEASE
	));
	private final Button quitButton = new Button(LanguageManager.getText("main_menu_quit"), QUIT_BUTTON_RECTANGLE);
	private final Key quitButtonFunc = quitButton.addFunction(new KeyBuilder(
			MouseInteraction.of(QUIT_BUTTON_RECTANGLE, MouseInteraction.MouseButton.LEFT),
			new Action(GamePanel::quitGame, (ignored) -> MenuManager.getCurrent().equals(this), false),
			KeyInputType.ON_RELEASE));
	private final SmoothValue smoothTitleScale = new SmoothValue(1.6f);
	private final SmoothValue smoothTitleRotation = new SmoothValue(0f);
	private final List<Key> keys = new ArrayList<>();

	@Override
	public void draw(Graphics2D g2d) {
		title.setScale(smoothTitleScale.getCurrentFloat() - smoothTitleScale.getCurrentFloat() % 0.04f);
		title.setRotation(smoothTitleRotation.getCurrentFloat());
		smoothTitleScale.update(0.05f);
		smoothTitleRotation.update(0.05f);
		registry.drawActive(g2d);
	}

	@Override
	public void init() {
		keys.add(startButtonFunc);
		keys.add(quitButtonFunc);
		for (Key key : keys) {
			KeyRegistry.addKey(key);
		}

		registry.add(MainBackground.class);
		smoothTitleScale.addModifiers(
				new SineWaveModifier(0.3f, 0.8f),
				new SineWaveModifier(0.2f, 0.04f)
		);
		smoothTitleRotation.addModifiers(
				new SineWaveModifier(4f, 0.6f)
		);
		registry.add(title);
		registry.add(startButton);
		registry.add(quitButton);
		registry.add(new Button(LanguageManager.getText("uwu"), new Rectangle(originalScreenWidth / 2 - 64, originalScreenHeight - 30, 128, 20)));
	}
}