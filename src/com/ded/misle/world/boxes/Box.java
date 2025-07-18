package com.ded.misle.world.boxes;

import com.ded.misle.renderer.image.Painter;
import com.ded.misle.renderer.image.Palette;
import com.ded.misle.renderer.image.PaletteShifter;
import com.ded.misle.renderer.smoother.SmoothValue;
import com.ded.misle.world.data.BoxPreset;
import com.ded.misle.world.data.Direction;
import com.ded.misle.world.entities.Entity;
import com.ded.misle.world.logic.PhysicsEngine;
import com.ded.misle.renderer.image.ImageManager;
import com.ded.misle.renderer.smoother.SmoothPosition;
import com.ded.misle.world.logic.World;
import com.ded.misle.world.logic.effects.Effect;
import com.ded.misle.world.entities.player.Player;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static com.ded.misle.core.Path.getPath;
import static com.ded.misle.game.GamePanel.*;
import static com.ded.misle.renderer.image.ImageManager.*;
import static com.ded.misle.renderer.ColorManager.defaultBoxColor;
import static com.ded.misle.world.logic.PhysicsEngine.ObjectType.BOX;
import static com.ded.misle.world.data.WorldLoader.loadBoxes;
import static com.ded.misle.renderer.MainRenderer.*;

public class Box {
	private int worldX;
	private int worldY;
	public int worldLayer;

	private Color color = defaultBoxColor;
	public String textureName = "solid";

	private boolean hasCollision = false;
	private PhysicsEngine.ObjectType objectType = BOX;
	public Effect effect;
	private boolean interactsWithPlayer = true;
	public boolean isMoving = false;

	private double visualRotation = 0;
	private double visualScaleHorizontal = 1;
	private double visualScaleVertical = 1;
	protected SmoothValue visualOffsetX = new SmoothValue(0);
	protected SmoothValue visualOffsetY = new SmoothValue(0);
	private final SmoothPosition smoothPos = new SmoothPosition(worldX, worldY, originalTileSize);

	private static final Map<String, BufferedImage> cachedTextures = new HashMap<>();
	private static final Map<String, Integer> rotationInstruction = new HashMap<>();
	static {
				rotationInstruction.put("W", 0);
				rotationInstruction.put("D", 90);
				rotationInstruction.put("S", 180);
				rotationInstruction.put("A", 270);
	}

	public Box(int x, int y) {
		worldX = x;
		worldY = y;
		this.setOrigin(new Point(x, y));
		this.setRoomId(player.pos.getRoomID());
		player.pos.world.setPos(this, worldX, worldY);
	}

	// For player creation or dummy box
	public Box() {}

	public void draw(Graphics2D g2d, double cameraOffsetX, double cameraOffsetY) {
		updateVisualPosition(20f);
		float renderX = smoothPos.getRenderX();
		float renderY = smoothPos.getRenderY();

		// Apply the camera offset to the current position
		int screenX = (int) (renderX - cameraOffsetX - this.visualOffsetX.getCurrentFloat() * originalTileSize);
		int screenY = (int) (renderY - cameraOffsetY - this.visualOffsetY.getCurrentFloat() * originalTileSize);
		if (isInvalid(screenX, screenY)) return;

		try {
			if (Objects.equals(this.textureName, "solid")) {
				drawSolid(g2d, screenX, screenY);
			} else if (textureName.equals("invisible")) {
				;
			} else if (BoxPreset.valueOf(textureName.toUpperCase().split("\\.")[0]).hasSides()) {
				drawPresetWithSides(g2d, screenX, screenY);
			} else {
				drawRawTexture(g2d, screenX, screenY);
			}
		} catch (IllegalArgumentException e) {
			drawRawTexture(g2d, screenX, screenY);
		}
	}

	private void drawSolid(Graphics2D g2d, int screenX, int screenY) {
		g2d.setColor(color);
		Rectangle solidBox = new Rectangle(screenX,
				screenY,
				(int) (originalTileSize * visualScaleHorizontal),
				(int) (originalTileSize * visualScaleVertical));
		drawRotatedRect(g2d, solidBox, this.visualRotation);
	}

