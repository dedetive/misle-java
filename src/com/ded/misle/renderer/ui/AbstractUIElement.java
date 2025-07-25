package com.ded.misle.renderer.ui;

public abstract class AbstractUIElement implements UIElement {

	protected boolean isActive = true;

	@Override
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	@Override
	public boolean isActive() {
		return isActive;
	}
}
