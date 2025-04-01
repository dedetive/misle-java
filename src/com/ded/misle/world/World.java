package com.ded.misle.world;

import com.ded.misle.world.boxes.Box;

import static com.ded.misle.core.GamePanel.player;
import static com.ded.misle.core.GamePanel.setWorldBorders;
import static com.ded.misle.world.boxes.BoxHandling.lineAddScaledBox;

public class World {
    public int width;
    public int height;
    public Box[][] grid;
    RoomManager.Room room;

    public World(int worldWidth, int worldHeight) {
        this.width = worldWidth;
        this.height = worldHeight;
        this.grid = new Box[worldWidth][worldHeight];

        setWorldBorders(worldWidth, worldHeight);

        this.room = RoomManager.roomIDToName(player.pos.getRoomID());
        System.out.println("Loading room: " + this.room);

        player.pos.world = this;

        fillGrass();
    }

    public void setPos(Box box, int x, int y, boolean force) {
        try {
            if (force) {
                this.grid[x][y] = box;
            } else if (this.grid[x][y] == null) {
                this.grid[x][y] = box;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Index " + x + ", " + y + " is out of bounds for length " + this.grid[0].length + ", " + this.grid[1].length);
        }
    }

    private void fillGrass() {
        double interval = 2.05;
        lineAddScaledBox(0, 0, (int) Math.ceil((double) width / (interval)),
            (int) Math.ceil((double) height / (interval)), "fill", interval, "grass");
    }
}
