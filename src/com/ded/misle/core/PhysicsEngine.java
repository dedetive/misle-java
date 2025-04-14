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
			result = box != null && box != responsibleBox;

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
		try {
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
		} catch (NullPointerException ignored) {}
	}
}
