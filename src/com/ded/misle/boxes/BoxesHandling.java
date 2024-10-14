package com.ded.misle.boxes;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BoxesHandling {

	private static final List<Box> boxes = new ArrayList<>();

	public static void addBox(double x, double y, Color color) {
		boxes.add(new Box(x, y, color));
	}

	// Render boxes with camera offset, scale, and tileSize
	public static void renderBoxes(Graphics2D g2d, double cameraOffsetX, double cameraOffsetY, double scale, int tileSize) {
		for (Box box : boxes) {
			box.draw(g2d, cameraOffsetX, cameraOffsetY, scale, tileSize);
		}
	}

	public static boolean isCoordinateOccupied(double px, double py, double scale, int tileSize) {
		for (Box box : boxes) {
			if (box.isPointInside(px, py, scale, tileSize)) {
				return true;
			}
		}
		return false;
	}
}