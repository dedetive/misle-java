package com.ded.misle.boxes;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BoxesHandling {

	private static final List<Box> boxes = new ArrayList<>();

	/**
	 *
	 *  ...
	 *
	 */
	public static void addBox(double x, double y, Color color, boolean hasCollision, double boxScaleHorizontal, double boxScaleVertical, String[] effect) {
		boxes.add(new Box(x, y, color, hasCollision, boxScaleHorizontal, boxScaleVertical, effect));
	}

	public static void addBox(double x, double y) {
		boxes.add(new Box(x, y));
	}

	public static void addBox(double x, double y, String preset) {
		boxes.add(new Box(x, y));
		loadPreset(boxes.getLast(), preset);
	}

	public static int lineAddBox(double startX, double startY, int boxesX, int boxesY, double interval, String preset, String mode) {
		int Counter = 0;
		for (int i = 0; i < boxesX; i++) {
			for (int j = 0; j < boxesY; j++) {
				boxes.add(new Box(startX + i * interval, startY + j * interval));
				loadPreset(boxes.getLast(), preset);
				Counter++;
			}
		}
		return Counter;
	}

	public static int lineAddBox(double startX, double startY, int boxesX, int boxesY, String preset, String mode) {
		int Counter = 0;
		for (int i = 0; i < boxesX; i++) {
			for (int j = 0; j < boxesY; j++) {
				switch (mode) {
					case "hollow":
						if ((i == 0 || i == boxesX - 1) || (j == 0 || j == boxesY - 1)) {
							boxes.add(new Box(startX + i * 20, startY + j * 20));
							loadPreset(boxes.getLast(), preset);
							Counter++;
						}
						break;
					case "fill":
					default:
						boxes.add(new Box(startX + i * 20, startY + j * 20));
						loadPreset(boxes.getLast(), preset);
						Counter++;
						break;
				}
			}
		}
		return Counter;
	}

	public static int lineAddBox(double startX, double startY, int boxesX, int boxesY, double interval, String preset) {
		int Counter = 0;
		for (int i = 0; i < boxesX; i++) {
			for (int j = 0; j < boxesY; j++) {
				boxes.add(new Box(startX + i * interval, startY + j * interval));
				loadPreset(boxes.getLast(), preset);
				Counter++;
			}
		}
		return Counter;
	}

	public static int lineAddBox(double startX, double startY, int boxesX, int boxesY, String preset) {
		int Counter = 0;
		for (int i = 0; i < boxesX; i++) {
			for (int j = 0; j < boxesY; j++) {
				boxes.add(new Box(startX + i * 20, startY + j * 20));
				loadPreset(boxes.getLast(), preset);
				Counter++;
			}
		}
		return Counter;
	}

	private static void loadPreset(Box box, String preset) {
		switch (preset) {
			case "spawnpoint":
				editLastBox("effect", "{spawnpoint, -1}");
				editLastBox("color", "0xF0F05A");
				break;
			case "chest":
//				editLastBox("effect", "{chest, 1}");
				editLastBox("hasCollision", "true");
				editLastBox("color", "0xA07030");
				break;
			case "wall":
				editLastBox("hasCollision", "true");
				editLastBox("color", "0x606060");
				break;
			case "grass":
				editLastBox("hasCollision", "false");
				editLastBox("color", "0x1EA81E");
				break;
		}
	}


	/**
	 *
	 * @param key name of the parameter to be edited. The key can be either x, y, color, hasCollision, boxScaleHorizontal, boxScaleVertical and effect
	 * @param value value to be changed to
	 */
	public static void editLastBox(String key, String value) {
		switch (key) {
			case "x":
				boxes.getLast().setCurrentX(Double.parseDouble(value));
				break;
			case "y":
				boxes.getLast().setCurrentY(Double.parseDouble(value));
				break;
			case "color":
				boxes.getLast().setColor(Color.decode(value));
				break;
			case "hasCollision":
				boxes.getLast().setHasCollision(Boolean.parseBoolean(value));
				break;
			case "boxScaleHorizontal":
				boxes.getLast().setBoxScaleHorizontal(Double.parseDouble(value));
				break;
			case "boxScaleVertical":
				boxes.getLast().setBoxScaleVertical(Double.parseDouble(value));
				break;
			case "effect":
				value = value.replaceAll("[{}]", "");
				String[] effectArray = value.split(",\\s*");
				boxes.getLast().setEffect(effectArray);
		}
	}

	public static void editLastBox(String key, String value, int boxCount) {
		for (int i = 1; i < boxCount + 1; i++) {
			switch (key) {
				case "x":
					boxes.get(boxes.size() - i).setCurrentX(Double.parseDouble(value));
					break;
				case "y":
					boxes.get(boxes.size() - i).setCurrentY(Double.parseDouble(value));
					break;
				case "color":
					boxes.get(boxes.size() - i).setColor(Color.decode(value));
					break;
				case "hasCollision":
					boxes.get(boxes.size() - i).setHasCollision(Boolean.parseBoolean(value));
					break;
				case "boxScaleHorizontal":
					boxes.get(boxes.size() - i).setBoxScaleHorizontal(Double.parseDouble(value));
					break;
				case "boxScaleVertical":
					boxes.get(boxes.size() - i).setBoxScaleVertical(Double.parseDouble(value));
					break;
				case "effect":
					value = value.replaceAll("[{}]", "");
					String[] effectArray = value.split(",\\s*");
					boxes.get(boxes.size() - i).setEffect(effectArray);
			}
		}
	}

	// Render boxes with camera offset, scale, and tileSize
	public static void renderBoxes(Graphics2D g2d, double cameraOffsetX, double cameraOffsetY, double playerX, double playerY, double range, double scale, int tileSize) {
		List<Box> nearbyBoxes = getBoxesInRange(playerX, playerY, range, scale, tileSize);
		for (Box box : nearbyBoxes) {
			box.draw(g2d, cameraOffsetX, cameraOffsetY, scale, tileSize, box.getBoxScaleHorizontal(), box.getBoxScaleVertical());
		}
	}

	public static List<Box> getAllBoxes() {
      return new ArrayList<>(boxes);
  }

	public static List<Box> getBoxesInRange(double playerX, double playerY, double range, double scale, int tileSize) {
    	List<Box> boxesInRange = new ArrayList<>();
    	for (Box box : boxes) {
        	double scaledX = box.getCurrentX() * scale;
        	double scaledY = box.getCurrentY() * scale;

        	// Calculate bounding box range based on the player's position
		    if (scaledX + tileSize * box.getBoxScaleHorizontal() / 1.5 >= playerX - range && scaledX <= playerX + range
				    && scaledY + tileSize * box.getBoxScaleVertical() / 1.5 >= playerY - range && scaledY <= playerY + range) {
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
        	double scaledX = box.getCurrentX() * scale;
        	double scaledY = box.getCurrentY() * scale;

        	// Calculate bounding box range based on the player's position
		    if (scaledX + tileSize * box.getBoxScaleHorizontal() / 1.5 >= playerX - range && scaledX <= playerX + range
				    && scaledY + tileSize * box.getBoxScaleVertical() / 1.5 >= playerY - range && scaledY <= playerY + range) {
			    boxesInRange.add(box);
		    }
    	}
    	return boxesInRange;
	}

	public static List<Box> getNonCollisionBoxesInRange(double playerX, double playerY, double range, double scale, int tileSize) {
		List<Box> boxesInRange = new ArrayList<>();
		for (Box box : boxes) {
			if (box.getHasCollision()) {
				continue;
			}
			double scaledX = box.getCurrentX() * scale;
			double scaledY = box.getCurrentY() * scale;
			// Calculate bounding box range based on the player's position
			if (scaledX + tileSize * box.getBoxScaleHorizontal() / 1.5 >= playerX - range && scaledX <= playerX + range
					&& scaledY + tileSize * box.getBoxScaleVertical() / 1.5 >= playerY - range && scaledY <= playerY + range) {
				boxesInRange.add(box);
			}
		}
		return boxesInRange;
	}
}
