package com.ded.misle.boxes;

import com.ded.misle.GameRenderer;
import com.ded.misle.player.PlayerAttributes;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.ded.misle.ChangeSettings.getPath;
import static com.ded.misle.GamePanel.player;

public class Box {
	private final double originalX; // The original world position (unscaled)
	private final double originalY; // The original world position (unscaled)
	private double currentX; // The current world position (unscaled)
	private double currentY; // The current world position (unscaled)
	private Color color;
	private String texture;
	private boolean hasCollision;
	private double boxScaleHorizontal;
	private double boxScaleVertical;
	private String[] effect;
	private long lastDamageTime = 0;

	private BufferedImage cachedTexture1;
	private Map<String, BufferedImage> cachedTexture2 = new HashMap<>();
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
	public Box(double x, double y, Color color, String texture, boolean hasCollision, double boxScaleHorizontal, double boxScaleVertical, String[] effect) {
		this.originalX = x;
		this.originalY = y;
		this.currentX = this.originalX;
		this.currentY = this.originalY;
		this.color = color;
		this.texture = texture;
		this.hasCollision = hasCollision;
		this.boxScaleHorizontal = boxScaleHorizontal;
		this.boxScaleVertical = boxScaleVertical;
		this.effect = effect;
	}

	public Box(double x, double y) {
		this.originalX = x;
		this.originalY = y;
		this.currentX = this.originalX;
		this.currentY = this.originalY;
		this.color = new Color(255, 255, 255);
		this.texture = "solid";
		this.hasCollision = false;
		this.boxScaleHorizontal = 1;
		this.boxScaleVertical = 1;
		this.effect = new String[]{""};
	}

