package com.ded.misle.core;

import com.ded.misle.world.World;
import com.ded.misle.world.boxes.Box;
import com.ded.misle.world.boxes.HPBox;
import com.ded.misle.world.player.PlayerAttributes;

import java.util.Objects;

import static com.ded.misle.core.GamePanel.player;

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
		boolean result = true;

		for (int layer = 0; layer < world.layers; layer++) {
			try {
				Box box = world.grid[targetX][targetY][layer];
				result = box != null && box != responsibleBox;

				if (result) {
					handleEffect(box, responsibleBox, responsibleBox.getKnockbackDirection());
				}

				result = result && box.getHasCollision();
				if (result) return true;

			} catch (NullPointerException | NegativeArraySizeException | ArrayIndexOutOfBoundsException e) {
				result = true;
			}
		}



		return result;
	}

	private static void handleEffect(Box culprit, Box victim, PlayerAttributes.KnockbackDirection direction) {
		try {
			// Touching culprit gets effect
			if (victim instanceof HPBox && !culprit.getEffect().isEmpty()) {
				if (Objects.equals(culprit.getEffect(), "damage")) {
					culprit.setKnockbackDirection(direction);
				}
				culprit.handleEffect((HPBox) victim);
			}
			// Responsible culprit gets effect
			if (culprit instanceof HPBox && !victim.getEffect().isEmpty()) {
				if (Objects.equals(victim.getEffect(), "damage")) {
					victim.setKnockbackDirection(direction.getOppositeDirection());
				}
				victim.handleEffect((HPBox) culprit);
			}

			if (player.attr.getLastVelocityBox() != null) {
				player.attr.setEnvironmentSpeedModifier(1.0); // Reset to default speed
				player.attr.setLastVelocityBox(null); // Clear the last velocity culprit
			}
		} catch (NullPointerException ignored) {}
	}
}