	private void drawPresetWithSides(Graphics2D g2d, int screenX, int screenY) {
		// Split texture once and reuse the result
		String[] textureParts = textureName.split("\\.");
		String textureName = textureParts[0].toLowerCase();

		boolean mirror =
				this instanceof Entity &&
				((Entity) this).getHorizontalDirection().equals(Direction.LEFT);

		try {
			drawRotatedImage(g2d,
					getTexture(textureName),
					screenX,
					screenY,
					(int) (originalTileSize * visualScaleHorizontal),
					(int) (originalTileSize * visualScaleVertical),
					this.visualRotation, mirror);

			// Draw sides if they exist
			if (textureParts.length > 1) {
				String sides = textureParts[1];
				String[] eachSide = sides.split("");

				for (String side : eachSide) {
					if (side.isEmpty()) continue;
					drawRotatedImage(g2d,
							getTexture(textureName + "_overlayW"),
							screenX,
							screenY,
							(int) (originalTileSize * visualScaleHorizontal),
							(int) (originalTileSize * visualScaleVertical),
							rotationInstruction.get(side) + this.visualRotation, mirror);
				}
			}

			// Draw corners if they exist
			if (textureParts.length > 2) {
				String[] eachCorner = textureParts[2].split("");

				for (String corner : eachCorner) {
					if (Objects.equals(corner, "")) {
						continue;
					}
					drawRotatedImage(g2d,
							getTexture(textureName + "_overlayC"),
							screenX,
							screenY,
							(int) (originalTileSize * visualScaleHorizontal),
							(int) (originalTileSize * visualScaleVertical),
							rotationInstruction.get(corner) + this.visualRotation, mirror);
				}
			}

		} catch (IndexOutOfBoundsException e) {
			// This is fine and not an error; IndexOutOfBounds here mean object has no sides and thus is base image
		}
	}

	private void drawRawTexture(Graphics2D g2d, int screenX, int screenY) {
		boolean mirror =
				this instanceof Entity &&
						((Entity) this).getHorizontalDirection().equals(Direction.LEFT);

		drawRotatedImage(g2d,
				this.getTexture(),
				screenX,
				screenY,
				(int) (originalTileSize * visualScaleHorizontal),
				(int) (originalTileSize * visualScaleVertical),
				this.visualRotation, mirror);
	}

	public static boolean isInvalid(double screenX, double screenY) {
		double margin = originalTileSize * 2;
		return !(screenX >= -margin && screenX <= originalScreenWidth + margin &&
			screenY >= 0 - margin && screenY <= originalScreenHeight + margin);
	}

	public void updateVisualPosition(float speed) {
		smoothPos.setTarget(worldX, worldY, originalTileSize);
		this.smoothPos.update(speed);
	}

	public float getRenderX() {
		return smoothPos.getRenderX();
	}
	public float getRenderY() {
		return smoothPos.getRenderY();
	}

	// COLLISION

	// Check if a point is inside this box
	public boolean isPointColliding(int pointX, int pointY) {
		return pointX == worldX && pointY == worldY;
	}

	public boolean getHasCollision() {
		return hasCollision;
	}

	// BOX POSITION AND SCALING

	public int getX() {
		return worldX;
	}

	public int getY() {
		return worldY;
	}

	public void setX(int x) {
		setPos(x, worldY);
	}

	public void setY(int y) {
		setPos(worldX, y);
	}

	public Point getPos() {
		return new Point(worldX, worldY);
	}

	public void setPos(int x, int y) {
		if (this instanceof Entity) ((Entity) this).updateLastDirection(Direction.interpretDirection(
				x - this.worldX, y - this.worldY
		));

		setPos(x, y, -1);
	}

