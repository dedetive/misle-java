package com.ded.misle.world.logic.attacks;

import java.awt.*;
import java.util.ArrayList;

public class Range {
    private final Point[] points;
    private static final Point[] defaultPoints = new Point[]{new Point(1, 0)};
    /**
     * The range used when {@link #getDefaultRange()} is called. Refers to a Range with a single point to the right of the origin.
     */
    private static final Range defaultRange = new Range(defaultPoints);

    private Range(Point[] points) {
        this.points = points;
    }

    /**
     * Converts a string representation of a range (with 'O' as the origin (usually the player) and 'X' as the affected area)
     * into a Range object. If the string contains multiple 'O's or no 'O', it will return the {@link #defaultRange}.
     *
     * @param s the string representing the range
     * @return a Range object containing the calculated points
     */
    public static Range toRange(String s) {
        if (!isValidRangeString(s)) {
            System.err.println("Invalid characters found in the range string!");
            return getDefaultRange();
        }

        String[] rows = s.split("\n");
        int[] origin = findOrigin(rows);

        if (origin == null) {
            return getDefaultRange();
        }

        return new Range(calculateRange(rows, origin[0], origin[1]));
    }

    /**
     * Finds the coordinates of the 'O' character in the given 2D grid.
     * If there are multiple origins or none at all, returns null.
     *
     * @param rows the string array representing the 2D grid
     * @return an array with two integers, representing the x and y coordinates of 'O', or null if no 'O' is found or if there are multiple 'O'
     */
    private static int[] findOrigin(String[] rows) {
        int oX = -1, oY = -1;

        for (int y = 0; y < rows.length; y++) {
            for (int x = 0; x < rows[y].length(); x++) {
                if (rows[y].charAt(x) == 'O') {
                    if (oX != -1 || oY != -1) {
                        System.err.println("More than one 'O' found when parsing Range!");
                        return null;
                    }
                    oX = x;
                    oY = y;
                }
            }
        }

        if (oX == -1 || oY == -1) {
            System.err.println("No 'O' found when parsing Range!");
            return null;
        }

        return new int[]{oX, oY};
    }

    /**
     * Calculates the relative points for all 'X' characters in the grid,
     * based on the origin point ('O').
     *
     * @param rows the string array representing the 2D grid
     * @param oX   the x coordinate of the origin ('O')
     * @param oY   the y coordinate of the origin ('O')
     * @return an array of Points, where each point represents a relative position of 'X'
     */
    private static Point[] calculateRange(String[] rows, int oX, int oY) {
        ArrayList<Point> points = new ArrayList<>();

        for (int y = 0; y < rows.length; y++) {
            for (int x = 0; x < rows[y].length(); x++) {
                if (rows[y].charAt(x) == 'X') {
                    points.add(new Point(x - oX, y - oY));
                }
            }
        }

        return points.toArray(new Point[0]);
    }

    /**
     * Validates that the string only contains allowed characters: 'O', 'X', space, and new line.
     *
     * @param s the string to validate
     * @return true if the string only contains valid characters, false otherwise
     */
    private static boolean isValidRangeString(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c != 'O' && c != 'X' && c != ' ' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the default range when there are parsing errors or no 'O' is found.
     * The default range is a single point to the right of the origin.
     *
     * @return the default range
     */
    public static Range getDefaultRange() {
        return defaultRange;
    }

    /**
     * Gets the array of points representing the calculated range.
     *
     * @return an array of points
     */
    public Point[] getPoints() {
        return points;
    }

    /**
     * Add an offset to each of this Range's points. All of this' points will get added with the value given.
     *
     * @param offset the value for the points to be added, with respect to their x and y individually
     * @return the same Range object for chaining
     */
    public Range offset(Point offset) {
        for (Point point : points) {
            point.translate(offset.x, offset.y);
        }

        return this;
    }

    public enum RangeRotation {
        DEG_0,
        DEG_90,
        DEG_180,
        DEG_270
    }

    /**
     * Rotates the range around the origin by the specified angle.
     *
     * @param rotation the rotation to apply (90°, 180°, 270°, or none)
     * @return the same Range object for chaining
     */
    public Range rotate(RangeRotation rotation) {
        if (rotation == RangeRotation.DEG_0) return this;
        for (int i = 0; i < points.length; i++) {
            int x = points[i].x;
            int y = points[i].y;

            switch (rotation) {
                case DEG_90 -> points[i] = new Point(y, -x);
                case DEG_180 -> points[i] = new Point(-x, -y);
                case DEG_270 -> points[i] = new Point(-y, x);
            }
        }

        return this;
    }

    /**
     * Represents a mirroring direction for a Range.
     * Note: A vertical mirror followed by a horizontal one is equivalent to a 180-degree rotation.
     * If you need both, prefer using {@code RangeRotation.DEG_180}.
     */
    public enum MirrorDirection {
        /**
         * Mirrors the Range horizontally (left-right), flipping the x-coordinate.
         */
        HORIZONTAL,

        /**
         * Mirrors the Range vertically (up-down), flipping the y-coordinate.
         */
        VERTICAL
    }

    /**
     * Mirrors this Range's points in the specified direction.
     *
     * @param direction the direction to mirror the points: horizontally (flip x) or vertically (flip y)
     * @return this object, after applying the mirror, for chaining
     */
    public Range mirror(MirrorDirection direction) {
        for (int i = 0; i < points.length; i++) {
            int x = points[i].x;
            int y = points[i].y;

            switch (direction) {
                case HORIZONTAL -> points[i] = new Point(-x, y);
                case VERTICAL -> points[i] = new Point(x, -y);
            }
        }

        return this;
    }
}
