package com.ded.misle.world.boxes;

import com.ded.misle.core.PhysicsEngine;
import com.ded.misle.renderer.ImageManager;
import com.ded.misle.world.World;
import com.ded.misle.world.player.Player;
import com.ded.misle.world.player.PlayerAttributes;

import javax.imageio.ImageIO;
import javax.swing.Timer;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static com.ded.misle.core.SettingsManager.getPath;
import static com.ded.misle.core.GamePanel.*;
import static com.ded.misle.renderer.ImageManager.*;
import static com.ded.misle.world.boxes.BoxManipulation.moveBox;
import static com.ded.misle.world.player.PlayerAttributes.KnockbackDirection.NONE;
import static com.ded.misle.renderer.ColorManager.defaultBoxColor;
import static com.ded.misle.core.PhysicsEngine.ObjectType.BOX;
import static com.ded.misle.world.boxes.BoxHandling.*;
import static com.ded.misle.world.WorldLoader.loadBoxes;
import static com.ded.misle.items.Item.createDroppedItem;
import static com.ded.misle.renderer.MainRenderer.*;

public class Box {
	private int worldX;
	private int worldY;
	public int worldLayer;

	private Color color;
	public String textureName;
	private boolean hasCollision;
	private double boxScaleHorizontal;
	private double boxScaleVertical;
	public Effect effect;
    private static ArrayList<Box> selectedBoxes;
	private PhysicsEngine.ObjectType objectType;
	private PlayerAttributes.KnockbackDirection knockbackDirection;
	boolean interactsWithPlayer;
	public boolean isMoving = false;
	private double visualRotation = 0;
	public double visualOffsetX = 0;
	public double visualOffsetY = 0;

	private static BufferedImage cachedTexture1;
	private static String cachedTexture1Name;
	private static final Map<String, BufferedImage> cachedTexture2 = new HashMap<>();
	private static final Map<String, Integer> rotationInstruction = new HashMap<>();
	static {
				rotationInstruction.put("W", 0);
				rotationInstruction.put("D", 90);
				rotationInstruction.put("S", 180);
				rotationInstruction.put("A", 270);
	}

	/**
	 *
	 * Effect is what the box can do when interacted. If it has collision, the effect will be given after colliding with it.
	 * If the box has no collision, simply walking above it will give the effect. <br><br>
	 *
	 * List of effects: <br><br>
	 *
	 * - "": for no effect <br>
	 * - "damage": for damaging over time. Second value within the list is the damage amount. Third value is the rate
	 * at which the damage is given. The fourth value is the reason of the damage. See {@link HPBox#takeDamage(double, String, String[], PlayerAttributes.KnockbackDirection)}
	 * for a list of reasons.
	 * - "velocity": for changing the speed. Second value is the multiplier of the speed based on player's playerSpeed.
	 *
	 * @param x original x of the box
	 * @param y original y of the box
	 * @param color color of the box
	 * @param texture texture of the box
	 * @param hasCollision a boolean of whether the box has collision
	 * @param boxScaleHorizontal how many tilesizes is the box in the x axis
	 * @param boxScaleVertical how many tilesizes is the box in the y axis
	 * @param effect first value is the type of effect. See above for a list of effects. Set "" if none
	 */
	public Box(int x, int y, Color color, String texture, boolean hasCollision, double boxScaleHorizontal, double boxScaleVertical, Effect effect, double rotation, PhysicsEngine.ObjectType objectType, boolean interactsWithPlayer) {
		worldX = x;
		worldY = y;
		World world = player.pos.world;
		world.setPos(this, worldX, worldY);
		this.color = color;
		this.textureName = texture;
		this.hasCollision = hasCollision;
		this.boxScaleHorizontal = boxScaleHorizontal;
		this.boxScaleVertical = boxScaleVertical;
		this.effect = effect;
		this.visualRotation = rotation;
		this.objectType = objectType;
		this.knockbackDirection = NONE;
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
		this.boxScaleHorizontal = 1;
		this.boxScaleVertical = 1;
		this.effect = null;
		this.visualRotation = 0;
		this.objectType = BOX;
		this.knockbackDirection = NONE;
		this.interactsWithPlayer = true;
		addBoxToCache(this);
	}

	// For player creation or dummy box
	public Box() {}

