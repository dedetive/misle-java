package com.ded.misle.world.boxes;

import com.ded.misle.world.logic.PhysicsEngine;
import com.ded.misle.world.logic.TurnTimer;

import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.ded.misle.game.GamePanel.mouseHandler;
import static com.ded.misle.game.GamePanel.player;
import static com.ded.misle.world.data.Direction.interpretDirection;

public class BoxManipulation {

	/**
	 * Moves the given box by the specified delta along the X and Y axes using the turn-based timer.
	 * Collision will be checked and respected during movement.
	 *
	 * @param box the box to move
	 * @param dx  how many units to move along the X-axis
	 * @param dy  how many units to move along the Y-axis
	 */
	public static void moveBox(Box box, int dx, int dy) {
		moveBox(box, dx, dy, false);
	}

	/**
	 * Moves the given box by the specified delta along the X and Y axes using the turn-based timer.
	 * Allows specifying whether to ignore collisions with other boxes during movement.
	 *
	 * @param box             the box to move
	 * @param dx              how many units to move along the X-axis
	 * @param dy              how many units to move along the Y-axis
	 * @param ignoreCollision true to move without checking for collisions; false to stop when blocked
	 */
	public static void moveBox(Box box, int dx, int dy, boolean ignoreCollision) {
		int turns = Math.max(Math.abs(dx), Math.abs(dy));
		box.isMoving = true;

		int[] dxFinal = new int[]{dx};
		int[] dyFinal = new int[]{dy};

		TurnTimer.schedule(1, true, e -> {
			moveAxis(box, dxFinal, 0, ignoreCollision);
			moveAxis(box, dyFinal, 1, ignoreCollision);
		}).setStopsAt(turns).setRoomScoped(true).setOnFinish(e ->
			box.isMoving = false
		);
	}

	/**
	 * Attempts to move the given box by one unit along a specific axis (X or Y), taking collision into account.
	 * This method is designed to be called from within a scheduled loop to gradually move a box over multiple turns.
	 *
	 * @param box          the box to move
	 * @param delta        a single-element array representing the remaining movement along the axis; it will be modified
	 * @param axisIndex    0 for X-axis, 1 for Y-axis
	 * @param ignoreCollision whether to skip collision checks when moving
	 */
	private static boolean moveAxis(Box box, int[] delta, int axisIndex, boolean ignoreCollision) {
		Point next = tryMoveAxis(box.getX(), box.getY(), delta[0], axisIndex, box, ignoreCollision);
		if (next == null) return false;

		box.setPos(next.x, next.y);
		return true;
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

	public static void moveToward(Box box, Point target, boolean ignoreCollision) {
		Point next = getNextPositionToward(box, target, ignoreCollision);
		if (!next.equals(box.getPos())) {
			box.setPos(next.x, next.y);
		}
	}

	public static Point getNextPositionToward(Box box, Point target, boolean ignoreCollision) {
		int dx = target.x - box.getX();
		int dy = target.y - box.getY();

		if (dx == 0 && dy == 0) return new Point(box.getX(), box.getY());

		int x = box.getX();
		int y = box.getY();

		if (Math.abs(dx) >= Math.abs(dy)) {
			Point result = tryMoveAxis(x, y, dx, 0, box, ignoreCollision);
			if (result != null) return result;
			result = tryMoveAxis(x, y, dy, 1, box, ignoreCollision);
			if (result != null) return result;
		} else {
			Point result = tryMoveAxis(x, y, dy, 1, box, ignoreCollision);
			if (result != null) return result;
			result = tryMoveAxis(x, y, dx, 0, box, ignoreCollision);
			if (result != null) return result;
		}

		return new Point(x, y);
	}

	private static Point tryMoveAxis(int x, int y, int delta, int axisIndex, Box box, boolean ignoreCollision) {
		int signum = Integer.signum(delta);
		if (signum == 0) return null;

		int targetX = axisIndex == 0 ? x + signum : x;
		int targetY = axisIndex == 1 ? y + signum : y;

		if (!ignoreCollision && isDestinationOccupied(targetX, targetY, box)) {
			return null;
		}

		return new Point(targetX, targetY);
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
		player.stats.incrementSteps(interpretDirection(x, y));
		player.updateLastDirection(interpretDirection(x, y));
        x = player.getX() + x;
        y = player.getY() + y;
        player.setPos(x, y);
	    mouseHandler.updateCurrentMouseRotation();
    }
}
