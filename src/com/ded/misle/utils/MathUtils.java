package com.ded.misle.utils;

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

    /**
     * Linearly interpolates between two float values {@code a} and {@code b} by the factor {@code t}.
     * <p>
     * This method is commonly used in animations and gradient calculations to find an intermediate value.
     *
     * @param a the starting value
     * @param b the ending value
     * @param t the interpolation factor, typically in the range [0, 1]
     * @return the interpolated value between {@code a} and {@code b}
     */
    public static float lerp(float a, float b, float t) {
        return a * (1 - t) + b * t;
    }

    /**
     * Converts an sRGB color to linear space using gamma correction.
     * <p>
     * This is useful when performing color interpolation or blending in linear space
     * to produce visually accurate results.
     *
     * @param c the original {@link Color} in sRGB space
     * @param gamma the gamma correction factor (usually 2.2 for standard displays)
     * @return an array of 3 floats (R, G, B), each in the range [0.0, 1.0], representing the color in linear space
     */
    public static float[] gammaToLinear(Color c, float gamma) {
        return new float[]{
                (float) Math.pow(c.getRed() / 255.0, gamma),
                (float) Math.pow(c.getGreen() / 255.0, gamma),
                (float) Math.pow(c.getBlue() / 255.0, gamma)
        };
    }

    /**
     * Applies gamma correction to a linear value and converts it back to an 8-bit color component.
     * <p>
     * Used to convert a color from linear space back to sRGB after interpolation or blending.
     *
     * @param v the linear-space color component, typically in [0.0, 1.0]
     * @param gamma the gamma correction factor to apply (e.g., 2.2)
     * @return the corrected component in the 0–255 sRGB range
     */
    public static int linearToGamma(float v, float gamma) {
        return Math.min(255, Math.max(0, (int) Math.round(Math.pow(v, 1.0 / gamma) * 255)));
    }
}