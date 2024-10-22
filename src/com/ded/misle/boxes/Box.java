package com.ded.misle.boxes;

import java.awt.*;

public class Box {
	private final double originalX; // The original world position (unscaled)
	private final double originalY; // The original world position (unscaled)
	private final Color color;
	private final boolean hasCollision;
	private final double boxScaleHorizontal;
	private final double boxScaleVertical;
	private final String[] canDamage;
	private long lastDamageTime = 0;

	/**
	 *
	 * @param x original x of the box
	 * @param y original y of the box
	 * @param color color of the box
	 * @param hasCollision a boolean of whether the box has collision
	 * @param boxScaleHorizontal how many tilesizes is the box in the x axis
	 * @param boxScaleVertical how many tilesizes is the box in the y axis
	 * @param canDamage first value, if negative, means the box cannot deal damage. If it is positive, it means the box will deal that much damage. The second value is the rate in milliseconds of the damage dealt
	 */
	public Box(double x, double y, Color color, boolean hasCollision, double boxScaleHorizontal, double boxScaleVertical, String[] canDamage) {
		this.originalX = x; // Store the original position
		this.originalY = y; // Store the original position
		this.color = color;
		this.hasCollision = hasCollision;
		this.boxScaleHorizontal = boxScaleHorizontal;
		this.boxScaleVertical = boxScaleVertical;
		this.canDamage = canDamage;
	}

	// Method to render the box with the current tileSize and scale the position
	public void draw(Graphics2D g2d, double cameraOffsetX, double cameraOffsetY, double scale, int tileSize, double boxScaleHorizontal, double boxScaleVertical) {
		// Scale the position based on the current scale
		double scaledX = originalX * scale;
		double scaledY = originalY * scale;

		// Apply the camera offset to the scaled position
		int screenX = (int) (scaledX - cameraOffsetX);
		int screenY = (int) (scaledY - cameraOffsetY);

		// Draw the box with the scaled position and tileSize
		g2d.setColor(color);
		g2d.fillRect(screenX, screenY, (int) (tileSize * boxScaleHorizontal), (int) (tileSize * boxScaleVertical));
	}

	// COLLISION

	// Check if a point (e.g., player) is inside this box (adjusted for the new scale)
	public boolean isPointColliding(double pointX, double pointY, double scale, double objectWidth, double objectHeight) {
		double scaledX = originalX * scale;
		double scaledY = originalY * scale;
		return pointX >= scaledX && pointX <= scaledX + objectWidth * boxScaleHorizontal && pointY >= scaledY && pointY <= scaledY + objectHeight * boxScaleVertical;
	}

	public boolean getHasCollision() {
		return hasCollision;
	}

	// BOX POSITION AND SCALING

	public double getOriginalX() {
        return originalX;
	}

    public double getOriginalY() {
      return originalY;
    }

	public double getBoxScaleHorizontal() {
		return boxScaleHorizontal;
	}

	public double getBoxScaleVertical() {
		return boxScaleVertical;
	}

	// DAMAGE

	public double getDamageValue() {
		return Double.parseDouble(canDamage[0]);
	}

	public double getDamageRate() {
		return Double.parseDouble(canDamage[1]);
	}

	public void setDamageValue(double damageValue) {
		this.canDamage[0] = String.valueOf(damageValue);
	}

	public void setDamageRate(double damageRate) {
		this.canDamage[1] = String.valueOf(damageRate);
	}

	public String getDamageReason() {
		return this.canDamage[2];
	}

	public void setDamageReason(String reason) {
		this.canDamage[2] = reason;
	}

	public long getLastDamageTime() {
		return lastDamageTime;
	}

	public void setLastDamageTime(long lastDamageTime) {
		this.lastDamageTime = lastDamageTime;
	}
}
