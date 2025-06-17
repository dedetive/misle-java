package com.ded.misle.world.boxes;

import com.ded.misle.world.data.Direction;
import com.ded.misle.world.logic.PhysicsEngine;
import com.ded.misle.renderer.ImageManager;
import com.ded.misle.renderer.smoother.SmoothPosition;
import com.ded.misle.world.logic.World;
import com.ded.misle.world.logic.effects.Collectible;
import com.ded.misle.world.logic.effects.Effect;
import com.ded.misle.world.entities.player.Player;

import javax.imageio.ImageIO;
import javax.swing.Timer;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static com.ded.misle.core.Path.getPath;
import static com.ded.misle.game.GamePanel.*;
import static com.ded.misle.renderer.ImageManager.*;
import static com.ded.misle.world.boxes.BoxManipulation.moveBox;
import static com.ded.misle.renderer.ColorManager.defaultBoxColor;
import static com.ded.misle.world.logic.PhysicsEngine.ObjectType.BOX;
import static com.ded.misle.world.boxes.BoxHandling.*;
import static com.ded.misle.world.data.WorldLoader.loadBoxes;
import static com.ded.misle.items.Item.createDroppedItem;
import static com.ded.misle.renderer.MainRenderer.*;

public class Box {
	private int worldX;
	private int worldY;
	public int worldLayer;
	private Direction knockbackDirection;

	private Color color;
	public String textureName;

	private boolean hasCollision;
	private PhysicsEngine.ObjectType objectType;
	public Effect effect;
	private boolean interactsWithPlayer;
	public boolean isMoving = false;

	private double visualRotation = 0;
	private double visualScaleHorizontal;
	private double visualScaleVertical;
	public double visualOffsetX = 0;
	public double visualOffsetY = 0;
	private final SmoothPosition smoothPos = new SmoothPosition(worldX, worldY, originalTileSize);

    private static ArrayList<Box> selectedBoxes;
	private static final Map<String, BufferedImage> cachedTextures = new HashMap<>();
	private static final Map<String, Integer> rotationInstruction = new HashMap<>();
	static {
				rotationInstruction.put("W", 0);
				rotationInstruction.put("D", 90);
				rotationInstruction.put("S", 180);
				rotationInstruction.put("A", 270);
	}

	/**
	 *
	 * @param x original x of the box
	 * @param y original y of the box
	 * @param color color of the box
	 * @param texture texture of the box
	 * @param hasCollision whether the box has collision
	 * @param visualScaleHorizontal how many tilesizes is the box in the x-axis
	 * @param visualScaleVertical how many tilesizes is the box in the y-axis
	 * @param effect what the box does when touched or activated
	 */
	public Box(int x, int y, Color color, String texture, boolean hasCollision, double visualScaleHorizontal, double visualScaleVertical, Effect effect, double rotation, PhysicsEngine.ObjectType objectType, boolean interactsWithPlayer) {
		worldX = x;
		worldY = y;
		World world = player.pos.world;
		world.setPos(this, worldX, worldY);
		this.color = color;
		this.textureName = texture;
		this.hasCollision = hasCollision;
		this.visualScaleHorizontal = visualScaleHorizontal;
		this.visualScaleVertical = visualScaleVertical;
		this.effect = effect;
		this.visualRotation = rotation;
		this.objectType = objectType;
		this.knockbackDirection = Direction.NONE;
		this.interactsWithPlayer = interactsWithPlayer;
	}

	public Box(int x, int y) {
		worldX = x;
		worldY = y;
		World world = player.pos.world;
		world.setPos(this, worldX, worldY);
		this.color = defaultBoxColor;
		this.textureName = "solid";
		this.hasCollision = false;
		this.visualScaleHorizontal = 1;
		this.visualScaleVertical = 1;
		this.effect = null;
		this.visualRotation = 0;
		this.objectType = BOX;
		this.knockbackDirection = Direction.NONE;
		this.interactsWithPlayer = true;
		addBoxToCache(this);
	}

	// For player creation or dummy box
	public Box() {}

	public void draw(Graphics2D g2d, double cameraOffsetX, double cameraOffsetY) {
		updateVisualPosition(20f);
		float renderX = smoothPos.getRenderX();
		float renderY = smoothPos.getRenderY();

		// Apply the camera offset to the current position
		int screenX = (int) (renderX - cameraOffsetX - this.visualOffsetX * originalTileSize);
		int screenY = (int) (renderY - cameraOffsetY - this.visualOffsetY * originalTileSize);
		if (isInvalid(screenX, screenY)) return;

		try {
			if (Objects.equals(this.textureName, "solid")) {
				drawSolid(g2d, screenX, screenY);
			} else if (textureName.equals("invisible")) {
				;
			} else if (BoxHandling.checkIfPresetHasSides(BoxPreset.valueOf(textureName.toUpperCase().split("\\.")[0]))) {
				drawPresetWithSides(g2d, screenX, screenY);
			} else {
				drawRawTexture(g2d, screenX, screenY);
			}
		} catch (IllegalArgumentException e) {
			drawRawTexture(g2d, screenX, screenY);
		}

		try {
			if (selectedBoxes.contains(this)) {
				for (int i = 0; i <= 270; i += 90) {
					System.out.println(i);
					drawRotatedImage(g2d, getTexture("wall_default_overlayW"), screenX, screenY,
							(int) (originalTileSize * visualScaleHorizontal), (int) (originalTileSize * visualScaleVertical), i + this.visualRotation);
				}
			}
		} catch (NullPointerException e) {
			// This just means selectedBoxes is empty
		}
	}

