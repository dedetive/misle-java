package com.ded.misle.world.entities.config;

import com.ded.misle.world.data.items.DropTable;
import com.ded.misle.world.entities.config.api.EntityConfigurator;
import com.ded.misle.world.entities.config.api.GenericConfigurations;
import com.ded.misle.world.entities.ai.behaviors.ChainBehavior;
import com.ded.misle.world.entities.ai.behaviors.WaitBehavior;
import com.ded.misle.world.entities.ai.behaviors.WanderBehavior;

public enum EntityConfigurations implements GenericConfigurations {
	MUNI(muni -> {
		muni.setProportionalMaxHP(3)
				.setTexture("muni")
				.setTurnsToRespawn(20)
				.setDropTable(DropTable.MUNI)
				.setVisualScaleHorizontal(0.8)
				.setVisualScaleVertical(0.8);

		var wait = new WaitBehavior(5);
		var wander = new WanderBehavior(2);

		ChainBehavior wanderThenWait = new ChainBehavior(
				wander,
				wait
		);

		muni.setBehaviors(wanderThenWait);
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