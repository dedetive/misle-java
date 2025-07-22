package com.ded.misle.world.entities.player.attributes.core;

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

	public void setAttributes(Attribute<?, ?>... attributes) {
		this.attributes = attributes;
	}

	public Attribute<?, ?>[] getAttributes() {
		return attributes;
	}
}
