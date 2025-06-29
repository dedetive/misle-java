package com.ded.misle.core;

import java.awt.*;

public class MathUtils {
    private MathUtils() {}

    public static int manhattan(Point a, Point b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }
}