package com.ded.misle.world.player;

import java.util.*;

import static com.ded.misle.world.player.PlayerStats.Direction.*;

public class PlayerStats {

	long startTimestamp;
	long totalPlaytime;

	public enum Direction {
		UP,
		DOWN,
		LEFT,
		RIGHT,
		TOTAL,
		NONE

		;

		private int steps;

		public static Direction walkingDirection;
		public static Direction horizontalDirection;
		public static Direction verticalDirection;
		private static long lastDirectionUpdate;

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
			updateLastDirection(this);
		}

		public void reset() {
			steps = 0;
		}

		public static void resetAll() {
			for (Direction direction : Direction.values()) direction.reset();
		}

		public static void updateLastDirection(Direction direction) {
			walkingDirection = direction;
            switch (direction) {
                case LEFT, RIGHT -> horizontalDirection = direction;
                case UP, DOWN -> verticalDirection = direction;
            }
			lastDirectionUpdate = System.currentTimeMillis();
		}

		public static Direction interpretDirection(int x, int y) {
			if (x == y) return RIGHT;
			if (Math.abs(x) > Math.abs(y)) {
				if (x > 0) return RIGHT;
				else return LEFT;
			}
            if (y > 0) return DOWN;
            else return UP;
        }

		public static Direction getRecentDirection(long precision) {
			return getDirectionIfPrecision(walkingDirection, precision);
		}

		public static Direction getRecentHorizontalDirection(long precision) {
			return getDirectionIfPrecision(horizontalDirection, precision);
		}

		public static Direction getRecentVerticalDirection(long precision) {
			return getDirectionIfPrecision(verticalDirection, precision);
		}

		private static Direction getDirectionIfPrecision(Direction direction, long precision) {
			return lastDirectionUpdate + precision > System.currentTimeMillis()
				? direction
				: NONE;
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
		walkingDirection = RIGHT;
		horizontalDirection = RIGHT;
		verticalDirection = UP;
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
		direction.setSteps(steps);
	}

	public int getSteps(Direction direction) {
		return direction.getSteps();
	}

	public void incrementSteps(Direction direction) {
		incrementTotalSteps();
		direction.incrementSteps();
	}

	private void incrementTotalSteps() {
		TOTAL.incrementSteps();
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
