package com.ded.misle.world.entities.player.attributes.core;

public interface Attribute<ValueType, ApplyType> {
	void update();
	ApplyType apply(ApplyType arg);
}