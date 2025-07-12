package com.ded.misle.world.entities.config;

import com.ded.misle.world.entities.Entity;

public interface EntityConfigurator extends GenericConfigurator<Entity> {
	void configure(Entity enemy);
}