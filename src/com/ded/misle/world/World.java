package com.ded.misle.world;

import com.ded.misle.world.boxes.Box;

import static com.ded.misle.core.GamePanel.player;
import static com.ded.misle.core.GamePanel.setWorldBorders;
import static com.ded.misle.world.boxes.BoxHandling.lineAddScaledBox;

public class World {
    int width;
    int height;
    Box[][] world;
    RoomManager.Room room;

    World(int worldWidth, int worldHeight) {
        this.width = worldWidth;
        this.height = worldHeight;
        this.world = new Box[worldWidth][worldHeight];

        setWorldBorders(worldWidth, worldHeight);

        this.room = RoomManager.roomIDToName(player.pos.getRoomID());
        System.out.println("Loading room: " + this.room);

        fillGrass();
    }

    private void fillGrass() {
        double interval = 2.05;
        lineAddScaledBox(0, 0, (int) Math.ceil((double) width / (interval * 20)),
            (int) Math.ceil((double) height / (interval * 20)), "fill", interval, "grass");
    }
}
