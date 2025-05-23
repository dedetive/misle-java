package com.ded.misle.world.logic;

import com.ded.misle.world.data.TilePattern;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Path extends TilePattern {
    /**
     * Creates a Path object with predefined points.
     * @param points the points the Path initially holds.
     */
    public Path(Point[] points) {
        super(points);
    }

    /**
     * Creates an empty Path object.
     */
    public Path() {
        super(new Point[]{});
    }

    /**
     * Concatenates a point to the current points.
     * @param point point to be added at the end of the array
     * @return this Path for chaining
     */
    public Path addPoint(Point point) {
        Point[] newPoints = new Point[points.length + 1];
        System.arraycopy(points, 0, newPoints, 0, points.length);
        newPoints[points.length] = point;
        points = newPoints;
        return this;
    }

    /**
     * Concatenate given points to the current points.
     * @param newPoints points to be added at the end of the array
     * @return this Path for chaining
     */
    public Path addPoints(Point[] newPoints) {
        Point[] combined = new Point[points.length + newPoints.length];
        System.arraycopy(points, 0, combined, 0, points.length);
        System.arraycopy(newPoints, 0, combined, points.length, newPoints.length);
        points = combined;
        return this;
    }

    /**
     * Removes a specific point from this Path. Does nothing if point does not exist within this Path.
     * This does remove multiple instances of the point.
     * @param point the point to be removed
     * @return this Path for chaining
     */
    public Path removePoint(Point point) {
        points = Arrays.stream(points)
            .filter(p -> !p.equals(point))
            .toArray(Point[]::new);
        return this;
    }

    /**
     * Returns whether this Path contains any point in common with other Path.
     * @param other the other Path to compare points
     * @return whether any intersection has been found
     */
    public boolean intersects(Path other) {
        Set<Point> set = new HashSet<>(Arrays.asList(points));
        for (Point p : other.getPoints()) {
            if (set.contains(p)) return true;
        }
        return false;
    }

    /**
     * Returns whether this Path contains given point.
     * @param point the point to check
     * @return whether this Path contains this point
     */
    public boolean contains(Point point) {
        for (Point p : points) {
            if (p.equals(point)) return true;
        }
        return false;
    }

    /**
     * Returns first point, or {@code null} if this Path contains no points.
     * @return the starting point of this Path
     */
    public Point getStart() {
        return points.length > 0 ? points[0] : null;
    }

    /**
     * Returns last point, or {@code null} if this Path contains no points.
     * @return the last point of this Path
     */
    public Point getEnd() {
        return points.length > 0 ? points[points.length - 1] : null;
    }

    /**
     * Add an offset to each of this Path's points. All points will get incremented by the given value.
     *
     * @param offset the value for the points to be added, with respect to their x and y individually
     * @return the same Path object for chaining
     */
    @Override
    public Path offset(Point offset) {
        super.offset(offset);
        return this;
    }

    /**
     * Rotates this Path around the origin by the specified angle.
     *
     * @param rotation the rotation to apply (90°, 180°, 270°, or none)
     * @return the same Path object for chaining
     */
    @Override
    public Path rotate(Rotation rotation) {
        super.rotate(rotation);
        return this;
    }

    /**
     * Mirrors this Path's points in the specified direction.
     *
     * @param direction the direction to mirror the points: horizontally (flip x) or vertically (flip y)
     * @return the same Path object for chaining
     */
    @Override
    public Path mirror(MirrorDirection direction) {
        super.mirror(direction);
        return this;
    }
}