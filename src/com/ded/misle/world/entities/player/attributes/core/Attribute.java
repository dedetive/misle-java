package com.ded.misle.world.entities.player.attributes.core;

public interface Attribute<ValueType, ApplyType> {
	void setValue(ValueType value);
	ValueType getValue();

	interface ActiveAttribute<V, A> extends Attribute<V, A> {
		A apply(A arg);
	}

	interface PassiveAttribute<V, A> extends Attribute<V, A> {
		void update();
	}
}