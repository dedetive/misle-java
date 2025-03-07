package com.ded.misle.world.player;

import java.util.*;

import static com.ded.misle.world.boxes.BoxHandling.*;

public class PlayerStats {

	long startTimestamp;
	long totalPlaytime;
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
	Direction horizontalDirection;
	Direction verticalDirection;
	long lastDirectionUpdate;
	long lastHorizontalDirectionUpdate;
	long lastVerticalDirectionUpdate;

	public enum Direction {
		UP,
		DOWN,
		LEFT,
		RIGHT,
		TOTAL,
		NONE
	}
	public enum PlaytimeMode {
		MILLIS,
		SECONDS,
		MINUTES,
		HOURS
	}

	public PlayerStats() {
		startTimestamp = System.currentTimeMillis();
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
		this.horizontalDirection = Direction.RIGHT;
		this.verticalDirection = Direction.UP;
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
            case NONE -> 0;
        };
	}

	public double getDistance(Direction direction) {
		return switch (direction) {
			case UP -> distanceUp;
			case DOWN -> distanceDown;
			case LEFT -> distanceLeft;
			case RIGHT -> distanceRight;
			case TOTAL -> totalDistance;
			case NONE -> 0;
		};
	}

	public void incrementDistance(Direction direction, double distance) {
		totalDistance += distance;
		walkingDirection = direction;
		incrementSteps(direction);
		switch (direction) {
			case UP -> {
				distanceUp += distance;
				verticalDirection = Direction.UP;
				lastVerticalDirectionUpdate = System.currentTimeMillis();
			}
			case DOWN -> {
				distanceDown += distance;
				verticalDirection = Direction.DOWN;
				lastVerticalDirectionUpdate = System.currentTimeMillis();
			}
			case LEFT -> {
				distanceLeft += distance;
				horizontalDirection = Direction.LEFT;
				lastHorizontalDirectionUpdate = System.currentTimeMillis();
			}
			case RIGHT -> {
				distanceRight += distance;
				horizontalDirection = Direction.RIGHT;
				lastHorizontalDirectionUpdate = System.currentTimeMillis();
			}
		}
		lastDirectionUpdate = System.currentTimeMillis();
	}

	private void incrementSteps(Direction direction) {
		incrementTotalSteps();
		switch (direction) {
			case UP -> stepsUp++;
			case DOWN -> stepsDown++;
			case LEFT -> stepsLeft++;
			case RIGHT -> stepsRight++;
		}
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

	public void increaseDistance(double x, double y) {
		if (x > 0) {
			incrementDistance(Direction.RIGHT, x);
		} else if (x < 0) {
			incrementDistance(Direction.LEFT,-x);
		}
		if (y > 0) {
			incrementDistance(Direction.DOWN,y);
		} else if (y < 0) {
			incrementDistance(Direction.UP,-y);
		}
	}

	public Direction getWalkingDirection() {
		return walkingDirection;
	}

	public Direction getHorizontalDirection() {
		return horizontalDirection;
	}

	public Direction getVerticalDirection() {
		return verticalDirection;
	}

	public Direction getCurrentWalkingDirection(long precisionMS) {
		if (lastDirectionUpdate + precisionMS < System.currentTimeMillis()) return Direction.NONE;

		return walkingDirection;
	}

	public Direction getCurrentHorizontalDirection(long precisionMS) {
		if (lastHorizontalDirectionUpdate + precisionMS < System.currentTimeMillis()) return Direction.NONE;

		return horizontalDirection;
	}

	public Direction getCurrentVerticalDirection(long precisionMS) {
		if (lastVerticalDirectionUpdate + precisionMS < System.currentTimeMillis()) return Direction.NONE;

		return verticalDirection;
	}

	public void setDistance(Direction direction, double distance) {
		switch (direction) {
			case UP -> distanceUp = distance;
			case DOWN -> distanceDown = distance;
			case LEFT -> distanceLeft = distance;
			case RIGHT -> distanceRight = distance;
			case TOTAL -> totalDistance = distance;
		}
	}

	public void setSteps(Direction direction, int steps) {
		switch (direction) {
			case UP -> stepsUp = steps;
			case DOWN -> stepsDown = steps;
			case LEFT -> stepsLeft = steps;
			case RIGHT -> stepsRight = steps;
			case TOTAL -> totalSteps = steps;
		}
	}

	public long getStartTimestamp() {
		return startTimestamp;
	}

	public void resetStartTimestamp() {
		startTimestamp = System.currentTimeMillis();
	}

	public long getCurrentPlaytime(PlaytimeMode playtimeMode) {
		long millisPlaytime = System.currentTimeMillis() - startTimestamp;
		return switch (playtimeMode) {
			case MILLIS -> (millisPlaytime) % 1000;
            case SECONDS -> (millisPlaytime / 1000) % 60;
            case MINUTES -> (millisPlaytime / (60 * 1000)) % 60;
            case HOURS -> (millisPlaytime / (60 * 60 * 1000)) % 60;
        };
	}

	public long getCurrentTotalPlaytime(PlaytimeMode playtimeMode) {
		long millisPlaytime = System.currentTimeMillis() - startTimestamp;
		long millisTotalPlaytime = getTotalPlaytime(PlaytimeMode.MILLIS);
		return switch (playtimeMode) {
			case MILLIS -> (millisPlaytime + millisTotalPlaytime);
			case SECONDS -> ((millisPlaytime + millisTotalPlaytime) / 1000);
			case MINUTES -> ((millisPlaytime + millisTotalPlaytime) / (60 * 1000));
			case HOURS -> ((millisPlaytime + millisTotalPlaytime) / (60 * 60 * 1000));
		};
	}

	public void setTotalPlaytime(long playtime) {
		this.totalPlaytime = playtime;
	}

	public long getTotalPlaytime(PlaytimeMode playtimeMode) {
		return switch (playtimeMode) {
			case MILLIS -> totalPlaytime;
			case SECONDS -> (totalPlaytime / 1000);
			case MINUTES -> (totalPlaytime / (60 * 1000));
			case HOURS -> (totalPlaytime / (60 * 60 * 1000));
		};
	}
}
