package com.ded.misle.boxes;

import java.util.HashMap;
import java.util.Map;

import static com.ded.misle.GamePanel.player;
import static com.ded.misle.GamePanel.setWorldBorders;
import static com.ded.misle.boxes.BoxesHandling.*;
import static com.ded.misle.boxes.BoxesHandling.EditBoxKeys.*;

public class BoxesLoad {
	static HashMap<String, Integer> room = new HashMap<>();


	static {
		room.put("void", 0);
		room.put("city_tuani", 1);
		room.put("cliff", 2);
	}


	public static void loadBoxes() {
		System.out.println("Loading room: " + roomIDToName(player.pos.getRoomID()));
		switch (roomIDToName(player.pos.getRoomID())) {
			case "void" -> {
				;
			}

			case "city_tuani" -> {
				int worldWidth = 1000;
				int worldHeight = 990;
				setupWorld(worldWidth, worldHeight);
				lineAddBox(0, 100, worldWidth / 20 - 2, 1, "wallDefault", LineAddBoxModes.HOLLOW);
				int boxesAdded = lineAddBox(0, 100, 1, worldHeight / 20 - 7, "wallDefault", LineAddBoxModes.HOLLOW);
				editBoxNegativeIndex(TEXTURE, "wallDefault.AW.S", boxesAdded);
				boxesAdded = lineAddBox(worldWidth - 20, 100, 1, worldHeight / 20 - 7, "wallDefault", LineAddBoxModes.HOLLOW);
				editBoxNegativeIndex(TEXTURE, "wallDefault.WD.A", boxesAdded);
				boxesAdded = lineAddBox(0, worldHeight - 30, worldWidth / 20 - 2, 1, "wallDefault", LineAddBoxModes.HOLLOW);
				editBoxNegativeIndex(TEXTURE, "wallDefault.AS.D", boxesAdded);
				editLastBox(TEXTURE, "wallDefault.SD.W");


				lineAddBox(300, 420, 7, 6, "wallDefault", LineAddBoxModes.HOLLOW);
				deleteBox(3, 2);
				editBoxNegativeIndex(TEXTURE, "wallDefault@Deco.AWD..@", 2);
				editBoxNegativeIndex(TEXTURE, "wallDefault@Deco.ASD..@", 3);

				int travelBoxesAdded = lineAddBox(440, 40, 6, 1, "travel", LineAddBoxModes.FILL);
				editLastBox(EFFECT, "{travel, 2, 300, 440}", travelBoxesAdded);

				addBox(500, 540, "spawnpoint");
			}

			case "cliff" -> {
				setupWorld(1190, 490);
				lineAddBox(0, 0, 1190 / 20 - 2, 500 / 20 - 2, "wallDefault@Deco", LineAddBoxModes.HOLLOW);
				for (int i = 100; i < 106; i++) {
					deleteBox(i);
				}
				editBoxNegativeIndex(TEXTURE, "wallDefault@Deco.AWS..@", 98);
				editBoxNegativeIndex(TEXTURE, "wallDefault@Deco.WSD..@", 106);

				int travelBoxesAdded = lineAddBox(250, 460, 6, 1, "travel", LineAddBoxModes.FILL);
				editLastBox(EFFECT, "{travel, 1, 500, 61}", travelBoxesAdded);
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
		double interval = 2.05;
		lineAddScaledBox(0, 0, (int) Math.ceil((double) worldWidth / (interval * 20)), (int) Math.ceil((double) worldHeight / (interval * 20)), "fill", interval, "grass");
	}

	private static void setupWorld(int worldWidth, int worldHeight) {
		setWorldBorders(worldWidth, worldHeight);
		fillGrass(worldWidth, worldHeight);
	}

	public static void unloadBoxes() {
		clearAllBoxes();
	}
}