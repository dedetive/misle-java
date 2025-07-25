package com.ded.misle.renderer.menu.menus;

import com.ded.misle.core.LanguageManager;
import com.ded.misle.renderer.menu.core.Menu;
import com.ded.misle.renderer.smoother.SmoothValue;
import com.ded.misle.renderer.smoother.modifiers.SineWaveModifier;
import com.ded.misle.renderer.ui.core.UIRegistry;
import com.ded.misle.renderer.ui.elements.MainBackground;
import com.ded.misle.renderer.ui.elements.Title;

import java.awt.*;

public class MainMenu implements Menu {
	private final UIRegistry registry = new UIRegistry();
	private final Title title = new Title(LanguageManager.getText("misle"));
	private final SmoothValue smoothTitleScale = new SmoothValue(1.6f);

	@Override
	public void draw(Graphics2D g2d) {
		title.setScale(smoothTitleScale.getCurrentFloat() - smoothTitleScale.getCurrentFloat() % 0.04f);
		smoothTitleScale.update(0.05f);
		registry.drawActive(g2d);
	}

	@Override
	public void init() {
		registry.add(MainBackground.class);
		title.setScale(smoothTitleScale.getCurrentFloat());
		smoothTitleScale.addModifiers(
				new SineWaveModifier(0.3f, 0.8f),
				new SineWaveModifier(0.2f, 0.04f)
		);
		registry.add(title);
	}
}