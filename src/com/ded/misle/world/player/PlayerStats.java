package com.ded.misle.world.player;

import java.util.*;

import static com.ded.misle.world.boxes.BoxHandling.*;
import static com.ded.misle.world.player.PlayerStats.Direction.*;

public class PlayerStats {

	long startTimestamp;
	long totalPlaytime;
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

		;

		private int steps;

		Direction() {
			reset();
		}

        public int getSteps() {
            return steps;
        }

        public void setSteps(int steps) {
            this.steps = steps;
        }

		public void incrementSteps() {
			steps++;
		}

		public void reset() {
			steps = 0;
		}

		public static void resetAll() {
			for (Direction direction : Direction.values()) direction.reset();
		}
    }

	public enum PlaytimeMode {
		MILLIS,
		SECONDS,
		MINUTES,
		HOURS
	}

	public PlayerStats() {
		Direction.resetAll();
		startTimestamp = System.currentTimeMillis();
		this.walkingDirection = RIGHT;
		this.horizontalDirection = RIGHT;
		this.verticalDirection = UP;
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

	public void setSteps(Direction direction, int steps) {
		direction.steps = steps;
	}

	public int getSteps(Direction direction) {
		return direction.steps;
	}

	public void incrementSteps(Direction direction) {
		incrementTotalSteps();
		direction.steps++;
	}

	private void incrementTotalSteps() {
		for (int level = maxLevel; level > 0; level--) {
			if (TOTAL.steps == 0) {
				storeCachedBoxes(level);
			} else if (TOTAL.steps % Math.pow(2, level) == 0) {
				storeCachedBoxes(level);
			}
		}
		TOTAL.steps++;
	}

	public List<Direction> getHighestStep() {
		HashMap<Direction, Integer> mostTravelled = new HashMap<>();
		mostTravelled.put(UP, getSteps(UP));
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
		leastTravelled.put(UP, getSteps(UP));
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
}
