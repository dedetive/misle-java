package com.ded.misle.renderer.particles;

import java.awt.*;

import static com.ded.misle.game.GamePanel.originalTileSize;
import static com.ded.misle.game.GamePanel.player;

public interface ParticleInterface {
	void update();
	void draw(Graphics2D g);
	void destroy();
	void start();
	boolean isRunning();

	static Point getDrawPos(Point origin) {
		return new Point(
				(int) (origin.x * originalTileSize - player.pos.getCameraOffsetX()),
				(int) (origin.y * originalTileSize- player.pos.getCameraOffsetY()));
	}
}