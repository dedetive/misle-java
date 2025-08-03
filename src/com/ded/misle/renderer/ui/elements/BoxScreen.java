package com.ded.misle.renderer.ui.elements;

import com.ded.misle.renderer.ui.core.AbstractUIElement;
import com.ded.misle.world.boxes.Box;
import com.ded.misle.world.boxes.BoxHandling;

import java.awt.*;
import java.util.*;
import java.util.List;

public class BoxScreen extends AbstractUIElement.SingletonUIElement {

	private static final List<BoxRepresentation> boxes = new ArrayList<>();

	public static void triggerGlobalUpdate() {
		List<BoxRepresentation> boxes = new ArrayList<>(BoxScreen.boxes);
		for (BoxRepresentation box : boxes) {
			box.triggerUpdate();
		}
	}

	public static void updateRepresentations() {
		List<Box> boxes = new ArrayList<>(BoxHandling.getAllBoxes());
		for (Box box : boxes) {
			box.updateVisualOffset(10f);
			box.updateVisualPosition(20f);
			box.representation.updatePosition(box);
		}
	}

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
		updateRepresentations();
		List<BoxRepresentation> boxes = new ArrayList<>(BoxScreen.boxes);
		boxes.sort(Comparator.comparingInt(BoxRepresentation::getPriority));
		for (BoxRepresentation box : boxes) {
			box.drawIfPossible(g2d);
		}
	}
}
