package com.ded.misle.renderer.ui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class UIRegistry {
	private UIRegistry() {}

	private static final List<UIElement> elements = new ArrayList<>();

	public static <T extends UIElement> void add(T element) {
		if (elements.contains(element)) return;
		elements.add(element);
	}

	public static <T extends UIElement> void remove(T element) {
		elements.remove(element);
	}

	public static void clear() {
		elements.clear();
	}

	public static List<UIElement> getAll() {
		return elements;
	}

	public static List<UIElement> getActive() {
		List<UIElement> active = new ArrayList<>(elements.size());
		List<UIElement> copy = new ArrayList<>(elements);
		for (UIElement e : copy) {
			if (e.isActive()) active.add(e);
		}
		return active;
	}

	public static void drawActive(Graphics2D g2d) {
		for (UIElement e : getActive()) {
			if (e.isActive()) {
				e.drawIfPossible(g2d);
			}
		}
	}
}
