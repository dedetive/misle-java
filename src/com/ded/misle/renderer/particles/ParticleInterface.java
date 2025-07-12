package com.ded.misle.renderer.particles;

import java.awt.*;
import java.awt.geom.Point2D;

import static com.ded.misle.game.GamePanel.player;

public interface ParticleInterface {
	void update();
	void draw(Graphics2D g);
	void destroy();
	void start();
	boolean isRunning();

	static Point2D.Float getDrawPos(Point2D.Float worldPosition) {
		return new Point2D.Float(
				(int) (worldPosition.x - player.pos.getCameraOffsetX()),
				(int) (worldPosition.y - player.pos.getCameraOffsetY()));
	}
}