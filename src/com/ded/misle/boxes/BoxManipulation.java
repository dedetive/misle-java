package com.ded.misle.boxes;

import com.ded.misle.Physics;
import com.ded.misle.player.PlayerAttributes;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.ded.misle.GamePanel.*;
import static com.ded.misle.Launcher.scale;
import static com.ded.misle.player.PlayerAttributes.KnockbackDirection.*;

public class BoxManipulation {

	/**
	 *
	 * @param box the box to be teleported
	 * @param x position in pixels in x axis
	 * @param y position in pixels in y axis
	 * @return new position
	 */
	public static double[] teleportBox(Box box, double x, double y) {
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
	public static void moveBox(Box box, double x, double y, double delay) {
		int frames = (int)(delay / 1000 * 60);
		double dx = x / (double) frames;
		double dy = y / (double) frames;

		Timer timer = new Timer(1000 / 60, new ActionListener() {
			int count = 0;
			public void actionPerformed(ActionEvent evt) {
				if (count < frames) {
					box.setX(box.getX() + dx);
					box.setY(box.getY() + dy);
					count++;
				} else {
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
	public static void moveCollisionBox(Box box, double x, double y, double delay) {
		int frames = (int)(delay / 1000 * 60);
		double dx = x / (double) frames;
		double dy = y / (double) frames;

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
		Timer timer = new Timer(1000 / 60, new ActionListener() {
			int count = 0;
			public void actionPerformed(ActionEvent evt) {
				if (count < frames) {
					if (!Physics.isPixelOccupied((box.getX() + dx) * scale, (box.getY() + dy) * scale, box.getBoxScaleHorizontal() * tileSize, box.getBoxScaleVertical() * tileSize, tileSize, 11, Physics.ObjectType.BOX, finalDirection)) {
						box.setX(box.getX() + dx);
						box.setY(box.getY() + dy);
					}
					count++;
				} else {
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
