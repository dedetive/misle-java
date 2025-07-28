package com.ded.misle.renderer.ui.elements;

import com.ded.misle.input.*;
import com.ded.misle.input.interaction.MouseInteraction;
import com.ded.misle.renderer.*;
import com.ded.misle.renderer.ui.core.AbstractUIElement;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import static com.ded.misle.renderer.ColorManager.*;

public class Button extends AbstractUIElement {
	private static final Point[] shadowIterationPoints = new Point[]{
			new Point(-MainRenderer.textShadow, 0),
			new Point(MainRenderer.textShadow, 0),
			new Point(0, -MainRenderer.textShadow),
			new Point(0, MainRenderer.textShadow),
			new Point(-MainRenderer.textShadow, -MainRenderer.textShadow),
			new Point(MainRenderer.textShadow, -MainRenderer.textShadow),
			new Point(-MainRenderer.textShadow, MainRenderer.textShadow),
			new Point(MainRenderer.textShadow, MainRenderer.textShadow),
	};
	private static final HashMap<String, BufferedImage> innerImageCache = new HashMap<>();
	private static final HashMap<String, BufferedImage> textImageCache = new HashMap<>();
	private static final HashMap<String, Integer> innerTextWidthCache = new HashMap<>();
	private static final Color BUTTON_FRAME = new Color(0x3f2206);
	private static final Color BUTTON_BODY_SHADOW = new Color(0x8A6945);
	private static final Color BUTTON_BODY = new Color(0xBCA163);
	private static final int BORDER_SIZE = 2;
	private static final int SHADOW_SIZE = 3;

	private String text;
	private Rectangle bounds;
	private final java.util.List<Key> keys = new ArrayList<>();

	private BufferedImage buttonImage;
	private BufferedImage textImage;
	private int textDrawX, textDrawY;
	private boolean needsRecalculation = true;

	public Button(String treatedText, Rectangle bounds) {
		this.text = treatedText;
		this.bounds = bounds;
	}
	public Button(Rectangle bounds) {
		this(null, bounds);
	}

	public Button setText(String text) {
		this.text = text;
		needsRecalculation = true;
		return this;
	}
	public Button setBounds(Rectangle bounds) {
		this.bounds = bounds;
		needsRecalculation = true;
		return this;
	}
	public Key addFunction(Action action, MouseInteraction.MouseButton mouseButton) {
		return addFunction(new KeyBuilder(MouseInteraction.of(bounds, mouseButton),
				action,
				KeyInputType.ON_RELEASE));
	}
	public Key addFunction(KeyBuilder keyBuilder) {
		Key key = keyBuilder.build();
		this.keys.add(key);
		return key;
	}


	private void recalculate(Graphics2D g2d) {
		if (!needsRecalculation) return;
		needsRecalculation = false;

		buttonImage = innerImageCache.computeIfAbsent(text + bounds.toString(), s ->
				constructBaseImage()
		);
		textImage = textImageCache.computeIfAbsent(text + bounds.toString(), s ->
				constructTextImage(g2d)
		);
	}

	private BufferedImage constructBaseImage() {
		BufferedImage img = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gImg = img.createGraphics();

		gImg.setColor(BUTTON_FRAME);
		gImg.fillRect(0, 0, bounds.width, bounds.height);

		gImg.setColor(BUTTON_BODY_SHADOW);
		gImg.fillRect(BORDER_SIZE, BORDER_SIZE,
				bounds.width - BORDER_SIZE * 2,
				bounds.height - BORDER_SIZE * 2);

		gImg.setColor(BUTTON_BODY);
		gImg.fillRect(
				BORDER_SIZE,
				BORDER_SIZE,
				bounds.width - BORDER_SIZE * 2 - SHADOW_SIZE,
				bounds.height - BORDER_SIZE * 2 - SHADOW_SIZE);

		return img;
	}

	private BufferedImage constructTextImage(Graphics2D g2d) {
		FontMetrics fm = FontManager.getCachedMetrics(g2d, FontManager.titleFont);
		int textWidth = innerTextWidthCache.computeIfAbsent(text,
				s -> fm.stringWidth(ColorManager.removeColorIndicators(text)));
		float textHeight = fm.getHeight();

		float textScale = Math.clamp(bounds.height / 18f, 0.7f, 2.2f);
		textScale -= textScale % 0.7f;

		BufferedImage img = new BufferedImage((int) (textWidth * textScale), (int) ((textHeight + 4) * textScale), BufferedImage.TYPE_INT_ARGB);
		Graphics2D gImg = img.createGraphics();

		gImg.scale(textScale, textScale);
		for (Point p : shadowIterationPoints) {
			drawColoredText(gImg, text, p.x, (int) (p.y + textHeight), menuTitleShadowColor);
		}

		drawColoredText(gImg, text, 0, (int) textHeight, menuTitleColor);

		textDrawX = (int) (bounds.x + (bounds.width - textWidth / (2.5 / textScale)) / 2);
		textDrawY = (int) (bounds.y + (bounds.height - textHeight * 10 / (6 / textScale)) / 2);
		/* empirically chosen values, looks good though */

		return img;
	}

	@Override
	public void drawIfPossible(Graphics2D g2d) {
		recalculate(g2d);
		g2d.drawImage(buttonImage, bounds.x, bounds.y, bounds.width, bounds.height, null);
		g2d.drawImage(textImage, textDrawX, textDrawY, null);
	}
}