	private void drawSolid(Graphics2D g2d, int screenX, int screenY) {
		g2d.setColor(color);
		Rectangle solidBox = new Rectangle(screenX, screenY, (int) (originalTileSize * visualScaleHorizontal), (int) (originalTileSize * visualScaleVertical));
		drawRotatedRect(g2d, solidBox, this.visualRotation);
	}

	private void drawPresetWithSides(Graphics2D g2d, int screenX, int screenY) {
		// Split texture once and reuse the result
		String[] textureParts = textureName.split("\\.");
		String textureName = textureParts[0].toLowerCase();

		String textureExtra = "";

		try {
			if (textureName.contains("@")) {
				textureExtra = textureName.substring(textureName.indexOf("@") + 1);
				textureName = textureName.substring(0, textureName.indexOf("@"));
			} else {
				drawRotatedImage(g2d, getTexture(textureName), screenX, screenY, (int) (originalTileSize * visualScaleHorizontal), (int) (originalTileSize * visualScaleVertical), this.visualRotation);
			}

			// Draw extras if any
			if (textureParts.length > 3) {
				if (textureParts[3].equals("@")) {
					switch (textureExtra) {
						case "Deco":
							drawRotatedImage(g2d, getTexture(textureName + textureExtra), screenX, screenY, (int) (originalTileSize * visualScaleHorizontal), (int) (originalTileSize * visualScaleVertical), this.visualRotation);
					}
				}
			} else {
				drawRotatedImage(g2d, getTexture(textureName), screenX, screenY, (int) (originalTileSize * visualScaleHorizontal), (int) (originalTileSize * visualScaleVertical), this.visualRotation);
			}

			// Draw sides if they exist
			if (textureParts.length > 1) {
				String sides = textureParts[1];
				String[] eachSide = sides.split("");

				for (String side : eachSide) {
					if (side.isEmpty()) continue;
					drawRotatedImage(g2d, getTexture(textureName + "_overlayW"), screenX, screenY,
						(int) (originalTileSize * visualScaleHorizontal), (int) (originalTileSize * visualScaleVertical), rotationInstruction.get(side) + this.visualRotation);
				}
			}

			// Draw corners if they exist
			if (textureParts.length > 2) {
				String[] eachCorner = textureParts[2].split("");

				for (String corner : eachCorner) {
					if (Objects.equals(corner, "")) {
						continue;
					}
					drawRotatedImage(g2d, getTexture(textureName + "_overlayC"), screenX, screenY,
						(int) (originalTileSize * visualScaleHorizontal), (int) (originalTileSize * visualScaleVertical), rotationInstruction.get(corner) + this.visualRotation);
				}
			}

		} catch (IndexOutOfBoundsException e) {
			// This is fine and not an error; IndexOutOfBounds here mean object has no sides and thus is base image
		}
	}

	private void drawRawTexture(Graphics2D g2d, int screenX, int screenY) {
		if (textureName.contains("@")) {
			textureName = textureName.replace("@", "");
		}
		drawRotatedImage(g2d, this.getTexture(), screenX, screenY, (int) (originalTileSize * visualScaleHorizontal), (int) (originalTileSize * visualScaleVertical), this.visualRotation);
	}

	public static boolean isInvalid(double screenX, double screenY) {
		double margin = originalTileSize * 2;
		return !(screenX >= -margin && screenX <= originalScreenWidth + margin &&
			screenY >= 0 - margin && screenY <= originalScreenHeight + margin);
	}

	public void updateVisualPosition(float speed) {
		smoothPos.setTarget(worldX, worldY, originalTileSize);
		this.smoothPos.update(speed);
	}

	public float getRenderX() {
		return smoothPos.getRenderX();
	}
	public float getRenderY() {
		return smoothPos.getRenderY();
	}

	// COLLISION

	// Check if a point is inside this box
	public boolean isPointColliding(int pointX, int pointY) {
		return pointX == worldX && pointY == worldY;
	}

	public boolean getHasCollision() {
		return hasCollision;
	}

	// BOX POSITION AND SCALING

	public int getX() {
		return worldX;
	}

	public int getY() {
		return worldY;
	}

	public void setX(int x) {
		setPos(x, worldY);
	}

	public void setY(int y) {
		setPos(worldX, y);
	}

	public Point getPos() {
		return new Point(worldX, worldY);
	}

	public void setPos(int x, int y) {
		this.setDirection(
			Direction.interpretDirection
				((x - this.worldX), (y - this.worldY))
		);

		setPos(x, y, -1);
	}

