package com.ded.misle.world.logic;

import com.ded.misle.world.data.TilePattern;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Deque;
import java.util.LinkedList;

public class Path extends TilePattern {
    private final Deque<Point[]> history = new LinkedList<>();

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
        saveState();
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
        saveState();
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
        saveState();
        points = Arrays.stream(points)
            .filter(p -> !p.equals(point))
            .toArray(Point[]::new);
        return this;
    }

    /**
     * Removes a specific point from this Path. Does nothing if index is invalid within this Path.
     * @param index the index of the point to be removed
     * @return this Path for chaining
     */
    public Path removePoint(int index) {
        saveState();
        if (points.length <= index) {
            return this;
        }

        Point[] newPoints = new Point[points.length - 1];
        for (int i = 0; i < points.length - 1; i++) {
            if (i == index) continue;
            newPoints[i] = points[i];
        }
        points = newPoints;
        return this;
    }


    /**
     * Add an offset to each of this Path's points. All points will get incremented by the given value.
     *
     * @param offset the value for the points to be added, with respect to their x and y individually
     * @return the same Path object for chaining
     */
    @Override
    public Path offset(Point offset) {
        saveState();
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
        saveState();
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
        saveState();
        super.mirror(direction);
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
     * Returns how many points this Path has.
     * @return the number of points in {@link #points}
     */
    public int getLength() {
        return points.length;
    }

    /**
     * Returns the Euclidean distance from the starting point to the final point.
     * This is calculated by the square root of the sum of both squares, known as Pythagorean theorem (√(Δx² + Δy²)).
     *
     * @return the straight-line distance from the first to the last point
     */
    public double getSpan() {
        if (points.length < 2) return 0;
        Point start = getStart();
        Point end = getEnd();
        return start.distance(end);
    }

    /**
     * Saves a snapshot of the current state so it can be undone later.
     */
    private void saveState() {
        Point[] snapshot = new Point[points.length];
        for (int i = 0; i < points.length; i++) {
            snapshot[i] = new Point(points[i]);
        }
        history.push(snapshot);
    }

    /**
     * Returns to the most recent snapshot saved by {@link #saveState()}.
     * @return this Path object for chaining
     */
    public Path undo() {
        if (canUndo()) {
            points = history.pop();
        }
        return this;
    }

    /**
     * Returns whether {@link #history} is empty or not, and thus has a modification to undo or not.
     * @return whether the most recent modification can be undone
     */
    private boolean canUndo() {
        return !history.isEmpty();
    }
}