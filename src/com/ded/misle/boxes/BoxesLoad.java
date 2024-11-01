package com.ded.misle.boxes;

import static com.ded.misle.boxes.BoxesHandling.*;

public class BoxesLoad {
	public static void loadBoxes() {

		addBox(200, 150, "spawnpoint");

		addBox(400, 150, "chest");

		lineAddBox(160, 110, 15, 10, "wallDefault", "hollow");

		addBox(300, 230);
		editLastBox("effect","{damage, 10, 350, normal, {''}}");
		editLastBox("hasCollision", "true");
		editLastBox("color", "0xFE4040");
		editLastBox("texture", "fire");
	}
}