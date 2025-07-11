package com.ded.misle.renderer.particles;

import java.awt.*;

import static com.ded.misle.game.GamePanel.originalTileSize;
import static com.ded.misle.game.GamePanel.player;

public interface Particle {
	void update();
	void draw(Graphics2D g);

	default void destroy() {
		ParticleRegistry.remove(this);
	}

	default void start() {
		ParticleRegistry.add(this);
	}

	default boolean isRunning() {
		return ParticleRegistry.contains(this);
	}

	static Point getDrawPos(Point origin) {
		return new Point(
				(int) (origin.x * originalTileSize - player.pos.getCameraOffsetX()),
				(int) (origin.y * originalTileSize- player.pos.getCameraOffsetY()));
	}
}