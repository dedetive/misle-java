package com.ded.misle.world.data;

import java.awt.*;
import java.util.Arrays;

public abstract class TilePattern {
    protected Point[] points;

    protected TilePattern(Point[] points) {
        this.points = points;
    }

    /**
     * Gets the array of points representing this tile pattern.
     *
     * @return an array of points
     */
    public Point[] getPoints() {
        return deepCopy();
    }

    /**
     * Add an offset to each of this tile pattern's points. All points will get incremented by the given value.
     *
     * @param offset the value for the points to be added, with respect to their x and y individually
     * @return the same tile pattern object for chaining
     */
    public TilePattern offset(Point offset) {
        for (Point point : points) {
            point.translate(offset.x, offset.y);
        }
        return this;
    }

    /**
     * Rotates this tile pattern around the origin by the specified angle.
     *
     * @param rotation the rotation to apply (90°, 180°, 270°, or none)
     * @return the same tile pattern object for chaining
     */
    public TilePattern rotate(Rotation rotation) {
        for (int i = 0; i < points.length; i++) {
            int x = points[i].x;
            int y = points[i].y;

            switch (rotation) {
                case DEG_90 -> points[i] = new Point(y, -x);
                case DEG_180 -> points[i] = new Point(-x, -y);
                case DEG_270 -> points[i] = new Point(-y, x);
                case DEG_0 -> {} // no change
            }
        }
        return this;
    }

    /**
     * Mirrors this tile pattern's points in the specified direction.
     *
     * @param direction the direction to mirror the points: horizontally (flip x) or vertically (flip y)
     * @return this object, after applying the mirror, for chaining
     */
    public TilePattern mirror(MirrorDirection direction) {
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

    public enum Rotation {
        DEG_0, DEG_90, DEG_180, DEG_270
    }

    /**
     * Represents a mirroring direction for a tile pattern.
     * Note: A vertical mirror followed by a horizontal one is equivalent to a 180-degree rotation.
     * If you need both, prefer using {@code Rotation.DEG_180}.
     */
    public enum MirrorDirection {
        /**
         * Mirrors the tile pattern horizontally (left-right), flipping the x-coordinate.
         */
        HORIZONTAL,

        /**
         * Mirrors the tile pattern vertically (up-down), flipping the y-coordinate.
         */
        VERTICAL
    }

    /**
     * Creates a deep copy of this tile pattern.
     * @return the points that make up the copy
     */
    private Point[] deepCopy() {
        Point[] copy = new Point[points.length];
        for (int i = 0; i < points.length; i++) {
            copy[i] = new Point(points[i]);
        }
        return copy;
    }

    @Override
    public String toString() {
        return Arrays.toString(points);
    }
}
