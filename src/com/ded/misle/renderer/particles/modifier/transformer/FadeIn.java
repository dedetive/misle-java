package com.ded.misle.renderer.particles.modifier.transformer;

import com.ded.misle.renderer.particles.core.*;
import com.ded.misle.renderer.smoother.SmoothValue;
import com.ded.misle.renderer.smoother.ValueModifier;

import java.awt.*;
import java.awt.image.BufferedImage;

@ModifierType(ModifierType.Type.TRANSFORMER)
public class FadeIn implements ParticleModifier {

	private final SmoothValue alpha;
	private final float speed;

	public FadeIn(float initial, float max, float speed, ValueModifier... modifiers) {
		this.alpha = new SmoothValue(initial);
		this.alpha.addModifiers(modifiers);
		this.alpha.setTarget(max);
		this.speed = speed;
	}

	public FadeIn(float max, float speed, ValueModifier... modifiers) {
		this(0f,  max, speed, modifiers);
	}

	public FadeIn(float max, ValueModifier... modifiers) {
		this(1f, max, modifiers);
	}

	public static FadeIn of(float initial, float max, float speed, ValueModifier... modifiers) {
		return new FadeIn(initial, max, speed, modifiers);
	}

	public static FadeIn of(float max, float speed, ValueModifier... modifiers) {
		return new FadeIn(max, speed, modifiers);
	}

	public static FadeIn of(float speed, ValueModifier... modifiers) {
		return new FadeIn(speed, modifiers);
	}

	@Override
	public void modify(Particle particle) {
		fade(particle, alpha, speed);
	}

	@Override
	public ActivationTime getActivationTime() {
		return ActivationTime.FRAME;
	}

	static void fade(Particle particle, SmoothValue alpha, float speed) {
		BufferedImage original = particle.getImage();

		int w = original.getWidth();
		int h = original.getHeight();

		BufferedImage faded = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = faded.createGraphics();
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha.getCurrentFloat());
		g.setComposite(ac);
		g.drawImage(original, 0, 0, null);
		g.dispose();
		original.flush();
		particle.setImage(faded);

		alpha.update(speed);
	}
}