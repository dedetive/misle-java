package com.ded.misle.world;

import com.ded.misle.world.boxes.Box;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.ded.misle.core.GamePanel.player;
import static com.ded.misle.core.GamePanel.setWorldBorders;
import static com.ded.misle.world.boxes.BoxHandling.*;

public class World {
    public int width;
    public int height;
    public int layers;
    public final int LAYER_COUNT = 30;
    public Box[][][] grid;
    public Background background;

    public World(int worldWidth, int worldHeight) {
        this.setBackground(Background.DEFAULT);

        this.width = worldWidth;
        this.height = worldHeight;
        this.layers = LAYER_COUNT;
        this.grid = new Box[worldWidth][worldHeight][layers];

        setWorldBorders(worldWidth, worldHeight);

        player.pos.world = this;
    }

    public World(int worldWidth, int worldHeight, Background background) {
        this(worldWidth, worldHeight);
        this.setBackground(background);
    }

    public void setPos(Box box, int x, int y) {
        setPos(box, x, y, -1, false);
    }

    public void setPos(Box box, int x, int y, int z, boolean force) {
        int[] previousPos = findPreviousPosition(box);
        int previousX = previousPos[0], previousY = previousPos[1], previousLayer = previousPos[2];
        boolean relevantPrevious = grid[previousX][previousY][previousLayer] == box;

        try {
            if (isValidLayer(z) && isFree(x, y, z)) {
                placeBox(box, x, y, z);
                clearOldPosition(box, previousX, previousY, previousLayer, x, y, z, relevantPrevious);
                return;
            }

            List<Integer> freeLayers = getFreeLayers(x, y);
            int highestOccupied = getHighestOccupiedLayer(x, y);

            if (freeLayers.size() == 1) {
                int chosen = freeLayers.getFirst();
                placeBox(box, x, y, chosen);
                clearOldPosition(box, previousX, previousY, previousLayer, x, y, chosen, relevantPrevious);
                return;
            }

            int aboveHighest = highestOccupied + 1;
            if (aboveHighest < layers && isFree(x, y, aboveHighest)) {
                placeBox(box, x, y, aboveHighest);
                clearOldPosition(box, previousX, previousY, previousLayer, x, y, aboveHighest, relevantPrevious);
                return;
            }

            if (force) {
                for (int k = 0; k < layers; k++) {
                    if (isFree(x, y, k) || k == layers - 1) {
                        placeBox(box, x, y, k);
                        clearOldPosition(box, previousX, previousY, previousLayer, x, y, k, relevantPrevious);
                        return;
                    }
                }
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.printf("Index %d, %d is out of bounds for length %d, %d%n", x, y, width, height);
        }
    }

    private int[] findPreviousPosition(Box box) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                for (int k = 0; k < layers; k++) {
                    if (grid[i][j][k] == box) {
                        return new int[]{i, j, k};
                    }
                }
            }
        }
        return new int[]{box.getX(), box.getY(), box.worldLayer};
    }

    private List<Integer> getFreeLayers(int x, int y) {
        List<Integer> free = new ArrayList<>();
        for (int k = 0; k < layers; k++) {
            if (grid[x][y][k] == null) free.add(k);
        }
        return free;
    }

    private int getHighestOccupiedLayer(int x, int y) {
        int highest = -1;
        for (int k = 0; k < layers; k++) {
            if (grid[x][y][k] != null) highest = k;
        }
        return highest;
    }

    private boolean isValidLayer(int z) {
        return z >= 0 && z < layers;
    }

    private boolean isFree(int x, int y, int z) {
        return grid[x][y][z] == null;
    }

    private void placeBox(Box box, int x, int y, int z) {
        grid[x][y][z] = box;
        box.worldLayer = z;
    }

    private void clearOldPosition(Box box, int oldX, int oldY, int oldZ, int newX, int newY, int newZ, boolean relevantPrevious) {
        boolean moved = oldX != newX || oldY != newY || oldZ != newZ;
        if (moved && relevantPrevious) {
            grid[oldX][oldY][oldZ] = null;
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
