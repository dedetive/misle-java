package com.ded.misle.world.data;

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