	// Method to render the box with the current tileSize and scale the position
	public void draw(Graphics2D g2d, double cameraOffsetX, double cameraOffsetY, double boxScaleHorizontal, double boxScaleVertical) {
		double scaledX = worldX * tileSize;
		double scaledY = worldY * tileSize;

		// Apply the camera offset to the scaled position
		int screenX = (int) (scaledX - cameraOffsetX - this.visualOffsetX * tileSize);
		int screenY = (int) (scaledY - cameraOffsetY - this.visualOffsetY * tileSize);

		// Draw the box with the scaled position and tileSize
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
							(int) (tileSize * boxScaleHorizontal), (int) (tileSize * boxScaleVertical), i + this.visualRotation);
				}
			}
		} catch (NullPointerException e) {
			// This just means selectedBoxes is empty
		}
	}

	private void drawSolid(Graphics2D g2d, int screenX, int screenY) {
		g2d.setColor(color);
		Rectangle solidBox = new Rectangle(screenX, screenY, (int) (tileSize * boxScaleHorizontal), (int) (tileSize * boxScaleVertical));
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
				drawRotatedImage(g2d, getTexture(textureName), screenX, screenY, (int) (tileSize * boxScaleHorizontal), (int) (tileSize * boxScaleVertical), this.visualRotation);
			}

			// Draw extras if any
			if (textureParts.length > 3) {
				if (textureParts[3].equals("@")) {
					switch (textureExtra) {
						case "Deco":
							drawRotatedImage(g2d, getTexture(textureName + textureExtra), screenX, screenY, (int) (tileSize * boxScaleHorizontal), (int) (tileSize * boxScaleVertical), this.visualRotation);
					}
				}
			} else {
				drawRotatedImage(g2d, getTexture(textureName), screenX, screenY, (int) (tileSize * boxScaleHorizontal), (int) (tileSize * boxScaleVertical), this.visualRotation);
			}

			// Draw sides if they exist
			if (textureParts.length > 1) {
				String sides = textureParts[1];
				String[] eachSide = sides.split("");

				for (String side : eachSide) {
					if (side.isEmpty()) continue;
					drawRotatedImage(g2d, getTexture(textureName + "_overlayW"), screenX, screenY,
						(int) (tileSize * boxScaleHorizontal), (int) (tileSize * boxScaleVertical), rotationInstruction.get(side) + this.visualRotation);
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
						(int) (tileSize * boxScaleHorizontal), (int) (tileSize * boxScaleVertical), rotationInstruction.get(corner) + this.visualRotation);
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
		drawRotatedImage(g2d, this.getTexture(), screenX, screenY, (int) (tileSize * boxScaleHorizontal), (int) (tileSize * boxScaleVertical), this.visualRotation);
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

	public void setPos(int x, int y) {
		setPos(x, y, 0);
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

	public double getBoxScaleHorizontal() {
		return boxScaleHorizontal;
	}

	public double getBoxScaleVertical() {
		return boxScaleVertical;
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

	public void setHasCollision(boolean hasCollision) {
		this.hasCollision = hasCollision;
	}

	public void setBoxScaleHorizontal(double boxScaleHorizontal) {
		this.boxScaleHorizontal = boxScaleHorizontal;
	}

	public void setBoxScaleVertical(double boxScaleVertical) {
		this.boxScaleVertical = boxScaleVertical;
	}

	public void setTexture(String texture) {
		this.textureName = texture;
	}

	public BufferedImage getTexture() {
		String fileName = this.textureName + ".png";
		Path fullPath = getPath().resolve("resources/images/boxes/").resolve(fileName);

		// Only reload the texture if the cached texture doesn't match the current texture
		if (cachedTexture1 == null || !cachedTexture1Name.equals(fileName)) {
			try {
				cachedTexture1 = ImageIO.read(fullPath.toFile());
				cachedTexture1Name = fileName; // Store the current texture name
			} catch (IOException e) {
				System.out.println("Couldn't find box texture " + fullPath + "!");
				return null;
			}
		}
		return cachedTexture1;
	}


	public static BufferedImage getTexture(String boxTextureName) {
		// Check if the texture is already cached
		if (!cachedTexture2.containsKey(boxTextureName)) {
			Path basePath = getPath().resolve("resources/images/boxes/");
			String fileName = boxTextureName + ".png";
			Path fullPath = basePath.resolve(fileName);

			try {
				// Load the texture and cache it
				BufferedImage texture = ImageIO.read(fullPath.toFile());
				cachedTexture2.put(boxTextureName, texture); // Cache the loaded image
			} catch (IOException e) {
				System.out.println("Can't read Box texture input file: " + fullPath);
				return null; // Return null if image fails to load
			}
		}
		return cachedTexture2.get(boxTextureName); // Return the cached imageaddBox(BoxPreset.WALL_DEFAULT)
	}

	// EFFECT RELATED

	public PlayerAttributes.KnockbackDirection getKnockbackDirection() {
		return knockbackDirection;
	}

	public void setKnockbackDirection(PlayerAttributes.KnockbackDirection knockbackDirection) {
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

		moveBox(droppedItem, multiplier, 1, delay, true);

		((Effect.Collectible) droppedItem.effect).collectible = false;

		Timer timer = new Timer((int) (delay * 1.5), e -> {
			((Effect.Collectible) droppedItem.effect).collectible = true;
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
	
	public static void editSelectedBoxes(EditBoxKeys key, String value) {
		try {
			for (Box box : selectedBoxes) {
				editBox(box, key, value);
			}
		} catch (NullPointerException e) {
			// This just means list is empty, so do nothing
		}
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
}
