package com.ded.misle.player;

import java.util.*;

import static com.ded.misle.boxes.BoxesHandling.*;

public class PlayerStats {

	int totalSteps;
	int stepsUp;
	int stepsDown;
	int stepsLeft;
	int stepsRight;
	double totalDistance;
	double distanceUp;
	double distanceDown;
	double distanceLeft;
	double distanceRight;
	Direction walkingDirection;
	public enum Direction {
		UP,
		DOWN,
		LEFT,
		RIGHT,
		TOTAL
	}

	public PlayerStats() {
		this.totalSteps = 0;
		this.stepsUp = 0;
		this.stepsDown = 0;
		this.stepsLeft = 0;
		this.stepsRight = 0;
		this.totalDistance = 0;
		this.distanceUp = 0;
		this.distanceDown = 0;
		this.distanceLeft = 0;
		this.distanceRight = 0;
		this.walkingDirection = Direction.RIGHT;
	}

	/**
	 * @return a list containing, as the index 0, the number associated with the highest value and,
	 * as index 1, the most travelled distance (as in 'up', 'down', 'left', 'right').
	 */
	public List<Direction> getMostDistanceTravelled() {
		HashMap<Direction, Double> mostTravelled = new HashMap<>();
		mostTravelled.put(Direction.UP, getDistance(Direction.UP));
		mostTravelled.put(Direction.DOWN, getDistance(Direction.DOWN));
		mostTravelled.put(Direction.LEFT, getDistance(Direction.LEFT));
		mostTravelled.put(Direction.RIGHT, getDistance(Direction.RIGHT));
		Double highestValue = Collections.max(mostTravelled.values());
		List<Direction> mostDistanceTravelled = new ArrayList<>();
		for (Map.Entry<Direction, Double> entry : mostTravelled.entrySet()) {
			if (entry.getValue() >= highestValue) {
				mostDistanceTravelled.add(entry.getKey());
			}
		}
		mostDistanceTravelled.add(Direction.valueOf(highestValue.toString()));

		return mostDistanceTravelled;
	}

	/**
	 * @return a list containing, as the index 0, the number associated with the lowest value and,
	 * as index 1, the least travelled distance (as in 'up', 'down', 'left', 'right').
	 */
	public List<Direction> getLeastDistanceTravelled() {
		HashMap<Direction, Double> leastTravelled = new HashMap<>();
		leastTravelled.put(Direction.UP, getDistance(Direction.UP));
		leastTravelled.put(Direction.DOWN, getDistance(Direction.DOWN));
		leastTravelled.put(Direction.LEFT, getDistance(Direction.LEFT));
		leastTravelled.put(Direction.RIGHT, getDistance(Direction.RIGHT));
		Double lowestValue = Collections.min(leastTravelled.values());
		List<Direction> leastDistanceTravelled = new ArrayList<>();
		leastDistanceTravelled.add(Direction.valueOf(lowestValue.toString()));

		for (Map.Entry<Direction, Double> entry : leastTravelled.entrySet()) {
			if (entry.getValue() <= lowestValue) {
				leastDistanceTravelled.add(entry.getKey());
			}
		}

		return leastDistanceTravelled;
	}

	public int getSteps(Direction direction) {
		return switch (direction) {
			case UP -> stepsUp;
			case DOWN -> stepsDown;
			case LEFT -> stepsLeft;
			case RIGHT -> stepsRight;
			case TOTAL -> totalSteps;
		};
	}

	public double getDistance(Direction direction) {
		return switch (direction) {
			case UP -> distanceUp;
			case DOWN -> distanceDown;
			case LEFT -> distanceLeft;
			case RIGHT -> distanceRight;
			case TOTAL -> totalDistance;
		};
	}

	public void incrementTotalDistance() {
		totalDistance++;
	}

	public void incrementTotalSteps() {
		for (int level = maxLevel; level > 0; level--) {
			if (totalSteps == 0) {
				storeCachedBoxes(level);
			} else if (totalSteps % Math.pow(2, level) == 0) {
				storeCachedBoxes(level);
			}
		}
		totalSteps++;
	}

	public void incrementStepsUp() {
		stepsUp++;
		incrementTotalSteps();
	}

	public void incrementStepsDown() {
		stepsDown++;
		incrementTotalSteps();
	}

	public void incrementStepsLeft() {
		stepsLeft++;
		incrementTotalSteps();
	}

	public void incrementStepsRight() {
		stepsRight++;
		incrementTotalSteps();
	}

	public void increaseDistanceUp(double distance) {
		distanceUp += distance;
		incrementTotalDistance();
		walkingDirection = Direction.UP;
		incrementStepsUp();
	}

	public void increaseDistanceDown(double distance) {
		distanceDown += distance;
		incrementTotalDistance();
		walkingDirection = Direction.DOWN;
		incrementStepsDown();
	}

	public void increaseDistanceLeft(double distance) {
		distanceLeft += distance;
		incrementTotalDistance();
		walkingDirection = Direction.LEFT;
		incrementStepsLeft();
	}

	public void increaseDistanceRight(double distance) {
		distanceRight += distance;
		incrementTotalDistance();
		walkingDirection = Direction.RIGHT;
		incrementStepsRight();
	}

	public void increaseDistance(double x, double y) {
		if (x > 0) {
			increaseDistanceRight(x);
		} else if (x < 0) {
			increaseDistanceLeft(-x);
		}
		if (y > 0) {
			increaseDistanceDown(y);
		} else if (y < 0) {
			increaseDistanceUp(-y);
		}
	}

	public Direction getWalkingDirection() {
		return walkingDirection;
	}
}
