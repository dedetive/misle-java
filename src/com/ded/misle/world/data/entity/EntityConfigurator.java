package com.ded.misle.world.data.entity;

import com.ded.misle.world.entities.Entity;

public interface EntityConfigurator extends GenericConfigurator<Entity> {
	void configure(Entity enemy);
}