package com.ded.misle.boxes;

import com.ded.misle.Physics;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.ded.misle.GamePanel.*;
import static com.ded.misle.Launcher.scale;

public class BoxManipulation {

	/**
	 *
	 * @param box the box to be teleported
	 * @param x position in pixels in x axis
	 * @param y position in pixels in y axis
	 * @return new position
	 */
	public static double[] teleportBox(Box box, double x, double y) {
		box.setCurrentX(x);
		box.setCurrentY(y);

		return new double[]{box.getCurrentX(), box.getCurrentY()};
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
					box.setCurrentX(box.getCurrentX() + dx);
					box.setCurrentY(box.getCurrentY() + dy);
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

		Timer timer = new Timer(1000 / 60, new ActionListener() {
			int count = 0;
			public void actionPerformed(ActionEvent evt) {
				if (count < frames) {
					if (!Physics.isPixelOccupied((box.getCurrentX() + dx) * scale, (box.getCurrentY() + dy) * scale, box.getBoxScaleHorizontal() * tileSize, box.getBoxScaleVertical() * tileSize, tileSize, 11, Physics.ObjectType.BOX)) {
						box.setCurrentX(box.getCurrentX() + dx);
						box.setCurrentY(box.getCurrentY() + dy);
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
