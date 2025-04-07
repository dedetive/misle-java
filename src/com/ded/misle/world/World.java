package com.ded.misle.world;

import com.ded.misle.world.boxes.Box;

import java.util.Arrays;

import static com.ded.misle.core.GamePanel.player;
import static com.ded.misle.core.GamePanel.setWorldBorders;
import static com.ded.misle.world.boxes.BoxHandling.*;

public class World {
    public int width;
    public int height;
    public Box[][] grid;
    public Background background;
    RoomManager.Room room;

    public World(int worldWidth, int worldHeight) {
        this.background = Background.DEFAULT;

        this.width = worldWidth;
        this.height = worldHeight;
        this.grid = new Box[worldWidth][worldHeight];

        setWorldBorders(worldWidth, worldHeight);

        this.room = RoomManager.roomIDToName(player.pos.getRoomID());
        System.out.println("Loading room: " + this.room);

        player.pos.world = this;
    }

    public World(int worldWidth, int worldHeight, Background background) {
        this(worldWidth, worldHeight);
        this.background = background;
        this.background.updateBackground();
    }

    public void setPos(Box box, int x, int y, boolean force) {
        int previousX = box.getX();
        int previousY = box.getY();

        try {
            for (int i = 0; i < this.grid.length; i++) {
                for (int j = 0; j < this.grid[0].length; j++) {
                    if (this.grid[i][j] == box) {
                        previousX = i;
                        previousY = j;
                    }
                }
            }

            if (force) {
                this.grid[x][y] = box;
            } else if (this.grid[x][y] == null) {
                this.grid[x][y] = box;
            }

            boolean hasMoved = !Arrays.equals(new int[]{previousX, previousY}, new int[]{x, y});

            if (hasMoved) {
                this.grid[previousX][previousY] = null;
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Index " + x + ", " + y + " is out of bounds for length " + this.grid[0].length + ", " + this.grid[1].length);
        }
    }


    public enum Background {
        GRASS(createDummyBox(), new Runnable() {
            @Override
            public void run() {
                loadPreset(GRASS.box, "grass");
            }
        }),

        ;

        public static final Background DEFAULT = GRASS;
        public final Box box;
        public final Runnable updateBackground;

        public void updateBackground() {
            this.updateBackground.run();
        }

        Background(Box box, Runnable updateBackground) {
            this.box = box;
            this.updateBackground = updateBackground;
        }
    }
}
