package com.ded.misle.boxes;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
	 * @return final position
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
}
