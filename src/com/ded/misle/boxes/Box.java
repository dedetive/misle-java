package com.ded.misle.boxes;

import com.ded.misle.GameRenderer;
import com.ded.misle.Physics;
import com.ded.misle.player.PlayerAttributes;

import javax.imageio.ImageIO;
import javax.swing.Timer;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static com.ded.misle.AudioPlayer.playThis;
import static com.ded.misle.ChangeSettings.getPath;
import static com.ded.misle.GamePanel.*;
import static com.ded.misle.GameRenderer.fadeIn;
import static com.ded.misle.GameRenderer.fadeOut;
import static com.ded.misle.Launcher.scale;
import static com.ded.misle.Physics.ObjectType.BOX;
import static com.ded.misle.Physics.ObjectType.HP_BOX;
import static com.ded.misle.boxes.BoxManipulation.moveBox;
import static com.ded.misle.boxes.BoxesHandling.*;
import static com.ded.misle.boxes.BoxesLoad.loadBoxes;
import static com.ded.misle.boxes.BoxesLoad.unloadBoxes;
import static com.ded.misle.chests.ChestTables.getChestDropID;
import static com.ded.misle.items.Item.createDroppedItem;
import static com.ded.misle.items.Item.createItem;
import static java.lang.System.currentTimeMillis;

public class Box {
	private double x; // The current world position (unscaled)
	private double y; // The current world position (unscaled)
	private Color color;
	private String texture;
	private boolean hasCollision;
	private double boxScaleHorizontal;
	private double boxScaleVertical;
	private String[] effect;
	private long lastDamageTime = 0;
	private double rotation = 0;
	private static ArrayList<Box> selectedBoxes;
	private double maxHP;
	private double HP;
	private Physics.ObjectType objectType;

