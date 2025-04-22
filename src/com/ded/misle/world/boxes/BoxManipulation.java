package com.ded.misle.world.boxes;

import com.ded.misle.core.PhysicsEngine;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class BoxManipulation {

	/**
	 *
	 * @param box the box to be moved
	 * @param dx how many coordinates in the x-axis
	 * @param dy how many coordinates in the y-axis
	 * @param delay how long it takes in milliseconds for the box to be fully moved
	 */
	public static void moveBox(Box box, int dx, int dy, double delay) {
		moveBox(box, dx, dy, delay, false);
	}

	/**
	 *
	 * @param box the box to be moved
	 * @param dx how many coordinates in the x-axis
	 * @param dy how many coordinates in the y-axis
	 * @param delay how long it takes in milliseconds for the box to be fully moved
	 * @param ignoreCollision whether box should ignore collision or not when touching other boxes
	 */
	public static void moveBox(Box box, int dx, int dy, double delay, boolean ignoreCollision) {
		int totalSteps = Math.abs(dx) + Math.abs(dy);
		if (totalSteps == 0) return;

		int stepDuration = (int)(delay / totalSteps);
		List<int[]> steps = new ArrayList<>();

		int xDir = Integer.signum(dx);
		int yDir = Integer.signum(dy);

		for (int i = 0; i < Math.abs(dx); i++) steps.add(new int[]{xDir, 0});
		for (int i = 0; i < Math.abs(dy); i++) steps.add(new int[]{0, yDir});

		box.isMoving = true;
		Timer timer = new Timer(stepDuration, new ActionListener() {
			int stepIndex = 0;

			public void actionPerformed(ActionEvent evt) {
				if (stepIndex >= steps.size()) {
					box.isMoving = false;
					((Timer) evt.getSource()).stop();
					return;
				}

				int[] step = steps.get(stepIndex);
				int nextX = box.getX() + step[0];
				int nextY = box.getY() + step[1];

				if (ignoreCollision || !PhysicsEngine.isSpaceOccupied(nextX, nextY, box)) {
					box.setX(nextX);
					box.setY(nextY);
					stepIndex++;
				} else {
					box.isMoving = false;
					((Timer) evt.getSource()).stop();
				}
			}
		});

		timer.start();
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
