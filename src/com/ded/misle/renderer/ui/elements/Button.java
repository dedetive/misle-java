package com.ded.misle.renderer.ui.elements;

import com.ded.misle.renderer.*;
import com.ded.misle.renderer.ui.core.AbstractUIElement;

import java.awt.*;
import java.awt.image.BufferedImage;
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
	private static final Color BUTTON_BODY = new Color(0xa18053);

	private String text;
	private Rectangle bounds;
	private static final int borderSize = 2;

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
		gImg.setColor(BUTTON_BODY);
		gImg.fillRect(borderSize, borderSize, bounds.width - borderSize * 2, bounds.height - borderSize * 2);

		return img;
	}

	private BufferedImage constructTextImage(Graphics2D g2d) {
		FontMetrics fm = FontManager.getCachedMetrics(g2d, FontManager.titleFont);
		int textWidth = innerTextWidthCache.computeIfAbsent(text,
				s -> fm.stringWidth(ColorManager.removeColorIndicators(text)));

		BufferedImage img = new BufferedImage(textWidth, fm.getHeight() + 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gImg = img.createGraphics();

		for (Point p : shadowIterationPoints) {
			drawColoredText(gImg, text, p.x, p.y + fm.getHeight(), menuTitleShadowColor);
		}

		drawColoredText(gImg, text, 0, fm.getHeight(), menuTitleColor);

		textDrawX = (int) (bounds.x + (bounds.width - textWidth / 2.5) / 2);
		textDrawY = bounds.y + (bounds.height - fm.getHeight() * 11 / 6) / 2;
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
