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

	public static abstract class SingletonUIElement extends AbstractUIElement {
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;

			return this.getClass() == obj.getClass();
		}

		@Override
		public int hashCode() {
			return getClass().hashCode();
		}
	}
}
