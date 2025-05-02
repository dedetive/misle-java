package com.ded.misle.world.boxes;

import com.ded.misle.core.PhysicsEngine;
import com.ded.misle.core.TurnTimer;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BoxManipulation {

	/**
	 * @param box the box to be moved
	 * @param dx  how many coordinates in the x-axis
	 * @param dy  how many coordinates in the y-axis
	 */
	public static void moveBox(Box box, int dx, int dy) {
		moveBox(box, dx, dy, false);
	}

	/**
	 *
	 * @param box the box to be moved
	 * @param dx how many coordinates in the x-axis
	 * @param dy how many coordinates in the y-axis
	 * @param ignoreCollision whether box should ignore collision or not when touching other boxes
	 */
	public static void moveBox(Box box, int dx, int dy, boolean ignoreCollision) {
		int turns = Math.max(Math.abs(dx), Math.abs(dy));
		box.isMoving = true;
		int[] dxFinal = new int[]{dx};
		int[] dyFinal = new int[]{dy};

		TurnTimer.schedule(1, true, e -> {
			int x = box.getX();
			int y = box.getY();
			int signumX = Integer.signum(dxFinal[0]);
			int signumY = Integer.signum(dyFinal[0]);
			int targetX = x + signumX;
			int targetY = y + signumY;

			if (!ignoreCollision) {
				// Check X-axis first
				if (isDestinationOccupied(targetX, y, box)) {
					signumX = 0;
				}
				// Then check Y-axis separately
				if (isDestinationOccupied(x, targetY, box)) {
					signumY = 0;
				}

				box.setPos(x + signumX, y + signumY);
			} else {
				box.setPos(targetX, targetY);
			}

			dxFinal[0] = dxFinal[0] - signumX;
			dyFinal[0] = dyFinal[0] - signumY;
		}).setStopsAt(turns).setRoomScoped(true);
	}

	private static boolean isDestinationOccupied(int targetX, int targetY, Box box) {
		return PhysicsEngine.isSpaceOccupied(targetX, targetY, box);
	}

	public static void delayedRotateBox(Box box, double angle, double delay) {
		int frames = (int)(delay / 1000 * 60);
		double dangle = angle / frames;
		Timer timer = new Timer(1000 / 60, new ActionListener() {
			int count = 0;
			public void actionPerformed(ActionEvent evt) {
				if (count < frames) {
					box.setVisualRotation(box.getVisualRotation() + dangle);
					count++;
				} else {
					((Timer) evt.getSource()).stop();  // Stop the timer when done
				}
			}
		});
		timer.start();
	}
}
