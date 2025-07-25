package com.ded.misle.renderer.ui.elements;

import com.ded.misle.renderer.*;
import com.ded.misle.renderer.ui.core.AbstractUIElement;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import static com.ded.misle.game.GamePanel.*;
import static com.ded.misle.renderer.ColorManager.*;
import static com.ded.misle.renderer.ColorManager.drawColoredText;

public final class Title extends AbstractUIElement {
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
	private static final HashMap<String, Integer> innerTextWidthCache = new HashMap<>();
	private static final int textX = originalScreenWidth / 2;
	private static final int textY = 28;

	private final String text;
	private double scale = 1.0;

	/* Weakly cached render data */
	private BufferedImage finalImage;
	private int drawX, drawY;
	private int drawWidth, drawHeight;

	private boolean needsRecalculation = true;

	public Title(String treatedText) {
		this.text = treatedText;
	}

	public void setScale(double scale) {
		this.scale = scale;
		needsRecalculation = true;
	}

	private void recalculate(Graphics2D g2d) {
		if (!needsRecalculation) return;
		needsRecalculation = false;

		String plainText = ColorManager.removeColorIndicators(text);

		FontMetrics fm = FontManager.getCachedMetrics(g2d, FontManager.titleFont);
		int textWidth = innerTextWidthCache.computeIfAbsent(text + ":" + scale,
				s -> fm.stringWidth(plainText));

		finalImage = innerImageCache.computeIfAbsent(text + ":" + scale, s ->
				generateTextImage(text, textWidth, fm.getHeight() + MainRenderer.textShadow)
		);

		drawWidth = (int) (textWidth * 2 * scale);
		drawHeight = (int) (64 * scale);
		drawX = (int) (textX - textWidth / 2.5 * scale);
		drawY = (int) (textY - 32 * scale);
	}

	private BufferedImage generateTextImage(String text, int textWidth, int imageHeight) {
		BufferedImage img = new BufferedImage(textWidth, imageHeight + 8, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gImg = img.createGraphics();

		gImg.setColor(menuTitleShadowColor);
		for (Point p : shadowIterationPoints) {
			drawColoredText(gImg, text, p.x, p.y + imageHeight);
		}

		gImg.setColor(menuTitleColor);
		drawColoredText(gImg, text, 0, imageHeight);
		gImg.dispose();
		return img;
	}

	@Override
	public void drawIfPossible(Graphics2D g2d) {
		recalculate(g2d);
		g2d.drawImage(finalImage, drawX, drawY, drawWidth, drawHeight, null);
	}
}