	// Method to render the box with the current tileSize and scale the position
	public void draw(Graphics2D g2d, double cameraOffsetX, double cameraOffsetY, double scale, int tileSize, double boxScaleHorizontal, double boxScaleVertical) {
		// Scale the position based on the current scale
		double scaledX = currentX * scale;
		double scaledY = currentY * scale;

		// Apply the camera offset to the scaled position
		int screenX = (int) (scaledX - cameraOffsetX);
		int screenY = (int) (scaledY - cameraOffsetY);

		// Draw the box with the scaled position and tileSize
		if (Objects.equals(this.texture, "solid")) {
			g2d.setColor(color);
			g2d.fillRect(screenX, screenY, (int) (tileSize * boxScaleHorizontal), (int) (tileSize * boxScaleVertical));
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
					g2d.drawImage(this.getTexture(textureName), screenX, screenY, (int) (tileSize * boxScaleHorizontal), (int) (tileSize * boxScaleVertical), null);
				}

				// Draw extras if any
				if (textureParts.length > 3) {
					if (textureParts[3].equals("@")) {
						switch (textureExtra) {
							case "Deco":
								g2d.drawImage(this.getTexture(textureName + textureExtra), screenX, screenY, (int) (tileSize * boxScaleHorizontal), (int) (tileSize * boxScaleVertical), null);
						}
					}
				} else {
					g2d.drawImage(this.getTexture(textureName), screenX, screenY, (int) (tileSize * boxScaleHorizontal), (int) (tileSize * boxScaleVertical), null);
				}

				// Draw sides if they exist
				if (textureParts.length > 1) {
					String sides = textureParts[1];
					String[] eachSide = sides.split("");

					for (String side : eachSide) {
						GameRenderer.drawRotatedImage(g2d, getTexture(textureName + "OverlayW"), screenX, screenY,
								(int) (tileSize * boxScaleHorizontal), (int) (tileSize * boxScaleVertical), rotationInstruction.get(side));
					}
				}

				// Draw corners if they exist
				if (textureParts.length > 2) {
					String[] eachCorner = textureParts[2].split("");

					for (String corner : eachCorner) {
						GameRenderer.drawRotatedImage(g2d, getTexture(textureName + "OverlayC"), screenX, screenY,
								(int) (tileSize * boxScaleHorizontal), (int) (tileSize * boxScaleVertical), rotationInstruction.get(corner));
					}
				}

			} catch (IndexOutOfBoundsException e) {
				// This is fine and not an error; IndexOutOfBounds here mean object has no sides and thus is base image
			}
		} else {
			if (texture.contains("@")) {
				texture.replace("@", "");
			}
			g2d.drawImage(this.getTexture(), screenX, screenY, (int) (tileSize * boxScaleHorizontal), (int) (tileSize * boxScaleVertical), null);
		}
	}

	// COLLISION

	// Check if a point (e.g., player) is inside this box (adjusted for the new scale)
	public boolean isPointColliding(double pointX, double pointY, double scale, double objectWidth, double objectHeight) {
		double scaledX = currentX * scale;
		double scaledY = currentY * scale;
		return pointX >= scaledX && pointX <= scaledX + objectWidth * boxScaleHorizontal && pointY >= scaledY && pointY <= scaledY + objectHeight * boxScaleVertical;
	}

	public boolean getHasCollision() {
		return hasCollision;
	}

	// BOX POSITION AND SCALING

	/**
	 * Is unscaled.
	 */
	public double getOriginalX() {
        return originalX;
	}

	/**
	 * Is unscaled.
	 */
    public double getOriginalY() {
      return originalY;
    }

	/**
	 * Is unscaled.
	 */
	public double getCurrentX() {
		return currentX;
	}

	/**
	 * Is unscaled.
	 */
	public double getCurrentY() {
		return currentY;
	}

	/**
	 * Is unscaled.
	 */
	public void setCurrentX(double x) {
		this.currentX = x;
	}

	/**
	 * Is unscaled.
	 */
	public void setCurrentY(double y) {
		this.currentY = y;
	}

	public double getBoxScaleHorizontal() {
		return boxScaleHorizontal;
	}

	public double getBoxScaleVertical() {
		return boxScaleVertical;
	}

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
		return switch (getEffect()) {
			case "damage" -> Double.parseDouble(effect[1]);
			case "velocity" -> Double.parseDouble(effect[1]);
			case "heal" -> Double.parseDouble(effect[1]);
			default -> 0;
		};
	}

	public double getEffectRate() {
		return switch (getEffect()) {
			case "damage" -> Double.parseDouble(effect[2]);
			case "heal" -> Double.parseDouble(effect[2]);
			default -> 0;
		};
	}

	public void setEffect(String effect) {
		this.effect[0] = effect;
	}

	public void setEffect(String[] effect) {
		this.effect = effect;
	}

	public static void handleEffect(Box box) {
		switch (box.effect[0]) {
			case "damage" -> handleBoxDamageCooldown(box);
			case "heal" -> handleBoxHealCooldown(box);
			case "velocity" -> handleBoxVelocity(box);
			case "spawnpoint" -> handleBoxCheckpoint(box);
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
			default -> "";
		};
	}

	public void setEffectReason(String reason) {
		switch (reason) {
			case "damage" -> this.effect[3] = reason;
			case "heal" -> this.effect[3] = reason;
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

	// EFFECT HANDLING

	public long getLastEffectTime() {
		return lastDamageTime;
	}

	public void setLastEffectTime(long lastDamageTime) {
		this.lastDamageTime = lastDamageTime;
	}

	private static void handleBoxDamageCooldown(Box box) {
		long currentTime = System.currentTimeMillis();
		long cooldownDuration = (long) box.getEffectRate(); // Use the box's damage rate for cooldown

		// Check if enough time has passed since the last damage was dealt
		if (currentTime - box.getLastEffectTime() >= cooldownDuration) {
			box.setLastEffectTime(currentTime); // Update the last damage time
			player.attr.takeDamage(box.getEffectValue(), box.getEffectReason(), box.getEffectArgs());
//			System.out.println(box.getEffectValue() + " " + box.getEffectReason() + " damage dealt! Now at " + player.attr.getPlayerHP() + " HP.");
		}
	}

	private static void handleBoxHealCooldown(Box box) {
		long currentTime = System.currentTimeMillis();
		long cooldownDuration = (long) box.getEffectRate();

		if (currentTime - box.getLastEffectTime() >= cooldownDuration) {
			box.setLastEffectTime(currentTime);
			player.attr.receiveHeal(box.getEffectValue(), box.getEffectReason());
//			System.out.println(box.getEffectValue() + " " + box.getEffectReason() + " heal received! Now at " + player.attr.getPlayerHP() + " HP.");
		}
	}

	private static void handleBoxVelocity(Box box) {
		player.attr.setPlayerEnvironmentSpeedModifier(box.getEffectValue());
		player.attr.setLastVelocityBox(box);
	}

	private static void handleBoxCheckpoint(Box box) {
		if ((Objects.equals(box.effect[1], "-1") || Integer.parseInt(box.effect[1]) > 0) && !Arrays.equals(player.pos.getSpawnpoint(), new double[]{box.getCurrentX(), box.getCurrentY()})) {
			player.pos.setSpawnpoint(box.getCurrentX(), box.getCurrentY());
			System.out.println("Saved spawnpoint as " + box.getCurrentX() + ", " + box.getCurrentY());
			if (Integer.parseInt(box.effect[1]) > 0) {
				box.effect[1] = String.valueOf(Integer.parseInt(box.effect[1]) - 1);
			}
		}
	}

	public BufferedImage getTexture() {
		if (cachedTexture1 == null) { // Load the texture only once
			Path basePath = getPath().resolve("resources/images/boxes/");
			String fileName = this.texture + ".png";
			Path fullPath = basePath.resolve(fileName);

			try {
				cachedTexture1 = ImageIO.read(fullPath.toFile()); // Load and cache the image
			} catch (IOException e) {
				e.printStackTrace();
				return null; // Return null if image fails to load
			}
		}
		return cachedTexture1; // Return cached image
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
}
