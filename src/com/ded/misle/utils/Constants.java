package com.ded.misle.utils;

/**
 * Utility class for storing global constants used across the game.
 * <p>
 * This class provides values that do not change at runtime and are
 * referenced by multiple subsystems to ensure consistency.
 */
public final class Constants {
	/**
	 * Private constructor to prevent instantiation.
	 */
	private Constants() {}

	/**
	 * Default gamma correction value used in color interpolation and blending.
	 * <p>
	 * This constant corresponds to the approximate gamma of the sRGB color space,
	 * and is used to convert between gamma-encoded and linear color representations.
	 */
	public final static float DEFAULT_GAMMA_CORRECTION = 2.2f;
}