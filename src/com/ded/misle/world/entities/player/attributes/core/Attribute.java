package com.ded.misle.world.entities.player.attributes.core;

public interface Attribute<ValueType, ApplyType> {
	void setValue(ValueType value);
	ValueType getValue();
	void update();
	ApplyType apply(ApplyType arg);
}