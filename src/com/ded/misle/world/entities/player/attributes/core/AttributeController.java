package com.ded.misle.world.entities.player.attributes.core;

import java.util.Arrays;
import java.util.Optional;

public final class AttributeController {
	private Attribute<?, ?>[] attributes;

	public AttributeController(Attribute<?, ?>... attributes) {
		this.attributes = attributes;
	}

	public void updateAttributes() {
		for (Attribute<?, ?> attribute : attributes) {
			attribute.update();
		}
	}

	public void set(Attribute<?, ?>... attributes) {
		this.attributes = attributes;
	}

	@SuppressWarnings("unchecked")
	public <T extends Attribute<?, ?>> Optional<T> get(Class<T> clazz) {
		for (Attribute<?, ?> attribute : attributes) {
			if (attribute.getClass().equals(clazz)) {
				return Optional.of((T) attribute);
			}
		}
		return Optional.empty();
	}

	public void add(Attribute<?, ?> attribute) {
		this.attributes = Arrays.copyOf(attributes, attributes.length + 1);
		this.attributes[attributes.length - 1] = attribute;
	}

	public Attribute<?, ?>[] get() {
		return attributes;
	}
}
