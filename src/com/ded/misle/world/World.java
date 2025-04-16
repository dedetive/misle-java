package com.ded.misle.world;

import com.ded.misle.world.boxes.Box;

import java.util.Arrays;

import static com.ded.misle.core.GamePanel.player;
import static com.ded.misle.core.GamePanel.setWorldBorders;
import static com.ded.misle.world.boxes.BoxHandling.*;

public class World {
    public int width;
    public int height;
    public int layers;
    public final int LAYER_COUNT = 5;
    public Box[][][] grid;
    public Background background;
    RoomManager.Room room;

    public World(int worldWidth, int worldHeight) {
        this.setBackground(Background.DEFAULT);

        this.width = worldWidth;
        this.height = worldHeight;
        this.layers = LAYER_COUNT;
        this.grid = new Box[worldWidth][worldHeight][layers];

        setWorldBorders(worldWidth, worldHeight);

//        this.room = RoomManager.roomIDToName(player.pos.getRoomID());
//        System.out.println("Loading room: " + this.room.name);

        player.pos.world = this;
    }

    public World(int worldWidth, int worldHeight, Background background) {
        this(worldWidth, worldHeight);
        this.setBackground(background);
    }

    public void setPos(Box box, int x, int y, boolean force) {
        int previousX = box.getX();
        int previousY = box.getY();
        int previousLayer = box.worldLayer;

        try {
            for (int i = 0; i < this.width; i++) {
                for (int j = 0; j < this.height; j++) {
                    for (int k = 0; k < this.layers; k++) {
                        if (this.grid[i][j][k] == box) {
                            previousX = i;
                            previousY = j;
                            previousLayer = k;
                        }
                    }
                }
            }

            boolean relevantPrevious = this.grid[previousX][previousY][previousLayer] == box;

            for (int k = 0; k < this.layers; k++) {
                boolean isLast = k == this.layers - 1;
                if (force && isLast) {
                    this.grid[x][y][k] = box;
                    box.worldLayer = k;
                    break;
                } else if (this.grid[x][y][k] == null) {
                    this.grid[x][y][k] = box;
                    box.worldLayer = k;
                    break;
                }


                boolean hasMoved = !Arrays.equals(new int[]{previousX, previousY, previousLayer}, new int[]{x, y, k});

                if (hasMoved && relevantPrevious) {
                    this.grid[previousX][previousY][previousLayer] = null;
                }
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Index " + x + ", " + y + " is out of bounds for length " + this.width + ", " + this.height);
        }
    }

    public Box[][][] getNeighborhood(int centerX, int centerY, int radius) {
        Box[][][] b = new Box[radius][radius][layers];
        for (int i = 0; i < radius; i++) {
            for (int j = 0; j < radius; j++) {
                for (int k = 0; k < this.layers; k++) {
                    try {
                        b[i][j][k] = this.grid[i + centerX - radius / 2][j + centerY - radius / 2][k];
                    } catch (ArrayIndexOutOfBoundsException ignored) {
                    }
                }
            }
        }
        return b;
    }

    public enum Background {
        GRASS(createDummyBox(), new Runnable() {
            @Override
            public void run() {
                loadPreset(GRASS.box, BoxPreset.GRASS);
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

    public void setBackground(Background background) {
        this.background = background;
        this.background.updateBackground();
    }
}
