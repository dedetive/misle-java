package com.ded.misle.world.data.entity.configurations;

import com.ded.misle.world.data.entity.EntityConfigurator;
import com.ded.misle.world.data.entity.GenericConfigurations;

public enum EntityConfigurations implements GenericConfigurations {

	;

	/**
	 * The functional configurator for this entity setup.
	 */
	public final EntityConfigurator c;

	EntityConfigurations(EntityConfigurator configurator) {
		this.c = configurator;
	}
}
