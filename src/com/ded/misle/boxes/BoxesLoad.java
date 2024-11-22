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
		System.out.println(player.pos.getSpawnpoint());
		System.out.println(roomIDToName(player.pos.getRoomID()));
		switch (roomIDToName(player.pos.getRoomID())) {
			case "void" -> {
				;
			}
			case "city_tuani" -> {
				setWorldBorders(600, 600);
				int grassAdded = lineAddScaledBox(0, 0, 29, 29,"fill", 1.98);
				editLastBox("texture", "grass", grassAdded);
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

	public static void unloadBoxes() {
		clearAllBoxes();
	}
}