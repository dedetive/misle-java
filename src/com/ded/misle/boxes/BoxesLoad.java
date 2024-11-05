package com.ded.misle.boxes;

import static com.ded.misle.boxes.BoxesHandling.*;

public class BoxesLoad {
	public static void loadBoxes() {

		int boxesAdded = lineAddScaledBox(0,0, 60, 60, "fill", 1.98);
		editLastBox("texture", "grass", boxesAdded);

		addBox(200, 150, "spawnpoint");
		addBox(400, 250, "spawnpoint");

		addBox(400, 150, "mountainChest");

		lineAddScaledBox(160, 110, 15, 10, "wallDefault@Deco", "hollow");
		deleteBox(getAllBoxes().get(getAllBoxes().size() - 5));
		deleteBox(getAllBoxes().get(getAllBoxes().size() - 5));
		editBox(getAllBoxes().get(getAllBoxes().size() - 5), "texture", "wallDefault.ASD");
		editBox(getAllBoxes().get(getAllBoxes().size() - 4), "texture", "wallDefault.AWD");
		
		lineAddScaledBox(620, 110, 5, 5, "wallDefault", "fill");

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