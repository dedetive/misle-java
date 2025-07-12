package com.ded.misle.renderer.particles.core;

import java.lang.annotation.*;

/**
 * Indicates the functional category of a {@link ParticleModifier}.
 * <p>
 * This annotation is primarily used for classification and tooling purposes, such as:
 * <ul>
 *     <li>Enabling IDEs or editors to display the modifier's role in tooltips</li>
 *     <li>Supporting filtering or behavior logic based on modifier type</li>
 *     <li>Providing insight when inspecting modifier behavior through reflection or logs</li>
 * </ul>
 * The annotation is {@link Documented}, so it will appear in generated Javadoc and IDE hover popups.
 *
 * <p>
 * Example:
 * <pre>{@code
 * @ModifierType(ModifierType.Type.POSITIONAL)
 * public class Offset implements ParticleModifier {
 *     ...
 * }
 * }</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ModifierType {
	/**
	 * The classification of the modifier.
	 * This can be used to determine its primary purpose or behavioral effect.
	 *
	 * @return the type of modifier
	 */
	Type value();

	/**
	 * Enumerates the known categories for {@link ParticleModifier}s.
	 * <p>
	 * These values are intended to aid tooling, filtering, or documentation.
	 */
	enum Type {
		/**
		 * A modifier that causes the particle to be destroyed or removed under certain conditions.
		 */
		DESTRUCTIVE,

		/**
		 * A modifier that adjusts the particle's world-space position.
		 */
		POSITIONAL,

		/**
		 * A modifier that alters the particle's appearance or other visual properties.
		 */
		TRANSFORMER,

		/**
		 * A general-purpose modifier that does not fit any of the other categories.
		 */
		GENERIC,
	}
}