package com.ded.misle.world.boxes;

import com.ded.misle.core.PhysicsEngine;
import com.ded.misle.world.player.PlayerAttributes;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.ded.misle.Launcher.scale;
import static com.ded.misle.core.GamePanel.*;
import static com.ded.misle.world.boxes.BoxHandling.getCachedBoxes;
import static com.ded.misle.world.player.PlayerAttributes.KnockbackDirection.*;

public class BoxManipulation {

	/**
	 *
	 * @param box the box to be teleported
	 * @param x position in pixels in x axis
	 * @param y position in pixels in y axis
	 * @return new position
	 */
	public static double[] teleportBox(Box box, int x, int y) {
		box.setX(x);
		box.setY(y);

		return new double[]{box.getX(), box.getY()};
	}

	/**
	 *
	 * @param box the box to b
	 * @param x how many pixels in the x axis
	 * @param y how many pixels in the y axis
	 * @param delay how long it takes in milliseconds for the box to be fully moved
	 */
	public static void moveBox(Box box, int x, int y, double delay) {
		int frames = (int) (delay / 1000 * 60);
		int dx = x / frames;
		int dy = y / frames;

		box.isMoving = true;
		Timer timer = new Timer(1000 / 60, new ActionListener() {
			int count = 0;
			public void actionPerformed(ActionEvent evt) {
				if (count < frames) {
					box.setX(box.getX() + dx);
					box.setY(box.getY() + dy);
					count++;
				} else {
					box.isMoving = false;
					((Timer) evt.getSource()).stop();  // Stop the timer when done
				}
			}
		});
		timer.start();
	}

	/**
	 *
	 * @param box the box to b
	 * @param x how many pixels in the x axis
	 * @param y how many pixels in the y axis
	 * @param delay how long it takes in milliseconds for the box to be fully moved
	 */
	public static void moveCollisionBox(Box box, int x, int y, double delay) {
		int frames = Math.max((int)(delay / 1000 * 60), 1);
		int dx = x / frames;
		int dy = y / frames;

		PlayerAttributes.KnockbackDirection direction = NONE;
		if (dx > 0 && dy == 0) {
			direction = RIGHT;
		} else if (dx < 0 && dy == 0) {
			direction = LEFT;
		} else if (dx == 0 && dy > 0) {
			direction = DOWN;
		} else if (dx == 0 && dy < 0) {
			direction = UP;
		}

		PlayerAttributes.KnockbackDirection finalDirection = direction;
		box.isMoving = true;
		Timer timer = new Timer(1000 / 60, new ActionListener() {
			int count = 0;
			public void actionPerformed(ActionEvent evt) {
				if (count < frames) {
					if (!PhysicsEngine.isPixelOccupied(box, box.getX() + dx, box.getY() + dy, tileSize, 10, finalDirection)) {
						box.setX(box.getX() + dx);
						box.setY(box.getY() + dy);
					} else {
						count = frames; // Force stop
					}
					count++;
				} else {
					box.isMoving = false;
					((Timer) evt.getSource()).stop();  // Stop the timer when done
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
					box.setRotation(box.getRotation() + dangle);
					count++;
				} else {
					((Timer) evt.getSource()).stop();  // Stop the timer when done
				}
			}
		});
		timer.start();
	}
}
