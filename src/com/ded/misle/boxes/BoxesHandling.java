package com.ded.misle.boxes;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BoxesHandling {

	private static final List<Box> boxes = new ArrayList<>();

	public static void addBox(double x, double y, Color color, boolean hasCollision, double boxScaleHorizontal, double boxScaleVertical) {
		boxes.add(new Box(x, y, color, hasCollision, boxScaleHorizontal, boxScaleVertical));
	}

	// Render boxes with camera offset, scale, and tileSize
	public static void renderBoxes(Graphics2D g2d, double cameraOffsetX, double cameraOffsetY, double scale, int tileSize) {
		for (Box box : boxes) {
			box.draw(g2d, cameraOffsetX, cameraOffsetY, scale, tileSize, box.getBoxScaleHorizontal(), box.getBoxScaleVertical());
		}
	}

	public static List<Box> getAllBoxes() {
      return new ArrayList<>(boxes);
  }

	public static List<Box> getBoxesInRange(double playerX, double playerY, double range, double scale, int tileSize) {
    	List<Box> boxesInRange = new ArrayList<>();
    	for (Box box : boxes) {
        	double scaledX = box.getOriginalX() * scale;
        	double scaledY = box.getOriginalY() * scale;

        	// Calculate bounding box range based on the player's position
        	if (scaledX >= playerX - range && scaledX <= playerX + range
            	&& scaledY >= playerY - range && scaledY <= playerY + range) {
            	boxesInRange.add(box);
        	}
    	}
    	return boxesInRange;
	}
	
	public static List<Box> getCollisionBoxesInRange(double playerX, double playerY, double range, double scale, int tileSize) {
    	List<Box> boxesInRange = new ArrayList<>();
    	for (Box box : boxes) {
    			if (!box.getHasCollision()) {
    				continue;
    			}
        	double scaledX = box.getOriginalX() * scale;
        	double scaledY = box.getOriginalY() * scale;

        	// Calculate bounding box range based on the player's position
        	if (scaledX >= playerX - range && scaledX <= playerX + range
            	&& scaledY >= playerY - range && scaledY <= playerY + range) {
            	boxesInRange.add(box);
        	}
    	}
    	return boxesInRange;
	}
}
