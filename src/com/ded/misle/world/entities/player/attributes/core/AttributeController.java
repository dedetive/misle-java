package com.ded.misle.world.entities.player.attributes.core;

import java.util.Arrays;
import java.util.Optional;

public final class AttributeController {
	private Attribute<?, ?>[] attributes;

	public AttributeController(Attribute<?, ?>... attributes) {
		this.attributes = attributes;
	}

	public void updateAttributes() {
		Arrays.stream(attributes)
				.filter(t -> t instanceof Attribute.PassiveAttribute<?,?>)
				.forEach(t ->
						((Attribute.PassiveAttribute<?,?>) t).update());
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
		for (Attribute<?, ?> a : attributes) {
			if (a.getClass().equals(attribute.getClass())) {
				System.err.println("Warning: Attempted to add an attribute that already exists");
				return;
			}
		}
		this.attributes = Arrays.copyOf(attributes, attributes.length + 1);
		this.attributes[attributes.length - 1] = attribute;
	}

	public Attribute<?, ?>[] get() {
		return attributes;
	}
}
