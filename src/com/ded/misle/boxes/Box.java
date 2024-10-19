package com.ded.misle.boxes;

import java.awt.*;

public class Box {
	private final double originalX; // The original world position (unscaled)
	private final double originalY; // The original world position (unscaled)
	private final Color color;
	private final boolean hasCollision;
	private final double boxScaleHorizontal;
	private final double boxScaleVertical;

	public Box(double x, double y, Color color, boolean hasCollision, double boxScaleHorizontal, double boxScaleVertical) {
		this.originalX = x; // Store the original position
		this.originalY = y; // Store the original position
		this.color = color;
		this.hasCollision = hasCollision;
		this.boxScaleHorizontal = boxScaleHorizontal;
		this.boxScaleVertical = boxScaleVertical;
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

	// Check if a point (e.g., player) is inside this box (adjusted for the new scale)
	public boolean isPointColliding(double pointX, double pointY, double scale, double objectWidth, double objectHeight) {
		double scaledX = originalX * scale;
		double scaledY = originalY * scale;
		return pointX >= scaledX && pointX <= scaledX + objectWidth * boxScaleHorizontal && pointY >= scaledY && pointY <= scaledY + objectHeight * boxScaleVertical;
	}

	public double getOriginalX() {
        return originalX;
	}

    public double getOriginalY() {
      return originalY;
    }

    public boolean getHasCollision() {
  	    return hasCollision;
    }

	public double getBoxScaleHorizontal() {
		return boxScaleHorizontal;
	}

	public double getBoxScaleVertical() {
		return boxScaleVertical;
	}
}
