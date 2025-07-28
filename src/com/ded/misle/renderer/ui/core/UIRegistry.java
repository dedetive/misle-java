package com.ded.misle.renderer.ui.core;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class UIRegistry {
	public UIRegistry() {}

	private final List<UIElement> elements = new ArrayList<>();

	public <T extends UIElement> void add(T element) {
		if (element == null) return;
		if (elements.contains(element)) return;
		elements.add(element);
	}

	public <T extends AbstractUIElement.SingletonUIElement> void add(Class<T> clazz) {
		try {
			T element = clazz.getDeclaredConstructor().newInstance();
			if (elements.contains(element)) return;
			elements.add(element);
		} catch (ReflectiveOperationException e) {
			System.err.println(("Failed to instantiate UI element: " + clazz.getName() + " — " + e.getMessage()));
		}
	}

	public <T extends UIElement> void remove(T element) {
		elements.remove(element);
	}

	public void clear() {
		elements.clear();
	}

	public List<UIElement> getAll() {
		return elements;
	}

	public List<UIElement> getActive() {
		List<UIElement> active = new ArrayList<>(elements.size());
		List<UIElement> copy = new ArrayList<>(elements);
		for (UIElement e : copy) {
			if (e.isActive()) active.add(e);
		}
		return active;
	}

	public void drawActive(Graphics2D g2d) {
		for (UIElement e : getActive()) {
			if (e.isActive()) {
				e.drawIfPossible(g2d);
			}
		}
	}
}
