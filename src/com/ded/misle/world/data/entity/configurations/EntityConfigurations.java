package com.ded.misle.world.data.entity.configurations;

import com.ded.misle.world.data.entity.EntityConfigurator;
import com.ded.misle.world.data.entity.GenericConfigurations;
import com.ded.misle.world.entities.ai.behaviors.ChainBehavior;
import com.ded.misle.world.entities.ai.behaviors.WaitBehavior;
import com.ded.misle.world.entities.ai.behaviors.WanderBehavior;

public enum EntityConfigurations implements GenericConfigurations {
	BUNNY(bunny -> {
		bunny.setProportionalMaxHP(3)
				.setTexture("bunny")
				.setTurnsToRespawn(20)
				.setVisualScaleHorizontal(0.8)
				.setVisualScaleVertical(0.8);

		var wait = new WaitBehavior(5);
		var wander = new WanderBehavior(2);

		ChainBehavior wanderThenWait = new ChainBehavior(
				wander,
				wait
		);

		bunny.setBehaviors(wanderThenWait);
	})

	;

	/**
	 * The functional configurator for this entity setup.
	 */
	public final EntityConfigurator c;

	EntityConfigurations(EntityConfigurator configurator) {
		this.c = configurator;
	}
}