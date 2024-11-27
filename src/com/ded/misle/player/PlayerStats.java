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
		RIGHT
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
		mostTravelled.put(Direction.UP, getDistanceUp());
		mostTravelled.put(Direction.DOWN, getDistanceDown());
		mostTravelled.put(Direction.LEFT, getDistanceLeft());
		mostTravelled.put(Direction.RIGHT, getDistanceRight());
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
		leastTravelled.put(Direction.UP, getDistanceUp());
		leastTravelled.put(Direction.DOWN, getDistanceDown());
		leastTravelled.put(Direction.LEFT, getDistanceLeft());
		leastTravelled.put(Direction.RIGHT, getDistanceRight());
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

	public int getTotalSteps() {
		return totalSteps;
	}

	public int getStepsUp() {
		return stepsUp;
	}

	public int getStepsDown() {
		return stepsDown;
	}

	public int getStepsLeft() {
		return stepsLeft;
	}

	public int getStepsRight() {
		return stepsRight;
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

	public double getTotalDistance() {
		return totalDistance;
	}

	public double getDistanceUp() {
		return distanceUp;
	}

	public double getDistanceDown() {
		return distanceDown;
	}

	public double getDistanceLeft() {
		return distanceLeft;
	}

	public double getDistanceRight() {
		return distanceRight;
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
