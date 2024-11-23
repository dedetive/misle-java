package com.ded.misle.boxes;

import java.util.HashMap;
import java.util.Map;

import static com.ded.misle.GamePanel.player;
import static com.ded.misle.GamePanel.setWorldBorders;
import static com.ded.misle.boxes.BoxesHandling.*;

public class BoxesLoad {
	static HashMap<String, Integer> room = new HashMap<>();


	static {
		room.put("void", 0);
		room.put("city_tuani", 1);
	}


	public static void loadBoxes() {
		System.out.println("Loading room: " + roomIDToName(player.pos.getRoomID()));
		switch (roomIDToName(player.pos.getRoomID())) {
			case "void" -> {
				;
			}

			case "city_tuani" -> {
				int worldWidth = 990;
				int worldHeight = 1000;
				setWorldBorders(worldWidth, worldHeight);
				fillGrass(worldWidth, worldHeight);
				lineAddBox(0, 0, worldWidth / 20 - 1, worldHeight / 20 - 2, "wallDefault@Deco", LineAddBoxMode.HOLLOW);

				addBox(800, 160, "mountainChest");
				addBox(160, 800, "mountainChest");

				lineAddBox(300, 420, 7, 6, "wallDefault", LineAddBoxMode.HOLLOW);
				deleteBox(3, 2);
				editBoxNegativeIndex("texture", "wallDefault@Deco.AWD..@", 2);
				editBoxNegativeIndex("texture", "wallDefault@Deco.ASD..@", 3);

				addBox(500, 540, "spawnpoint");
			}

			case null -> {}
			default -> throw new IllegalStateException("Unexpected value: " + roomIDToName(player.pos.getRoomID()) + ", ID: " + player.pos.getRoomID());
		}
	}

	public static int roomNameToID(String roomName) {
		return room.get(roomName);
	}

	public static String roomIDToName(int roomID) {
		for (Map.Entry<String, Integer> entry : room.entrySet()) {
			if (entry.getValue() == roomID) {
				return entry.getKey();
			}
		}
		return null;
	}

	private static void fillGrass(int worldWidth, int worldHeight) {
		double interval = 1.9;
		int grassAdded = lineAddScaledBox(0, 0, (int) Math.floor((double) worldWidth / (interval * 20)), (int) Math.floor((double) worldHeight / (interval * 20)), "fill", interval);
		editLastBox("texture", "grass", grassAdded);
	}

	public static void unloadBoxes() {
		clearAllBoxes();
	}
}