	private BufferedImage cachedTexture1;
	private String cachedTexture1Name;
	private final Map<String, BufferedImage> cachedTexture2 = new HashMap<>();
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
	 * at which the damage is given. The fourth value is the reason of the damage. See {@link PlayerAttributes#takeDamage(double, String, String[])}
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
	public Box(double x, double y, Color color, String texture, boolean hasCollision, double boxScaleHorizontal, double boxScaleVertical, String[] effect, double rotation, double maxHP, double HP, Physics.ObjectType objectType) {
		this.x = x;
		this.y = y;
		this.color = color;
		this.texture = texture;
		this.hasCollision = hasCollision;
		this.boxScaleHorizontal = boxScaleHorizontal;
		this.boxScaleVertical = boxScaleVertical;
		this.effect = effect;
		this.rotation = rotation;
		this.maxHP = maxHP;
		this.HP = HP;
		this.objectType = objectType;
	}

	public Box(double x, double y) {
		this.x = x;
		this.y = y;
		this.color = new Color(255, 255, 255);
		this.texture = "solid";
		this.hasCollision = false;
		this.boxScaleHorizontal = 1;
		this.boxScaleVertical = 1;
		this.effect = new String[]{""};
		this.rotation = 0;
		this.maxHP = -1;
		this.HP = -1;
		this.objectType = BOX;
	}

	// For player creation
	public Box() {
		this.setTexture("solid");
		this.setColor(new Color(255, 255, 255));
		this.setObjectType(Physics.ObjectType.PLAYER);
		this.hasCollision = true;
	}

	// Method to render the box with the current tileSize and scale the position
	public void draw(Graphics2D g2d, double cameraOffsetX, double cameraOffsetY, double scale, int tileSize, double boxScaleHorizontal, double boxScaleVertical) {
		// Scale the position based on the current scale
		double scaledX = x * scale;
		double scaledY = y * scale;

		// Apply the camera offset to the scaled position
		int screenX = (int) (scaledX - cameraOffsetX);
		int screenY = (int) (scaledY - cameraOffsetY);

		// Draw the box with the scaled position and tileSize
		if (Objects.equals(this.texture, "solid")) {
			g2d.setColor(color);
			Rectangle solidBox = new Rectangle(screenX, screenY, (int) (tileSize * boxScaleHorizontal), (int) (tileSize * boxScaleVertical));
			GameRenderer.drawRotatedRect(g2d, solidBox, this.rotation);
		} else if (texture.equals("invisible")) {
			;
		} else if (BoxesHandling.checkIfPresetHasSides(texture.split("\\.")[0])) {
			// Split texture once and reuse the result
			String[] textureParts = texture.split("\\.");
			String textureName = textureParts[0];
			String textureExtra = "";

			try {
				if (textureName.contains("@")) {
					textureExtra = textureName.substring(textureName.indexOf("@") + 1);
					textureName = textureName.substring(0, textureName.indexOf("@"));
				} else {
					GameRenderer.drawRotatedImage(g2d, this.getTexture(textureName), screenX, screenY, (int) (tileSize * boxScaleHorizontal), (int) (tileSize * boxScaleVertical), this.rotation);
				}

				// Draw extras if any
				if (textureParts.length > 3) {
					if (textureParts[3].equals("@")) {
						switch (textureExtra) {
							case "Deco":
								GameRenderer.drawRotatedImage(g2d, this.getTexture(textureName + textureExtra), screenX, screenY, (int) (tileSize * boxScaleHorizontal), (int) (tileSize * boxScaleVertical), this.rotation);
						}
					}
				} else {
					GameRenderer.drawRotatedImage(g2d, this.getTexture(textureName), screenX, screenY, (int) (tileSize * boxScaleHorizontal), (int) (tileSize * boxScaleVertical), this.rotation);
				}

				// Draw sides if they exist
				if (textureParts.length > 1) {
					String sides = textureParts[1];
					String[] eachSide = sides.split("");

					for (String side : eachSide) {
						GameRenderer.drawRotatedImage(g2d, getTexture(textureName + "OverlayW"), screenX, screenY,
								(int) (tileSize * boxScaleHorizontal), (int) (tileSize * boxScaleVertical), rotationInstruction.get(side) + this.rotation);
					}
				}

				// Draw corners if they exist
				if (textureParts.length > 2) {
					String[] eachCorner = textureParts[2].split("");

					for (String corner : eachCorner) {
						if (Objects.equals(corner, "")) {
							continue;
						}
						GameRenderer.drawRotatedImage(g2d, getTexture(textureName + "OverlayC"), screenX, screenY,
								(int) (tileSize * boxScaleHorizontal), (int) (tileSize * boxScaleVertical), rotationInstruction.get(corner) + this.rotation);
					}
				}

			} catch (IndexOutOfBoundsException e) {
				// This is fine and not an error; IndexOutOfBounds here mean object has no sides and thus is base image
			}
		} else {
			if (texture.contains("@")) {
				texture = texture.replace("@", "");
			}
			GameRenderer.drawRotatedImage(g2d, this.getTexture(), screenX, screenY, (int) (tileSize * boxScaleHorizontal), (int) (tileSize * boxScaleVertical), this.rotation);
		}

		try {
			if (selectedBoxes.contains(this)) {
				for (int i = 0; i <= 270; i += 90) {
					System.out.println(i);
					GameRenderer.drawRotatedImage(g2d, getTexture("wallDefaultOverlayW"), screenX, screenY,
							(int) (tileSize * boxScaleHorizontal), (int) (tileSize * boxScaleVertical), i + this.rotation);
				}
			}
		} catch (NullPointerException e) {
			// This just means selectedBoxes is empty
		}
	}

	// COLLISION

	// Check if a point (e.g., player) is inside this box (adjusted for the new scale)
	public boolean isPointColliding(double pointX, double pointY, double scale, double objectWidth, double objectHeight) {
		double scaledX = x * scale;
		double scaledY = y * scale;
		return pointX >= scaledX && pointX <= scaledX + objectWidth * boxScaleHorizontal && pointY >= scaledY && pointY <= scaledY + objectHeight * boxScaleVertical;
	}

	public boolean getHasCollision() {
		return hasCollision;
	}

	// BOX POSITION AND SCALING

	/**
	 * Is unscaled.
	 */
	public double getX() {
		return x;
	}

	/**
	 * Is unscaled.
	 */
	public double getY() {
		return y;
	}

	/**
	 * Is unscaled.
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * Is unscaled.
	 */
	public void setY(double y) {
		this.y = y;
	}

	public double getBoxScaleHorizontal() {
		return boxScaleHorizontal;
	}

	public double getBoxScaleVertical() {
		return boxScaleVertical;
	}

	public void setRotation(double rotation) { this.rotation = rotation; }

	public double getRotation() { return rotation; }

	// BOX CHARACTERISTICS

	public void setColor(Color color) {
		this.color = color;
	}

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
		this.texture = texture;
	}

