package com.ded.misle.renderer.image;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public final class ImageCloner {
	private ImageCloner() {}

	public static BufferedImage cloneBufferedImage(BufferedImage source) {
		if (source == null) return null;

		BufferedImage clonedImage = new BufferedImage(
				source.getWidth(),
				source.getHeight(),
				source.getType());

		Graphics2D g2d = clonedImage.createGraphics();
		g2d.drawImage(source, 0, 0, null);
		g2d.dispose();

		return clonedImage;
	}
}