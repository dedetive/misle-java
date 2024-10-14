package com.ded.misle.boxes;

import java.awt.*;

public class Box {
	private final double originalX; // The original world position (unscaled)
	private final double originalY; // The original world position (unscaled)
	private final Color color;

	public Box(double x, double y, Color color) {
		this.originalX = x; // Store the original position
		this.originalY = y; // Store the original position
		this.color = color;
	}

	// Method to render the box with the current tileSize and scale the position
	public void draw(Graphics2D g2d, double cameraOffsetX, double cameraOffsetY, double scale, int tileSize) {
		// Scale the position based on the current scale
		double scaledX = originalX * scale;
		double scaledY = originalY * scale;

		// Apply the camera offset to the scaled position
		int screenX = (int) (scaledX - cameraOffsetX);
		int screenY = (int) (scaledY - cameraOffsetY);

		// Draw the box with the scaled position and tileSize
		g2d.setColor(color);
		g2d.fillRect(screenX, screenY, tileSize, tileSize);
	}

	// Check if a point (e.g., player) is inside this box (adjusted for the new scale)
	public boolean isPointInside(double px, double py, double scale, int tileSize) {
		double scaledX = originalX * scale;
		double scaledY = originalY * scale;
		return px >= scaledX && px <= scaledX + tileSize && py >= scaledY && py <= scaledY + tileSize;
	}
}