	// EFFECTS

	public String getEffect() {
		return effect[0];
	}

	public double getEffectValue() {
		switch (getEffect()) {
			case "damage":
			case "velocity":
			case "heal":
				return Double.parseDouble(effect[1]);
			case "travel":
				return Integer.parseInt(effect[1]);
			default:
				return 0;
		}
	}

	public double getEffectRate() {
		switch (getEffect()) {
			case "damage":
			case "heal":
				return Double.parseDouble(effect[2]);
			case "chest":
				return 1000 * Double.parseDouble(effect[1]);
			default:
				return 0;
		}
	}

	public void setEffect(String effect) {
		this.effect[0] = effect;
	}

	public void setEffect(String[] effect) {
		this.effect = effect;
	}

	public void handleEffect(Physics.ObjectType objectType) {
		switch (this.effect[0]) {
			case "damage" -> this.handleBoxDamageCooldown();
			case "heal" -> this.handleBoxHealCooldown();
			case "velocity" -> this.handleBoxVelocity();
			case "spawnpoint" -> this.handleBoxSpawnpoint();
			case "chest" -> this.handleBoxChest();
			case "item" -> this.handleBoxItemCollectible();
			case "travel" -> handleBoxTravel();
		}
	}

	public void setEffectValue(double effectValue) {
		this.effect[1] = String.valueOf(effectValue);
	}

	public void setEffectRate(double effectRate) {
		this.effect[2] = String.valueOf(effectRate);
	}

	public String getEffectReason() {
		return switch (getEffect()) {
			case "damage" -> this.effect[3];
			case "heal" -> this.effect[3];
			case "item" -> this.effect[3];
			default -> "";
		};
	}

	public void setEffectReason(String reason) {
		switch (reason) {
			case "damage" -> this.effect[3] = reason;
			case "heal" -> this.effect[3] = reason;
			default -> this.effect[3] = reason;
		}
	}

	public String[] getEffectArgs() {
		return switch (getEffect()) {
			case "damage" -> new String[]{this.effect[4]};
			default -> new String[]{""};
		};
	}

	public void setEffectArgs(String[] args) {
		switch (getEffect()) {
			case "damage" -> this.effect[4] = Arrays.toString(args);
		}
	}

	public long getLastEffectTime() {
		return lastDamageTime;
	}

	public void setLastEffectTime(long lastDamageTime) {
		this.lastDamageTime = lastDamageTime;
	}


