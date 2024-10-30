package com.ded.misle.boxes;

import static com.ded.misle.boxes.BoxesHandling.*;

public class BoxesLoad {
	public static void loadBoxes() {

		System.out.println("Boxes loaded!");

		// CHECKPOINTS

		addBox(200, 150);
		editLastBox("effect", "{spawnpoint, -1}");
		editLastBox("color", "0xF0F05A");

		addBox(400, 150);
		editLastBox("effect", "{spawnpoint, -1}");
		editLastBox("color", "0xF0F05A");

		addBox(200, 200);
		editLastBox("color", "0xFEC5E5");

		addBox(200, 264);
		editLastBox("color", "0xBEBEBE");
		editLastBox("hasCollision", "true");
		addBox(200, 312);
		editLastBox("color", "0x7D9AEE", 1);

		lineAddBox(200, 332, 5, 2, 20, "wall");
	}
}
