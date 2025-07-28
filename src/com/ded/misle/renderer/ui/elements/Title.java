package com.ded.misle.renderer.ui.elements;

import com.ded.misle.renderer.*;
import com.ded.misle.renderer.image.*;
import com.ded.misle.renderer.ui.core.AbstractUIElement;

import java.awt.*;
import java.awt.geom.AffineTransform;
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
	private static final int defaultHeight = 64;

	private final String text;
	private double scale = 1.0;
	private double degrees = 0.0;
	private float rainbowness = 0.0f;
	private float currentHue = 0.0f;
	private AffineTransform transform;

	/* Weakly cached render data */
	private BufferedImage finalImage;
	private int drawX, drawY;
	private int drawWidth, drawHeight;

	private boolean needsRecalculation = true;

	public Title(String treatedText) {
		this.text = treatedText;
	}

	public Title setScale(double scale) {
		this.scale = scale;
		needsRecalculation = true;
		return this;
	}

	public Title setRotation(double degrees) {
		this.degrees = degrees;
		needsRecalculation = true;
		if (degrees != 0.0) {
			this.transform = new AffineTransform();
			transform.rotate(Math.toRadians(degrees), textX, textY + (double) defaultHeight / 2);
		} else {
			this.transform = null;
		}
		return this;
	}

	public Title setRainbowness(float rainbowness) {
		this.rainbowness = rainbowness;
		return this;
	}

	private void recalculate(Graphics2D g2d) {
		if (!needsRecalculation) return;
		needsRecalculation = false;

		String plainText = ColorManager.removeColorIndicators(text);

		FontMetrics fm = FontManager.getCachedMetrics(g2d, FontManager.titleFont);
		int textWidth = innerTextWidthCache.computeIfAbsent(text,
				s -> fm.stringWidth(plainText));

		finalImage = innerImageCache.computeIfAbsent(text, s ->
				generateTextImage(text, textWidth, fm.getHeight() + MainRenderer.textShadow)
		);

		drawWidth = (int) (textWidth * 2 * scale);
		drawHeight = (int) (defaultHeight * scale);
		drawX = (int) (textX - textWidth / 2.5 * scale);
		drawY = (int) (textY - (double) defaultHeight / 2 * scale);

		Palette p = new Palette(finalImage);
		PaletteShifter shifter = new PaletteShifter(p);
		p = shifter.hueShift(currentHue);
		Painter painter = new Painter(p);
		finalImage = painter.paint(finalImage);
		currentHue += rainbowness;
	}

	private BufferedImage generateTextImage(String text, int textWidth, int imageHeight) {
		BufferedImage img = new BufferedImage(textWidth, imageHeight + 8, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gImg = img.createGraphics();

		for (Point p : shadowIterationPoints) {
			drawColoredText(gImg, text, p.x, p.y + imageHeight, menuTitleShadowColor);
		}

		drawColoredText(gImg, text, 0, imageHeight, menuTitleColor);
		gImg.dispose();
		return img;
	}

	@Override
	public void drawIfPossible(Graphics2D g2d) {
		recalculate(g2d);
		if (transform != null) {
			AffineTransform original = g2d.getTransform();
			g2d.transform(transform);
			g2d.drawImage(finalImage, drawX, drawY, drawWidth, drawHeight, null);
			g2d.setTransform(original);
		}
		else g2d.drawImage(finalImage, drawX, drawY, drawWidth, drawHeight, null);
	}
}
