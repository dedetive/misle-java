package com.ded.misle.boxes;

import static com.ded.misle.boxes.BoxesHandling.*;

public class BoxesLoad {
	public static void loadBoxes() {

		int boxesAdded = lineAddBox(0,0, 60, 60, "fill", 1.98);
		editLastBox("texture", "grass", boxesAdded);

//		boxesAdded = lineAddBox(180, 130, 13, 8, "fill");
//		editLastBox("texture", "grass2", boxesAdded);
//
//		boxesAdded = lineAddBox(180, 130, 13, 8, "fill");
//		editLastBox("texture", "grass3", boxesAdded);

		addBox(200, 150, "spawnpoint");

		addBox(400, 150, "mountainChest");

		lineAddBox(160, 110, 15, 10, "wallDefault@Deco", "hollow");


		addBox(300, 230);
		editLastBox("effect","{damage, 10, 350, normal, {''}}");
		editLastBox("hasCollision", "true");
		editLastBox("color", "0xFE4040");
		editLastBox("texture", "fire");
	}

	public static void unloadBoxes() {
		clearAllBoxes();
	}
}