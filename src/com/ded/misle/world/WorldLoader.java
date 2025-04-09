package com.ded.misle.world;

import com.ded.misle.world.boxes.Box;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import static com.ded.misle.core.GamePanel.player;
import static com.ded.misle.core.SettingsManager.getPath;
import static com.ded.misle.world.RoomManager.Room.TUANI_CITY;
import static com.ded.misle.world.RoomManager.roomIDToName;
import static com.ded.misle.world.World.Background.GRASS;
import static com.ded.misle.world.boxes.BoxHandling.*;

public class WorldLoader {
	public static void loadBoxes() {
		World world = null;
		RoomManager.Room room = roomIDToName(player.pos.getRoomID());
		room = roomIDToName(TUANI_CITY.id); // TEMPORARILY FORCING TUANI_CITY
        assert room != null;

		Path basePath = getPath().resolve("resources/worlds/");
        Path fullPath = basePath.resolve(room.toString());
		BufferedImage roomImage;
		try {
			roomImage = ImageIO.read(fullPath.toFile());
		} catch (IOException e) {
			System.out.println(fullPath);
			throw new RuntimeException(e);
		}

		// Set dimensions based on image dimensions
		int worldWidth = roomImage.getWidth();
		int worldHeight = roomImage.getHeight();
		world = new World(worldWidth, worldHeight, GRASS);

		// Read values and set as boxes
		for (int x = 0; x < worldWidth; x++) {
			for (int y = 0; y < worldHeight; y++) {
				Color color = new Color(roomImage.getRGB(x, y));
				int rgb = color.getRGB() & 0xFFFFFF;

                Box box = null;
                try {
					// Gets the created box
                    box = RGBToBox.get(rgb).call();
                } catch (Exception e) {
                    // This would mean it failed to create the box, so ignore
                }

                // Ignore null boxes
				if (box == null) continue;

				box.setPos(x, y);
			}
		}


//		switch (room) {
//			case VOID -> {
//				// Setup
//					int worldWidth = 20;
//					int worldHeight = 20;
//					world = new World(worldWidth, worldHeight, GRASS);
//				// Building Boxes
//					// Here is where structural boxes should be placed, such as walls.
////					lineAddBox(0, 0, 4, 4, "wall_default", HOLLOW);
//					// Typically, more specific structures such as houses inside a city should be left to the bottom of this section.
//
//					// TESTING
//		//				int boxesX = 10;
//		//				int boxesY = 10;
//		//				int index = boxesY + boxesY / 2 + (boxesX - 1) * 2 - 1;
//		//				lineAddBox(40, 20, boxesX, boxesY, "wall_default", HOLLOW);
//		//				editBoxNegativeIndex(TEXTURE, "invisible", index);
//
//						// LINEADDBOX BOXES ARE ADDED FROM LEFT-TOP TO RIGHT-BOTTOM
//						// For example: in a 7x7 hollow, the right-bottom-most box that is not a corner
//						// would be 8 (height + 1) and the top equivalent would be 9 (height + 2)
//						// The left equivalents would be 7 + 5 * 2 - 1 (height + (length - 2) * 2) - 1 for bottom and
//						// add +1 for top.
//						// For the middle bottom, it is 7 + 6 * 2 - 7 (height + (length - 1) * 2 - length)
//						// For the top, add +1. It will look wrong for even-numbered X's though.
//						// For the middle right side, it is height / 2 + 1.
//						// For the left, it is height + height / 2 + (length - 1) * 2 - 1
//
//				// Chests
//					// Type of chest must be specified.
//					addBox(1, 1, "mountain_chest");
//
//				// Spawnpoint Box
//					///  There should ideally be either 0 or 1 spawnpoint per room,
//					/// and where player should spawn is specified in [PlayerPosition#reloadSpawnpoint()].
//					addBox(2, 2, "spawnpoint");
//
//				// Travel Boxes
//					///  First line is to add the boxes with the travel preset,
//					/// second line is to edit the boxes to have the specified travel property (room to travel to, position).
//					/// All of which are specified in the [RoomManager.TravelTransition].
//					int travelBoxesAdded = lineAddBox(3, 3, 3, 1, "travel", FILL);
////					editLastBox(EFFECT, "{travel, TUANI_CITY_TO_1}", travelBoxesAdded);
//
//				// Enemies
//					/// Valid enemies are specified in [Enemy.EnemyType].
//					/// Magnification affects only MAX HP and DAMAGE.
////					new Enemy(10, 10, GOBLIN, 1);
//
//				// NPCs
//					/// Valid NPC types are specified in [NPC.InteractionType].
//					/// Dialogs can be modified in the language property files, such as
//					/// resources/lang/messages_en_US.properties. They are written in the format
//					/// DIALOG_{ID}.{DIALOG_NUMBER_ORDER}. The DIALOG_{ID} without the number order
//					/// must be an integer that represents the final ID to be shown to the player.
//					/// Alternatively to the COLOR, there should be their TEXTURE.
//					NPC magentaBlock = new NPC(22, 10, DIALOG);
//					magentaBlock.setColor(new Color(0xFF00FF));
//					magentaBlock.setDialogID(2);
//					magentaBlock.name = "Magenta block";
//					magentaBlock.nameColor = new Color(0xFF00FF);
//			}
//			case TUANI_CITY -> {
//				//Setup
//				int worldWidth = 45;
//				int worldHeight = 45;
//				world = new World(worldWidth, worldHeight);
//
//				// Top left section
//				lineAddBox(0, 0, 14, 5, "wall_default", FILL);
//				// Top Right section
//				lineAddBox(17, 0, 12, 5, "wall_default", FILL);
//				// Left column
//				int boxesAdded = lineAddBox(0, 5, 1, worldHeight - 7, "wall_default", HOLLOW);
//				editBoxNegativeIndex(TEXTURE, "wall_default.AD", boxesAdded); // Intersection fix
//				// Right column
//				boxesAdded = lineAddBox(28, 5, 1, worldHeight - 7, "wall_default", HOLLOW);
//				editBoxNegativeIndex(TEXTURE, "wall_default.AD", boxesAdded); // Intersection fix
//				// Bottom row
//				boxesAdded = lineAddBox(0, worldHeight - 1, worldWidth - 2, 1, "wall_default", HOLLOW);
//				editBoxNegativeIndex(TEXTURE, "wall_default.AS.D", boxesAdded); // Intersection fix
//				editLastBox(TEXTURE, "wall_default.SD.W"); // Intersection fix
//
//				// House 1
//				lineAddBox(15, 21, 7, 7, "wall_default", HOLLOW);
//				deleteBox(4);
//				editBoxNegativeIndex(TEXTURE, "wall_default@Deco.AWD..@", 3);
//				editBoxNegativeIndex(TEXTURE, "wall_default@Deco.ASD..@", 4);
//					// Interior
//				boxesAdded = lineAddBox(16, 22, 5, 5, "wall_default", FILL);
//				editLastBox(HAS_COLLISION, "false", boxesAdded);
//					// Door
////				addBox(21, 24, "travel");
////				editLastBox(HAS_COLLISION, "true");
////				editLastBox(EFFECT, "{travel, ENTERING_TUANI_HOUSE_1}");
//					// Chest and spawnpoint
//				addBox(16, 22, "spawnpoint");
//				addBox(17, 22, "mountain_chest");
//					// Corner block
//				addBox(19, 25);
//				editLastBox(EditBoxKeys.COLOR, "#A02020");
//
//				// Travel Boxes
////				int travelBoxesAdded = lineAddBox(22, 0, 6, 1, "travel", FILL);
////				editLastBox(EFFECT, "{travel, TUANI_CITY_TO_1}", travelBoxesAdded);
//
//				// NPC testing
//				NPC yellowBlock = new NPC(25, 15, DIALOG);
//				yellowBlock.setDialogID(1);
//				yellowBlock.name = "Yellow block";
//				yellowBlock.nameColor = new Color(0xFFFF00);
//
//				NPC magentaBlock = new NPC(27, 17, DIALOG);
//				editBox(magentaBlock, COLOR, "#FF00FF");
//				magentaBlock.setDialogID(2);
//				magentaBlock.name = "Magenta block";
//				magentaBlock.nameColor = new Color(0xFF00FF);
//
//				NPC cyanBlock = new NPC(23, 17, NONE);
//				editBox(cyanBlock, COLOR, "#00FFFF");
//			}
////			case TUANI_HOUSE_1 -> {
////				//Setup
////				int worldWidth = 500;
////				int worldHeight = 400;
////				world = new World(worldWidth, worldHeight);
////
////				// Walls
////				lineAddBox(240, 20, 9, 9, "wall_default", HOLLOW);
////
////				// Door
////				deleteBox(5);
////				editBoxNegativeIndex(TEXTURE, "wall_default@Deco.AWD..@", 4);
////				editBoxNegativeIndex(TEXTURE, "wall_default@Deco.ASD..@", 5);
////
////				addBox(405, 104, "travel");
////				editLastBox(HAS_COLLISION, "true");
////				editLastBox(EFFECT, "{travel, LEAVING_TUANI_HOUSE_1}");
////
////				// Floor
////				int boxesAdded = lineAddBox(261, 41, 7, 7, "wall_default", FILL);
////				editLastBox(HAS_COLLISION, "false", boxesAdded);
//////				addBox(20, 20); 		NEED TO ADD ALPHA PARAMETER TO BOXES
//////				editLastBox(BOX_SCALE_HORIZONTAL, "14");
//////				editLastBox(BOX_SCALE_VERTICAL, "14");
//////				editLastBox(COLOR, "#808080");
////
////				// Chest
////				addBox(281, 41, "mountain_chest");
////
////				// Spawnpoint
////				addBox(261, 41, "spawnpoint");
////
////				// Corner block
////				addEnemyBox(365, 145, Enemy.EnemyType.RED_BLOCK, 0.5);
////			}
////			case TUANI_1 -> {
////				// Setup
////				int worldWidth = 1190;
////				int worldHeight = 490;
////				world = new World(worldWidth / 20, worldHeight / 20);
////
////				// Bottom left section
////				lineAddBox(-10, 390, 12, 5, "wall_default", FILL);
////				// Bottom right section
////				lineAddBox(18 * 20 + 10, 390, 40, 5, "wall_default", FILL);
////				// Top row
////				lineAddBox(0, 0, 1190 / 20 - 24, 6, "wall_default", FILL);
////				int boxesAdded = lineAddBox((1190 / 20 - 23) * 20, 104, 15, 1, "wall_default", FILL);
////				editLastBox(TEXTURE, "wall_default.WS", boxesAdded);
////				editBoxNegativeIndex(TEXTURE, "wall_default.S.W", 2);
////				editLastBox(TEXTURE, "wall_default.SD");
////				lineAddBox((1190 / 20 - 9) * 20 - 9, 0, 2, 5, "wall_default", FILL);
////				editBoxNegativeIndex(TEXTURE, "wall_default.D", 1);
////				editBoxNegativeIndex(TEXTURE, "wall_default.A", 6);
////				// CHEST AREA
////				lineAddBox((1190 / 20 - 23) * 20 - 2, 0, 5, 2, "wall_default", FILL);
////				editLastBox(TEXTURE, "wall_default.SD");
////				editBoxNegativeIndex(TEXTURE, "wall_default@Deco.W", 10);
////				for (int i = 3; i < 10; i += 2) {
////					editBoxNegativeIndex(TEXTURE, "wall_default.S", i);
////				}
////				addBox((1190 / 20 - 23) * 20 + 20, 60, "mountain_chest");
////				// Right column
////				lineAddBox(1170, -7, 1, 500 / 20 - 5, "wall_default", FILL);
////				editLastBox(TEXTURE, "wall_default", 1);
////
////				// Travel Boxes
////					// TUANI_1_TO_CITY
////				int travelBoxesAdded = lineAddBox(250, 460, 6, 1, "travel", FILL);
////				editLastBox(EFFECT, "{travel, TUANI_1_TO_CITY}", travelBoxesAdded);
////					// TUANI_1_TO_2
//////				travelBoxesAdded = lineAddBox(((double) 1190 / 20 - 8) * 20, 10, 7, 1, "travel", FILL);
//////				editLastBox(EFFECT, "{travel, TUANI_1_TO_2}", travelBoxesAdded);
////
////				// Enemies
////				new Enemy(300, 200, GOBLIN, 0.5);
////				new Enemy(380, 240, GOBLIN, 0.5);
////				new Enemy(570, 320, GOBLIN, 0.5);
////				new Enemy(620, 220, GOBLIN, 0.5);
////			}
////			case TUANI_2 -> {
////				// Setup
////			int worldWidth = 1;
////			int worldHeight = 1;
////			world = new World(worldWidth, worldHeight);
////			}
//
//
//			case null -> {}
//			default -> throw new IllegalStateException("Unexpected value: " + roomIDToName(player.pos.getRoomID()) + ", ID: " + player.pos.getRoomID());
//
//		}
	}

	private static final Map<Integer, Callable<Box>> RGBToBox = Map.of(
		0xC4C4C4, () -> addBox("wall_default")
    );

	public static void unloadBoxes() {
		clearAllBoxes();
	}
}