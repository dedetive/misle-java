package com.ded.misle.core;

import java.awt.*;

/**
 * Utility class providing mathematical operations commonly used in game logic.
 * <p>
 * This class offers static helper methods that operate primarily on {@link Point} objects
 * or simple mathematical constructs. It is not meant to be instantiated.
 * <p>
 * All methods in this class are stateless and side effect free.
 */
public class MathUtils {
    /**
     * Private constructor to prevent instantiation.
     * <p>
     * {@code MathUtils} is a purely static utility class and should not be instantiated.
     */
    private MathUtils() {}

    /**
     * Computes the <b>Manhattan distance</b> between two points on a grid.
     * <p>
     * Also known as taxicab or city-block distance, this metric sums the absolute
     * horizontal and vertical differences between two points, ignoring diagonal movement.
     * <p>
     * It is commonly used in grid-based pathfinding algorithms (such as A*) when
     * movement is restricted to four cardinal directions.
     *
     * @param a the first point
     * @param b the second point
     * @return the Manhattan distance between {@code a} and {@code b}
     *
     * @see <a href="https://en.wikipedia.org/wiki/Taxicab_geometry">Taxicab geometry (Wikipedia)</a>
     */
    public static int manhattan(Point a, Point b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }
}