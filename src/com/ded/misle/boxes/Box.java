package com.ded.misle.boxes;

import java.awt.*;
import java.util.Arrays;
import java.util.Objects;

import static com.ded.misle.GamePanel.player;

public class Box {
	private final double originalX; // The original world position (unscaled)
	private final double originalY; // The original world position (unscaled)
	private final Color color;
	private final boolean hasCollision;
	private final double boxScaleHorizontal;
	private final double boxScaleVertical;
	private final String[] effect;
	private long lastDamageTime = 0;

	/**
	 *
	 * Effect is what the box can do when interacted. If it has collision, the effect will be given after colliding with it.
	 * If the box has no collision, simply walking above it will give the effect. <br><br>
	 *
	 * List of effects: <br><br>
	 *
	 * - "": for no effect <br>
	 * - "damage": for damaging over time. Second value within the list is the damage amount. Third value is the rate
	 * at which the damage is given. The fourth value is the reason of the damage. See {@link com.ded.misle.player.PlayerAttributes#takeDamage(double, String, String[])}
	 * for a list of reasons.
	 * - "velocity": for changing the speed. Second value is the multiplier of the speed based on player's playerSpeed.
	 *
	 * @param x original x of the box
	 * @param y original y of the box
	 * @param color color of the box
	 * @param hasCollision a boolean of whether the box has collision
	 * @param boxScaleHorizontal how many tilesizes is the box in the x axis
	 * @param boxScaleVertical how many tilesizes is the box in the y axis
	 * @param effect first value is the type of effect. See above for a list of effects. Set "" if none
	 */
	public Box(double x, double y, Color color, boolean hasCollision, double boxScaleHorizontal, double boxScaleVertical, String[] effect) {
		this.originalX = x; // Store the original position
		this.originalY = y; // Store the original position
		this.color = color;
		this.hasCollision = hasCollision;
		this.boxScaleHorizontal = boxScaleHorizontal;
		this.boxScaleVertical = boxScaleVertical;
		this.effect = effect;
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

	// EFFECTS

	public String getEffect() {
		return effect[0];
	}

	public double getEffectValue() {
		if (getEffect().equals("damage")) {
			return Double.parseDouble(effect[1]);
		}
		if (getEffect().equals("velocity")) {
			return Double.parseDouble(effect[1]);
		}
		return 0;
	}

	public double getEffectRate() {
		if (getEffect().equals("damage")) {
			return Double.parseDouble(effect[2]);
		}
		return 0;
	}

	public void setEffect(String effect) {
		this.effect[0] = effect;
	}

	public static void handleEffect(Box box) {
		if (box.effect[0].equals("damage")) {
			handleBoxDamageCooldown(box);
		}
		if (box.effect[0].equals("velocity")) {
			handleBoxVelocity(box);
		}
		if (box.effect[0].equals("spawnpoint")) {
			handleBoxCheckpoint(box);
		}
	}

	public void setEffectValue(double effectValue) {
		this.effect[1] = String.valueOf(effectValue);
	}

	public void setEffectRate(double effectRate) {
		this.effect[2] = String.valueOf(effectRate);
	}

	public String getEffectReason() {
		if (getEffect().equals("damage")) {
			return this.effect[3];
		}
		return "";
	}

	public void setEffectReason(String reason) {
		if (reason.equals("damage")) {
			this.effect[3] = reason;
		}
	}

	public String[] getEffectArgs() {
		if (getEffect().equals("damage")) {
			return new String[]{this.effect[4]};
		}
		return new String[]{""};
	}

	public void setEffectArgs(String[] args) {
		if (getEffect().equals("damage")) {
			this.effect[4] = Arrays.toString(args);
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
			box.setEffectReason(box.getEffectReason());
			player.attr.takeDamage(box.getEffectValue(), box.getEffectReason(), box.getEffectArgs());
			System.out.println(box.getEffectValue() + " " + box.getEffectReason() + " damage dealt! Now at " + player.attr.getPlayerHP() + " HP.");
		}
	}

	private static void handleBoxVelocity(Box box) {
		player.attr.setPlayerEnvironmentSpeedModifier(box.getEffectValue());
		player.attr.setLastVelocityBox(box);
	}

	private static void handleBoxCheckpoint(Box box) {
		if ((Objects.equals(box.effect[1], "-1") || Integer.parseInt(box.effect[1]) > 0) && !Arrays.equals(player.pos.getSpawnpoint(), new double[]{box.getOriginalX(), box.getOriginalY()})) {
			player.pos.setSpawnpoint(box.getOriginalX(), box.getOriginalY());
			System.out.println("Saved spawnpoint as " + box.getOriginalX() + ", " + box.getOriginalY());
			if (Integer.parseInt(box.effect[1]) > 0) {
				box.effect[1] = String.valueOf(Integer.parseInt(box.effect[1]) - 1);
			}
		}
	}
}
