package com.ded.misle.world;

import com.ded.misle.world.enemies.Enemy;
import com.ded.misle.world.npcs.NPC;
import com.ded.misle.world.player.PlayerPosition;

import java.awt.*;

import static com.ded.misle.core.GamePanel.player;
import static com.ded.misle.core.GamePanel.setWorldBorders;
import static com.ded.misle.world.RoomManager.roomIDToName;
import static com.ded.misle.world.boxes.BoxHandling.*;
import static com.ded.misle.world.boxes.BoxHandling.EditBoxKeys.*;
import static com.ded.misle.world.boxes.BoxHandling.LineAddBoxModes.FILL;
import static com.ded.misle.world.boxes.BoxHandling.LineAddBoxModes.HOLLOW;
import static com.ded.misle.world.enemies.Enemy.EnemyType.GOBLIN;
import static com.ded.misle.world.npcs.NPC.InteractionType.DIALOG;
import static com.ded.misle.world.npcs.NPC.InteractionType.NONE;

public class WorldLoader {
	public static void loadBoxes() {
		System.out.println("Loading room: " + roomIDToName(player.pos.getRoomID()));
		switch (roomIDToName(player.pos.getRoomID())) {
			case VOID -> {
				// Setup
					int worldWidth = 400;
					int worldHeight = 400;
					setupWorld(worldWidth, worldHeight);
				// Building Boxes
					// Here is where structural boxes should be placed, such as walls.
					lineAddBox(0, 0, 4, 4, "wall_default", HOLLOW);
					// Typically, more specific structures such as houses inside a city should be left to the bottom of this section.

					// TESTING
		//				int boxesX = 10;
		//				int boxesY = 10;
		//				int index = boxesY + boxesY / 2 + (boxesX - 1) * 2 - 1;
		//				lineAddBox(40, 20, boxesX, boxesY, "wall_default", HOLLOW);
		//				editBoxNegativeIndex(TEXTURE, "invisible", index);

						// LINEADDBOX BOXES ARE ADDED FROM LEFT-TOP TO RIGHT-BOTTOM
						// For example: in a 7x7 hollow, the right-bottom-most box that is not a corner
						// would be 8 (height + 1) and the top equivalent would be 9 (height + 2)
						// The left equivalents would be 7 + 5 * 2 - 1 (height + (length - 2) * 2) - 1 for bottom and
						// add +1 for top.
						// For the middle bottom, it is 7 + 6 * 2 - 7 (height + (length - 1) * 2 - length)
						// For the top, add +1. It will look wrong for even-numbered X's though.
						// For the middle right side, it is height / 2 + 1.
						// For the left, it is height + height / 2 + (length - 1) * 2 - 1

				// Chests
					// Type of chest must be specified.
					addBox(20, 20, "mountain_chest");

				// Spawnpoint Box
					///  There should ideally be either 0 or 1 spawnpoint per room,
					/// and where player should spawn is specified in [PlayerPosition#reloadSpawnpoint()].
					addBox(40, 20, "spawnpoint");

				// Travel Boxes
					///  First line is to add the boxes with the travel preset,
					/// second line is to edit the boxes to have the specified travel property (room to travel to, position).
					/// All of which are specified in the [RoomManager.TravelTransition].
					int travelBoxesAdded = lineAddBox(60, 60, 3, 1, "travel", FILL);
					editLastBox(EFFECT, "{travel, TUANI_CITY_TO_1}", travelBoxesAdded);

				// Enemies
					/// Valid enemies are specified in [Enemy.EnemyType].
					/// Magnification affects only MAX HP and DAMAGE.
					new Enemy(200, 200, GOBLIN, 1);

				// NPCs
					/// Valid NPC types are specified in [NPC.InteractionType].
					/// Dialogs can be modified in the language property files, such as
					/// resources/lang/messages_en_US.properties. They are written in the format
					/// DIALOG_{ID}.{DIALOG_NUMBER_ORDER}. The DIALOG_{ID} without the number order
					/// must be an integer that represents the final ID to be shown to the player.
					/// Alternatively to the COLOR, there should be their TEXTURE.
					NPC magentaBlock = new NPC(240, 200, DIALOG);
					magentaBlock.setColor(new Color(0xFF00FF));
					magentaBlock.setDialogID(2);
					magentaBlock.setName("Magenta block");
					magentaBlock.setNameColor(new Color(0xFF00FF));
			}
			case TUANI_CITY -> {
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
				editLastBox(EFFECT, "{travel, ENTERING_TUANI_HOUSE_1}");
					// Chest and spawnpoint
				addBox(321, 441, "spawnpoint");
				addBox(341, 441, "mountain_chest");
					// Corner block
				addBox(382, 502);
				editLastBox(EditBoxKeys.COLOR, "#A02020");

				// Travel Boxes
				int travelBoxesAdded = lineAddBox(440, 10, 6, 1, "travel", FILL);
				editLastBox(EFFECT, "{travel, TUANI_CITY_TO_1}", travelBoxesAdded);

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
			case TUANI_HOUSE_1 -> {
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
				editLastBox(EFFECT, "{travel, LEAVING_TUANI_HOUSE_1}");

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
				addEnemyBox(365, 145, Enemy.EnemyType.RED_BLOCK, 0.5);
			}
			case TUANI_1 -> {
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

				// Travel Boxes
					// TUANI_1_TO_CITY
				int travelBoxesAdded = lineAddBox(250, 460, 6, 1, "travel", FILL);
				editLastBox(EFFECT, "{travel, TUANI_1_TO_CITY}", travelBoxesAdded);
					// TUANI_1_TO_2
//				travelBoxesAdded = lineAddBox(((double) 1190 / 20 - 8) * 20, 10, 7, 1, "travel", FILL);
//				editLastBox(EFFECT, "{travel, TUANI_1_TO_2}", travelBoxesAdded);

				// Enemies
				new Enemy(300, 200, GOBLIN, 0.5);
				new Enemy(380, 240, GOBLIN, 0.5);
				new Enemy(570, 320, GOBLIN, 0.5);
				new Enemy(620, 220, GOBLIN, 0.5);
			}
//			case TUANI_2 -> {
//				// Setup
//				setupWorld(1190, 490);
//			}


			case null -> {}
			default -> throw new IllegalStateException("Unexpected value: " + roomIDToName(player.pos.getRoomID()) + ", ID: " + player.pos.getRoomID());
		}
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