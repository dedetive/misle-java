package com.ded.misle.world;

import com.ded.misle.world.boxes.Box;

import static com.ded.misle.core.GamePanel.player;
import static com.ded.misle.core.GamePanel.setWorldBorders;

public class World {
    int width;
    int height;
    Box[][] world;
    RoomManager.Room room;

    World(int worldWidth, int worldHeight) {
        width = worldWidth;
        height = worldHeight;
        world = new Box[worldWidth][worldHeight];

        setWorldBorders(worldWidth, worldHeight);

        room = RoomManager.roomIDToName(player.pos.getRoomID());
    }
}
