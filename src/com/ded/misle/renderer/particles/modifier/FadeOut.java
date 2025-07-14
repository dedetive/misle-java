package com.ded.misle.renderer.particles.modifier;

import com.ded.misle.renderer.particles.core.*;
import com.ded.misle.renderer.smoother.SmoothValue;
import com.ded.misle.renderer.smoother.ValueModifier;

import java.awt.*;
import java.awt.image.BufferedImage;

@ModifierType(ModifierType.Type.TRANSFORMER)
public class FadeOut implements ParticleModifier {

	private final SmoothValue alpha;
	private final float speed;

	public FadeOut(float min, float speed, ValueModifier... modifiers) {
		this.alpha = new SmoothValue(1f);
		this.alpha.addModifiers(modifiers);
		this.alpha.setTarget(min);
		this.speed = speed;
	}

	public FadeOut(float speed, ValueModifier... modifiers) {
		this(0f, speed, modifiers);
	}

	public static FadeOut of(float max, float speed, ValueModifier... modifiers) {
		return new FadeOut(max, speed, modifiers);
	}

	public static FadeOut of(float speed, ValueModifier... modifiers) {
		return new FadeOut(speed, modifiers);
	}

	@Override
	public void modify(Particle particle) {
		BufferedImage original = particle.getImage();

		int w = original.getWidth();
		int h = original.getHeight();

		BufferedImage faded = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = faded.createGraphics();
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha.getCurrentFloat());
		g.setComposite(ac);
		g.drawImage(original, 0, 0, null);
		g.dispose();
		particle.setImage(faded);

		alpha.update(speed);
	}

	@Override
	public ActivationTime getActivationTime() {
		return ActivationTime.FRAME;
	}
}