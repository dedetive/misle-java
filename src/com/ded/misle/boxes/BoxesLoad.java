package com.ded.misle.boxes;

import static com.ded.misle.boxes.BoxesHandling.*;

public class BoxesLoad {
	public static void loadBoxes() {

		addBox(200, 150, "spawnpoint");

		addBox(400, 150, "spawnpoint");

		lineAddBox(160, 110, 15, 5, "wall", "hollow");

	}
}
