package com.ded.misle.boxes;

import java.util.Objects;

import static com.ded.misle.GamePanel.player;
import static com.ded.misle.boxes.BoxesHandling.*;

public class BoxesLoad {
	public static void loadBoxes() {

		if (Objects.equals(player.pos.getRegion(), "Chain of Lies")) {
			int boxesAdded = lineAddScaledBox(0, 0, 100, 100, "fill", 2);
			editLastBox("texture", "grass", boxesAdded);

			addBox(200, 150, "spawnpoint");
			addBox(400, 250, "spawnpoint");

			addBox(400, 150, "mountainChest");

			lineAddBox(160, 110, 15, 10, "wallDefault@Deco", "hollow");
			deleteBox(getAllBoxes().get(getAllBoxes().size() - 5));
			deleteBox(getAllBoxes().get(getAllBoxes().size() - 5));
			editBox(getAllBoxes().get(getAllBoxes().size() - 5), "texture", "wallDefault.ASD");
			editBox(getAllBoxes().get(getAllBoxes().size() - 4), "texture", "wallDefault.AWD");

			lineCoordinatedAddBox(31, 5.5, 5, 1, "wallDefault", "fill");
			lineCoordinatedAddBox(30, 7.5, 1, 5, "wallDefault", "fill");

			lineCoordinatedAddBox(31, 13.5, 5, 1, "wallDefault@Deco", "fill");
			lineCoordinatedAddBox(36, 7.5, 1, 5, "wallDefault@Deco", "fill");

			addBox(300, 230);
			editLastBox("effect", "{damage, 10, 350, normal, {''}}");
			editLastBox("hasCollision", "true");
			editLastBox("color", "0xFE4040");
			editLastBox("texture", "fire");
		}
	}

	public static void unloadBoxes() {
		clearAllBoxes();
	}
}