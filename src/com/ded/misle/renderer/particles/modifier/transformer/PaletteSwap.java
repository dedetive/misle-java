package com.ded.misle.renderer.particles.modifier.transformer;

import com.ded.misle.renderer.image.Painter;
import com.ded.misle.renderer.image.Palette;
import com.ded.misle.renderer.particles.core.*;

import java.awt.image.BufferedImage;

@ModifierType(ModifierType.Type.TRANSFORMER)
public class PaletteSwap implements ParticleModifier {
	private BufferedImage editedImage;
	Palette newPalette;

	public PaletteSwap(Palette newPalette) {
		this.newPalette = newPalette;
	}

	public static PaletteSwap of(Palette newPalette) {
		return new PaletteSwap(newPalette);
	}

	@Override
	public void modify(Particle particle) {
		BufferedImage originalImage = particle.getImage();
		if (originalImage != editedImage) {
			// Base image changed, so invalidate current image and repaint new image
			editedImage = null;
		}
		if (editedImage == null) {
			repaint(originalImage, particle);
		}
	}

	private void repaint(BufferedImage originalImage, Particle particle) {
		Painter p = new Painter(newPalette);
		editedImage = p.paint(originalImage);
		particle.setImage(editedImage);
	}

	@Override
	public ActivationTime getActivationTime() {
		return ActivationTime.FRAME;
	}
}