	public BufferedImage getTexture() {
		String fileName = this.texture + ".png";
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


	public BufferedImage getTexture(String boxTextureName) {
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
				e.printStackTrace();
				return null; // Return null if image fails to load
			}
		}
		return cachedTexture2.get(boxTextureName); // Return the cached image
	}

	// EFFECT HANDLING

	private void handleBoxDamageCooldown() {
		long currentTime = currentTimeMillis();
		long cooldownDuration = (long) this.getEffectRate(); // Use the box's damage rate for cooldown

		// Check if enough time has passed since the last damage was dealt
		if (currentTime - this.getLastEffectTime() >= cooldownDuration) {
			this.setLastEffectTime(currentTime); // Update the last damage time
			player.attr.takeDamage(this.getEffectValue(), this.getEffectReason(), this.getEffectArgs());
//			System.out.println(box.getEffectValue() + " " + box.getEffectReason() + " damage dealt! Now at " + player.attr.getHP() + " HP.");
		}
	}

	private void handleBoxHealCooldown() {
		long currentTime = currentTimeMillis();
		long cooldownDuration = (long) this.getEffectRate();

		if (currentTime - this.getLastEffectTime() >= cooldownDuration) {
			this.setLastEffectTime(currentTime);
			player.attr.receiveHeal(this.getEffectValue(), this.getEffectReason());
//			System.out.println(box.getEffectValue() + " " + box.getEffectReason() + " heal received! Now at " + player.attr.getHP() + " HP.");
		}
	}

	private void handleBoxVelocity() {
		player.attr.setEnvironmentSpeedModifier(this.getEffectValue());
		player.attr.setLastVelocityBox(this);
	}

	private void handleBoxSpawnpoint() {
		if ((Objects.equals(this.effect[1], "-1") || Integer.parseInt(this.effect[1]) > 0) && player.pos.getSpawnpoint() != player.pos.getRoomID()) {
			player.pos.setSpawnpoint(player.pos.getRoomID());
			System.out.println("Saved spawnpoint as room " + player.pos.getRoomID());
			if (Integer.parseInt(this.effect[1]) > 0) {
				this.effect[1] = String.valueOf(Integer.parseInt(this.effect[1]) - 1);
			}
		}
	}

	private void handleBoxChest() {
		long currentTime = currentTimeMillis();
		long cooldownDuration = (long) this.getEffectRate();

		if (currentTime - this.getLastEffectTime() >= cooldownDuration) {
			this.setLastEffectTime(currentTime);
			int[] results = getChestDropID(this.effect[2]);
			int id = results[0];
			int count = results[1];
			boolean canGoMinus = false;
			boolean canGoPlus = false;
			if (getCollisionBoxesInRange(this.x - 20, this.y * scale, 0, 6).isEmpty()) {
				canGoMinus = true;
			}
			if (getCollisionBoxesInRange(this.x + 20, this.y * scale, 0, 6).isEmpty()) {
				canGoPlus = true;
			}

			this.boxSpawnItem(canGoMinus, canGoPlus, id, count);
		}
	}

	private void boxSpawnItem(boolean canGoMinus, boolean canGoPlus, int id, int count) {
		double randomNumber = Math.random();

		int delay = 750;
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
		System.out.println("ID: " + id + "\nCount: " + count + "\nmultiplier: " + multiplier);

		this.setTexture("chestOpen");

		droppedItem = createDroppedItem(this.getX(), this.getY() - 10, id, count);
		moveBox(droppedItem, multiplier * 20, 10, delay);

		editLastBox(EditBoxKeys.COLLECTIBLE, "false");

		Timer timer = new Timer((int) (delay * 1.5), e -> {
			editBox(droppedItem, EditBoxKeys.COLLECTIBLE, "true");
			this.setTexture("chest");
		});
		timer.setRepeats(false);
		timer.start();
	}

	private void handleBoxItemCollectible() {
		if (this.getEffectReason().equals("false")) {
			return;
		}
		double xDistance = Math.abs(this.getX() - player.getX() / scale);
		double yDistance = Math.abs(this.getY() - player.getY() / scale);
		double totalDistance = Math.sqrt(Math.pow(xDistance, 2) + Math.pow(yDistance, 2));

		if (totalDistance < 30) {
			playThis("collectItem");
			player.inv.addItem(createItem(Integer.parseInt(this.effect[1]), Integer.parseInt(this.effect[2])));
			GameRenderer.updateSelectedItemNamePosition();
			deleteBox(this);
		}
	}

	private void handleBoxTravel() {
		fadeIn();
		gameState = GameState.FROZEN_PLAYING;
		Timer fadingIn = new Timer(750, e -> {
			int newRoomID = (int) getEffectValue();
			player.pos.setRoomID(newRoomID);
			player.setX(scale * Double.parseDouble(this.effect[2]));
			player.setY(scale * Double.parseDouble(this.effect[3]));
			unloadBoxes();
			loadBoxes();

			Timer loadWait = new Timer(200, evt -> {
				fadeOut();
				gameState = GameState.PLAYING;
			});
			loadWait.setRepeats(false);
			loadWait.start();
		});
		fadingIn.setRepeats(false);
		fadingIn.start();
	}

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

	// HP

	public void setHP(double HP) {
		this.HP = HP;
		checkIfDead();
	}

	public void damageBox(double damage) {
		this.HP -= Math.max(damage, 0);
		checkIfDead();
	}

	public void setMaxHP(double maxHP) {
		this.maxHP = maxHP;
		if (maxHP == -1) {
			setObjectType(BOX);
		} else {
			setObjectType(HP_BOX);
		}
	}

	public double getHP() {
		return HP;
	}

	public double getMaxHP() {
		return maxHP;
	}

	public boolean checkIfDead() {
		if (this.HP == 0) {
			deleteBox(this);
			return true;
		}
		return false;
	}

	// Object type (BOX, HP_BOX)

	public Physics.ObjectType getObjectType() {
		return objectType;
	}

	public void setObjectType(Physics.ObjectType objectType) {
		this.objectType = objectType;
	}
}
