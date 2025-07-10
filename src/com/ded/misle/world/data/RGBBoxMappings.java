package com.ded.misle.world.data;

import com.ded.misle.world.boxes.Box;
import com.ded.misle.world.entities.Entity;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;

import static com.ded.misle.world.boxes.BoxHandling.addBox;
import static com.ded.misle.world.data.BoxPreset.*;

/**
 * Maps RGB values from room bitmaps to {@link Box} instances.
 * Used exclusively in {@link WorldLoader}.
 */
final class RGBBoxMappings {

	private RGBBoxMappings() {} // Utility class

	/** Mapping values from RGB integer to {@link Box} supplier. */
	static final Map<Integer, Callable<Box>> MAP = Map.of(
			0xC4C4C4, () -> addBox(STONE_BRICK_WALL),
			0xDFDFDF, () -> {
				Entity e = new Entity();
				CRACKED_STONE_BRICK_WALL.load(e);
				return e;
			},
			0xB38960, () -> addBox(WOODEN_FLOOR)
	);

	/**
	 * Returns a Box based on the given RGB value.
	 * <p>
	 * If no mapping exists for the provided RGB, returns {@link Optional#empty()},
	 * meaning the pixel will be ignored.
	 *
	 * @param rgb RGB value from bitmap (no alpha)
	 * @return Optional containing the {@link Box}, or empty if unknown color
	 * @throws Exception if box supplier throws
	 */
	static Optional<Box> get(int rgb) throws Exception {
		Callable<Box> supplier = MAP.get(rgb);
		if (supplier == null) return Optional.empty();
		return Optional.ofNullable(supplier.call());
	}
}
