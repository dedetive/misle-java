package com.ded.misle.world.data;

public enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT,
    TOTAL,
    NONE;

    public static Direction interpretDirection(int x, int y) {
        if (x == y) return RIGHT;
        if (Math.abs(x) > Math.abs(y)) {
            if (x > 0) return RIGHT;
            else return LEFT;
        }
        if (y > 0) return DOWN;
        else return UP;
    }

    public Direction getOpposite() {
        return switch (this) {
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
            case DOWN -> UP;
            case UP -> DOWN;
            default -> NONE;
        };
    }
}
