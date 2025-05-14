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
	Direction walkingDirection;
	Direction horizontalDirection;
	Direction verticalDirection;

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
		this.walkingDirection = Direction.RIGHT;
		this.horizontalDirection = Direction.RIGHT;
		this.verticalDirection = Direction.UP;
	}

	public List<Direction> getHighestStep() {
		HashMap<Direction, Integer> mostTravelled = new HashMap<>();
		mostTravelled.put(Direction.UP, getSteps(Direction.UP));
		mostTravelled.put(Direction.DOWN, getSteps(Direction.DOWN));
		mostTravelled.put(Direction.LEFT, getSteps(Direction.LEFT));
		mostTravelled.put(Direction.RIGHT, getSteps(Direction.RIGHT));
		Integer highestValue = Collections.max(mostTravelled.values());
		List<Direction> mostDistanceTravelled = new ArrayList<>();
		for (Map.Entry<Direction, Integer> entry : mostTravelled.entrySet()) {
			if (entry.getValue() >= highestValue) {
				mostDistanceTravelled.add(entry.getKey());
			}
		}
		mostDistanceTravelled.add(Direction.valueOf(highestValue.toString()));

		return mostDistanceTravelled;
	}

	public List<Direction> getLowestStep() {
		HashMap<Direction, Integer> leastTravelled = new HashMap<>();
		leastTravelled.put(Direction.UP, getSteps(Direction.UP));
		leastTravelled.put(Direction.DOWN, getSteps(Direction.DOWN));
		leastTravelled.put(Direction.LEFT, getSteps(Direction.LEFT));
		leastTravelled.put(Direction.RIGHT, getSteps(Direction.RIGHT));
		Integer lowestValue = Collections.min(leastTravelled.values());
		List<Direction> leastDistanceTravelled = new ArrayList<>();
		leastDistanceTravelled.add(Direction.valueOf(lowestValue.toString()));

		for (Map.Entry<Direction, Integer> entry : leastTravelled.entrySet()) {
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

	public void incrementSteps(Direction direction) {
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

	public Direction getWalkingDirection() {
		return walkingDirection;
	}

	public Direction getHorizontalDirection() {
		return horizontalDirection;
	}

	public Direction getVerticalDirection() {
		return verticalDirection;
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