	public void setPos(int x, int y, int layer) {
		this.worldX = x;
		this.worldY = y;
		World world = player.pos.world;

		try {
			world.setPos(this, x, y, layer, false);
		} catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
			loadBoxes();
			world = player.pos.world;
			world.setPos(this, x, y, layer, false);
		}
	}

	public double getVisualScaleHorizontal() {
		return visualScaleHorizontal;
	}

	public double getVisualScaleVertical() {
		return visualScaleVertical;
	}

	public void setVisualRotation(double visualRotation) { this.visualRotation = visualRotation; }

	public double getVisualRotation() { return visualRotation; }

	// BOX CHARACTERISTICS

	public void setColor(Color color) {
		this.color = color;
		if (this instanceof Player) {
			for (ImageManager.ImageName img : playerImages) {
				editImageColor(cachedImages.get(img), color);
			}
		}
	}

	public Color getColor() { return color; }

	public void setCollision(boolean hasCollision) {
		this.hasCollision = hasCollision;
	}

	public void setVisualScaleHorizontal(double visualScaleHorizontal) {
		this.visualScaleHorizontal = visualScaleHorizontal;
	}

	public void setVisualScaleVertical(double visualScaleVertical) {
		this.visualScaleVertical = visualScaleVertical;
	}

	public void setTexture(String texture) {
		this.textureName = texture;
	}

	public BufferedImage getTexture() {
		return getTexture(textureName);
	}


	public static BufferedImage getTexture(String boxTextureName) {
		// Check if the texture is already cached
		if (!cachedTextures.containsKey(boxTextureName)) {
			Path basePath = getPath(com.ded.misle.core.Path.PathTag.RESOURCES).resolve("images/boxes/");
			String fileName = boxTextureName + ".png";
			Path fullPath = basePath.resolve(fileName);

			try {
				// Load the texture and cache it
				BufferedImage texture = ImageIO.read(fullPath.toFile());
				cachedTextures.put(boxTextureName, texture); // Cache the loaded image
			} catch (IOException e) {
				System.out.println("Can't read Box texture input file: " + fullPath);
				return null; // Return null if image fails to load
			}
		}
		return cachedTextures.get(boxTextureName); // Return the cached image
	}

	// EFFECT RELATED

	public Direction getKnockbackDirection() {
		return knockbackDirection;
	}

	public void setKnockbackDirection(Direction knockbackDirection) {
		this.knockbackDirection = knockbackDirection;
	}

	public void spawnItem(boolean canGoMinus, boolean canGoPlus, int id, int count) {
		double randomNumber = Math.random();

		int delay = 550;
		Box droppedItem;
		int multiplier = 0;
		if (canGoMinus && canGoPlus) {
			if (randomNumber > 0.5) {
				multiplier = 1;
			} else {
				multiplier = -1;
			}
		} else if (canGoPlus) {
			multiplier = 1;
		} else if (canGoMinus) {
			multiplier = -1;
		}

		if (Objects.equals(this.textureName, "chest")) {
			this.setTexture("chest_open");
		}

		droppedItem = createDroppedItem(this.getX(), this.getY() - 1, id, count);

		moveBox(droppedItem, multiplier, 1, true);

		((Collectible) droppedItem.effect).collectible = false;

		Timer timer = new Timer((int) (delay * 1.5), e -> {
			((Collectible) droppedItem.effect).collectible = true;
			if (Objects.equals(this.textureName, "chest_open")) {
				this.setTexture("chest");
			}
		});
		timer.setRepeats(false);
		timer.start();
	}

	// Selecting boxes

	public void addSelectedBox() {
		try {
			selectedBoxes.add(this);
		} catch (NullPointerException e) {
			// This just means list is empty, so do nothing
		}
	}

	public void replaceSelectedBox() {
		try {
			clearSelectedBoxes();
			selectedBoxes.add(this);
		} catch (NullPointerException e) {
			// This just means list is empty, so do nothing
		}
	}

	public static void clearSelectedBoxes() {
		try {
			selectedBoxes.clear();
		} catch (NullPointerException e) {
			// This just means list is empty, so do nothing
		}
	}

	public static ArrayList<Box> getSelectedBoxes() {
		try {
			return selectedBoxes;
		} catch (NullPointerException e) {
			// This just means list is empty, so do nothing
		}
		return null;
	}

	// Object type (BOX, HP_BOX)

	public PhysicsEngine.ObjectType getObjectType() {
		return objectType;
	}

	public void setObjectType(PhysicsEngine.ObjectType objectType) {
		this.objectType = objectType;
	}

	// Interacts with player

	public void setInteractsWithPlayer(boolean interactsWithPlayer) {
		this.interactsWithPlayer = interactsWithPlayer;
	}

	public boolean getInteractsWithPlayer() {
		return interactsWithPlayer;
	}

	// Misc

	@Override
	public String toString() {
		return "Box{" +
			"textureName=" + textureName +
			", x=" + worldX +
			", y=" + worldY +
			", z=" + worldLayer +
			", Effect=" + (effect != null ? effect.toString() : "null") +
			'}';
	}
}
