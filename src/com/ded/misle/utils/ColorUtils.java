package com.ded.misle.utils;

import java.awt.*;

import static com.ded.misle.utils.MathUtils.lerp;

public final class ColorUtils {
	private ColorUtils() {}

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

	/**
	 * Interpolates between two colors using gamma-correct blending.
	 * <p>
	 * The colors are first converted to linear RGB space using the specified {@code gamma},
	 * interpolated linearly based on {@code t}, and then converted back to gamma-encoded space.
	 *
	 * @param c1    The starting color.
	 * @param c2    The ending color.
	 * @param t     The interpolation factor, where 0 returns {@code c1} and 1 returns {@code c2}.
	 * @param gamma The gamma value used for gamma-linear conversion.
	 * @return The gamma-correct interpolated color.
	 */
	public static Color interpolateColor(Color c1, Color c2, float t, float gamma) {
		float[] rgb1 = gammaToLinear(c1, gamma);
		float[] rgb2 = gammaToLinear(c2, gamma);

		float r = lerp(rgb1[0], rgb2[0], t);
		float g = lerp(rgb1[1], rgb2[1], t);
		float b = lerp(rgb1[2], rgb2[2], t);
		float a = lerp(c1.getAlpha() / 255f, c2.getAlpha() / 255f, t);

		return new Color(
				linearToGamma(r, gamma),
				linearToGamma(g, gamma),
				linearToGamma(b, gamma),
				Math.round(a * 255)
		);
	}
}
