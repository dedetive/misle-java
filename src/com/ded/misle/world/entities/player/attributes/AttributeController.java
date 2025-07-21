package com.ded.misle.world.entities.player.attributes;

public final class AttributeController {
	private Attribute[] attributes;

	public AttributeController(Attribute... attributes) {
		this.attributes = attributes;
	}

	public void applyAttributes() {
		for (Attribute attribute : attributes) {
			attribute.apply();
		}
	}

	public void updateAttributes() {
		for (Attribute attribute : attributes) {
			attribute.update();
		}
	}

	public void setAttributes(Attribute... attributes) {
		this.attributes = attributes;
	}

	public Attribute[] getAttributes() {
		return attributes;
	}
}
