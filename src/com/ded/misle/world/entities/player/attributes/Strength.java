package com.ded.misle.world.entities.player.attributes;

import com.ded.misle.world.entities.player.attributes.core.Attribute;
import com.ded.misle.world.logic.attacks.Attacker;

public class Strength implements Attribute.ActiveAttribute<Integer, Attacker> {
	private int value = 0;

	public Strength(int value) {
		this.value = value;
	}

	public static Strength of(int value) {
		return new Strength(value);
	}

	@Override
	public void setValue(Integer value) {
		this.value = value;
	}

	@Override
	public Integer getValue() {
		return value;
	}

	@Override
	public Attacker apply(Attacker attacker) {
		attacker.setDamage(
				calculateStrengthFormula(attacker.getDamage(), value)
		);

		return attacker;
	}

	public static double calculateStrengthFormula(double d, double v) {
		double safeV = Math.max(v, 1);
		double safeD = Math.max(d, 1);

		double logV = Math.log10(safeV);
		double numerator = Math.log10(safeD);

		return (d * (1 + logV) + (numerator) * Math.pow(safeV, 1.2) / 5);
	}
}