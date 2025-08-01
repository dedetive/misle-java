package com.ded.misle.renderer.ui.elements;

import com.ded.misle.renderer.ui.core.AbstractUIElement;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BoxScreen extends AbstractUIElement.SingletonUIElement {

	private static final List<BoxRepresentation> boxes = new ArrayList<>();

	public static void flush() {
		boxes.clear();
	}

	public static void addBox(BoxRepresentation box) {
		boxes.add(box);
	}

	public static void removeBox(BoxRepresentation box) {
		boxes.remove(box);
	}

	@Override
	public void drawIfPossible(Graphics2D g2d) {
		for (BoxRepresentation box : boxes)
			box.drawIfPossible(g2d);
	}
}
