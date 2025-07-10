package com.ded.misle.renderer.utils;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Plus {
	private static final Map<String, BufferedImage> plusCache = new HashMap<>();

	private Rectangle bounds;
	private Color color;
	private float sizeMultiplier;
	private int thickness;
	private int roundness;

	public Plus(Rectangle bounds, Color color, float sizeMultiplier, int thickness, int roundness) {
		this.bounds = bounds;
		this.color = color;
		this.sizeMultiplier = sizeMultiplier;
		this.thickness = thickness;
		this.roundness = roundness;
	}

	public void draw(Graphics2D g2d) {
		BufferedImage img = this.generate();

		g2d.drawImage(img, bounds.x, bounds.y, null);
	}

	public BufferedImage generate() {
		String key = getKey();
		if (plusCache.containsKey(key)) return plusCache.get(key);

		BufferedImage image = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = image.createGraphics();

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setColor(color);

		double verticalX = (bounds.width - thickness) / 2d;
		double verticalY = (bounds.height * (1 - sizeMultiplier)) / 2d;
		double verticalHeight = bounds.height * sizeMultiplier;

		double horizontalX = (bounds.width * (1 - sizeMultiplier)) / 2d;
		double horizontalY = (bounds.height - thickness) / 2d;
		double horizontalWidth = bounds.width * sizeMultiplier;

		RoundRectangle2D vertical = new RoundRectangle2D.Double(
				verticalX, verticalY, thickness, verticalHeight, roundness, roundness);
		RoundRectangle2D horizontal = new RoundRectangle2D.Double(
				horizontalX, horizontalY, horizontalWidth, thickness, roundness, roundness);

		g2d.fill(vertical);
		g2d.fill(horizontal);

		g2d.dispose();
		plusCache.put(key, image);
		return image;
	}

	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public void setSizeMultiplier(float sizeMultiplier) {
		this.sizeMultiplier = sizeMultiplier;
	}
	public void setThickness(int thickness) {
		this.thickness = thickness;
	}
	public void setRoundness(int roundness) {
		this.roundness = roundness;
	}

	private String getKey() {
		return bounds.width + "x" + bounds.height + "_size" + sizeMultiplier + "_thick" + thickness + "_round" + roundness + "_color" + color.getRGB();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Plus plus = (Plus) o;
		return Float.compare(plus.sizeMultiplier, sizeMultiplier) == 0 &&
				thickness == plus.thickness &&
				roundness == plus.roundness &&
				Objects.equals(bounds, plus.bounds) &&
				Objects.equals(color, plus.color);
	}

	@Override
	public int hashCode() {
		return Objects.hash(bounds, color, sizeMultiplier, thickness, roundness);
	}

	public Plus copy() {
		return new Plus(new Rectangle(bounds), color, sizeMultiplier, thickness, roundness);
	}
}
