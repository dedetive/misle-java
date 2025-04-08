package com.ded.misle.core;

import com.ded.misle.world.World;
import com.ded.misle.world.boxes.Box;
import com.ded.misle.world.boxes.BoxHandling;
import com.ded.misle.world.boxes.HPBox;
import com.ded.misle.world.enemies.Enemy;
import com.ded.misle.world.npcs.NPC;
import com.ded.misle.world.player.Player;
import com.ded.misle.world.player.PlayerAttributes;
import com.ded.misle.world.player.PlayerStats;

import java.util.List;
import java.util.Objects;

import static com.ded.misle.core.GamePanel.player;
import static com.ded.misle.core.GamePanel.tileSize;
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
	public static void movePlayer(int x, int y) {
		player.stats.increaseDistance(x, y);
		x = player.getX() + x;
		y = player.getY() + y;
		player.setX(x);
		player.setY(y);

//		if (!levelDesigner) {
//			// Select or unselect NPCs
//			double playerCenterX = (player.getX() + player.getBoxScaleHorizontal() / 2 * tileSize);
//			double playerCenterY = (player.getY() + player.getBoxScaleVertical() / 2 * tileSize);
//			List<NPC> distantNPCs = BoxHandling.getInteractableNPCsInRange(playerCenterX, playerCenterY, 196);
//			for (NPC npc : distantNPCs)
//				npc.setSelected(false);
//			List<NPC> nearbyNPCs = BoxHandling.getInteractableNPCsInRange(playerCenterX, playerCenterY, 96);
//			for (NPC npc : nearbyNPCs)
//				npc.setSelected(true);
//		}
	}

	public static boolean isSpaceOccupied(int targetX, int targetY, Box responsibleBox) {
		World world = player.pos.world;
		boolean result;

		try {
			Box box = world.grid[targetX][targetY];
			result = box != null;

			if (result) {
				handleEffect(box, responsibleBox, responsibleBox.getKnockbackDirection());
			}

			result = result && box.getHasCollision();

		} catch (NullPointerException | NegativeArraySizeException | ArrayIndexOutOfBoundsException e) {
			result = true;
		}


		return result;
	}

	private static void handleEffect(Box box, Box responsibleBox, PlayerAttributes.KnockbackDirection direction) {
		// Touching box gets effect
		if (responsibleBox instanceof HPBox && !box.getEffect().isEmpty()) {
			if (Objects.equals(box.getEffect(), "damage")) {
				box.setKnockbackDirection(direction);
			}
			box.handleEffect((HPBox) responsibleBox);
		}
		// Responsible box gets effect
		if (box instanceof HPBox && !responsibleBox.getEffect().isEmpty()) {
			if (Objects.equals(responsibleBox.getEffect(), "damage")) {
				responsibleBox.setKnockbackDirection(direction.getOppositeDirection());
			}
			responsibleBox.handleEffect((HPBox) box);
		}

		if (player.attr.getLastVelocityBox() != null) {
			player.attr.setEnvironmentSpeedModifier(1.0); // Reset to default speed
			player.attr.setLastVelocityBox(null); // Clear the last velocity box
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
	public static boolean isPixelOccupied(Box responsibleBox, int pixelX, int pixelY, double range, int level, PlayerAttributes.KnockbackDirection direction) {
		int objectWidth;
		int objectHeight;
		objectWidth = 1;
//		objectWidth = responsibleBox.getBoxScaleHorizontal() * tileSize;
		objectHeight = 1;
//		objectHeight = responsibleBox.getBoxScaleVertical() * tileSize;

		List<Box> nearbyNonCollisionBoxes = ((BoxHandling.getNonCollisionBoxesInRange(player.getX(), player.getY(), range)));
		for (Box nonColBox : nearbyNonCollisionBoxes) {
			if (responsibleBox == nonColBox) continue;
			if (!nonColBox.getEffect().isEmpty()) {
				try {
					nonColBox.handleEffect((HPBox) responsibleBox);
				} catch (ClassCastException e) {
					//
				}
			} if (!responsibleBox.getEffect().isEmpty()) {
				try {
					responsibleBox.handleEffect((HPBox) nonColBox);
				} catch (ClassCastException e) {
					//
				}
			}
		}
		List<Box> nearbyCollisionBoxes = BoxHandling.getCollisionBoxesInRange(pixelX, pixelY, range, level);
		for (Box box : nearbyCollisionBoxes) {
			// Not allow box to check for itself
			if (responsibleBox == box) continue;
			// If it doesn't interact with the player, do not check for it
			if (responsibleBox instanceof Player && !box.getInteractsWithPlayer()) continue;

			// If it is a regular box (scales above 1)
			if ((box.getBoxScaleHorizontal() >= 1 && box.getBoxScaleVertical() >= 1) && (responsibleBox.getBoxScaleHorizontal() >= 1 && responsibleBox.getBoxScaleVertical() >= 1)) {
				if (box.isPointColliding(pixelX, pixelY) || // Up-left corner
					(box.isPointColliding(pixelX + objectWidth, pixelY)) || // Up-right corner
					(box.isPointColliding(pixelX, pixelY + objectHeight)) || // Bottom-left corner
					(box.isPointColliding(pixelX + objectWidth, pixelY + objectHeight)) // Bottom-right corner
				) {
					// Touching box gets effect
					if (responsibleBox instanceof HPBox && !box.getEffect().isEmpty()) {
						if (Objects.equals(box.getEffect(), "damage")) {
							box.setKnockbackDirection(direction);
						}
						box.handleEffect((HPBox) responsibleBox);
					}
					// Responsible box gets effect
					if (box instanceof HPBox && !responsibleBox.getEffect().isEmpty()) {
						if (Objects.equals(responsibleBox.getEffect(), "damage")) {
							responsibleBox.setKnockbackDirection(direction.getOppositeDirection());
						}
						responsibleBox.handleEffect((HPBox) box);
					}

					if (player.attr.getLastVelocityBox() != null) {
						player.attr.setEnvironmentSpeedModifier(1.0); // Reset to default speed
						player.attr.setLastVelocityBox(null); // Clear the last velocity box
					}
					return true;
				}
//			} else {
//				// If it is not a regular box (scales smaller than 1)
//				int inverseBoxScale = (int) Math.min(1 / Math.min(box.getBoxScaleHorizontal(), box.getBoxScaleVertical()),
//					(1 / Math.min(responsibleBox.getBoxScaleHorizontal(), responsibleBox.getBoxScaleVertical()))) + 1;
//				for (int i = 0; i <= inverseBoxScale; i++) {
//					// Check for each edge for box and responsible box
//					if ((box.isPointColliding(pixelX + i * objectWidth / inverseBoxScale, pixelY, objectWidth, objectHeight)) || // Top edge
//						(box.isPointColliding(pixelX, pixelY + i * objectHeight / inverseBoxScale, objectWidth, objectHeight)) || // Left edge
//						(box.isPointColliding(pixelX + objectWidth, pixelY + i * objectHeight / inverseBoxScale, objectWidth, objectHeight)) || // Right edge
//						(box.isPointColliding(pixelX + i * objectWidth / inverseBoxScale, pixelY + objectHeight, objectWidth, objectHeight) || // Bottom edge
//
//						(responsibleBox.isPointColliding(pixelX + i * objectWidth / inverseBoxScale, pixelY, objectWidth, objectHeight)) || // Top edge
//						(responsibleBox.isPointColliding(pixelX, pixelY + i * objectHeight / inverseBoxScale, objectWidth, objectHeight)) || // Left edge
//						(responsibleBox.isPointColliding(pixelX + objectWidth, pixelY + i * objectHeight / inverseBoxScale, objectWidth, objectHeight)) || // Right edge
//						(responsibleBox.isPointColliding(pixelX + i * objectWidth / inverseBoxScale, pixelY + objectHeight, objectWidth, objectHeight))
//						)
//					) {
//						// Touching box gets effect
//						if (responsibleBox instanceof HPBox && !box.getEffect().isEmpty()) {
//							if (Objects.equals(box.getEffect(), "damage")) {
//								box.setKnockbackDirection(direction);
//							}
//							box.handleEffect((HPBox) responsibleBox);
//						}
//						// Responsible box gets effect
//						if (box instanceof HPBox && !responsibleBox.getEffect().isEmpty()) {
//							if (Objects.equals(responsibleBox.getEffect(), "damage")) {
//								responsibleBox.setKnockbackDirection(direction.getOppositeDirection());
//							}
//							responsibleBox.handleEffect((HPBox) box);
//						}
//						return true;
//					}
//				}
			}
		}
		return false;
	}

	public static boolean isPixelOccupied(Box responsibleBox, double range, int level, PlayerAttributes.KnockbackDirection direction) {
		int pixelX;
		int pixelY;
		if (!(responsibleBox instanceof Player)) {
			pixelX = responsibleBox.getX();
			pixelY = responsibleBox.getY();
		} else {
			pixelX = responsibleBox.getX();
			pixelY = responsibleBox.getY();
		}

		return isPixelOccupied(responsibleBox, pixelX, pixelY, range, level, direction);
	}

	public static boolean isPixelOccupied(Box responsibleBox, int pixelX, int pixelY, double range, int level, PlayerAttributes.KnockbackDirection direction, Enemy.EnemyType immuneTo) {
		int objectWidth = 1;
		int objectHeight = 1;
		if (responsibleBox instanceof Player) {
//			objectWidth = responsibleBox.getBoxScaleHorizontal();
//			objectHeight = responsibleBox.getBoxScaleVertical();
		} else {
//			objectWidth = responsibleBox.getBoxScaleHorizontal() * tileSize;
//			objectHeight = responsibleBox.getBoxScaleVertical() * tileSize;
		}

		List<Box> nearbyNonCollisionBoxes = ((BoxHandling.getNonCollisionBoxesInRange(player.getX(), player.getY(), range)));
		for (Box nonColBox : nearbyNonCollisionBoxes) {
			try {
				switch (immuneTo) {
					case GOBLIN -> {
						if (((Enemy) nonColBox).getEnemyType() == immuneTo) continue;
					}
				}
			} catch (ClassCastException e) {
				//
			}
			if (responsibleBox == nonColBox) continue;
			if (!nonColBox.getEffect().isEmpty()) {
				try {
					nonColBox.handleEffect((HPBox) responsibleBox);
				} catch (ClassCastException e) {
					//
				}
			} if (!responsibleBox.getEffect().isEmpty()) {
				try {
					responsibleBox.handleEffect((HPBox) nonColBox);
				} catch (ClassCastException e) {
					//
				}
			}
		}
		List<Box> nearbyCollisionBoxes = BoxHandling.getCollisionBoxesInRange(pixelX, pixelY, range, level);
		for (Box box : nearbyCollisionBoxes) {
			try {
				switch (immuneTo) {
					case GOBLIN -> {
						if (((Enemy) box).getEnemyType() == immuneTo) continue;
					}
				}
			} catch (ClassCastException e) {
				//
			}
			// Not allow box to check for itself
			if (responsibleBox == box) continue;
			// If it doesn't interact with the player, do not check for it
			if (responsibleBox instanceof Player && !box.getInteractsWithPlayer()) continue;

			// If it is a regular box (scales above 1)
			if ((box.getBoxScaleHorizontal() >= 1 && box.getBoxScaleVertical() >= 1) && (responsibleBox.getBoxScaleHorizontal() >= 1 && responsibleBox.getBoxScaleVertical() >= 1)) {
				if (box.isPointColliding(pixelX, pixelY) || // Up-left corner
					(box.isPointColliding(pixelX + objectWidth, pixelY)) || // Up-right corner
					(box.isPointColliding(pixelX, pixelY + objectHeight)) || // Bottom-left corner
					(box.isPointColliding(pixelX + objectWidth, pixelY + objectHeight)) // Bottom-right corner
				) {
					// Touching box gets effect
					if (responsibleBox instanceof HPBox && !box.getEffect().isEmpty()) {
						if (Objects.equals(box.getEffect(), "damage")) {
							box.setKnockbackDirection(direction);
						}
						box.handleEffect((HPBox) responsibleBox);
					}
					// Responsible box gets effect
					if (box instanceof HPBox && !responsibleBox.getEffect().isEmpty()) {
						if (Objects.equals(responsibleBox.getEffect(), "damage")) {
							responsibleBox.setKnockbackDirection(direction.getOppositeDirection());
						}
						responsibleBox.handleEffect((HPBox) box);
					}

					if (player.attr.getLastVelocityBox() != null) {
						player.attr.setEnvironmentSpeedModifier(1.0); // Reset to default speed
						player.attr.setLastVelocityBox(null); // Clear the last velocity box
					}
					return true;
				}
//			} else {
//				// If it is not a regular box (scales smaller than 1)
//				int inverseBoxScale = (int) Math.min(1 / Math.min(box.getBoxScaleHorizontal(), box.getBoxScaleVertical()),
//					(1 / Math.min(responsibleBox.getBoxScaleHorizontal(), responsibleBox.getBoxScaleVertical()))) + 1;
//				for (int i = 0; i <= inverseBoxScale; i++) {
//					// Check for each edge for box and responsible box
//					if ((box.isPointColliding(pixelX + i * objectWidth / inverseBoxScale, pixelY, objectWidth, objectHeight)) || // Top edge
//						(box.isPointColliding(pixelX, pixelY + i * objectHeight / inverseBoxScale, objectWidth, objectHeight)) || // Left edge
//						(box.isPointColliding(pixelX + objectWidth, pixelY + i * objectHeight / inverseBoxScale, objectWidth, objectHeight)) || // Right edge
//						(box.isPointColliding(pixelX + i * objectWidth / inverseBoxScale, pixelY + objectHeight, objectWidth, objectHeight) || // Bottom edge
//
//							(responsibleBox.isPointColliding(pixelX + i * objectWidth / inverseBoxScale, pixelY, objectWidth, objectHeight)) || // Top edge
//							(responsibleBox.isPointColliding(pixelX, pixelY + i * objectHeight / inverseBoxScale, objectWidth, objectHeight)) || // Left edge
//							(responsibleBox.isPointColliding(pixelX + objectWidth, pixelY + i * objectHeight / inverseBoxScale, objectWidth, objectHeight)) || // Right edge
//							(responsibleBox.isPointColliding(pixelX + i * objectWidth / inverseBoxScale, pixelY + objectHeight, objectWidth, objectHeight))
//						)
//					) {
//						// Touching box gets effect
//						if (responsibleBox instanceof HPBox && !box.getEffect().isEmpty()) {
//							if (Objects.equals(box.getEffect(), "damage")) {
//								box.setKnockbackDirection(direction);
//							}
//							box.handleEffect((HPBox) responsibleBox);
//						}
//						// Responsible box gets effect
//						if (box instanceof HPBox && !responsibleBox.getEffect().isEmpty()) {
//							if (Objects.equals(responsibleBox.getEffect(), "damage")) {
//								responsibleBox.setKnockbackDirection(direction.getOppositeDirection());
//							}
//							responsibleBox.handleEffect((HPBox) box);
//						}
//						return true;
//					}
//				}
			}
		}
		return false;
	}
}
