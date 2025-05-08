package com.ded.misle.core;

import com.ded.misle.world.World;
import com.ded.misle.world.boxes.Box;
import com.ded.misle.world.boxes.Effect;
import com.ded.misle.world.boxes.HPBox;
import com.ded.misle.world.player.PlayerAttributes;

import static com.ded.misle.core.GamePanel.player;

public class PhysicsEngine {

	public enum ObjectType {
		PLAYER,
		BOX,
		HP_BOX,
		NPC
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
			// Victim gets effect
			if (victim instanceof HPBox && culprit.effect != null) {
				if (culprit.effect instanceof Effect.Damage) {
					culprit.setKnockbackDirection(direction);
				}
				culprit.effect.run(culprit, victim);
			}
			// Culprit gets effect
			if (culprit instanceof HPBox && victim.effect != null) {
				if (victim.effect instanceof Effect.Damage) {
					victim.setKnockbackDirection(direction.getOppositeDirection());
				}
				victim.effect.run(victim, culprit);
			}

		} catch (NullPointerException ignored) {}
	}
}