	public void setPos(int x, int y, int layer) {
		this.worldX = x;
		this.worldY = y;
		World world = player.pos.world;

		try {
			world.setPos(this, x, y, layer, false);
		} catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
			loadBoxes();
			world = player.pos.world;
			world.setPos(this, x, y, layer, false);
		}
	}

	public double getVisualOffsetX() {
		return visualOffsetX.getCurrentFloat();
	}

	public double getVisualOffsetY() {
		return visualOffsetY.getCurrentFloat();
	}

	public void updateVisualOffset(float speed) {
		visualOffsetX.update(speed);
		visualOffsetY.update(speed);
	}

	public double getVisualScaleHorizontal() {
		return visualScaleHorizontal;
	}

	public double getVisualScaleVertical() {
		return visualScaleVertical;
	}

	public void setVisualRotation(double visualRotation) { this.visualRotation = visualRotation; }

	public double getVisualRotation() { return visualRotation; }

	// BOX CHARACTERISTICS

	public void setColor(Color color) {
		this.color = color;
		if (this instanceof Player) {
			for (ImageManager.ImageName img : playerImages) {
				editImageColor(cachedImages.get(img), color);
			}
		}
	}

	public Color getColor() { return color; }

	public void setCollision(boolean hasCollision) {
		this.hasCollision = hasCollision;
	}

	public Box setVisualScaleHorizontal(double visualScaleHorizontal) {
		this.visualScaleHorizontal = visualScaleHorizontal;
		return this;
	}

	public Box setVisualScaleVertical(double visualScaleVertical) {
		this.visualScaleVertical = visualScaleVertical;
		return this;
	}

	public Box setTexture(String texture) {
		this.textureName = texture;
		return this;
	}

	public BufferedImage getTexture() {
		return getTexture(textureName);
	}


	public static BufferedImage getTexture(String boxTextureName) {
		if (boxTextureName.contains("invisible")) return null;

		// Check if the texture is already cached
		if (!cachedTextures.containsKey(boxTextureName)) {
			Path basePath = getPath(com.ded.misle.core.Path.PathTag.RESOURCES).resolve("images/boxes/");
			String fileName = boxTextureName + ".png";
			Path fullPath = basePath.resolve(fileName);

			try {
				// Load the texture and cache it
				BufferedImage texture = ImageIO.read(fullPath.toFile());
				cachedTextures.put(boxTextureName, texture); // Cache the loaded image
			} catch (IOException e) {
				// Attempt to recreate
				if (boxTextureName.contains("_overlay")) {
					String overlayType = boxTextureName.substring(boxTextureName.lastIndexOf("_"));
					BufferedImage origTexture = getTexture(boxTextureName.substring(0, boxTextureName.indexOf("_overlay")));
					BufferedImage defaultOverlayTexture = getTexture("stone_brick_wall" + overlayType);

					Palette palette = new Palette(origTexture);
					Palette other = new Palette(defaultOverlayTexture);

					PaletteShifter ps = new PaletteShifter(palette);
					ps.rotated(2);
					ps.mergedWith(other);
					ps.limited(palette.size() + 1);
					ps.offset(1);
					ps.gamma(1.35f);
					palette = ps.getPalette();

					Painter painter = new Painter(palette);
					BufferedImage texture = painter.paint(defaultOverlayTexture);
					cachedTextures.put(boxTextureName, texture);
                    try {
                        ImageIO.write(texture, "png", fullPath.toFile());
						System.out.println("Recreated texture: " + boxTextureName);
					} catch (IOException ex) {
						System.err.println("Failed to recreate texture: " + boxTextureName + " at path: " + fullPath);
						return null;
                    }
                    return texture;
				}

				System.err.println("Can't read Box texture input file: " + fullPath);
				return null; // Return null if image fails to load
			}
		}
		return cachedTextures.get(boxTextureName); // Return the cached image
	}

	// Object type (BOX, HP_BOX)

	public PhysicsEngine.ObjectType getObjectType() {
		return objectType;
	}

	public void setObjectType(PhysicsEngine.ObjectType objectType) {
		this.objectType = objectType;
	}

	// Interacts with player

	public void setInteractsWithPlayer(boolean interactsWithPlayer) {
		this.interactsWithPlayer = interactsWithPlayer;
	}

	public boolean getInteractsWithPlayer() {
		return interactsWithPlayer;
	}

	// Misc

	private int roomId;
	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	private Point origin;
	public void setOrigin(Point origin) {
		this.origin = origin;
	}

	public Point getOrigin() {
		return origin;
	}

	public String getId() {
		return this.effect != null
			? roomId + "-" + this.getOrigin().x + "." + this.getOrigin().y + "-" + this.effect.getClass().getSimpleName()
			: roomId + "-" + this.getOrigin().x + "." + this.getOrigin().y;
	}

	@Override
	public String toString() {
		return "Box{" +
			"textureName=" + textureName +
			", x=" + worldX +
			", y=" + worldY +
			", z=" + worldLayer +
			", Effect=" + (effect != null ? effect.toString() : "null") +
			'}';
	}
}
