package com.ded.misle.boxes;

import java.util.HashMap;
import java.util.Map;

import static com.ded.misle.GamePanel.player;
import static com.ded.misle.GamePanel.setWorldBorders;
import static com.ded.misle.boxes.BoxesHandling.*;
import static com.ded.misle.boxes.BoxesHandling.EditBoxKeys.*;
import static com.ded.misle.boxes.BoxesHandling.LineAddBoxModes.FILL;
import static com.ded.misle.boxes.BoxesHandling.LineAddBoxModes.HOLLOW;

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
				//Setup
				int worldWidth = 1000;
				int worldHeight = 990;
				setupWorld(worldWidth, worldHeight);

				// Top left section
				lineAddBox(0, -2, (worldWidth / 20 - 2) / 2 - 3, 5, "wall_default", FILL);
				// Top Right section
				lineAddBox((double) worldWidth / 2 + 70, -2, (worldWidth / 20 - 2) / 2 - 3, 5, "wall_default", FILL);
				// Left column
				int boxesAdded = lineAddBox(0, 100, 1, worldHeight / 20 - 7, "wall_default", HOLLOW);
				editBoxNegativeIndex(TEXTURE, "wall_default.AD", boxesAdded); // Intersection fix
				// Right column
				boxesAdded = lineAddBox(worldWidth - 20, 100, 1, worldHeight / 20 - 7, "wall_default", HOLLOW);
				editBoxNegativeIndex(TEXTURE, "wall_default.AD", boxesAdded); // Intersection fix
				// Bottom row
				boxesAdded = lineAddBox(0, worldHeight - 30, worldWidth / 20 - 2, 1, "wall_default", HOLLOW);
				editBoxNegativeIndex(TEXTURE, "wall_default.AS.D", boxesAdded); // Intersection fix
				editLastBox(TEXTURE, "wall_default.SD.W"); // Intersection fix

				// "House"
				lineAddBox(300, 420, 7, 7, "wall_default", HOLLOW);
				deleteBox(4);
				editBoxNegativeIndex(TEXTURE, "wall_default@Deco.AWD..@", 3);
				editBoxNegativeIndex(TEXTURE, "wall_default@Deco.ASD..@", 4);

				addBox(360, 480);
				editLastBox(EditBoxKeys.COLOR, "#A02020");
				editLastBox(HAS_COLLISION, "true");
				editLastBox(MAX_HP, "20");
				editLastBox(EFFECT, "{damage, 15, 300, normal, 0}");

				// For travelling to cliff
				int travelBoxesAdded = lineAddBox(440, 10, 6, 1, "travel", FILL);
				editLastBox(EFFECT, "{travel, 2, 300, 440}", travelBoxesAdded);

				// Extra
				addBox(500, 540, "spawnpoint");
			}

			case "cliff" -> {
				// Setup
				setupWorld(1190, 490);

				// Bottom left section
				lineAddBox(-10, 390, 12, 5, "wall_default", FILL);
				// Bottom right section
				lineAddBox(18 * 20 + 10, 390, 40, 5, "wall_default", FILL);
				// Top row
				lineAddBox(0, 0, 1190 / 20 - 24, 6, "wall_default", FILL);
				int boxesAdded = lineAddBox(((double) 1190 / 20 - 23) * 20, 104, 15, 1, "wall_default", FILL);
				editLastBox(TEXTURE, "wall_default.WS", boxesAdded);
				editBoxNegativeIndex(TEXTURE, "wall_default.S.W", 2);
				editLastBox(TEXTURE, "wall_default.SD");
				lineAddBox(((double) 1190 / 20 - 9) * 20 - 9, 0, 2, 5, "wall_default", FILL);
				editBoxNegativeIndex(TEXTURE, "wall_default.D", 1);
				editBoxNegativeIndex(TEXTURE, "wall_default.A", 6);
				// CHEST AREA
				lineAddBox(((double) 1190 / 20 - 23) * 20 - 2, 0, 5, 2, "wall_default", FILL);
				editLastBox(TEXTURE, "wall_default.SD");
				editBoxNegativeIndex(TEXTURE, "wall_default@Deco.W", 10);
				for (int i = 3; i < 10; i += 2) {
					editBoxNegativeIndex(TEXTURE, "wall_default.S", i);
				}
				addBox(((double) 1190 / 20 - 23) * 20 + 20, 60, "mountain_chest");
				// Right column
				lineAddBox(1170, -7, 1, 500 / 20 - 5, "wall_default", FILL);
				editLastBox(TEXTURE, "wall_default", 1);

				int travelBoxesAdded = lineAddBox(250, 460, 6, 1, "travel", FILL);
				editLastBox(EFFECT, "{travel, 1, 500, 31}", travelBoxesAdded);
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