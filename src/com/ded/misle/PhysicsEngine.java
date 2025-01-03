package com.ded.misle;

import com.ded.misle.boxes.Box;
import com.ded.misle.boxes.BoxHandling;
import com.ded.misle.boxes.HPBox;
import com.ded.misle.npcs.NPC;
import com.ded.misle.player.Player;
import com.ded.misle.player.PlayerAttributes;

import java.awt.*;
import java.util.List;
import java.util.Objects;

import static com.ded.misle.GamePanel.player;
import static com.ded.misle.GamePanel.tileSize;
import static com.ded.misle.Launcher.levelDesigner;
import static com.ded.misle.Launcher.scale;

public class PhysicsEngine {

	public enum ObjectType {
		PLAYER,
		BOX,
		HP_BOX,
		NPC
	}

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
		player.setX(player.getX() + x);
		player.setY(player.getY() + y);
		player.stats.increaseDistance(x, y);

		if (!levelDesigner) {
			if (player.attr.getLastVelocityBox() != null) {
				player.attr.setEnvironmentSpeedModifier(1.0); // Reset to default speed
				player.attr.setLastVelocityBox(null); // Clear the last velocity box
			}

			List<Box> nearbyNonCollisionBoxes = ((BoxHandling.getNonCollisionBoxesInRange(player.getX(), player.getY(), GamePanel.tileSize)));
			for (Box box : nearbyNonCollisionBoxes) {
				if (!box.getEffect().isEmpty()) {
					box.handleEffect(player);
				}
			}

			// Select or unselect NPCs
			double playerCenterX = (player.getX() + player.getBoxScaleHorizontal() / 2);
			double playerCenterY = (player.getY() + player.getBoxScaleVertical() / 2);
			List<NPC> distantNPCs = BoxHandling.getInteractableNPCsInRange(playerCenterX, playerCenterY, 196);
			for (NPC npc : distantNPCs)
				npc.setSelected(false);
			List<NPC> nearbyNPCs = BoxHandling.getInteractableNPCsInRange(playerCenterX, playerCenterY, 96);
			for (NPC npc : nearbyNPCs)
				npc.setSelected(true);
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
 	 */
	public static boolean isPixelOccupied(Box responsibleBox, double pixelX, double pixelY, double objectWidth, double objectHeight, double range, int level, PlayerAttributes.KnockbackDirection direction) {
		List<Box> nearbyCollisionBoxes = BoxHandling.getCollisionBoxesInRange(pixelX, pixelY, range, level);
		for (Box box : nearbyCollisionBoxes) {
			if (responsibleBox == box) continue;
			if (responsibleBox instanceof Player && !box.getInteractsWithPlayer()) continue;
			if (box.getBoxScaleHorizontal() >= 1 && box.getBoxScaleVertical() >= 1) {
				if (box.isPointColliding(pixelX, pixelY, scale, objectWidth, objectHeight) || // Up-left corner
					(box.isPointColliding(pixelX + objectWidth, pixelY, scale, objectWidth, objectHeight)) || // Up-right corner
					(box.isPointColliding(pixelX, pixelY + objectHeight, scale, objectWidth, objectHeight)) || // Bottom-left corner
					(box.isPointColliding(pixelX + objectWidth, pixelY + objectHeight, scale, objectWidth, objectHeight)) // Bottom-right corner
				) {
					if (Objects.equals(responsibleBox.getColor(), new Color(0xA02020))) System.out.println("red box found: " + box.getColor());
					if (responsibleBox instanceof HPBox && !box.getEffect().isEmpty()) {
						if (Objects.equals(box.getEffect(), "damage")) {
							box.setKnockbackDirection(direction);
						}
						box.handleEffect((HPBox) responsibleBox);
					}
					return true;
				}
			} else {
				int inverseBoxScale = (int) (1 / Math.min(box.getBoxScaleHorizontal(), box.getBoxScaleVertical())) + 1;
				boolean result = false;
				for (int i = 0; i <= inverseBoxScale; i++) {
					if ((box.isPointColliding(pixelX + i * objectWidth / inverseBoxScale, pixelY, scale, objectWidth, objectHeight)) || // Top edge
						(box.isPointColliding(pixelX, pixelY + i * objectHeight / inverseBoxScale, scale, objectWidth, objectHeight)) || // Left edge
						(box.isPointColliding(pixelX + objectWidth, pixelY + i * objectHeight / inverseBoxScale, scale, objectWidth, objectHeight)) || // Right edge
						(box.isPointColliding(pixelX + i * objectWidth / inverseBoxScale, pixelY + objectHeight, scale, objectWidth, objectHeight)) // Bottom edge
					) {
						result = true;
					}
				}
				if (responsibleBox instanceof HPBox && !box.getEffect().isEmpty()) {
					box.handleEffect((HPBox) responsibleBox);
				}
				return true;
			}
		}
		return false;
	}

	public static boolean isPixelOccupied(Box responsibleBox, double range, int level, PlayerAttributes.KnockbackDirection direction) {
		double pixelX = responsibleBox.getX() * scale + tileSize;
		double pixelY = responsibleBox.getY() * scale + tileSize;
		double objectWidth = responsibleBox.getBoxScaleHorizontal() * tileSize;
		double objectHeight = responsibleBox.getBoxScaleVertical() * tileSize;

		return isPixelOccupied(responsibleBox, pixelX, pixelY, objectWidth, objectHeight, range, level, direction);
	}

	public static double coordinateToPixel(int coordinate) {
		return coordinate * GamePanel.tileSize;
	}

	public static int pixelToCoordinate(double pixel) {
		return (int) (pixel / GamePanel.tileSize);
	}
}
