package com.ded.misle.world;

import com.ded.misle.world.boxes.Box;

import java.util.ArrayList;
import java.util.List;

import static com.ded.misle.core.GamePanel.player;
import static com.ded.misle.core.GamePanel.setWorldBorders;
import static com.ded.misle.world.boxes.BoxHandling.*;

/**
 * Represents an internal 3D world grid (visually 2D) where Box objects can be placed across different layers.
 * Manages placement logic, background configuration, and access to neighbors.
 */
public class World {

    /** Width of the world grid (X dimension). */
    public int width;

    /** Height of the world grid (Y dimension). */
    public int height;

    /** Number of vertical layers (Z dimension) in the world grid. */
    public int layers;

    /** Default number of layers in the world grid. */
    public final int LAYER_COUNT = 30;

    /** 3D grid containing all boxes placed in the world. */
    public Box[][][] grid;

    /** Background of the world, which can affect visuals or presets. */
    public Background background;

    /**
     * Constructs a world with the specified width and height using the default background.
     *
     * @param worldWidth  Width of the world grid.
     * @param worldHeight Height of the world grid.
     */
    public World(int worldWidth, int worldHeight) {
        this.setBackground(Background.DEFAULT);
        this.width = worldWidth;
        this.height = worldHeight;
        this.layers = LAYER_COUNT;
        this.grid = new Box[worldWidth][worldHeight][layers];
        setWorldBorders(worldWidth, worldHeight);
        player.pos.world = this;
    }

    /**
     * Constructs a world with the specified dimensions and background.
     *
     * @param worldWidth  Width of the world grid.
     * @param worldHeight Height of the world grid.
     * @param background  The background to assign to the world.
     */
    public World(int worldWidth, int worldHeight, Background background) {
        this(worldWidth, worldHeight);
        this.setBackground(background);
    }

    /**
     * Places a box at the given (x, y) coordinates, letting the method determine the appropriate layer.
     *
     * @param box The box to place.
     * @param x   X-coordinate in the grid.
     * @param y   Y-coordinate in the grid.
     */
    public void setPos(Box box, int x, int y) {
        setPos(box, x, y, -1, false);
    }

