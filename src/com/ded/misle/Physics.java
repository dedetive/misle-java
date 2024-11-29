package com.ded.misle;

import com.ded.misle.boxes.Box;
import com.ded.misle.boxes.BoxesHandling;

import java.util.List;

import static com.ded.misle.Launcher.levelDesigner;
import static com.ded.misle.Launcher.scale;

public class Physics {
	/**
	 * This moves the player by x, oftentimes being the playerSpeed, or by y.
	 * Set the other as 0, unless you intend to move the player diagonally.
	 * <p></p>
	 * Example use:
	 * movePlayer(playerSpeed, 0);
	 *
	 * @param x double - How many pixels in x direction (this is not based on scale).
	 * @param y double - How many pixels in y direction (this is not based on scale).
	 */
	public static void movePlayer(double x, double y) {
		GamePanel.player.pos.setX(GamePanel.player.pos.getX() + x);
		GamePanel.player.pos.setY(GamePanel.player.pos.getY() + y);
		GamePanel.player.stats.increaseDistance(x, y);

		if (!levelDesigner) {
			if (GamePanel.player.attr.getLastVelocityBox() != null) {
				GamePanel.player.attr.setEnvironmentSpeedModifier(1.0); // Reset to default speed
				GamePanel.player.attr.setLastVelocityBox(null); // Clear the last velocity box
			}

			List<Box> nearbyNonCollisionBoxes = ((BoxesHandling.getNonCollisionBoxesInRange(GamePanel.player.pos.getX(), GamePanel.player.pos.getY(), GamePanel.tileSize, scale, GamePanel.tileSize)));
			for (Box box: nearbyNonCollisionBoxes) {
				if (!box.getEffect().isEmpty()) {
					box.handleEffect();
				}
			}
		}
	}

	/**
	 * This takes the top-left corner of an object as pixels and the object width and height and returns
	 * either true or false based if there's a box with collision on in the pixel detected.
	 * <p></p>
	 * Example use:
	 * (!isPixelOccupied((playerX + 45), playerY, playerWidth, playerHeight) will check if there's
	 * something blocking the player at 45 pixels in the X axis from where the player is, based on
	 * the player entire hitbox, not just from the top-left corner.
	 *
	 * @param pixelX double - The X location in pixels of the object.
	 * @param pixelY double - The Y location in pixels of the object.
	 * @param objectWidth double - The width of the object, in pixels.
	 * @param objectHeight double - The height of the object, in pixels.
 	 */
	public static boolean isPixelOccupied(double pixelX, double pixelY, double objectWidth, double objectHeight, double range, int level, boolean isPlayer) {
		List<Box> nearbyCollisionBoxes = BoxesHandling.getCollisionBoxesInRange(pixelX, pixelY, range, scale, GamePanel.tileSize, level);
		for (Box box : nearbyCollisionBoxes) {
			if (box.getBoxScaleHorizontal() >= 1 && box.getBoxScaleVertical() >= 1) {
				if (box.isPointColliding(pixelX, pixelY, scale, objectWidth, objectHeight) || // Up-left corner
					(box.isPointColliding(pixelX + objectWidth, pixelY, scale, objectWidth, objectHeight)) || // Up-right corner
					(box.isPointColliding(pixelX, pixelY + objectHeight, scale, objectWidth, objectHeight)) || // Bottom-left corner
					(box.isPointColliding(pixelX + objectWidth, pixelY + objectHeight, scale, objectWidth, objectHeight)) // Bottom-right corner
				) {
					if (isPlayer && !box.getEffect().isEmpty()) {
						box.handleEffect();
					}
					return true;
				}
			} else {
				int inverseBoxScale = (int) (1 / Math.min(box.getBoxScaleHorizontal(), box.getBoxScaleVertical())) + 1;
				for (int i = 0; i <= inverseBoxScale; i++) {
					if ((box.isPointColliding(pixelX + i * objectWidth / inverseBoxScale, pixelY, scale, objectWidth, objectHeight)) || // Top edge
						(box.isPointColliding(pixelX, pixelY + i * objectHeight / inverseBoxScale, scale, objectWidth, objectHeight)) || // Left edge
						(box.isPointColliding(pixelX + objectWidth, pixelY + i * objectHeight / inverseBoxScale, scale, objectWidth, objectHeight)) || // Right edge
						(box.isPointColliding(pixelX + i * objectWidth / inverseBoxScale, pixelY + objectHeight, scale, objectWidth, objectHeight)) // Bottom edge
					) {
						if (isPlayer && !box.getEffect().isEmpty()) {
							box.handleEffect();
						}
						return true;
					}
				}
			}
        }
    return false;
	}

	public static double coordinateToPixel(int coordinate) {
		return coordinate * GamePanel.tileSize;
	}

	public static int pixelToCoordinate(double pixel) {
		return (int) (pixel / GamePanel.tileSize);
	}
}
