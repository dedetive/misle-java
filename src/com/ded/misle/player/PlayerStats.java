package com.ded.misle.player;

import java.util.*;

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
	}

	/**
	 * @return a list containing, as the index 0, the number associated with the highest value and,
	 * as index 1, the most travelled distance (as in 'up', 'down', 'left', 'right').
	 */
	public List<String> getMostDistanceTravelled() {
		HashMap<String, Double> mostTravelled = new HashMap<>();
		mostTravelled.put("up", getDistanceUp());
		mostTravelled.put("down", getDistanceDown());
		mostTravelled.put("left", getDistanceLeft());
		mostTravelled.put("right", getDistanceRight());
		Double highestValue = Collections.max(mostTravelled.values());
		List<String> mostDistanceTravelled = new ArrayList<>();
		for (Map.Entry<String, Double> entry : mostTravelled.entrySet()) {
			if (entry.getValue() >= highestValue) {
				mostDistanceTravelled.add(entry.getKey());
			}
		}
		mostDistanceTravelled.add(highestValue.toString());

		return mostDistanceTravelled;
	}

	/**
	 * @return a list containing, as the index 0, the number associated with the lowest value and,
	 * as index 1, the least travelled distance (as in 'up', 'down', 'left', 'right').
	 */
	public List<String> getLeastDistanceTravelled() {
		HashMap<String, Double> leastTravelled = new HashMap<>();
		leastTravelled.put("up", getDistanceUp());
		leastTravelled.put("down", getDistanceDown());
		leastTravelled.put("left", getDistanceLeft());
		leastTravelled.put("right", getDistanceRight());
		Double lowestValue = Collections.min(leastTravelled.values());
		List<String> leastDistanceTravelled = new ArrayList<>();
		leastDistanceTravelled.add(lowestValue.toString());

		for (Map.Entry<String, Double> entry : leastTravelled.entrySet()) {
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

	public void incrementStepsUp() {
		stepsUp++;
		totalSteps++;
	}

	public void incrementStepsDown() {
		stepsDown++;
		totalSteps++;
	}

	public void incrementStepsLeft() {
		stepsLeft++;
		totalSteps++;
	}

	public void incrementStepsRight() {
		stepsRight++;
		totalSteps++;
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
		totalDistance += distance;
		incrementStepsUp();
	}

	public void increaseDistanceDown(double distance) {
		distanceDown += distance;
		totalDistance += distance;
		incrementStepsDown();
	}

	public void increaseDistanceLeft(double distance) {
		distanceLeft += distance;
		totalDistance += distance;
		incrementStepsLeft();
	}

	public void increaseDistanceRight(double distance) {
		distanceRight += distance;
		totalDistance += distance;
		incrementStepsRight();
	}

	public void increaseDistance(double x, double y) {
		if (x > 0) {
			increaseDistanceRight(x);
		} else {
			increaseDistanceLeft(-x);
		}
		if (y > 0) {
			increaseDistanceDown(y);
		} else {
			increaseDistanceUp(-y);
		}
	}
}
