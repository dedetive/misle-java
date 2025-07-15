package com.ded.misle.renderer.particles.core;

/**
 * Interface representing a modifier that can alter a {@link Particle}'s state or behavior.
 * <p>
 * ParticleModifiers encapsulate logic that is applied to particles at specific points in their lifecycle,
 * such as initialization, each frame update, start, or destruction.
 * They enable modular, reusable effects that can be composed to create complex particle behavior.
 * <p>
 * Implementations should define the {@link #modify(Particle)} method, which performs the actual modification,
 * and specify when they should be applied via {@link #getActivationTime()}.
 * <p>
 * By default, modifiers apply their effect only during the lifecycle phase returned by {@code getActivationTime()},
 * but this can be overridden with {@link #shouldUpdate(ActivationTime)} for custom timing.
 */
@ModifierType(ModifierType.Type.GENERIC)
public interface ParticleModifier {

	/**
	 * Applies this modifier's effect to the given particle.
	 * <p>
	 * This method is called when the modifier is activated, based on its configured {@link ActivationTime}.
	 *
	 * @param particle the particle to modify
	 */
	void modify(Particle particle);

	/**
	 * Returns the lifecycle phase during which this modifier should be applied.
	 *
	 * @return the activation time of this modifier
	 */
	ActivationTime getActivationTime();

	/**
	 * Represents the possible lifecycle phases when a modifier can be applied.
	 */
	enum ActivationTime {
		/**
		 * Modifier is applied once during particle initialization,
		 * immediately after the particle is constructed.
		 */
		INIT,

		/**
		 * Modifier is applied on every frame update, but only while the
		 * particle is inactive.
		 * This is useful for starting conditions.
		 */
		INACTIVE_FRAME,

		/**
		 * Modifier is applied once when the particle starts (e.g., when added to the registry).
		 */
		START,

		/**
		 * Modifier is applied on every frame update.
		 * This is used for continuous or time-dependent effects.
		 */
		FRAME,

		/**
		 * Modifier is applied once right before the particle is destroyed.
		 * Useful for cleanup or final effects.
		 */
		DESTROY
	}

	/**
	 * Determines if this modifier should apply its effect at the given lifecycle phase.
	 * <p>
	 * By default, this returns {@code true} only if the given {@code current} phase
	 * matches this modifier's {@link #getActivationTime()}.
	 * Override to customize behavior.
	 *
	 * @param current the current lifecycle phase being processed
	 * @return {@code true} if the modifier should update during this phase; {@code false} otherwise
	 */
	default boolean shouldUpdate(ActivationTime current) {
		return current == this.getActivationTime();
	}

	/**
	 * Calls {@link #modify(Particle)} if {@link #shouldUpdate(ActivationTime)} returns {@code true}
	 * for the given lifecycle phase.
	 * <p>
	 * This helper method simplifies conditionally applying the modifier during lifecycle events.
	 *
	 * @param current the current lifecycle phase being processed
	 * @param particle the particle to modify
	 */
	default void updateIfNeeded(ActivationTime current, Particle particle) {
		if (shouldUpdate(current)) this.modify(particle);
	}

	/**
	 * Returns the declared type of this modifier based on its {@link ModifierType} annotation.
	 * <p>
	 * This is used for introspection and tooling support, allowing systems and developers to
	 * classify modifiers (e.g., positional, destructive, etc.) without requiring manual checks.
	 * <p>
	 * If the modifier class is not annotated with {@link ModifierType}, it defaults to {@link ModifierType.Type#GENERIC}.
	 * This allows non-categorized modifiers to still function without causing errors.
	 *
	 * @return the type of this modifier, or {@code GENERIC} if unspecified
	 */
	default ModifierType.Type getType() {
		ModifierType ann = this.getClass().getAnnotation(ModifierType.class);
		return ann != null ? ann.value() : ModifierType.Type.GENERIC;
	}
}