    /**
     * Places a box at the specified coordinates and layer, with optional force placement.
     * If the layer is invalid or unavailable, this method searches for an alternative.
     *
     * @param box   The box to place.
     * @param x     X-coordinate in the grid.
     * @param y     Y-coordinate in the grid.
     * @param z     Desired layer to place the box in (-1 to auto-determine).
     * @param force Whether to force placement even if no layer is obviously free.
     */
    public void setPos(Box box, int x, int y, int z, boolean force) {
        int[] previousPos = findPreviousPosition(box);
        int previousX = previousPos[0], previousY = previousPos[1], previousLayer = previousPos[2];
        boolean relevantPrevious = grid[previousX][previousY][previousLayer] == box;

        try {
            if (isValidLayer(z) && isFree(x, y, z)) {
                placeBox(box, x, y, z);
                clearOldPosition(previousX, previousY, previousLayer, x, y, z, relevantPrevious);
                return;
            }

            List<Integer> freeLayers = getFreeLayers(x, y);
            int highestOccupied = getHighestOccupiedLayer(x, y);

            if (freeLayers.size() == 1) {
                int chosen = freeLayers.getFirst();
                placeBox(box, x, y, chosen);
                clearOldPosition(previousX, previousY, previousLayer, x, y, chosen, relevantPrevious);
                return;
            }

            int aboveHighest = highestOccupied + 1;
            if (aboveHighest < layers && isFree(x, y, aboveHighest)) {
                placeBox(box, x, y, aboveHighest);
                clearOldPosition(previousX, previousY, previousLayer, x, y, aboveHighest, relevantPrevious);
                return;
            }

            if (force) {
                for (int k = 0; k < layers; k++) {
                    if (isFree(x, y, k) || k == layers - 1) {
                        placeBox(box, x, y, k);
                        clearOldPosition(previousX, previousY, previousLayer, x, y, k, relevantPrevious);
                        return;
                    }
                }
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.printf("Index %d, %d is out of bounds for length %d, %d%n", x, y, width, height);
        }
    }

    /**
     * Attempts to find the last known position of the given box in the grid.
     *
     * @param box The box to locate.
     * @return An array of size 3 with coordinates [x, y, z].
     */
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

    /**
     * Returns a list of free layer indices at the given (x, y) coordinate.
     *
     * @param x X-coordinate.
     * @param y Y-coordinate.
     * @return List of free layer indices.
     */
    private List<Integer> getFreeLayers(int x, int y) {
        List<Integer> free = new ArrayList<>();
        for (int k = 0; k < layers; k++) {
            if (grid[x][y][k] == null) free.add(k);
        }
        return free;
    }

    /**
     * Returns the highest occupied layer at the specified (x, y) position.
     *
     * @param x X-coordinate.
     * @param y Y-coordinate.
     * @return The highest index of an occupied layer or -1 if none.
     */
    private int getHighestOccupiedLayer(int x, int y) {
        int highest = -1;
        for (int k = 0; k < layers; k++) {
            if (grid[x][y][k] != null) highest = k;
        }
        return highest;
    }

    /**
     * Checks whether the given layer index is valid.
     *
     * @param z Layer index.
     * @return True if within bounds, false otherwise.
     */
    private boolean isValidLayer(int z) {
        return z >= 0 && z < layers;
    }

    /**
     * Checks if the given grid position is free (null).
     *
     * @param x X-coordinate.
     * @param y Y-coordinate.
     * @param z Layer index.
     * @return True if unoccupied.
     */
    private boolean isFree(int x, int y, int z) {
        return grid[x][y][z] == null;
    }

    /**
     * Places the box at the specified position and updates its world layer.
     *
     * @param box The box to place.
     * @param x   X-coordinate.
     * @param y   Y-coordinate.
     * @param z   Layer index.
     */
    private void placeBox(Box box, int x, int y, int z) {
        grid[x][y][z] = box;
        box.worldLayer = z;
    }

    /**
     * Clears the old position of a box if it moved and was previously registered at the expected location.
     *
     * @param oldX             Previous X.
     * @param oldY             Previous Y.
     * @param oldZ             Previous layer.
     * @param newX             New X.
     * @param newY             New Y.
     * @param newZ             New layer.
     * @param relevantPrevious Whether the previous position is known and valid.
     */
    private void clearOldPosition(int oldX, int oldY, int oldZ, int newX, int newY, int newZ, boolean relevantPrevious) {
        boolean moved = oldX != newX || oldY != newY || oldZ != newZ;
        if (moved && relevantPrevious) {
            grid[oldX][oldY][oldZ] = null;
        }
    }

    /**
     * Returns a subgrid (cube) of boxes surrounding a center position.
     *
     * @param centerX X-coordinate of center.
     * @param centerY Y-coordinate of center.
     * @param radius  Width/height of the cube (must be odd).
     * @return A 3D array of boxes representing the neighborhood.
     */
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

    /**
     * Enum representing the background of the world and its corresponding preset data.
     */
    public enum Background {

        /**
         * Grass background as seen in {@systemProperty resources/images/boxes/grass.png}.
         */
        GRASS(createDummyBox(), new Runnable() {
            @Override
            public void run() {
                loadPreset(GRASS.box, BoxPreset.GRASS);
            }
        });

        /** The default background used in new worlds. */
        public static final Background DEFAULT = GRASS;

        /** A dummy box associated with this background. */
        public final Box box;

        /** Runnable that updates the background (e.g., applying a preset). */
        public final Runnable updateBackground;

        /** Triggers the background update. */
        public void updateBackground() {
            this.updateBackground.run();
        }

        Background(Box box, Runnable updateBackground) {
            this.box = box;
            this.updateBackground = updateBackground;
        }
    }

    /**
     * Sets the background of the world and triggers its update logic.
     *
     * @param background The new background to apply.
     */
    public void setBackground(Background background) {
        this.background = background;
        this.background.updateBackground();
    }
}
