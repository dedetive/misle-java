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
			moveAxis(box, dxFinal, 0, ignoreCollision);
			moveAxis(box, dyFinal, 1, ignoreCollision);
		}).setStopsAt(turns).setRoomScoped(true);
	}


	private static void moveAxis(Box box, int[] delta, int axisIndex, boolean ignoreCollision) {
		int signum = Integer.signum(delta[0]);

		if (signum == 0) return;

		int x = box.getX();
		int y = box.getY();
		int targetX = axisIndex == 0 ? x + signum : x;
		int targetY = axisIndex == 1 ? y + signum : y;

		if (!ignoreCollision) {
			if (isDestinationOccupied(targetX, targetY, box)) {
				return;
			}
		}

		if (axisIndex == 0) {
			box.setPos(x + signum, y);
		} else {
			box.setPos(x, y + signum);
		}

		delta[0] -= signum;
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
