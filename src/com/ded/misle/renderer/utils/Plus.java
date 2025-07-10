package com.ded.misle.renderer.utils;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A utility class that draws a scalable, rounded "plus" (+) sign inside a given rectangle,
 * with configurable thickness, roundness, and size multiplier.
 * <p>
 * This class is designed to generate a {@link BufferedImage} containing the plus sign,
 * automatically caching the result to optimize repeated drawing with the same parameters.
 *
 * <p>
 * The {@code Plus} class is ideal for:
 * <ul>
 *     <li>Displaying buttons with "add" or "plus" symbols</li>
 *     <li>Rendering UI icons dynamically at various sizes and colors</li>
 *     <li>Drawing mathematical or decorative plus signs with antialiasing</li>
 * </ul>
 *
 * <p>
 * Each unique combination of rectangle size, color, size multiplier, thickness, and roundness
 * is cached automatically for faster re-use.
 */
public class Plus {

	/**
	 * A cache for generated plus sign images, storing them by a unique key
	 * based on their size, color, thickness, roundness, and size multiplier.
	 */
	private static final Map<String, BufferedImage> plusCache = new HashMap<>();

	/**
	 * The bounds (size and position) where the plus sign will be drawn.
	 * The width and height of this rectangle define the size of the cached image.
	 */
	private Rectangle bounds;

	/**
	 * The color used to fill the plus sign.
	 */
	private Color color;

	/**
	 * The size multiplier of the plus sign relative to the rectangle's size.
	 * Values above {@code 1.0f} cause the plus sign to grow beyond the bounds.
	 */
	private float sizeMultiplier;

	/**
	 * The thickness of the plus sign's lines, in pixels.
	 */
	private int thickness;

	/**
	 * The roundness of the corners of the plus sign's lines.
	 * Higher values result in more rounded rectangles.
	 */
	private int roundness;

	/**
	 * Creates a new {@code Plus} instance with the given parameters.
	 *
	 * @param bounds         the bounding rectangle for the plus sign
	 * @param color          the fill color of the plus sign
	 * @param sizeMultiplier the size multiplier relative to the bounds
	 * @param thickness      the thickness of the plus sign's lines
	 * @param roundness      the roundness of the plus sign's corners
	 */
	public Plus(Rectangle bounds, Color color, float sizeMultiplier, int thickness, int roundness) {
		this.bounds = bounds;
		this.color = color;
		this.sizeMultiplier = sizeMultiplier;
		this.thickness = thickness;
		this.roundness = roundness;
	}

	/**
	 * Draws the cached or newly generated plus sign into the provided {@link Graphics2D} context.
	 * The plus sign is drawn at the position and size defined by {@link #bounds}.
	 *
	 * @param g2d the graphics context to draw into
	 */
	public void draw(Graphics2D g2d) {
		BufferedImage img = this.generate();
		g2d.drawImage(img, bounds.x, bounds.y, null);
	}

	/**
	 * Generates (or retrieves from cache) a {@link BufferedImage} containing the plus sign.
	 * <p>
	 * If the combination of parameters has been generated before, the cached image is reused.
	 *
	 * @return a {@link BufferedImage} with the drawn plus sign
	 */
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

	/**
	 * Sets the bounding rectangle for this plus sign.
	 *
	 * @param bounds the new {@link #bounds}
	 */
	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}

	/**
	 * Sets the {@link #color} of the plus sign.
	 *
	 * @param color the new color
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Sets the {@link #sizeMultiplier} for the plus sign.
	 *
	 * @param sizeMultiplier the new size multiplier
	 */
	public void setSizeMultiplier(float sizeMultiplier) {
		this.sizeMultiplier = sizeMultiplier;
	}

	/**
	 * Sets the {@link #thickness} of the plus sign's lines.
	 *
	 * @param thickness the new thickness in pixels
	 */
	public void setThickness(int thickness) {
		this.thickness = thickness;
	}

	/**
	 * Sets the {@link #roundness} of the plus sign's corners.
	 *
	 * @param roundness the new roundness value
	 */
	public void setRoundness(int roundness) {
		this.roundness = roundness;
	}

	/**
	 * Generates a unique cache key for the current plus sign settings.
	 * The key encodes the bounds, color, size multiplier, thickness, and roundness.
	 *
	 * @return a unique string key for caching
	 */
	private String getKey() {
		return bounds.width + "x" + bounds.height + "_size" + sizeMultiplier + "_thick" + thickness + "_round" + roundness + "_color" + color.getRGB();
	}

	/**
	 * Checks if this {@code Plus} object is equal to another, based on their properties.
	 *
	 * @param o the object to compare with
	 * @return {@code true} if they are equivalent in properties; {@code false} otherwise
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Plus plus = (Plus) o;
		return Float.compare(plus.sizeMultiplier, sizeMultiplier) == 0 &&
				thickness == plus.thickness &&
				roundness == plus.roundness &&
				Objects.equals(bounds, plus.bounds) &&
				Objects.equals(color, plus.color);
	}

	/**
	 * Computes the hash code of this {@code Plus} based on its properties.
	 *
	 * @return the hash code
	 */
	@Override
	public int hashCode() {
		return Objects.hash(bounds, color, sizeMultiplier, thickness, roundness);
	}

	/**
	 * Creates a deep copy of this {@code Plus} instance.
	 * The returned copy is independent of the original object.
	 *
	 * @return a new {@code Plus} instance with the same properties
	 */
	public Plus copy() {
		return new Plus(new Rectangle(bounds), color, sizeMultiplier, thickness, roundness);
	}
}