package com.ded.misle.world.logic;

import com.ded.misle.world.boxes.Box;
import com.ded.misle.world.entities.Entity;

import java.awt.*;

import static com.ded.misle.game.GamePanel.player;

public class PhysicsEngine {

	public enum ObjectType {
		PLAYER,
		BOX,
		ENTITY,
		NPC
	}

	public static boolean isSpaceOccupied(int targetX, int targetY, Box responsibleBox) {
		World world = player.pos.world;
		boolean result = true;

		for (int layer = 0; layer < world.layers; layer++) {
			try {
				Box box = world.grid[targetX][targetY][layer];
				result = box != null && box != responsibleBox;

				if (result && box.effect != null && box.effect.getTriggersOnContact()) {
					handleEffect(box, responsibleBox);
				}

				result = result && box.getHasCollision();
				if (result) return true;

			} catch (NullPointerException | NegativeArraySizeException | ArrayIndexOutOfBoundsException e) {
				result = true;
			}
		}

		return result;
	}

	/**
	 * Returns whether space is occupied or not. Will not handle effects in any way.
	 */
	public static boolean isSpaceOccupied(int targetX, int targetY) {
		World world = player.pos.world;
		boolean result = true;

		for (int layer = 0; layer < world.layers; layer++) {
			try {
				Box box = world.grid[targetX][targetY][layer];
				result = box != null && box.getHasCollision();

				if (result) return true;

			} catch (NullPointerException | NegativeArraySizeException | ArrayIndexOutOfBoundsException e) {
				result = true;
			}
		}

		return result;
	}

	public static boolean isPlayerAt(Point point) {
		return player.getPos().equals(point);
	}

	private static void handleEffect(Box culprit, Box victim) {
		try {
			// Victim gets effect
			if (victim instanceof Entity && culprit.effect != null) {
				culprit.effect.run(culprit, victim);
			}
			// Culprit gets effect
			if (culprit instanceof Entity && victim.effect != null) {
				victim.effect.run(victim, culprit);
			}

		} catch (NullPointerException ignored) {}
	}
}
