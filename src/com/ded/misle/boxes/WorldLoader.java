package com.ded.misle.boxes;

import com.ded.misle.npcs.NPC;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static com.ded.misle.GamePanel.player;
import static com.ded.misle.GamePanel.setWorldBorders;
import static com.ded.misle.boxes.BoxHandling.*;
import static com.ded.misle.boxes.BoxHandling.EditBoxKeys.*;
import static com.ded.misle.boxes.BoxHandling.LineAddBoxModes.FILL;
import static com.ded.misle.boxes.BoxHandling.LineAddBoxModes.HOLLOW;
import static com.ded.misle.npcs.NPC.InteractionType.DIALOG;
import static com.ded.misle.npcs.NPC.InteractionType.NONE;

public class WorldLoader {
	static HashMap<String, Integer> room = new HashMap<>();

	static {
		room.put("void", 0);
		room.put("city_tuani", 1);
		room.put("tuani_house1", 2);
		room.put("cliff", 3);
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

				// House 1
				lineAddBox(300, 420, 7, 7, "wall_default", HOLLOW);
				deleteBox(4);
				editBoxNegativeIndex(TEXTURE, "wall_default@Deco.AWD..@", 3);
				editBoxNegativeIndex(TEXTURE, "wall_default@Deco.ASD..@", 4);
					// Interior
				boxesAdded = lineAddBox(321, 441, 5, 5, "wall_default", FILL);
				editLastBox(HAS_COLLISION, "false", boxesAdded);
					// Door
				addBox(426, 483, "travel");
				editLastBox(HAS_COLLISION, "true");
				editLastBox(EFFECT, "{travel, 2, 350, 110}");
					// Chest and spawnpoint
				addBox(321, 441, "spawnpoint");
				addBox(341, 441, "mountain_chest");
					// Corner block
				addBox(382, 502);
				editLastBox(EditBoxKeys.COLOR, "#A02020");

				// For travelling to cliff
				int travelBoxesAdded = lineAddBox(440, 10, 6, 1, "travel", FILL);
				editLastBox(EFFECT, "{travel, 3, 300, 440}", travelBoxesAdded);

				// NPC testing
				NPC yellowBlock = new NPC(500, 300, DIALOG);
				yellowBlock.setDialogID(1);
				yellowBlock.setName("Yellow block");
				yellowBlock.setNameColor(new Color(0xFFFF00));

				NPC magentaBlock = new NPC(540, 340, DIALOG);
				editBox(magentaBlock, COLOR, "#FF00FF");
				magentaBlock.setDialogID(2);
				magentaBlock.setName("Magenta block");
				magentaBlock.setNameColor(new Color(0xFF00FF));

				NPC cyanBlock = new NPC(460, 340, NONE);
				editBox(cyanBlock, COLOR, "#00FFFF");
			}

			case "tuani_house1" -> {
				//Setup
				int worldWidth = 500;
				int worldHeight = 400;
				setupWorld(worldWidth, worldHeight);

				// Walls
				lineAddBox(240, 20, 9, 9, "wall_default", HOLLOW);

				// Door
				deleteBox(5);
				editBoxNegativeIndex(TEXTURE, "wall_default@Deco.AWD..@", 4);
				editBoxNegativeIndex(TEXTURE, "wall_default@Deco.ASD..@", 5);

				addBox(405, 104, "travel");
				editLastBox(HAS_COLLISION, "true");
				editLastBox(EFFECT, "{travel, 1, 460, 483}");

				// Floor
				int boxesAdded = lineAddBox(261, 41, 7, 7, "wall_default", FILL);
				editLastBox(HAS_COLLISION, "false", boxesAdded);
//				addBox(20, 20); 		NEED TO ADD ALPHA PARAMETER TO BOXES
//				editLastBox(BOX_SCALE_HORIZONTAL, "14");
//				editLastBox(BOX_SCALE_VERTICAL, "14");
//				editLastBox(COLOR, "#808080");

				// Chest
				addBox(281, 41, "mountain_chest");

				// Spawnpoint
				addBox(261, 41, "spawnpoint");

				// Corner block
				HPBox corner = addHPBox(365, 145);
				corner.setMaxHP(50);
				corner.setHP(50);
				editLastBox(EditBoxKeys.COLOR, "#A02020");
				editLastBox(EFFECT, "{damage, 5, 300, normal, 0}");
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